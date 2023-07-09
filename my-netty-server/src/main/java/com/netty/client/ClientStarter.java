package com.netty.client;

import com.netty.client.service.ClientChannelInit;
import com.netty.message.MakeMsgID;
import com.netty.message.MessageType;
import com.netty.message.NettyHeader;
import com.netty.message.NettyMessage;
import com.netty.util.EncryptUtils;
import com.netty.util.ServerConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientStarter implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(ClientStarter.class);

    /*Reconnect thread pool*/
    private ScheduledExecutorService executor = Executors
            .newScheduledThreadPool(1);
    private Channel channel;
    private EventLoopGroup group = new NioEventLoopGroup();

    /*Is closed by client*/
    private volatile boolean userClose = false;

    private volatile boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    public void connect(int port,String host) throws InterruptedException {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInit());
            ChannelFuture future = b.connect(new InetSocketAddress(host,port)).sync();
            LOG.info("Server connected");
            channel = future.channel();
            synchronized (this){
                this.connected = true;
                this.notifyAll();
            }
            channel.closeFuture().sync();
        } finally {
            if(!userClose){
                LOG.warn("Need to reconnect...");
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            connect(ServerConstant.SERVER_PORT,
                                    ServerConstant.SERVER_IP);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }else{
                /* Normal close*/
                channel = null;
                group.shutdownGracefully().sync();
                synchronized (this){
                    this.connected = false;
                    this.notifyAll();
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            connect(ServerConstant.SERVER_PORT,ServerConstant.SERVER_IP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ClientStarter nettyClient = new ClientStarter();
        nettyClient.connect(ServerConstant.SERVER_PORT
                , ServerConstant.SERVER_IP);
        nettyClient.send("v");
        nettyClient.close();
    }

    public void send(Object message) {
        if(channel==null||!channel.isActive()){
            throw new IllegalStateException("Unable to connect to server, please try again！");
        }
        NettyMessage msg = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setMsgID(MakeMsgID.getID());
        header.setType(MessageType.SERVICE_REQ.value());
        header.setMd5(EncryptUtils.encryptObj(message));
        msg.setNettyHeader(header);
        msg.setBody(message);
        channel.writeAndFlush(msg);
    }

    public void sendOneWay(Object message) {
        if(channel==null||!channel.isActive()){
            throw new IllegalStateException("Unable to connect to server, please try again！");
        }
        NettyMessage msg = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setMsgID(MakeMsgID.getID());
        header.setType(MessageType.ONE_WAY.value());
        header.setMd5(EncryptUtils.encryptObj(message));
        msg.setNettyHeader(header);
        msg.setBody(message);
        channel.writeAndFlush(msg);
    }

    public void close() {
        userClose = true;
        channel.close();
    }
}
