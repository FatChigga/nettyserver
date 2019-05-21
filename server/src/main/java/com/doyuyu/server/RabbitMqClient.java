package com.doyuyu.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author songyuxiang
 * @description topic 发布订阅模式
 * @date 2019/5/17
 */
public class RabbitMqClient {
    private static final String ADDR = "192.168.195.129";

    private static final Integer PORT = 5672;

    private static final ConnectionFactory connectionFactory = new ConnectionFactory();

    static {
        connectionFactory.setHost(ADDR);
        connectionFactory.setPort(PORT);
    }

    public static void sendMessage(QueueDefinition queueDefinition,BizMessage<?> message){
        try(Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(queueDefinition.getSignature(),queueDefinition.getType().name());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
