package com.netty.client.service.handler;

import io.netty.handler.timeout.IdleStateHandler;

public class CheckWriteIdleHandler extends IdleStateHandler {

    public CheckWriteIdleHandler() {
        super(0, 8, 0);
    }
}
