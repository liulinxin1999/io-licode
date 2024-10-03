package org.licode.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSingle {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        //绑定端口
        serverSocket.bind(new InetSocketAddress(8888));
        System.out.println("Start Server ....");
        int connectCount = 0;
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("accept client socket ... total =" + (++connectCount));

                //实例化与客户端通信的输入输出
                //实现了closeable的类，可以写在try()语句中，会自动关闭资源，不用在finally中声明关闭
                try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());) {

                    //接收客户端的输出，也就是服务端的输入
                    String read = inputStream.readUTF();
                    System.out.println("Accept client message: " + read);

                    //服务端的输出，也就是客户端的输入
                    outputStream.writeUTF("helle world ! " + read);
                    outputStream.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                }
            }
        } finally {
            serverSocket.close();
        }

    }
}
