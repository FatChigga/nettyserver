package com.doyuyu.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/27
 */
@Configuration
@ComponentScan
public class ManageConfig {

    @Bean
    public JedisConnectionFactory connectionFactory(@Autowired PropertiesConfiguration redisConfiguration){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisConfiguration.getString("biz.redis.host"));
        jedisConnectionFactory.setPort(redisConfiguration.getInt("biz.redis.port"));
        jedisConnectionFactory.setClientName(redisConfiguration.getString("biz.redis.name"));
        jedisConnectionFactory.setPassword(redisConfiguration.getString("biz.redis.password"));
        jedisConnectionFactory.setDatabase(redisConfiguration.getInt("biz.redis.database"));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisConfiguration.getInt("biz.redis.maxTotal"));
        jedisPoolConfig.setMaxIdle(redisConfiguration.getInt("biz.redis.maxIdle"));
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(redisConfiguration.getLong("biz.redis.timeBetweenEvictionRunsMillis"));
        jedisPoolConfig.setTestOnBorrow(redisConfiguration.getBoolean("biz.redis.testOnBorrow"));
        jedisPoolConfig.setMinEvictableIdleTimeMillis(redisConfiguration.getLong("biz.redis.minEvictableIdleTimeMillis"));
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return jedisConnectionFactory;
    }

}
