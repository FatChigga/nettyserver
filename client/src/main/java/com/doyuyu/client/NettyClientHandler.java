package com.doyuyu.client;

import com.doyuyu.common.CommonUtils;
import com.doyuyu.common.MessageStatusEnum;
import com.doyuyu.common.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf>{

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Autowired
    private TransactionGroup transactionGroup;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        RpcResponse rpcResponse = (RpcResponse)CommonUtils.Byte2TargetClassMethod(byteBuf,RpcResponse.class);
        TransactionStatus transactionStatus = transactionGroup.getTransaction(rpcResponse.getTransactionId());

        if(rpcResponse.getJoinStatusEnum().equals(MessageStatusEnum.FAILED)
                ||rpcResponse.getCommitStatusEnum().equals(MessageStatusEnum.FAILED)){
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        if(rpcResponse.getCommitStatusEnum().equals(MessageStatusEnum.SUCCESS)){
            dataSourceTransactionManager.commit(transactionStatus);
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
