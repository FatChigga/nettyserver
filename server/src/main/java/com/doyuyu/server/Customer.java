package com.doyuyu.server;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/17
 */
@Slf4j
public class Customer {
    public void listener(QueueDefinition queueDefinition){
        try(Connection connection = RabbitMqClient.getConnectionFactory().newConnection();
            Channel channel = connection.createChannel()){
            channel.queueDeclare();

            //管道绑定交换机
            channel.exchangeDeclare(queueDefinition.getAmqpExchangeName(),
                    BuiltinExchangeType.valueOf(queueDefinition.getType().name()),
                    true, false, false,null);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
