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
public class CommitHandler implements Handler {

    private RpcResponse rpcResponse;

    @Override
    public void executeTask(HandlerContext ctx, RpcRequest rpcRequest) {
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
        }else{
            ctx.fireTaskExecuted(rpcRequest);
        }
    }

    @Override
    public void afterCompletion(HandlerContext ctx) {
        if(!Objects.isNull(rpcResponse)){
            ctx.getChannelHandlerContext().writeAndFlush(rpcResponse);
        }else{
            ctx.fireAfterCompletion(ctx);
        }
    }
}
