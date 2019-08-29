package com.doyuyu.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author songyuxiang
 * @description
 * @date 2019/8/26
 */
@Configuration
public class ZookeeperConfig {
    @Autowired
    private Environment environment;

    @Bean(name="zookeeperConfiguration")
    public PropertiesConfiguration zookeeperConfiguration(){
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.addProperty("biz.zookeeper.url", environment.getProperty("biz.zookeeper.url"));
        propertiesConfiguration.addProperty("biz.zookeeper.namespace", environment.getProperty("biz.zookeeper.namespace"));
        return propertiesConfiguration;
    }
}
