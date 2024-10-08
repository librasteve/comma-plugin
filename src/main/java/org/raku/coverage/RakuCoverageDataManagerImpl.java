package org.raku.coverage;

import com.intellij.execution.configurations.CommandLineState;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Alarm;
import org.raku.filetypes.RakuModuleFileType;
import org.raku.psi.RakuFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RakuCoverageDataManagerImpl extends RakuCoverageDataManager {
    private final Project project;
    private static final Pattern indexMatcher = Pattern.compile("^([^\\t]+)\\t(.+)");
    private static final Pattern lineMatcher = Pattern.compile("^HIT {2}(.+?)(?: \\(.+\\))? {2}(\\d+)");
    private final Set<RakuCoverageSuite> coverageSuites = new HashSet<>();
    private RakuCoverageSuite currentSuite;
    private final ConcurrentMap<Editor, RakuCoverageSourceAnnotator> editorAnnotators =
        new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CoverageStatistics> fileCoverageStatsCache
            = new ConcurrentHashMap<>();

    public RakuCoverageDataManagerImpl(@NotNull Project project) {
        this.project = project;
        EditorFactory.getInstance().addEditorFactoryListener(new CoverageEditorFactoryListener(), project);
    }

    @Override
    void addSuiteFromSingleCoverageFile(File data, RakuCoverageCommandLineState state) {
        Map<String, Set<Integer>> covered = parseCoverageFile(data);
        RakuCoverageSuite suite = createSuite(state);
        suite.addCoverageData("main", covered);
        changeToSuite(suite);
    }

    @Override
    public void addSuiteFromIndexFile(File index, RakuCoverageTestRunningState state) {
        RakuCoverageSuite suite = createSuite(state);
        try (BufferedReader br = new BufferedReader(new FileReader(index, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = indexMatcher.matcher(line);
                if (matcher.matches())
                    suite.addCoverageData(matcher.group(1),
                            parseCoverageFile(new File(matcher.group(2))));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        changeToSuite(suite);
    }

    @Override
    public void changeToSuite(RakuCoverageSuite suite) {
        currentSuite = suite;
        fileCoverageStatsCache.clear();
        triggerPresentationUpdate();
    }

    @Override
    public void hideCoverageData() {
        currentSuite = null;
        fileCoverageStatsCache.clear();
        triggerPresentationUpdate();
    }

    @Override
    public boolean hasCurrentCoverageSuite() {
        return currentSuite != null;
    }

    private RakuCoverageSuite createSuite(CommandLineState state) {
        // Form suite info.
        String name = project.getName() + " - " + state.getEnvironment().getRunProfile().getName();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Create a directory for the suite.
        @NonNls final String coverageRootPath = PathManager.getSystemPath() + File.separator + "coverage";
        final String path = coverageRootPath + File.separator + FileUtil.sanitizeFileName(name) +
            File.separator + FileUtil.sanitizeFileName(timestamp);
        new File(path).mkdirs();

        // Create the suite record.
        RakuCoverageSuite suite = new RakuCoverageSuite(name, timestamp, path);
        coverageSuites.add(suite);
        return suite;
    }

    private Map<String,Set<Integer>> parseCoverageFile(File data) {
        try (BufferedReader br = new BufferedReader(new FileReader(data, StandardCharsets.UTF_8))) {
            Map<String, Set<Integer>> covered = new HashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = lineMatcher.matcher(line);
                if (matcher.matches()) {
                    String filename = matcher.group(1).replace(File.separator, "/");
                    if (isProjectFile(filename)) {
                        Integer lineNumber = Integer.parseInt(matcher.group(2));
                        if (!covered.containsKey(filename))
                            covered.put(filename, new HashSet<>());
                        covered.get(filename).add(lineNumber);
                    }
                }
            }
            return covered;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isProjectFile(String filename) {
        return filename.startsWith(project.getBasePath());
    }

    @Override
    public void triggerPresentationUpdate() {
        renewInformationInEditors();
        ProjectView.getInstance(project).refresh();
    }

    private void renewInformationInEditors() {
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        final VirtualFile[] openFiles = fileEditorManager.getOpenFiles();
        for (VirtualFile openFile : openFiles) {
            final FileEditor[] allEditors = fileEditorManager.getAllEditors(openFile);
            applyInformationToEditor(allEditors, openFile);
        }
    }

    private void applyInformationToEditor(FileEditor[] editors, final VirtualFile file) {
        PsiFile psiFile = ReadAction.compute(() -> PsiManager.getInstance(project).findFile(file));
        if (psiFile != null && psiFile.isPhysical()) {
            String path = psiFile.getVirtualFile().getPath();
            for (FileEditor editor : editors) {
                if (editor instanceof TextEditor) {
                    // Clear any existing annotations.
                    final Editor textEditor = ((TextEditor)editor).getEditor();
                    RakuCoverageSourceAnnotator ann = editorAnnotators.remove(textEditor);
                    if (ann != null)
                        Disposer.dispose(ann);

                    // Now add annotations.
                    if (currentSuite == null)
                        continue;
                    Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
                    if (fileData == null)
                        continue;
                    ann = new RakuCoverageSourceAnnotator(psiFile, textEditor, fileData);
                    editorAnnotators.put(textEditor, ann);
                    ann.showAnnotations();
                }
            }
        }
    }

    private class CoverageEditorFactoryListener implements EditorFactoryListener {
        private final Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, project);
        private final Map<Editor, Runnable> myCurrentEditors = new HashMap<>();

        @Override
        public void editorCreated(@NotNull EditorFactoryEvent event) {
            final Editor editor = event.getEditor();
            if (editor.getProject() != project) return;
            if (currentSuite == null) return;
            final PsiFile psiFile = ReadAction.compute(() -> {
                if (project.isDisposed()) return null;
                final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
                final Document document = editor.getDocument();
                return documentManager.getPsiFile(document);
            });

            if (psiFile != null && psiFile.isPhysical()) {
                String path = psiFile.getVirtualFile().getPath();
                Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
                if (fileData == null)
                    return;
                RakuCoverageSourceAnnotator ann = editorAnnotators.get(editor);
                if (ann == null) {
                    ann = new RakuCoverageSourceAnnotator(psiFile, editor, fileData);
                }
                editorAnnotators.put(editor, ann);
                ann.showAnnotations();

                final RakuCoverageSourceAnnotator finalAnn = ann;
                final Runnable request = () -> {
                    if (project.isDisposed()) return;
                    finalAnn.showAnnotations();
                };

                myCurrentEditors.put(editor, request);
                myAlarm.addRequest(request, 100);
            }
        }

        @Override
        public void editorReleased(@NotNull EditorFactoryEvent event) {
            final Editor editor = event.getEditor();
            if (editor.getProject() != project) return;
            try {
                RakuCoverageSourceAnnotator ann = editorAnnotators.remove(editor);
                if (ann != null) {
                    Disposer.dispose(ann);
                }
            }
            finally {
                final Runnable request = myCurrentEditors.remove(editor);
                if (request != null) {
                    myAlarm.cancelRequest(request);
                }
            }
        }
    }

    @Override
    public CoverageStatistics coverageForFile(VirtualFile file) {
        String path = file.getPath();
        CoverageStatistics result = fileCoverageStatsCache.get(path);
        if (result == null) {
            result = computeCoverageForFile(file);
            if (result != null)
                fileCoverageStatsCache.put(path, result);
        }
        return result;
    }

    private CoverageStatistics computeCoverageForFile(VirtualFile file) {
        // Get coverage data if available.
        if (currentSuite == null)
            return null;
        String path = file.getPath();
        Map<String, Set<Integer>> fileData = currentSuite.lineDataForPath(path);
        if (fileData == null)
            return null;

        // Look up file and line mapping for the file.
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null)
            return null;
        Map<Integer, List<Integer>> statementLineMap = ((RakuFile)psiFile).getStatementLineMap();

        // Make a flat map of all line starters we might cover.
        Set<Integer> coverable = new HashSet<>();
        for (Map.Entry<Integer, List<Integer>> lineMapping : statementLineMap.entrySet())
            if (lineMapping.getValue() != null)
                coverable.add(lineMapping.getKey());
        int coverableLines = coverable.size();
        if (coverableLines == 0)
            return null;

        // Delete the covered ones from the set, leaving the uncovered.
        for (Set<Integer> covered : fileData.values())
            for (int oneBasedLine : covered)
                coverable.remove(oneBasedLine - 1);
        int uncoveredLines = coverable.size();

        // Return statistics object.
        return new CoverageStatistics(coverableLines - uncoveredLines, coverableLines);
    }

    @Override
    public CoverageStatistics coverageForDirectory(VirtualFile dir) {
        /* Gather all modules files in the directory. */
        List<VirtualFile> allSourceFiles = new ArrayList<>();
        VfsUtilCore.visitChildrenRecursively(dir, new VirtualFileVisitor<>() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                if (!file.isDirectory() && file.getFileType() instanceof RakuModuleFileType) {
                    allSourceFiles.add(file);
                }
                return true;
            }
        });

        /* Gather all the statistics. */
        List<CoverageStatistics> allStatistics = allSourceFiles.stream()
                .map(f -> coverageForFile(f))
                .filter(s -> s != null)
                 .toList();
        if (allStatistics.isEmpty())
            return null;
        int totalCovered = 0;
        int totalCoverable = 0;
        for (CoverageStatistics stats : allStatistics) {
            totalCovered += stats.coveredLines();
            totalCoverable += stats.coverableLines();
        }
        return new CoverageStatistics(totalCovered, totalCoverable);
    }
}
