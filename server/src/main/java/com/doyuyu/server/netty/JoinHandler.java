package com.doyuyu.server.netty;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import com.doyuyu.common.TransactionStatusEnum;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/24
 */
@Component
public class JoinHandler implements Handler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void executeTask(HandlerContext ctx, RpcRequest rpcRequest) {
        if(rpcRequest.getTransactionStatus().equals(TransactionStatusEnum.JOIN)){
            NettyChannelMap.add(rpcRequest.getTransactionId(),(SocketChannel) ctx.getChannelHandlerContext().channel());
            redisTemplate.opsForList().leftPush(rpcRequest.getTransactionGroupId(),rpcRequest.getTransactionId());

            RpcResponse rpcResponse = RpcResponse.builder()
                    .joinStatusEnum(MessageStatusEnum.SUCCESS)
                    .commitStatusEnum(MessageStatusEnum.NORMAL)
                    .data("测试")
                    .id(UUID.randomUUID().toString())
                    .build();

            ctx.getChannelHandlerContext().writeAndFlush(rpcResponse);
        }
        ctx.fireTaskExecuted(rpcRequest);
    }
}
