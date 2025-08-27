/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see licence.txt file for details.
 */

package spyGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class SpyFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private CmdInputDlg launchDlg = new CmdInputDlg();
    private SpyGuiPane displayPane = new SpyGuiPane();
    public static RecordingPane recordingPane = new RecordingPane();
    private JMenuBar menuBar = new JMenuBar();

    public SpyFrame() {
        super("JSpy");
        setIconImage(new ImageIcon(getClass().getResource("spy.png")).getImage());
        setupMenuBar();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar();
        JButton reindexBtn = new JButton("Reindex");
        JButton startBtn = new JButton("Record");
        JButton stopBtn = new JButton("Stop");
        stopBtn.setEnabled(false);
        toolBar.add(reindexBtn);
        toolBar.add(startBtn);
        toolBar.add(stopBtn);

        reindexBtn.addActionListener(e -> SpyClientReader.sendCommand("REINDEX"));
        startBtn.addActionListener(e -> {
            SpyClientReader.sendCommand("START_REC");
            recordingPane.clear();
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        });
        stopBtn.addActionListener(e -> {
            SpyClientReader.sendCommand("STOP_REC");
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Inspector", displayPane);
        tabs.addTab("Recording", recordingPane);

        JPanel root = new JPanel(new BorderLayout());
        root.add(toolBar, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);

        JFrame.setDefaultLookAndFeelDecorated(true);

        setLocationRelativeTo(null);
        pack();
        setAlwaysOnTop(true);
        setVisible(true);
    }

    private void setupMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem launch = new JMenuItem("Launch");
        launch.setMnemonic(KeyEvent.VK_L);
        JMenuItem quit = new JMenuItem("Quit");
        quit.setMnemonic(KeyEvent.VK_Q);
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About");
        about.setMnemonic(KeyEvent.VK_A);
        JMenuItem shortcuts = new JMenuItem("Shortcuts");
        shortcuts.setMnemonic(KeyEvent.VK_S);

        fileMenu.add(launch);
        fileMenu.add(quit);
        helpMenu.add(about);
        helpMenu.add(shortcuts);

        ActionListener menuAct = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Action " + arg0);
                if (arg0.getActionCommand().equals("Quit")) {
                    System.exit(0);
                } else if (arg0.getActionCommand().equals("About")) {
                    new AboutDialog(SpyFrame.this);
                } else if (arg0.getActionCommand().equals("Launch")) {
                    launchDlg.pack();
                    launchDlg.setLocationRelativeTo(SpyFrame.this);
                    launchDlg.setAlwaysOnTop(true);
                    launchDlg.setVisible(true);
                } else if (arg0.getActionCommand().equals("Shortcuts")) {
                    new ShortcutsDialog(SpyFrame.this);
                }

            }
        };

        quit.addActionListener(menuAct);
        about.addActionListener(menuAct);
        launch.addActionListener(menuAct);
        shortcuts.addActionListener(menuAct);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
    }

}
