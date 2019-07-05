package com.doyuyu.client;

import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 *
 * @author Song
 * @date 2019/4/23
 */

@Configuration
@Slf4j
public class DtsConfiguration {

    @Value("${biz.netty.host}")
    private String host;

    @Value("${biz.netty.port}")
    private Integer port;

    @Bean
    public TransactionThreadGroup transactionThreadGroup(){
        return new TransactionThreadGroup("Dts");
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadFactory tractionThreadFactory
                = new ThreadFactoryBuilder().setNameFormat("traction-pool-%d").build();

        RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
            throw new RejectedExecutionException("Task"+r.toString()+"reject from"+executor.toString());
        };

        return new ThreadPoolExecutor(
                20,
                100,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                tractionThreadFactory,
                rejectedExecutionHandler);
    }

    @Bean
    public ChannelFuture channelFuture() throws InterruptedException {
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncode(RpcRequest.class))
                                .addLast(new RpcDecode(RpcResponse.class))
                                .addLast(new NettyClientHandler());
                    }
                });

        final ChannelFuture channelFuture = bootstrap.connect(host,port).sync();

        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            if(channelFuture1.isSuccess()){
                log.info("client startup success");
            }else{
                log.debug("client startup failed");
                channelFuture1.cause().printStackTrace();
                group.shutdownGracefully();
            }
        });

        return channelFuture;
    }

    @Bean
    public Channel channel(@Autowired ChannelFuture channelFuture){
        return channelFuture.channel();
    }
}
