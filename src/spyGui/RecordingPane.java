package spyGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecordingPane extends JPanel {
    private static final long serialVersionUID = 1L;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private final JButton saveButton = new JButton("Save");

    public RecordingPane() {
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveButton);
        add(bottom, BorderLayout.SOUTH);
        saveButton.addActionListener(this::saveToFile);
    }

    public void addRecord(String event, String locator) {
        model.addElement(event + " - " + locator);
    }

    public void clear() {
        model.clear();
    }

    private void saveToFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path p = chooser.getSelectedFile().toPath();
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < model.size(); i++) {
                lines.add(model.get(i));
            }
            try {
                Files.write(p, lines, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}