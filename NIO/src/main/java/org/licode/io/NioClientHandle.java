package org.licode.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandle implements Runnable{

    private String host;
    private int port;
    private volatile boolean started;
    private Selector selector;
    private SocketChannel socketChannel;

    public NioClientHandle(String ip, int port) {
        this.host = ip;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            started = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (started){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> key = selectionKeys.iterator();
                while (key.hasNext()){
                    SelectionKey it = key.next();
                    key.remove();
                    handleInput(it);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            SocketChannel sc = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if(sc.finishConnect()){
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }else System.exit(1);
            }
            if(key.isReadable()){
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(buffer);
                if(readBytes>0){
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String result = new String(bytes, "UTF-8");
                    System.out.println("客户端接收到消息：" + result);
                } else if(readBytes<0){
                    key.channel();
                    sc.close();
                }
            }
        }
    }

    public void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host, port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel channel,String request)
            throws IOException {
        //将消息编码为字节数组
        byte[] bytes = request.getBytes();
        //根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        //将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        //flip操作
        writeBuffer.flip();
        //发送缓冲区的字节数组
        /*关心事件和读写网络并不冲突*/
        channel.write(writeBuffer);
    }

    public void sendMsg(String msg) throws IOException {
        doWrite(socketChannel, msg);
    }
}
