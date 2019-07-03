package com.doyuyu.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/27
 */
@Configuration
@PropertySource(value= "classpath:application.properties")
public class RedisConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "redisConfiguration")
    public PropertiesConfiguration redisConfiguration(){
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.addProperty("biz.redis.host", environment.getProperty("biz.redis.host"));
        propertiesConfiguration.addProperty("biz.redis.database", environment.getProperty("biz.redis.database", Integer.class));
        propertiesConfiguration.addProperty("biz.redis.password", environment.getProperty("biz.redis.password"));
        propertiesConfiguration.addProperty("biz.redis.name", environment.getProperty("biz.redis.name"));
        propertiesConfiguration.addProperty("biz.redis.port", environment.getProperty("biz.redis.port", Integer.class));
        propertiesConfiguration.addProperty("biz.redis.maxTotal", environment.getProperty("biz.redis.maxTotal", Integer.class));
        propertiesConfiguration.addProperty("biz.redis.maxIdle", environment.getProperty("biz.redis.maxIdle", Integer.class));
        propertiesConfiguration.addProperty("biz.redis.timeBetweenEvictionRunsMillis", environment.getProperty("biz.redis.timeBetweenEvictionRunsMillis", Integer.class));
        propertiesConfiguration.addProperty("biz.redis.minEvictableIdleTimeMillis", environment.getProperty("biz.redis.minEvictableIdleTimeMillis", Integer.class));
        propertiesConfiguration.addProperty("biz.redis.testOnBorrow", environment.getProperty("biz.redis.testOnBorrow", Boolean.class));
        return propertiesConfiguration;
    }

}
