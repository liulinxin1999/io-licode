package org.licode.io;

public class NioServer {

    private static NioServerHandle nioServerHandle;

    public static void main(String[] args) {
        nioServerHandle = new NioServerHandle(8888);
        new Thread(nioServerHandle).start();

    }
}
