package com.doyuyu.server.netty;

import com.doyuyu.common.CommonUtils;
import com.doyuyu.common.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client and server connect success");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        //channel连接断开
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        Pipeline pipeline = newPipeline(CommonUtils.Byte2TargetClassMethod(byteBuf, RpcRequest.class),ctx);
        try {
            pipeline.taskReceived();
            pipeline.taskFiltered();
            pipeline.taskExecuted();
        }catch (TransactionServerException t){
            throw t;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("read complete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("server handler exception:{}",cause.getMessage());
        ctx.close();
    }

    private Pipeline newPipeline(Object msg,ChannelHandlerContext ctx){
        return applicationContext.getBean(DefaultPipeline.class,msg,ctx);
    }
}
