/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see licence.txt file for details.
 */

package spyAgent;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class WinEventDispatchListener implements AWTEventListener {
    WindowTracker winTrack = new WindowTracker();
    private static ArrayList<KeyboardFocusManager> activeFocusManagers = new ArrayList<>();

    public WinEventDispatchListener() {
        registerExistingWindows();
    }

    private void registerExistingWindows() {
        Window[] windows = Window.getWindows();
        for (Window win : windows) {
            if (win.isDisplayable()) {
                setupWindow(win);
            }
        }
    }

    private void setupWindow(Window win) {
        Thread enuTh = new Thread(new CompEnum(win));
        enuTh.setPriority(Thread.MAX_PRIORITY);
        enuTh.start();
        winTrack.activeWindow = win;
        win.addWindowFocusListener(winTrack);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (!activeFocusManagers.contains(manager)) {
            manager.addKeyEventDispatcher(new KeyboardListener(winTrack));
            activeFocusManagers.add(manager);
        }
    }

    public void eventDispatched(AWTEvent evt) {

        if (evt.getID() == WindowEvent.WINDOW_OPENED) {
            Component compon = (Component) evt.getSource();
            Window win = (Window) compon;
            setupWindow(win);
        }
    }

    public WindowTracker getWindowTracker() {
        return winTrack;
    }

}
