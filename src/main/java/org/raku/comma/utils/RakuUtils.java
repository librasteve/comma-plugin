package org.raku.comma.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raku.comma.pm.RakuDependencySpec;
import org.raku.comma.services.project.RakuProjectSdkService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RakuUtils {
    public static final Logger LOG = Logger.getInstance(RakuUtils.class);

    public static void writeCodeToPath(Path codePath, List<String> lines) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (!codePath.getParent().toFile().exists()) {
                    Files.createDirectories(codePath.getParent());
                }
                Files.write(codePath, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOG.error(e);
            }
        });
    }

    @Nullable
    public static File getResourceAsFile(String resourcePath) {
        var fileName = Arrays.stream(resourcePath.split(Pattern.quote(File.separator))).toList().getLast();
        var splitted = fileName.split("\\.");

        var prefix = splitted[0];
        var suffix = splitted.length > 1 ? ("." + splitted[1]) : ".tmp";

        File tempFile;
        try {
            tempFile = FileUtil.createTempFile(prefix, suffix);
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }

        try (
                InputStream in = RakuUtils.class.getClassLoader().getResourceAsStream(resourcePath);
                FileOutputStream out = new FileOutputStream(tempFile)
        )
        {
            if (in != null) {
                in.transferTo(out);
            }
        } catch (IOException e) {
            LOG.error(e);
        }

        return tempFile;
    }

    public static List<String> getResourceAsLines(String filepath) {
        List<String> lines = new ArrayList<>();
        try (
                InputStream resourceFileStream = RakuUtils.class.getClassLoader().getResourceAsStream(filepath);
                BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceFileStream), StandardCharsets.UTF_8))
        )
        {
            while (inputStreamReader.ready()) {
                lines.add(inputStreamReader.readLine());
            }
        } catch (IOException | NullPointerException e) {
            LOG.error(e);
        }
        return lines;
    }

    public static String getResourceAsString(String filepath) {
        return String.join("\n", getResourceAsLines(filepath));
    }

    public static String formatDelimiters(int originalText, String delimiter, int each) {
        String text = new StringBuilder(String.valueOf(originalText)).reverse().toString();
        StringBuilder builder = new StringBuilder(text.length() + text.length() / each + 1);
        int index = 0;
        while (index < text.length()) {
            builder.append(text, index, Math.min(index + 3, text.length()));
            index += each;

            if (index < text.length()) {
                builder.append(delimiter);
            }
        }
        return builder.reverse().toString();
    }

    public static String getNameExtension(@Nullable String filename) {
        return filename != null && filename.contains(".")
               ? filename.substring(filename.lastIndexOf(".") + 1)
               : "";
    }

    public static String escapeHTML(String str) {
        return str.chars().mapToObj(
                c -> (c > 127 || "\"'<>&".indexOf(c) != -1)
                     ? "&" + htmlReplace(c) + ";"
                     : String.valueOf((char) c)).collect(Collectors.joining());
    }

    public static String stripAuthVerApi(String name) {
        Pattern patten = Pattern.compile("(.+)((:ver|:auth|:api)(<(.*:).+>|\\(.+\\)))+");
        Matcher matcher = patten.matcher(name);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return name;
    }

    private static String htmlReplace(int c) {
        if (c == 62) return "gt";
        if (c == 60) return "lt";
        if (c == 38) return "amp";
        return String.valueOf(c);
    }

    @NotNull
    public static Set<String> installedModules(@NotNull Project project) throws ExecutionException {
        var findAllInstalledModules = new RakuCommandLine(project);
        findAllInstalledModules.addParameters("-MZef::CLI", "-e''", "list", "--installed");

        var output = findAllInstalledModules.executeAndRead(null);
        if (output.size() >= 3) {
            output.removeFirst();
            output.removeLast();
            output.removeLast();
        }

        return output.stream().map(module -> new RakuDependencySpec(module).getName()).collect(Collectors.toSet());
    }
}
