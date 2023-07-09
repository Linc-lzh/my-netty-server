package com.netty.server.service.hander;

import com.netty.message.NettyHeader;
import com.netty.message.NettyMessage;
import com.netty.server.service.SecurityCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.netty.message.MessageType;
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getNettyHeader() != null
                && message.getNettyHeader().getType() == MessageType.HEARTBEAT_REQ.value()){

            NettyMessage heartBeatResp = buildHeatBeat();
            LOG.debug("Heart Beat response： "+ heartBeatResp);
            ctx.writeAndFlush(heartBeatResp);
            ReferenceCountUtil.release(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setNettyHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            LOG.warn("No connection from client, close the connection");
            SecurityCenter.removeLoginUser(ctx.channel().remoteAddress().toString());
            ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.warn("Client has closed connection");
        super.channelInactive(ctx);
    }
}
