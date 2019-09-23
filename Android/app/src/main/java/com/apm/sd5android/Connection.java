package com.apm.sd5android;

import android.content.Context;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Connection {

    private static boolean initialized = false;
    private static Socket client;
    private static PrintStream out;
    private static Scanner in;

    public static boolean isConnected() {
        return initialized;
    }

    public static void connect(String ip, int port) throws IOException {
        if (!initialized) {
            client = new Socket(ip, port);

            out = new PrintStream(client.getOutputStream());
            in = new Scanner(client.getInputStream());

            initialized = true;
        }
    }

    public static String run(String command, Context ctx) {
        out.println(command);
        return getResponse(ctx);
    }

    public static String getResponse(Context ctx) {
        while(true) {
            if (in.hasNextLine()) {
                String msg = in.nextLine();

                if (msg.equalsIgnoreCase("ERRO")) {
                    return "";
                }

                return msg;
            }
        }
    }
}
