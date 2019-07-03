package com.doyuyu.server.mq;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcResponse;
import com.doyuyu.server.netty.NettyChannelMap;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/17
 */
@Slf4j
@Component
public class Consumer{

    @RabbitListener(queues = "")
    public void rollbackListener(Message message){
        RpcResponse response = RpcResponse.builder()
                .joinStatusEnum(MessageStatusEnum.SUCCESS)
                .commitStatusEnum(MessageStatusEnum.FAILED)
                .data("测试")
                .id(UUID.randomUUID().toString())
                .build();

        SocketChannel channel =
                NettyChannelMap.get(Long.parseLong(message.getBody().toString()));
        channel.writeAndFlush(response);
    }

    @RabbitListener(queues = "")
    public void commitListener(Message message){
        RpcResponse response = RpcResponse.builder()
                .joinStatusEnum(MessageStatusEnum.SUCCESS)
                .commitStatusEnum(MessageStatusEnum.SUCCESS)
                .data("测试")
                .id(UUID.randomUUID().toString())
                .build();

        SocketChannel channel =
                NettyChannelMap.get(Long.parseLong(message.getBody().toString()));
        channel.writeAndFlush(response);
    }
}
