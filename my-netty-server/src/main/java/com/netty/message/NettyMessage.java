package com.netty.message;

public class NettyMessage {
    private NettyHeader nettyHeader;

    private Object body;

    public final NettyHeader getNettyHeader() {
        return nettyHeader;
    }

    public final void setNettyHeader(NettyHeader nettyHeader) {
        this.nettyHeader = nettyHeader;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage [header=" + nettyHeader + "][body="+body+"]";
    }
}
