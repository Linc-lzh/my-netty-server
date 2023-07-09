package com.netty.server.service.hander;

import com.netty.message.MessageType;
import com.netty.message.NettyHeader;
import com.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.netty.server.service.SecurityCenter;

import java.net.InetSocketAddress;

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthRespHandler.class);

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        NettyMessage message = (NettyMessage) msg;

        if(message.getNettyHeader()!=null
                &&message.getNettyHeader().getType()== MessageType.LOGIN_REQ.value()){
            LOG.info("Receive auth request from client : " + message);
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            boolean chkAuthPass = false;
            if (SecurityCenter.isDupLog(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
                LOG.warn("Refused duplicated login, response : " + loginResp);
                ctx.writeAndFlush(loginResp);
                ctx.close();
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel()
                        .remoteAddress();
                String ip = address.getAddress().getHostAddress();
                if(SecurityCenter.isWhiteIP(ip)){
                    SecurityCenter.addLoginUser(nodeIndex);
                    loginResp = buildResponse((byte) 0);
                    LOG.info("Auth successfully，response : " + loginResp);
                    ctx.writeAndFlush(loginResp);
                }else{
                    loginResp = buildResponse((byte) -1);
                    LOG.warn("Auth failed，response : " + loginResp);
                    ctx.writeAndFlush(loginResp);
                    ctx.close();
                }
            }
            ReferenceCountUtil.release(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        NettyHeader msgHeader = new NettyHeader();
        msgHeader.setType(MessageType.LOGIN_RESP.value());
        message.setNettyHeader(msgHeader);
        message.setBody(result);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Remove cache
        SecurityCenter.removeLoginUser(ctx.channel().remoteAddress().toString());
        ctx.close();
    }
}
