package com.netty.kyrocodec;

import com.netty.message.*;
import com.netty.util.EncryptUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

public class TestKryoCodeC {

    public NettyMessage getMessage(int j) {
		String content = "abcdefg--------AAAAAA:" + j;
		NettyMessage message = new NettyMessage();
		NettyHeader header = new NettyHeader();
		header.setMsgID(MakeMsgID.getID());
		header.setType((byte) 1);
		header.setPriority((byte) 7);
		header.setMd5(EncryptUtils.encryptObj(content));
		Map<String, Object> attachment = new HashMap<String, Object>();
		for (int i = 0; i < 10; i++) {
			attachment.put("city --> " + i, "mark " + i);
		}
		header.setAttachment(attachment);
		message.setNettyHeader(header);
		message.setBody(content);
		return message;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
		TestKryoCodeC testC = new TestKryoCodeC();

		for (int i = 0; i < 5; i++) {
			ByteBuf sendBuf = Unpooled.buffer();
            NettyMessage message = testC.getMessage(i);
            System.out.println("Encode:"+message);
            KryoSerializer.serialize(message, sendBuf);
			NettyMessage decodeMsg = (NettyMessage) KryoSerializer.deserialize(sendBuf);
			System.out.println("Decode:"+decodeMsg);
			System.out
				.println("-------------------------------------------------");
		}

    }

}
