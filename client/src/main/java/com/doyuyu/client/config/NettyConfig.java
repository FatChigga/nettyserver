package com.doyuyu.client.config;

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
public class NettyConfig {

    @Autowired
    private Environment environment;

    @Bean
    public PropertiesConfiguration nettyConfiguration(){
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.addProperty("biz.netty.ip",environment.getProperty("biz.netty.ip"));
        propertiesConfiguration.addProperty("biz.netty.port",environment.getProperty("biz.netty.port"));
        return propertiesConfiguration;
    }
}
