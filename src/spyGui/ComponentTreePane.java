package spyGui;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

public class ComponentTreePane extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Components");
    private final DefaultTreeModel model = new DefaultTreeModel(root);
    private final JTree tree = new JTree(model);
    private final java.util.List<DefaultMutableTreeNode> stack = new ArrayList<>();

    public ComponentTreePane() {
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                Object obj = node.getUserObject();
                if (obj instanceof NodeData) {
                    String loc = ((NodeData) obj).locator;
                    SpyClientReader.sendCommand("HIGHLIGHT," + loc);
                }
            }
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> {
            root.removeAllChildren();
            stack.clear();
            model.reload();
        });
    }

    public void addNode(int level, String locator) {
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new NodeData(locator));
            if (level == 0) {
                root.add(node);
                stack.clear();
                stack.add(node);
            } else {
                while (stack.size() > level) {
                    stack.remove(stack.size() - 1);
                }
                DefaultMutableTreeNode parent = stack.get(level - 1);
                parent.add(node);
                stack.add(node);
            }
            model.reload();
        });
    }

    private static class NodeData {
        final String locator;
        NodeData(String locator) {
            this.locator = locator;
        }
        @Override
        public String toString() {
            return locator;
        }
    }
}

