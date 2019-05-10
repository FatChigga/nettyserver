package com.doyuyu.server;

import com.doyuyu.common.*;
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
        RpcResponse rpcResponse = null;

        logger.info("receive message from client:{}",rpcRequest.toString());

        if(rpcRequest.getTransactionStatus().equals(TransactionStatusEnum.ROLLBACK)){
            //读取该事务组所有的线程，发送消息通知回滚事务
            TransactionGroupMap.get(rpcRequest.getTransactionGroupId()).stream().forEach(
                    threadId -> {
                        RpcResponse response = RpcResponse.builder()
                                .joinStatusEnum(MessageStatusEnum.SUCCESS)
                                .commitStatusEnum(MessageStatusEnum.FAILED)
                                .data("测试")
                                .id(UUID.randomUUID().toString())
                                .build();

                        SocketChannel channel = NettyChannelMap.get(threadId);
                        channel.writeAndFlush(response);
                    }
            );

            rpcResponse = RpcResponse.builder()
                    .joinStatusEnum(MessageStatusEnum.SUCCESS)
                    .commitStatusEnum(MessageStatusEnum.FAILED)
                    .data("测试")
                    .id(UUID.randomUUID().toString())
                    .build();
        }

        if(rpcRequest.getTransactionStatus().equals(TransactionStatusEnum.COMMIT)){
            //读取该事务组所有的线程，发送消息通知提交事务
            TransactionGroupMap.get(rpcRequest.getTransactionGroupId()).stream().forEach(
                    threadId -> {
                        RpcResponse response = RpcResponse.builder()
                                .joinStatusEnum(MessageStatusEnum.SUCCESS)
                                .commitStatusEnum(MessageStatusEnum.SUCCESS)
                                .data("测试")
                                .id(UUID.randomUUID().toString())
                                .build();

                        SocketChannel channel = NettyChannelMap.get(threadId);
                        channel.writeAndFlush(response);
                    }
            );

            rpcResponse = RpcResponse.builder()
                    .joinStatusEnum(MessageStatusEnum.SUCCESS)
                    .commitStatusEnum(MessageStatusEnum.SUCCESS)
                    .data("测试")
                    .id(UUID.randomUUID().toString())
                    .build();
        }

        if(rpcRequest.getTransactionStatus().equals(TransactionStatusEnum.JOIN)){

            TransactionGroupMap.add(rpcRequest.getTransactionGroupId(),rpcRequest.getThreadId());
            NettyChannelMap.add(rpcRequest.getThreadId(),(SocketChannel) ctx.channel());

            rpcResponse = RpcResponse.builder()
                            .joinStatusEnum(MessageStatusEnum.SUCCESS)
                            .commitStatusEnum(MessageStatusEnum.NORMAL)
                            .data("测试")
                            .id(UUID.randomUUID().toString())
                            .build();
        }

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
