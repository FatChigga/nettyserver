package com.doyuyu.client;

import com.alibaba.fastjson.JSONObject;
import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf>{

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Autowired
    private TransactionThreadGroup transactionThreadGroup;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        int length = byteBuf.readInt();

        byte[] head = new byte[4];
        byteBuf.readBytes(head);
        String headString = new String(head);

        logger.info("receive server message:{}",headString);

        byte[] body = new byte[length - 4];
        byteBuf.readBytes(body);
        String bodyString = new String(body);

        RpcResponse rpcResponse = JSONObject.parseObject(bodyString, RpcResponse.class);

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
