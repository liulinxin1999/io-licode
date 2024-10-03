package org.licode.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerAcceptor {


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8888));
        System.out.println("server start ...");
        try {
            while (true) {
                new Thread(new ServerTask(serverSocket.accept())).start();
            }
        }finally {
            serverSocket.close();
        }

    }

    public static class ServerTask implements Runnable{

        private Socket socket = null;
        public ServerTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run(){

            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                String read = inputStream.readUTF();
                System.out.println("accept client message: " + read);

                outputStream.writeUTF("hello," + read);
                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }

            }

    }
}
