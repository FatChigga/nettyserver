package com.doyuyu.client;

import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyClient {
    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final Integer port;

    private final String host;

    private Channel channel;

    public NettyClient(int port,String host){
        this.host = host;
        this.port = port;
    }

    public void startup() throws Exception{
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncode(RpcRequest.class))
                                .addLast(new RpcDecode())
                                .addLast(new NettyClientHandler());
                    }
                });

        final ChannelFuture channelFuture = bootstrap.connect(host,port).sync();

        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            if(channelFuture1.isSuccess()){
                logger.info("client startup success");
            }else{
                logger.debug("client startup failed");
                channelFuture1.cause().printStackTrace();
                group.shutdownGracefully();
            }
        });

        this.channel = channelFuture.channel();
    }

    public Channel getChannel(){
        return channel;
    }
}
