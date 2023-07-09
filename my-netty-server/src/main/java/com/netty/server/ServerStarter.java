package com.netty.server;

import com.netty.server.service.ServerChannelInit;
import com.netty.util.ServerConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStarter {
    private static final Logger LOG = LoggerFactory.getLogger(ServerStarter.class);

    public void bind() throws Exception {
        // Config NIO thread group
        EventLoopGroup bossGroup = new NioEventLoopGroup(1,new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors(),
                new DefaultThreadFactory("nt_worker"));
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ServerChannelInit());

        b.bind(ServerConstant.SERVER_PORT).sync();
        LOG.info("Netty server start : "
                + (ServerConstant.SERVER_IP + " : "
                + ServerConstant.SERVER_PORT));
    }

    public static void main(String[] args) throws Exception {
        new ServerStarter().bind();
    }
}
