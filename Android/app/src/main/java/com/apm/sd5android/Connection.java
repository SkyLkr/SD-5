package com.apm.sd5android;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static void sendImageToServer(File file, Context ctx) {

        try {
            byte[] bytes = new byte[1024];

            InputStream in = new FileInputStream(file);
            OutputStream out = client.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
