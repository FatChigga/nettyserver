package com.doyuyu.common;

import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Song
 * @date 2019/4/4
 */
public class RedisClient {
    private static Jedis jedis;

    public Jedis getJedis(){
        return jedis;
    }

    public void init(){
        if(!Objects.isNull(jedis)){
            jedis.disconnect();
        }

        jedis = new Jedis("",6379);
        jedis.connect();
    }
}
