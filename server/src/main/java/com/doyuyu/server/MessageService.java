package com.doyuyu.server;

import com.rabbitmq.client.BuiltinExchangeType;

public interface MessageService {
    void sendMessage(QueueDefinition queueDefinition,BizMessage<?> bizMessage);

    void sendMessage(String exchangeName , String routingKey , BuiltinExchangeType builtinExchangeType, BizMessage<?> bizMessage);
}
