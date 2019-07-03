package com.doyuyu.server.netty;

import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.TransactionStatusEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/24
 */
@Component
public class CommitHandler implements Handler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void executeTask(HandlerContext ctx, RpcRequest rpcRequest) {
        if(rpcRequest.getTransactionStatus().equals(TransactionStatusEnum.COMMIT)){
            //读取该事务组所有的线程，发送消息通知提交事务
            while(redisTemplate.opsForList().size(rpcRequest.getTransactionGroupId()) > 0){
                String transactionId = redisTemplate.opsForList().leftPop(rpcRequest.getTransactionGroupId()).toString();
                rabbitTemplate.convertAndSend("","",transactionId);
            }

            redisTemplate.delete(rpcRequest.getTransactionGroupId());
        }else{
            ctx.fireTaskExecuted(rpcRequest);
        }
    }
}
