package com.doyuyu.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/27
 */
@Configuration
@ComponentScan(value = "com.doyuyu")
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

    @Bean
    public RedisTemplate redisTemplate(@Autowired PropertiesConfiguration redisConfiguration) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory(redisConfiguration));

        // 使用Jackson2JsonRedisSerialize 替换默认序列化（备注，此处我用Object为例，各位看官请换成自己的类型哦~）
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 最好是调用一下这个方法
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory(@Autowired PropertiesConfiguration rabbitConfiguration){
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
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(@Autowired ConnectionFactory rabbitConnectionFactory){
        //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }

    @Bean
    RabbitAdmin rabbitAdmin(@Autowired ConnectionFactory rabbitConnectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(rabbitConnectionFactory);

        //创建队列和交换机以及绑定

        /**
         * Topic
         *
         * 可以使得不同源头的数据投放到一个队列中(order.log , order.id, purchase.log, purchase.id)
         *
         * 通过路由键的命名分类来进行筛选
         */
        admin.declareQueue(new Queue("Transaction-Topic-Queue-1"));

        admin.declareExchange(new TopicExchange("Transaction-Topic", false, false));

        Binding topic1 = BindingBuilder.bind(new Queue("Transaction-Topic-Queue-1"))
                .to(new TopicExchange("Transaction-Topic", false, false)).with("transactoin_topic_queue");

        admin.declareBinding(topic1);

        return admin;
    }
}
