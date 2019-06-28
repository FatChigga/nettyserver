package com.doyuyu.server;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import com.doyuyu.common.TransactionStatusEnum;
import io.netty.channel.socket.SocketChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/24
 */
@Component
public class RollbackHandler implements Handler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void executeTask(HandlerContext ctx, RpcRequest rpcRequest) {
        if(rpcRequest.getTransactionStatus().equals(TransactionStatusEnum.ROLLBACK)){
            //读取该事务组所有的线程，发送消息通知回滚事务
            while(redisTemplate.opsForList().size(rpcRequest.getTransactionGroupId()) > 0){
                RpcResponse response = RpcResponse.builder()
                        .joinStatusEnum(MessageStatusEnum.SUCCESS)
                        .commitStatusEnum(MessageStatusEnum.FAILED)
                        .data("测试")
                        .id(UUID.randomUUID().toString())
                        .build();

                SocketChannel channel =
                        NettyChannelMap.get(
                                Long.parseLong(redisTemplate.opsForList().leftPop(rpcRequest.getTransactionGroupId()).toString())
                        );
                channel.writeAndFlush(response);
            }

            redisTemplate.delete(rpcRequest.getTransactionGroupId());
        }else{
            ctx.fireTaskExecuted(rpcRequest);
        }
    }
}
