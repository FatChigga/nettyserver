package com.doyuyu.server;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/23
 */
@Slf4j
public class Producer implements MessageService{
    @Override
    public void sendMessage(QueueDefinition queueDefinition,BizMessage<?> message){
        QueueParser queueParser = new QueueParser(queueDefinition);
        try(Connection connection = RabbitMqClient.getConnectionFactory().newConnection();
            Channel channel = connection.createChannel()) {
            //管道绑定交换机
            channel.exchangeDeclare(queueParser.getExchangeName(),BuiltinExchangeType.valueOf(queueParser.getExchangeType().name()),
                    true, false, false,null);

            //消息属性 contentHeader
            AMQP.BasicProperties basicProperties
                    = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .contentType("UTF-8")
                    .build();

            channel.basicPublish(queueParser.getExchangeName(),
                    queueDefinition.getAmqpRoutingKey(),true,
                    basicProperties, JSONObject.toJSONString(message.getPayload()).getBytes("UTF-8"));
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String exchangeName, String routingKey, BuiltinExchangeType builtinExchangeType,BizMessage<?> message) {
        try(Connection connection = RabbitMqClient.getConnectionFactory().newConnection();
            Channel channel = connection.createChannel()) {
            //管道绑定交换机
            channel.exchangeDeclare(exchangeName,builtinExchangeType,
                    true, false, false,null);

            //消息属性 contentHeader
            AMQP.BasicProperties basicProperties
                    = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .contentType("UTF-8")
                    .build();

            channel.basicPublish(exchangeName,
                    routingKey,true,
                    basicProperties, JSONObject.toJSONString(message.getPayload()).getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
