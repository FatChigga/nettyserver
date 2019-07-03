package com.doyuyu.server.mq;

import java.util.Map;

/**
 *@description: 队列定义
 *@author: songyuxiang
 *@date: 2019/5/23
 */
public interface QueueDefinition {

    String getSignature();

    default boolean isAutomaticCreation(){
        return true;
    }

    default QueueType getType(){
        return QueueType.queue;
    }

    default Map<String,Object> getArgs(){
        return null;
    }

    /**
     * 返回AMQP RoutingKey
     */
    String getAmqpRoutingKey();

    String getAmqpExchangeName();
}
