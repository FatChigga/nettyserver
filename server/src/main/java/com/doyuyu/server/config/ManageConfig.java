package com.doyuyu.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
    public JedisConnectionFactory connectionFactory(PropertiesConfiguration redisConfiguration){
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

    @Bean
    public RedisTemplate redisTemplate(@Autowired PropertiesConfiguration redisConfiguration) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory(redisConfiguration));
        redisTemplate.setEnableTransactionSupport(false);
        return redisTemplate;
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory(PropertiesConfiguration rabbitConfiguration){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setUsername(rabbitConfiguration.getString("biz.rabbit.username"));
        cachingConnectionFactory.setPassword(rabbitConfiguration.getString("biz.rabbit.password"));
        cachingConnectionFactory.setPort(rabbitConfiguration.getInt("biz.rabbit.port"));
        cachingConnectionFactory.setHost(rabbitConfiguration.getString("biz.rabbit.host"));
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(@Autowired PropertiesConfiguration rabbitConfiguration){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(rabbitConnectionFactory(rabbitConfiguration));
        return rabbitTemplate;
    }
}
