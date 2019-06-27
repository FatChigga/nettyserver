package com.doyuyu.server;

import com.doyuyu.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/20
 */
@Component
@Scope("prototype")
public class HandlerContext {
    HandlerContext next;
    HandlerContext prev;
    Handler handler;
    private ChannelHandlerContext channelHandlerContext;

    public void fireTaskReceived(Object object){
        invokeTaskReceived(getNext(),object);
    }

    /**
     * 处理接收到任务的事件
     */
    static void invokeTaskReceived(HandlerContext ctx, Object object) {
        if (ctx != null) {
            try {
                ctx.getHandler().receiveTask(ctx, object);
            } catch (Throwable e) {
                ctx.getHandler().exceptionCaught(ctx, e);
            }
        }
    }

    public void fireTaskFiltered(RpcRequest rpcRequest) {
        invokeTaskFiltered(getNext(), rpcRequest);
    }

    static void invokeTaskFiltered(HandlerContext ctx, RpcRequest rpcRequest){
        if (null != ctx) {
            try {
                ctx.getHandler().filterTask(ctx, rpcRequest);
            } catch (Throwable e) {
                ctx.getHandler().exceptionCaught(ctx, e);
            }
        }
    }

    public void fireTaskExecuted(RpcRequest rpcRequest) {
        invokeTaskExecuted(getNext(), rpcRequest);
    }

    /**

     * 处理执行任务事件
     */
    static void invokeTaskExecuted(HandlerContext ctx, RpcRequest rpcRequest) {
        if (null != ctx) {
            try {
                ctx.getHandler().executeTask(ctx, rpcRequest);
            } catch (Exception e) {
                ctx.getHandler().exceptionCaught(ctx, e);
            }
        }
    }

    public void fireAfterCompletion(HandlerContext ctx) {
        invokeAfterCompletion(getNext());
    }

    static void invokeAfterCompletion(HandlerContext ctx) {
        if (null != ctx) {
            ctx.getHandler().afterCompletion(ctx);
        }
    }

    private Handler getHandler(){
        return handler;
    }

    private HandlerContext getNext(){
        return next;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
