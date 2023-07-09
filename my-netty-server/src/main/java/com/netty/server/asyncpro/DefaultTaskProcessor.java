package com.netty.server.asyncpro;

import com.netty.message.NettyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTaskProcessor implements ITaskProcessor{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskProcessor.class);
    @Override
    public Runnable execAsyncTask(NettyMessage msg) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOG.info("DefaultTaskProcessor handle task：" + msg.getBody());
            }
        };
        return task;
    }
}
