package com.netty.server.service.hander;

import com.netty.message.NettyHeader;
import com.netty.message.NettyMessage;
import com.netty.server.asyncpro.AsyncBusinessProcess;
import com.netty.server.asyncpro.ITaskProcessor;
import com.netty.util.EncryptUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.netty.message.MessageType;
public class ServerBusinessLogicHandler extends SimpleChannelInboundHandler<NettyMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(ServerBusinessLogicHandler.class);
    private ITaskProcessor taskProcessor;

    public ServerBusinessLogicHandler(ITaskProcessor taskProcessor) {
        super();
        this.taskProcessor = taskProcessor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg)
            throws Exception {
        /*Check MD5*/
        String headMd5 = msg.getNettyHeader().getMd5();
        String calcMd5 = EncryptUtils.encryptObj(msg.getBody());
        if(!headMd5.equals(calcMd5)){
            LOG.error("Check md5 failed："+headMd5+" vs "+calcMd5+"，close connection");
            ctx.writeAndFlush(buildBusiResp("Check md5 failed，close connection"));
            ctx.close();
        }
        LOG.info(msg.toString());
        if(msg.getNettyHeader().getType() == MessageType.ONE_WAY.value()){
            LOG.debug("ONE_WAY message，async handle");
            AsyncBusinessProcess.submitTask(taskProcessor.execAsyncTask(msg));
        }else{
            LOG.debug("TWO_WAY message，need response");
            ctx.writeAndFlush(buildBusiResp("OK"));
        }
    }

    private NettyMessage buildBusiResp(String result) {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.SERVICE_RESP.value());
        message.setNettyHeader(header);
        message.setBody(result);
        return message;
    }
}
