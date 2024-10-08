package org.raku.readerMode;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBUI;
import org.raku.structureView.RakuStructureViewBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class RakuModuleViewEditor extends UserDataHolderBase implements TextEditor {
    private final String myName;
    private final TextEditor myEditor;
    private final VirtualFile myFile;
    private PodPreviewEditor myViewer;
    private JComponent myComponent;
    private Runnable myTriggerPodRenderCode;
    private RakuReaderModeState myState;

    public RakuModuleViewEditor(TextEditor editor,
                                PodPreviewEditor viewer,
                                @NotNull VirtualFile file,
                                String editorName) {
        myEditor = editor;
        myViewer = viewer;
        myFile = file;
        myName = editorName;
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return myFile;
    }

    @Override
    public @NotNull JComponent getComponent() {
        if (myComponent == null) {
            final JBSplitter splitter = new JBSplitter(false, 0.5f, 0.15f, 0.85f);
            splitter.setSplitterProportionKey("RakuModuleViewEditor.SplitterProportionKey");
            splitter.setFirstComponent(myEditor.getComponent());
            splitter.setSecondComponent(myViewer.getComponent());
            splitter.setDividerWidth(3);

            myComponent = JBUI.Panels.simplePanel(splitter);
            updateState(RakuReaderModeState.CODE);
        }
        return myComponent;
    }

    private void invalidateLayout() {
        myComponent.repaint();

        final JComponent focusComponent = getPreferredFocusedComponent();
        if (focusComponent != null) {
            IdeFocusManager.findInstanceByComponent(focusComponent).requestFocus(focusComponent, true);
        }
    }

    public void updateState(RakuReaderModeState state) {
        ApplicationManager.getApplication().invokeLater(() -> {
            myState = state;
            PsiFile psiFile = PsiDocumentManager.getInstance(Objects.requireNonNull(myEditor.getEditor().getProject()))
                .getPsiFile(myEditor.getEditor().getDocument());
            if (psiFile != null)
                psiFile.putUserData(RakuActionProvider.RAKU_EDITOR_MODE_STATE, state);
            myTriggerPodRenderCode.run();
            invalidateLayout();
            myEditor.getComponent().setVisible(state == RakuReaderModeState.CODE || state == RakuReaderModeState.SPLIT);
            myViewer.getComponent().setVisible(state == RakuReaderModeState.DOCS || state == RakuReaderModeState.SPLIT);
        });
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        if (myState == null)
            myState = RakuReaderModeState.CODE;
        switch (myState) {
            case SPLIT:
            case CODE:
                return myEditor.getPreferredFocusedComponent();
            case DOCS:
                return myViewer.getPreferredFocusedComponent();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return myName;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        if (state instanceof Perl6ModuleEditorState compositeState) {
            if (compositeState.getEditorState() != null) {
                myEditor.setState(compositeState.getEditorState());
            }
            if (compositeState.getViewerState() != null) {
                myViewer.setState(compositeState.getViewerState());
            }
        }
    }

    private void requestFocus() {
        final JComponent focusComponent = getPreferredFocusedComponent();
        if (focusComponent != null) {
            IdeFocusManager.findInstanceByComponent(focusComponent).requestFocus(focusComponent, true);
        }
    }

    @Override
    public @NotNull FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return new Perl6ModuleEditorState(myEditor.getState(level), myViewer.getState(level));
    }

    public RakuReaderModeState getPresentedState() {
        return myState;
    }

    @Override
    public @Nullable StructureViewBuilder getStructureViewBuilder() {
        PsiFile psiFile = PsiDocumentManager.getInstance(Objects.requireNonNull(myEditor.getEditor().getProject()))
            .getPsiFile(myEditor.getEditor().getDocument());
        return psiFile == null ? null : new RakuStructureViewBuilder(psiFile);
    }

    @Override
    public @Nullable BackgroundEditorHighlighter getBackgroundHighlighter() {
        return myEditor.getBackgroundHighlighter();
    }

  @Override
    public boolean isModified() {
        return myEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return myEditor.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        myEditor.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        myEditor.removePropertyChangeListener(listener);
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return myEditor.getCurrentLocation();
    }

    @Override
    public void dispose() {
        Disposer.dispose(myEditor);
    }

    public void setViewer(PodPreviewEditor viewer) {
        myViewer = viewer;
    }

    public void setCallback(Runnable triggerPodRenderCode) {
        myTriggerPodRenderCode = triggerPodRenderCode;
    }

    @Override
    public @NotNull Editor getEditor() {
        return myEditor.getEditor();
    }

    @Override
    public boolean canNavigateTo(@NotNull Navigatable navigatable) {
        return myEditor.canNavigateTo(navigatable);
    }

    @Override
    public void navigateTo(@NotNull Navigatable navigatable) {
        if (myState == RakuReaderModeState.DOCS)
            updateState(RakuReaderModeState.CODE);
        myEditor.navigateTo(navigatable);
        requestFocus();
    }

    private static class Perl6ModuleEditorState implements FileEditorState {
        private final FileEditorState myEditorState;
        private final FileEditorState myViewerState;

        private Perl6ModuleEditorState(@NotNull FileEditorState editorState, FileEditorState viewerState) {
            myEditorState = editorState;
            myViewerState = viewerState;
        }

        @Override
        public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
            return otherState instanceof Perl6ModuleEditorState;
        }

        public FileEditorState getEditorState() {
            return myEditorState;
        }

        public FileEditorState getViewerState() {
            return myViewerState;
        }
    }
}
