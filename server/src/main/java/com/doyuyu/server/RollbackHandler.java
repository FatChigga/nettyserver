package com.doyuyu.server;

import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import com.doyuyu.common.TransactionStatusEnum;
import io.netty.channel.socket.SocketChannel;
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

    @Override
    public void executeTask(HandlerContext ctx, RpcRequest rpcRequest) {
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

            RpcResponse rpcResponse = RpcResponse.builder()
                    .joinStatusEnum(MessageStatusEnum.SUCCESS)
                    .commitStatusEnum(MessageStatusEnum.FAILED)
                    .data("测试")
                    .id(UUID.randomUUID().toString())
                    .build();
        }else{
            ctx.fireTaskExecuted(rpcRequest);
        }
    }

    @Override
    public void afterCompletion(HandlerContext ctx) {

    }
}
