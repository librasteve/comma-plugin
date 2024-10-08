package org.raku.profiler.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RakuProfileCall {
    public static final Pattern CALL_MODULE_PATTERN = Pattern.compile("\\((.+)\\)$");

    private final int id;
    private final int routineID;
    @Nullable
    private RakuProfileCall parent;
    private final List<RakuProfileCall> callees = new LinkedList<>();
    // Data to navigate
    private final String name;
    private final String filename;
    private final int line;
    private final int inclusiveTime;
    private final int exclusiveTime;
    private final int entries;
    private final int inlinedEntries;
    private final int speshEntries;

    public RakuProfileCall(int id, int routineID,
                           int inclusiveTime, int exclusiveTime,
                           int entries, int speshEntries, int inlinedEntries,
                           String name, String filename, int line) {
        this.id = id;
        this.routineID = routineID;
        this.inclusiveTime = inclusiveTime;
        this.exclusiveTime = exclusiveTime;
        this.entries = entries;
        this.speshEntries = speshEntries;
        this.inlinedEntries = inlinedEntries;
        this.name = name;
        this.filename = filename;
        this.line = line;
    }

    public void setParent(@Nullable RakuProfileCall parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public int getRoutineID() {
        return routineID;
    }

    public String getName() {
        return name.isEmpty() ? "<anon>" : name;
    }

    @Nullable
    public String getModuleName() {
        Matcher matcher = CALL_MODULE_PATTERN.matcher(filename);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public String getOriginalFile() {
        return filename;
    }

    public int getLine() {
        return line;
    }

    public String getFilename(String basePath) {
        return renderFilename(basePath) + ":" + line;
    }

    public boolean isExternal(String basePath) {
        return !filename.startsWith(basePath);
    }

    private String renderFilename(String basePath) {
        if (filename.endsWith(".nqp")) {
            return "<nqp>";
        } else if (filename.startsWith("SETTING:")) {
            return "<CORE SETTING>";
        } else {
            String shortFileName = filename;
            Matcher match = CALL_MODULE_PATTERN.matcher(shortFileName);

            if (match.find())
                shortFileName = shortFileName.substring(match.start() + 1, shortFileName.length() - 1);

            if (shortFileName.startsWith(basePath)) {
                return shortFileName.substring(basePath.length());
            } else {
                return shortFileName;
            }
        }
    }

    public int getInclusiveTime() {
        return inclusiveTime;
    }

    public int getExclusiveTime() {
        return exclusiveTime;
    }

    public int getEntriesCount() {
        return entries;
    }

    @Nullable
    public RakuProfileCall getParent() {
        return parent;
    }

    public void addCallee(RakuProfileCall callee) {
        callees.add(callee);
    }

    public List<RakuProfileCall> getCallees() {
        return callees;
    }

    public List<String> calleesPercentageInfo() {
        List<String> lines = new ArrayList<>();
        for (RakuProfileCall callee : callees) {
            float percent = 100 * (float)callee.inclusiveTime / (float)inclusiveTime;
            String name = callee.name.isEmpty() ? "<anon>" : callee.name;
            lines.add(String.format("%s: %.3f%%", name, percent));
        }
        return lines;
    }

    public float averageCallTime() {
        return (float)inclusiveTime / (float)entries;
    }

    public float inlined() {
        return 100.0f * ((float)inlinedEntries / (float)entries);
    }

    public float spesh() {
        return (float)speshEntries / (float)entries;
    }
}
