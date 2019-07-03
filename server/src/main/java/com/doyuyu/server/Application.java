package com.doyuyu.server;

import com.doyuyu.server.config.ManageConfig;
import com.doyuyu.server.netty.NettyServer;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/29
 */
@EnableRabbit
public class Application {
    public static void main(String[] args) throws Exception{
        AnnotationConfigApplicationContext annotationConfigApplicationContext
                = new AnnotationConfigApplicationContext(ManageConfig.class);
        NettyServer nettyServer = new NettyServer();
        nettyServer.bind(8088);
    }
}
