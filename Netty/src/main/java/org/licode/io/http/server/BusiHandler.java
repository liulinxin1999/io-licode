package org.licode.io.http.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.ResponseCache;

public class BusiHandler extends ChannelInboundHandlerAdapter {



    private void send(ChannelHandlerContext channelHandlerContext, String context, HttpResponseStatus status){
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/palin;charset=UTF-8");
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        String result = "";
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        System.out.println(httpRequest.headers());
        String path = httpRequest.uri();
        String body = httpRequest.content().toString(CharsetUtil.UTF_8);
        HttpMethod method = httpRequest.method();
        System.out.println("receive: " + method);
        if(!"/test".equalsIgnoreCase(path)){
            result = "非法请求！" + path;
            send(ctx, result, HttpResponseStatus.BAD_REQUEST);
        }

        if(HttpMethod.GET.equals(method)){
            System.out.println("body:" + body);
            result = "GET 请求，应答：" + RespConstant.getNews();
            send(ctx, result, HttpResponseStatus.OK);
            return;
        }

        if(HttpMethod.POST.equals(method)){

        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接客户端地址：" + ctx.channel().remoteAddress());
    }
}
