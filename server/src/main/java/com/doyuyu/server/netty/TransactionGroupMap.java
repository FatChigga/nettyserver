package com.doyuyu.server.netty;

import com.google.common.collect.Lists;
import io.netty.channel.socket.SocketChannel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Song
 * @date 2019/4/24
 */
public class TransactionGroupMap {
    private static Map<String,List<Long>> map = new ConcurrentHashMap();

    public static void add(String groupId,Long threadId){
        List<Long> list = Lists.newArrayList();

        if(!Objects.isNull(map.get(groupId))){
            list = map.get(groupId);
        }

        list.add(threadId);
        map.put(groupId,list);
    }

    public static List<Long> get(String id){
        return map.get(id);
    }

    public static void remove(List<Long> threadIds){
        for (Map.Entry<String,List<Long>> entry: map.entrySet()) {
            if(entry.getValue().equals(threadIds)){
                map.remove(entry.getKey());
                break;
            }
        }
    }
}
