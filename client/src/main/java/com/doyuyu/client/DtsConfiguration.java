package com.doyuyu.client;

import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
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

    @Value("${biz.netty.channel.max}")
    private Integer maxChannel;

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
    public FixedChannelPool nettyChannelPool() throws InterruptedException {
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        // 连接地址
        InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved(host, port);
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
                }).remoteAddress(remoteAddress);

        FixedChannelPool channelPool = new FixedChannelPool(bootstrap, new NettyChannelPoolHandler(), maxChannel);
        return channelPool;
    }
}
