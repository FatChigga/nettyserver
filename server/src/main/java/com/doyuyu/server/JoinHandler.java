package com.doyuyu.server;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import com.doyuyu.common.TransactionStatusEnum;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
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
            NettyChannelMap.add(rpcRequest.getThreadId(),(SocketChannel) ctx.getChannelHandlerContext().channel());
            redisTemplate.opsForList().leftPush(rpcRequest.getTransactionGroupId(),rpcRequest.getThreadId());

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
