package org.licode.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPool {

    //Runtime.getRuntime().availableProcessors() 获取当前Java虚拟机可用的处理器数量,返回的整数表示可供Java应用程序并发执行的处理器核心数
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8888));
        System.out.println("start server ...");
        try {
            while (true) {
                executorService.execute(new SocketExecute(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }

    public static class SocketExecute implements Runnable {

        private Socket socket;

        public SocketExecute(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                String read = inputStream.readUTF();
                outputStream.writeUTF("hello:" + read);
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
