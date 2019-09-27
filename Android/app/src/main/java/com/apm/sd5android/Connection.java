package com.apm.sd5android;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.MainThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Connection {

    private static final int PACK_SIZE = 8*1024;

    private static boolean initialized = false;

    private static String ip;
    private static int port;

    public static boolean isConnected() {
        return initialized;
    }

    public static void connect(String ip, int port) throws IOException {
        if (!initialized) {
            Connection.ip = ip;
            Connection.port = port;

            initialized = true;
        }
    }

    public static File sendImageToServer(File file, File saveDirectory) {

        try {
            Socket client = new Socket(ip, port);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            BufferedInputStream in = new BufferedInputStream(client.getInputStream());

            InputStream fis = new FileInputStream(file);

            byte[] bytes = new byte[PACK_SIZE];

//            int numLoops = 1 + (int) file.length() / PACK_SIZE;
//
//            out.write(numLoops);
//            for (int i = 0; i < numLoops; i++) {
//                int read = fis.read(bytes);
//                out.write(bytes, 0, read);
//            }



            int read;
            while ((read = fis.read(bytes)) > 0) {
                out.write(bytes, 0, read);
            }

            out.flush();
            client.shutdownOutput();

//            fis.read(bytes);
//            out.write(bytes, 0, (int)file.length());

            File result = File.createTempFile("RESULT", ".jpg", saveDirectory);

            OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(result));

//            numLoops = in.read();
//
//            for (int i = 0; i < numLoops; i++) {
//                int read = in.read(bytes);
//                fileOut.write(bytes, 0, read);
//            }

//            fileOut.flush();

            while ((read = in.read(bytes)) > 0) {
                fileOut.write(bytes, 0, read);
            }

//            in.read(bytes);
//            fileOut.write(bytes, 0, fileSize);

            fileOut.close();
            in.close();
            fis.close();
            out.close();

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
