package com.doyuyu.server;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Song
 * @date 2019/4/24
 */
public class NettyChannelMap {
    private static Map<String,SocketChannel> map = new ConcurrentHashMap();

    public static void add(String id,SocketChannel socketChannel){
        map.put(id,socketChannel);
    }

    public static SocketChannel get(String id){
        return map.get(id);
    }

    public static void remove(SocketChannel socketChannel){
        for (Map.Entry<String,SocketChannel> entry: map.entrySet()) {
            if(entry.getValue().equals(socketChannel)){
                map.remove(entry.getKey());
                break;
            }
        }
    }
}
