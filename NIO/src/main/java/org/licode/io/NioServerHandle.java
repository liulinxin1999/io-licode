package org.licode.io;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServerHandle implements Runnable{

    private volatile boolean started;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public NioServerHandle(int port){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            started = true;
            System.out.println("服务端已启动，端口号为：" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (started){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    handleInput(key);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                System.out.println("======建立连接======");
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(buffer);
                if(readBytes>0){
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String message = new String(bytes, "UTF-8");
                    System.out.println("服务端接收到消息：" + message);
                    String response = Const.response(message);
                    doWrite(sc, response);
                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String response) throws IOException {
        byte[] bytes = response.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        sc.write(buffer);
    }

}
