package com.doyuyu.client;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse>{

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Autowired
    private TransactionThreadGroup transactionThreadGroup;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        logger.info("receive server message:{}",rpcResponse.toString());

        List<Thread> threads = Arrays.asList(transactionThreadGroup.getThreads());
        Thread currentThread = null;
        for(Thread thread:threads){
            if(thread.getId() == rpcResponse.getThreadId()){
                currentThread = thread;
                break;
            }
        }

        if(rpcResponse.getJoinStatusEnum().equals(MessageStatusEnum.FAILED)
                ||rpcResponse.getCommitStatusEnum().equals(MessageStatusEnum.FAILED)){
            currentThread.interrupt();
        }

        if(rpcResponse.getCommitStatusEnum().equals(MessageStatusEnum.SUCCESS)){
            currentThread.notify();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client and server connect success");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("client handler exception:{}",cause.getMessage());
        ctx.close();
    }
}
