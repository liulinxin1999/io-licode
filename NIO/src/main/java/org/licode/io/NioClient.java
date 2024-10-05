package org.licode.io;

import java.util.Scanner;

import static org.licode.io.Const.DEFAULT_PORT;
import static org.licode.io.Const.DEFAULT_SERVER_IP;

public class NioClient {

    private static NioClientHandle nioClientHandle;

    public static void start() {
        nioClientHandle = new NioClientHandle(DEFAULT_SERVER_IP, DEFAULT_PORT);
        new Thread(nioClientHandle, "client").start();
    }

    public static boolean sendMsg(String msg) throws Exception{
        nioClientHandle.sendMsg(msg);
        return true;
    }
    public static void main(String[] args) throws Exception {
        start();
        Scanner scanner = new Scanner(System.in);
        while (NioClient.sendMsg(scanner.next()));
    }
}
