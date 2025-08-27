
/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see licence.txt file for details.
 */

package spyAgent;

import common.Utilities;

import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyboardListener implements KeyEventDispatcher {
    public static String highlightedComponentName = "";

    private boolean altPressed = false, ctrlPressed = false;

    public KeyboardListener(WindowTracker winTrack) {
    }

    public boolean dispatchKeyEvent(KeyEvent arg0) {
        Component srcComp = arg0.getComponent();
        if (arg0.getID() == KeyEvent.KEY_PRESSED) {
            int code = arg0.getKeyCode();
            if (code == KeyEvent.VK_ALT) {
                altPressed = true;
            } else if (code == KeyEvent.VK_CONTROL) {
                ctrlPressed = true;
            } else if (ctrlPressed && altPressed && code == KeyEvent.VK_C) {
                Utilities.copyStringToClipboard(highlightedComponentName);
            } else if (ctrlPressed && altPressed && code == KeyEvent.VK_S) {
                CompMouseListner.setActive = !CompMouseListner.setActive;
            } else if (ctrlPressed && altPressed && code == KeyEvent.VK_P) {
                if (StepRecorder.isRecording()) {
                    StepRecorder.stop();
                } else {
                    StepRecorder.start();
                }
            } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_TAB || code == KeyEvent.VK_ESCAPE) {
                StepRecorder.recordAction("press key", StepRecorder.getLocator(srcComp) + " - " + KeyEvent.getKeyText(code));
            }

        } else if (arg0.getID() == KeyEvent.KEY_RELEASED) {
            if (arg0.getKeyCode() == KeyEvent.VK_ALT) {
                altPressed = false;
            } else if (arg0.getKeyCode() == KeyEvent.VK_CONTROL) {
                ctrlPressed = false;
            }
        } else if (arg0.getID() == KeyEvent.KEY_TYPED) {
            char ch = arg0.getKeyChar();
            if (!Character.isISOControl(ch)) {
                StepRecorder.recordAction("press key", StepRecorder.getLocator(srcComp) + " - " + ch);
            }
        }
        return false;
    }

}
