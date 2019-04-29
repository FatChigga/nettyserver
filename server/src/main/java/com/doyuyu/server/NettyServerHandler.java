package com.doyuyu.server;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RedisClient;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client and server connect success");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        //channel连接断开
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest)msg;

        logger.info("receive message from client:{}",rpcRequest.toString());

        NettyChannelMap.add(rpcRequest.getClientId(),(SocketChannel) ctx.channel());

        RpcResponse rpcResponse =
                RpcResponse.builder()
                        .joinStatusEnum(MessageStatusEnum.SUCCESS)
                        .data("测试")
                        .id(UUID.randomUUID().toString())
                        .build();

        ctx.writeAndFlush(rpcResponse);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("read complete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("server handler exception:{}",cause.getMessage());
        ctx.close();
    }
}
