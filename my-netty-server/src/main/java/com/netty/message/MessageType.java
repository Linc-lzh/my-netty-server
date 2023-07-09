package com.netty.message;

public enum MessageType {
    SERVICE_REQ((byte) 0),/*Buz request*/
    SERVICE_RESP((byte) 1), /*TWO_WAY message, need answer*/
    ONE_WAY((byte) 2), /*ONE_WAY message, no need answer*/
    LOGIN_REQ((byte) 3), /*Login Request message*/
    LOGIN_RESP((byte) 4), /*Login Response message*/
    HEARTBEAT_REQ((byte) 5), /*Heart Beat Request message*/
    HEARTBEAT_RESP((byte) 6);/*Heart Beat Response message*/

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }
}
