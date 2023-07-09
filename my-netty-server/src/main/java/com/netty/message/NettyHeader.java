package com.netty.message;

import java.util.HashMap;
import java.util.Map;

public class NettyHeader {
    private String md5;

    private long msgID;

    private byte type;


    private byte priority;

    private Map<String, Object> attachment = new HashMap<String, Object>();

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public final long getMsgID() {
        return msgID;
    }

    public final void setMsgID(long msgID) {
        this.msgID = msgID;
    }

    public final byte getType() {
        return type;
    }

    public final void setType(byte type) {
        this.type = type;
    }

    public final byte getPriority() {
        return priority;
    }

    public final void setPriority(byte priority) {
        this.priority = priority;
    }

    public final Map<String, Object> getAttachment() {
        return attachment;
    }

    public final void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "MyHeader [md5=" + md5
                + ", msgID=" + msgID
                + ", type=" + type
                + ", priority=" + priority
                + ", attachment=" + attachment + "]";
    }
}
