package com.netty.kyrocodec;

import com.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class KryoEncoder  extends MessageToByteEncoder<NettyMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage message,
                          ByteBuf out) throws Exception {
        KryoSerializer.serialize(message, out);
        ctx.flush();
    }
}
