package com.netty.client.service;

import com.netty.client.service.handler.CheckWriteIdleHandler;
import com.netty.client.service.handler.ClientBusinessLogicHandler;
import com.netty.client.service.handler.HearBeatReqHandler;
import com.netty.client.service.handler.LoginAuthReqHandler;
import com.netty.kyrocodec.KryoDecoder;
import com.netty.kyrocodec.KryoEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ClientChannelInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(new CheckWriteIdleHandler());
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,
                0,2,0,
                2));
        ch.pipeline().addLast(new LengthFieldPrepender(2));


        ch.pipeline().addLast(new KryoDecoder());
        ch.pipeline().addLast(new KryoEncoder());

        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(new LoginAuthReqHandler());

        ch.pipeline().addLast(new ReadTimeoutHandler(15));

        ch.pipeline().addLast(new HearBeatReqHandler());

        ch.pipeline().addLast(new ClientBusinessLogicHandler());
    }
}
