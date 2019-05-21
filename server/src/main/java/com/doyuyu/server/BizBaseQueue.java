package com.doyuyu.server;

import java.util.Map;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/21
 */
public enum  BizBaseQueue implements QueueDefinition{

    /**
     * 事务变化同步队列
     */
    REDIS_TRANSACTION_SYNC_QUEUE("redisTransactionSyncQueue",true,QueueType.topic);

    private final String signature;
    private final Boolean automaticCreation;
    private final QueueType type;
    private final Map<String,Object> arg;

    BizBaseQueue(String signature,Boolean automaticCreation,QueueType type,Map<String,Object> arg) {
        this.signature = signature;
        this.automaticCreation = automaticCreation;
        this.type = type;
        this.arg = arg;
    }

    BizBaseQueue(String signature,Boolean automaticCreation,QueueType type){
        this.signature = signature;
        this.automaticCreation = automaticCreation;
        this.type = type;
        this.arg = null;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public boolean isAutomaticCreation() {
        return automaticCreation;
    }

    @Override
    public QueueType getType() {
        return type;
    }

    @Override
    public Map<String, Object> getArgs() {
        return arg;
    }
}
