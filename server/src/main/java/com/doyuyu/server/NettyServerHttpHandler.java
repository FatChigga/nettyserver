package com.doyuyu.server;

import com.doyuyu.common.HttpMethodEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyServerHttpHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(NettyServerHttpHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client and server connect success");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest)msg;

            try {
                HttpMethod method = request.method();
                HttpHeaders httpHeaders = request.headers();
                ByteBuf byteBuf = request.content();

                String uri = request.uri();

                switch (HttpMethodEnum.valueOf(method.name())){
                    case GET:

                        break;
                    case PUT:

                        break;
                    case POST:

                        break;
                    case DELETE:

                        break;
                    default:
                        logger.debug("请求方式不正确");
                }

            }finally {
                request.release();
            }

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
}
