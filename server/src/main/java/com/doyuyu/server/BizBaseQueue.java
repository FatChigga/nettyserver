package com.doyuyu.server;

import com.doyuyu.common.StringTools;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public String getAmqpRoutingKey(){
        if(Objects.isNull(this.getSignature())){
            return "";
        }
        List<String> list = StringTools.split(this.getSignature(),":");
        if(CollectionUtils.isNotEmpty(list) && list.size() == 3){
            return list.get(1);
        }else{
            return "";
        }
    }

    @Override
    public String getAmqpExchangeName(){
        if(Objects.isNull(this.getSignature())){
            return "";
        }
        List<String> list = StringTools.split(this.getSignature(),":");
        if(CollectionUtils.isNotEmpty(list) && list.size() >= 1){
            return list.get(0);
        }else{
            return "";
        }
    }
}
