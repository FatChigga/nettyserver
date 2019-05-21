package com.doyuyu.server;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Song
 * @date 2019/4/4
 */
@Slf4j
public class RedisClient{

    private static JedisPool jedisPool = null;

    private final static String ADDR = "192.168.195.129";

    private final static Integer PORT = 6379;

    private final static Integer MAX_ACTIVE = 1024;

    private final static Integer MAX_IDLE  = 100;

    private final static Integer MAX_WAIT = 10000;

    private final static Integer TIMEOUT = 10000;

    private static synchronized Jedis getJedis(){
        if(!Objects.isNull(jedisPool)){
            Jedis jedis = jedisPool.getResource();
            return jedis;
        }else{
            return null;
        }
    }

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(MAX_IDLE);
        jedisPoolConfig.setMaxTotal(MAX_ACTIVE);
        jedisPoolConfig.setMaxWaitMillis(MAX_WAIT);
        jedisPool = new JedisPool(jedisPoolConfig,ADDR,PORT,TIMEOUT);
    }

    public static List listGetAll(String key){
        Jedis jedis = getJedis();
        List list = Lists.newArrayList();
        long len = jedis.llen(key);
        for(long i = 0;i < len;i++){
            String objectString = jedis.lindex(key,i);
            list.add(JSONObject.parse(objectString));
        }
        jedis.close();
        return list;
    }

    public static Object listGet(String key,Long index){
        Jedis jedis = getJedis();
        Object object = JSONObject.parse(jedis.lindex(key,index));
        jedis.close();
        return object;
    }

    public static void listInsert(String key,Object object){
        Jedis jedis = getJedis();
        String jsonString = JSONObject.toJSONString(object);
        jedis.lpush(key,jsonString);
        jedis.close();
    }

    public static void listRemove(String key){
        Jedis jedis = getJedis();
        jedis.del(key);
        jedis.close();
    }

    private static String objectSerialiable(Object object){

        String result = null;

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
            objectOutputStream.writeObject(object);
            result = byteArrayOutputStream.toString("ISO-8859-1");
            result = URLEncoder.encode(result,"UTF-8");
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Object deserializeString(String str){
        Object object = null;
        byte[] bytes = null;

        try {
            str = URLDecoder.decode(str,"UTF-8");
            bytes = str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){

            object = objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }
}
