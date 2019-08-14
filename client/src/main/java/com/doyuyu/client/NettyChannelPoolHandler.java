package com.doyuyu.client;

import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author songyuxiang
 * @description
 * @date 2019/8/13
 */
public class NettyChannelPoolHandler implements ChannelPoolHandler {
    /**
     * 使用完channel需要释放才能放入连接池
     */
    @Override
    public void channelReleased(Channel ch) throws Exception {
        ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    /**
     * 获取连接池中的channel
     */
    @Override
    public void channelAcquired(Channel ch) throws Exception {

    }

    /**
     * 当channel不足时会创建，但不会超过限制的最大channel数
     */
    @Override
    public void channelCreated(Channel ch) throws Exception {
        NioSocketChannel channel = (NioSocketChannel) ch;
        channel.pipeline().addLast(new RpcEncode(RpcRequest.class))
                .addLast(new RpcDecode())
                .addLast(new NettyClientHandler());
    }
}
