package org.licode.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);

        try {
            socket = new Socket();
            socket.connect(inetSocketAddress);
            System.out.println("connect Server Success !");
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeUTF("licode");
            outputStream.flush();
            String read = inputStream.readUTF();
            System.out.println("client get response:" + read);
        } finally {
            assert socket != null;
            socket.close();
            assert outputStream != null;
            outputStream.close();
            assert inputStream != null;
            inputStream.close();
        }
    }
}
