package com.netty.client.service.handler;

import com.netty.message.MessageType;
import com.netty.message.NettyHeader;
import com.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HearBeatReqHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(HearBeatReqHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT){
            NettyMessage heartBeat = buildHeatBeat();
            LOG.debug("Writing idle，send heart beat： "+ heartBeat);
            ctx.writeAndFlush(heartBeat);
        }
        super.userEventTriggered(ctx, evt);
    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.HEARTBEAT_REQ.value());
        message.setNettyHeader(header);
        return message;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if(message.getNettyHeader()!=null
                &&message.getNettyHeader().getType()==MessageType.HEARTBEAT_RESP.value()){
            LOG.debug("Receive heart beat");
            ReferenceCountUtil.release(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            LOG.warn("No receive heart beat, close connection");
            ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }
}
