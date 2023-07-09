package com.netty.server.service;

import com.netty.kyrocodec.KryoDecoder;
import com.netty.kyrocodec.KryoEncoder;
import com.netty.server.asyncpro.DefaultTaskProcessor;
import com.netty.server.service.hander.HeartBeatRespHandler;
import com.netty.server.service.hander.LoginAuthRespHandler;
import com.netty.server.service.hander.MetricsHandler;
import com.netty.server.service.hander.ServerBusinessLogicHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ServerChannelInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // Metric collect handler
        socketChannel.pipeline().addLast(new MetricsHandler());
        /*Split and sticky package handler*/
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,
                0,2,0,
                2));
        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));

        /* Encode and Decode handler*/
        socketChannel.pipeline().addLast(new KryoDecoder());
        socketChannel.pipeline().addLast(new KryoEncoder());

        /*Heart beat time out handler*/
        socketChannel.pipeline().addLast(new ReadTimeoutHandler(15));

        socketChannel.pipeline().addLast(new LoginAuthRespHandler());
        socketChannel.pipeline().addLast(new HeartBeatRespHandler());
        socketChannel.pipeline().addLast(new ServerBusinessLogicHandler(new DefaultTaskProcessor()));
    }
}
