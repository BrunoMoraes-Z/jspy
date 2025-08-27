/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see licence.txt file for details.
 */

package spyAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class Communicator {

    public static OutputStreamWriter outStream;
    private static BufferedReader inStream;
    private static WindowTracker winTrack;

    public static void startCommunicator(int port) {

        String machine = "localhost";

        try {
            InetAddress addr = InetAddress.getByName(machine);
            SocketAddress sockaddr = new InetSocketAddress(addr, port);
            final Socket sock = new Socket();
            sock.connect(sockaddr);
            outStream = new OutputStreamWriter(sock.getOutputStream());
            inStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            new Thread(() -> listen()).start();
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listen() {
        String line;
        try {
            while ((line = inStream.readLine()) != null) {
                if ("REINDEX".equalsIgnoreCase(line)) {
                    if (winTrack != null && winTrack.activeWindow != null) {
                        Thread th = new Thread(new CompEnum(winTrack.activeWindow));
                        th.start();
                    }
                } else if ("START_REC".equalsIgnoreCase(line)) {
                    StepRecorder.start();
                } else if ("STOP_REC".equalsIgnoreCase(line)) {
                    StepRecorder.stop();
                } else if (line.startsWith("HIGHLIGHT,")) {
                    String loc = line.substring("HIGHLIGHT,".length());
                    ComponentLocatorStore.highlight(loc);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setWindowTracker(WindowTracker wt) {
        winTrack = wt;
    }


    public static void writeToServer(String s) {
        try {
            outStream.write(s + "\n");
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
