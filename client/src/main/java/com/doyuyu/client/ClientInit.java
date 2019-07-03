package com.doyuyu.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author songyuxiang
 * @description
 * @date 2019/7/3
 */
@Component
@Order(value = 1)
public class ClientInit implements CommandLineRunner{

    @Value("${biz.netty.port}")
    private Integer port;

    @Value("${biz.netty.host}")
    private String host;

    @Override
    public void run(String... strings) throws Exception {
        //启动server服务
        NettyClient nettyClient = new NettyClient(port,host);
        nettyClient.startup();
    }
}
