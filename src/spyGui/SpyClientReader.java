/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see licence.txt file for details.
 */

package spyGui;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.net.Socket;

public class SpyClientReader implements Runnable {
    public Socket client;
    private static BufferedWriter out;

    public SpyClientReader(Socket sock) {
        client = sock;
    }

    public static void sendCommand(String cmd) {
        try {
            if (out != null) {
                out.write(cmd + "\n");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
            out.write("connected\n");
            out.flush();
            SpyGuiPane.topTextPane.setText("Select window and use the Action menu to control indexing and recording");
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            String tempText;

            while ((tempText = in.readLine()) != null) {
                out.write("ack\n");
                out.flush();
                if (tempText.startsWith("REC,")) {
                    String[] parts = tempText.split(",", 3);
                    if (parts.length >= 3) {
                        SpyFrame.recordingPane.addRecord(parts[1], parts[2]);
                    }
                    continue;
                }
                if (tempText.equals("TREE_BEGIN")) {
                    SpyFrame.treePane.clear();
                    continue;
                }
                if (tempText.startsWith("TREE,")) {
                    String[] parts = tempText.split(",", 3);
                    if (parts.length >= 3) {
                        int level = Integer.parseInt(parts[1]);
                        SpyFrame.treePane.addNode(level, parts[2]);
                    }
                    continue;
                }
                if (tempText.equals("TREE_END")) {
                    continue;
                }
                SpyGuiPane.printText("Clear");
                System.out.println("From Client: " + tempText);
                String splitText[] = tempText.split(",");
                for (int i = 0; i < splitText.length; i++) {
                    if (!(splitText[i].equals(""))) {
                        if (i == 4) {
                            int index = splitText[4].indexOf("[");
                            String className = splitText[4].substring(0, index + 1).replace("[", "");
                            String name = splitText[4].substring(index + 1, splitText[4].length());
                            SpyGuiPane.printText("Name- " + name);
                            SpyGuiPane.printText("Class Name- " + className);
                        } else if (i == 5) {
                            SpyGuiPane.printText("X: " + splitText[5]);
                        } else if (i == 6) {
                            SpyGuiPane.printText("Y: " + splitText[6]);
                        } else if (i == 7) {
                            SpyGuiPane.printText("Size: " + splitText[7]);
                        } else {
                            String temporary = splitText[i].replace("[", "\n");
                            String temp[] = temporary.split("\n");
                            for (int j = 0; j < temp.length; j++) {
                                // skip empty properties
                                // empty properties end with "="
                                String tempLastCharacter = temp[j].substring(temp[j].length() - 1, temp[j].length());
                                if ((!tempLastCharacter.equals("="))) {
                                    if (tempLastCharacter.equals("]")) {
                                        String tempBeforeLastCharacter = temp[j].substring(temp[j].length() - 2, temp[j].length() - 1);
                                        if ((!tempBeforeLastCharacter.equals("="))) {
                                            SpyGuiPane.printText(temp[j].substring(0, temp[j].length() - 1));
                                            SpyGuiPane.printText("new line");
                                        }
                                    } else {
                                        SpyGuiPane.printText(temp[j]);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            while (true) {
                while ((tempText = in.readLine()) != null) {
                    out.write("test from server\n");
                    out.flush();
                    SpyGuiPane.printText("Clear");
                    System.out.println("From Client: " + tempText);
                    String splitText[] = tempText.split(",");
                    for (int i = 0; i < splitText.length; i++) {
                        if (!(splitText[i].equals(""))) {
                            if (i == 4) {
                                int index = splitText[4].indexOf("[");
                                String className = splitText[4].substring(0, index + 1).replace("[", "");
                                String name = splitText[4].substring(index + 1, splitText[4].length());
                                SpyGuiPane.printText("Name- " + name);
                                SpyGuiPane.printText("Class Name- " + className);
                            } else if (i == 5) {
                                SpyGuiPane.printText("X: " + splitText[5]);
                            } else if (i == 6) {
                                SpyGuiPane.printText("Y: " + splitText[6]);
                            } else if (i == 7) {
                                SpyGuiPane.printText("Size: " + splitText[7]);
                            } else {
                                String temporary = splitText[i].replace("[", "\n");
                                String temp[] = temporary.split("\n");
                                for (int j = 0; j < temp.length; j++) {
                                    // skip empty properties
                                    // empty properties end with "="
                                    String tempLastCharacter = temp[j].substring(temp[j].length() - 1, temp[j].length());
                                    if ((!tempLastCharacter.equals("="))) {
                                        if (tempLastCharacter.equals("]")) {
                                            //properties without name
                                            String tempBeforeLastCharacter = temp[j].substring(temp[j].length() - 2, temp[j].length() - 1);
                                            if ((!tempBeforeLastCharacter.equals("="))) {
                                                SpyGuiPane.printText(temp[j].substring(0, temp[j].length() - 1));
                                                SpyGuiPane.printText("new line");
                                            }
                                        }else {
                                            SpyGuiPane.printText(temp[j]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR3: " + e.getMessage());
            SpyGuiPane.printText("Client Disconnected.");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignore) {
            }
            out = null;
        }

    }

}


