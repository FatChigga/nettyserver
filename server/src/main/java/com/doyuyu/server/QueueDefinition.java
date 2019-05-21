package com.doyuyu.server;

import java.util.Map;

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
}
