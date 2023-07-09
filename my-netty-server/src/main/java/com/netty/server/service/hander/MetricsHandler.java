package com.netty.server.service.hander;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsHandler extends ChannelDuplexHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsHandler.class);

    private static AtomicBoolean isStartTask = new AtomicBoolean(false);
    private static AtomicLong channelCount = new AtomicLong(0);
    private static AtomicLong totalReadBytes = new AtomicLong(0);
    private static AtomicLong totalWriteBytes = new AtomicLong(0);
    private static ScheduledExecutorService statService = new ScheduledThreadPoolExecutor(1);
    /*ChannelGroup save all connected channels*/
    private final static ChannelGroup connectedChannelGroup =
            new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelCount.incrementAndGet();
        if(isStartTask.compareAndSet(false,true)){
            statService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    LOG.info("----------------Start to collect metric data-------------------");
                    /*Current online channel count*/
                    LOG.info("Current online channel count：" + channelCount.get());

                    /*I/O thread pool queue size*/
                    Iterator<EventExecutor> executorGroups = ctx.executor().parent().iterator();
                    while (executorGroups.hasNext()) {
                        SingleThreadEventExecutor executor =
                                (SingleThreadEventExecutor) executorGroups.next();
                        int size = executor.pendingTasks();
                        if (executor == ctx.executor())
                            LOG.info(ctx.channel() + ":" + executor + " size of waiting queue :  " + size);
                        else
                            LOG.info(executor + " size of waiting queue : " + size);
                    }
                    /* Send piled message byte count*/
                    Iterator<Channel> channels = connectedChannelGroup.iterator();
                    while(channels.hasNext()){
                        Channel channel = channels.next();
                        if(channel instanceof ServerChannel) continue;
                        LOG.info(channel+"Send byte buffer size："+channel.unsafe().outboundBuffer().totalPendingWriteBytes());
                    }

                    LOG.info( "Speed of read(bytes/min)："+totalReadBytes.getAndSet(0));
                    LOG.info( "Speed of write(bytes/min)："+totalWriteBytes.getAndSet(0));

                    LOG.info("----------------End of collect metric data-------------------");
                }
            },0,10*1000, TimeUnit.MILLISECONDS);
        }
        connectedChannelGroup.add(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int readableBytes = ((ByteBuf)msg).readableBytes();
        totalReadBytes.getAndAdd(readableBytes) ;
        ctx.fireChannelRead(msg) ;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        int writeableBytes = ((ByteBuf)msg).readableBytes();
        totalWriteBytes.getAndAdd(writeableBytes) ;
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelCount.decrementAndGet();
        connectedChannelGroup.remove(ctx.channel());
        super.channelInactive(ctx);
    }
}
