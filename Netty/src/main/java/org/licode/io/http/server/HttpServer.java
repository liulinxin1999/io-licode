package org.licode.io.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class HttpServer {

    public static final int port = 6789;
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static ServerBootstrap serverBootstrap = new ServerBootstrap();
    private static final boolean SSL = false; //是否开启SSL模式

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        final SslContext sslContext;
        if (SSL){
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();
        }else {
            sslContext = null;
        }

        try {
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ServerHandlerInit(sslContext));
            ChannelFuture f = serverBootstrap.bind(port).sync();
            System.out.println("服务端启动，端口是：" + port);
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully(); //关闭EventLoopGroup，释放所有资源，包括创建的资源
        }
    }
}
