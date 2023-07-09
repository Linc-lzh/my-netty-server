package com.netty.server.asyncpro;

import com.netty.message.NettyMessage;


public interface ITaskProcessor {

    Runnable execAsyncTask(NettyMessage msg);

}
