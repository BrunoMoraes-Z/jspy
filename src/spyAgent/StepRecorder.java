/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see licence.txt file for details.
 */

package spyAgent;

import java.awt.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Records user actions on UI components.
 */
public class StepRecorder {

    private static final HierarchyMap hierarchyMap = new HierarchyMap();
    private static final List<ActionRecord> actions = new ArrayList<>();
    private static boolean recording = false;

    private static class ActionRecord {
        private final String event;
        private final String locator;

        ActionRecord(String event, String locator) {
            this.event = event;
            this.locator = locator;
        }

        @Override
        public String toString() {
            return event + "," + locator;
        }
    }

    public static synchronized void start() {
        actions.clear();
        recording = true;
    }

    public static synchronized void stop() {
        recording = false;
    }

    public static synchronized void recordAction(String event, String componentName) {
        if (!recording || event == null || componentName == null) {
            return;
        }
        actions.add(new ActionRecord(event, componentName));
        Communicator.writeToServer("REC," + event + "," + componentName);
    }

    public static void saveToFile(Path out) throws IOException {
        List<String> lines = new ArrayList<>();
        for (ActionRecord ar : actions) {
            lines.add(ar.toString());
        }
        Files.write(out, lines, StandardCharsets.UTF_8);
    }

    public static boolean isRecording() {
        return recording;
    }

    public static String getLocator(Component c) {
        if (c == null) {
            return "";
        }
        String name = c.getName();
        if (name != null && !name.isEmpty()) {
            return name;
        }
        String classType = hierarchyMap.getInstance(c);
        if (classType == null) {
            return "";
        }
        String index = hierarchyMap.index;
        return classType + "[" + index + "]";
    }
}