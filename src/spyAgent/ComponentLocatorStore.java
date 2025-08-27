package spyAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentLocatorStore {
    private static final Map<String, Component> map = new ConcurrentHashMap<>();
    private static Component highlighted;

    public static void clear() {
        map.clear();
        highlighted = null;
    }

    public static void put(String locator, Component comp) {
        map.put(locator, comp);
    }

    public static void highlight(String locator) {
        Component comp = map.get(locator);
        if (comp == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (highlighted != null && highlighted != comp) {
                MouseEvent exitEvt = new MouseEvent(highlighted, MouseEvent.MOUSE_EXITED,
                        System.currentTimeMillis(), 0, 0, 0, 0, false);
                for (MouseListener ml : highlighted.getMouseListeners()) {
                    ml.mouseExited(exitEvt);
                }
            }
            highlighted = comp;
            MouseEvent enterEvt = new MouseEvent(comp, MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), 0, 0, 0, 0, false);
            for (MouseListener ml : comp.getMouseListeners()) {
                ml.mouseEntered(enterEvt);
            }
        });
    }
}

