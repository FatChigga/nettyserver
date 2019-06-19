package com.doyuyu.server;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @author songyuxiang
 * @description topic 发布订阅模式
 * @date 2019/5/17
 */
public class RabbitMqClient {
    private static final String HOST = "192.168.195.129";
    private static final Integer PORT = 5672;
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final String VIRTUAL_HOST = "";
    public static final String DEFAULT_DIRECT_EXCHANGE_NAME = "com.biz.base.direct";
    public static final String DEFAULT_TOPIC_EXCHANGE_NAME = "com.biz.base.topic";

    public static ConnectionFactory getConnectionFactory(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setVirtualHost(VIRTUAL_HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setHost(HOST);

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(10000);

        return connectionFactory;
    }
}
