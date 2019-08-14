package com.doyuyu.server.netty;

import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class NettyServer {

    Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public void bind(int port) throws Exception{
        //bossGroup就是parentGroup，负责TCP/IP连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        //workGroup就是childGroup，负责处理channel的I/O事件
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                //初始化服务端可连接队列,指定了队列的大小128
                .option(ChannelOption.SO_BACKLOG,128)
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 绑定客户端连接时候触发操作
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        /**
                         * 可以理解为ChannelHandler的容器：
                         * 一个Channel包含一个ChannelPipeline，所有ChannelHandler都会注册到ChannelPipeline中，并按顺序组织起来。
                         * channel事件消息在ChannelPipeline中流动和传播，相应的事件能够被ChannelHandler拦截处理、传递、忽略或者终止。
                         * Netty的ChannelPipeline包含两条线路：Upstream和Downstream。
                         * Upstream对应上行，接收到的消息、被动的状态改变，都属于Upstream。
                         * Downstream则对应下行，发送的消息、主动的状态改变，都属于Downstream。
                         */
                        socketChannel.pipeline()
                                .addLast(new RpcDecode())
                                .addLast(new RpcEncode(RpcResponse.class))
                                .addLast(new NettyServerHandler());
                    }
                });

        //绑定监听端口，调用sync同步阻塞方法等待绑定操作完
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

        if(channelFuture.isSuccess()){
            logger.info("server startup success");
        }else{
            logger.debug("server startup failed");
            channelFuture.cause().printStackTrace();
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

        //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
        channelFuture.channel().closeFuture().sync();
    }
}
