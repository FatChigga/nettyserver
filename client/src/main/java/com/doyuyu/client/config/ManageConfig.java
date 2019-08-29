package com.doyuyu.client.config;

import com.doyuyu.client.NettyChannelPoolHandler;
import com.doyuyu.client.NettyClientHandler;
import com.doyuyu.client.TransactionGroup;
import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import feign.RequestInterceptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.concurrent.*;

/**
 * @author songyuxiang
 * @description
 * @date 2019/7/26
 */
@Configuration
@Slf4j
@ComponentScan(value = "com.doyuyu")
public class ManageConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()){
                String name = headerNames.nextElement();
                if("transactionGroupId".equals(headerNames.nextElement())){
                    String value = request.getHeader(name);
                    requestTemplate.header(name,value);
                    log.info("transactionGroupId:{}",value);
                    break;
                }
            }
        };
    }

    @Bean
    public FixedChannelPool nettyChannelPool(@Autowired PropertiesConfiguration nettyConfiguration) throws InterruptedException {
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        // 连接地址
        InetSocketAddress remoteAddress =
                InetSocketAddress.createUnresolved(nettyConfiguration.getString("biz.netty.ip"),nettyConfiguration.getInt("biz.netty.port"));
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
        FixedChannelPool channelPool =
                new FixedChannelPool(bootstrap, new NettyChannelPoolHandler(), nettyConfiguration.getInt("biz.netty.channel.max"));
        return channelPool;
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
    public TransactionGroup transactionThreadGroup(){
        return new TransactionGroup();
    }
}
