package com.doyuyu.client;

import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse>{

    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        logger.info("receive server message:{}",rpcResponse.toString());
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
