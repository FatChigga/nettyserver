package com.doyuyu.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Song
 * @date 2019/4/19
 */

@ChannelHandler.Sharable
@Slf4j
public class AcceptIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("心跳超时");
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.READER_IDLE){
                log.debug("丢失连接，关闭通道");
                ctx.channel().close();
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
