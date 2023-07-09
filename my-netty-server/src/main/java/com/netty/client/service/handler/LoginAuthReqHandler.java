package com.netty.client.service.handler;

import com.netty.message.MessageType;
import com.netty.message.NettyHeader;
import com.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mark老师
 * 类说明：发起登录请求
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyMessage loginMsg = buildLoginReq();
        LOG.info("Request auth from server : " + loginMsg);
        ctx.writeAndFlush(loginMsg);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if(message.getNettyHeader()!=null
                &&message.getNettyHeader().getType()==MessageType.LOGIN_RESP.value()){
            LOG.info("Receive response，is it ok for connection？");
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                LOG.warn("Auth failed，close connection: " + message);
                ctx.close();
            } else {
                LOG.info("Auth successfully，start to consume message : " + message);
                ctx.pipeline().remove(this);
                ReferenceCountUtil.release(msg);
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setNettyHeader(header);
        return message;
    }

}
