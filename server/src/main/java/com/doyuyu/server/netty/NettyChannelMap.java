package com.doyuyu.server.netty;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Song
 * @date 2019/4/24
 */
public class NettyChannelMap {
    private static Map<Long,SocketChannel> map = new ConcurrentHashMap();

    public static void add(Long id,SocketChannel socketChannel){
        map.put(id,socketChannel);
    }

    public static SocketChannel get(Long id){
        return map.get(id);
    }

    public static void remove(SocketChannel socketChannel){
        for (Map.Entry<Long,SocketChannel> entry: map.entrySet()) {
            if(entry.getValue().equals(socketChannel)){
                map.remove(entry.getKey());
                break;
            }
        }
    }
}
