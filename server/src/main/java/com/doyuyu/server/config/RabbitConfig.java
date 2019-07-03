package com.doyuyu.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/28
 */

@Configuration
public class RabbitConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "rabbitConfiguration")
    public PropertiesConfiguration rabbitConfiguration(){
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();

        propertiesConfiguration.setProperty("biz.rabbit.host",environment.getProperty("biz.rabbit.host"));
        propertiesConfiguration.setProperty("biz.rabbit.port",environment.getProperty("biz.rabbit.port"));
        propertiesConfiguration.setProperty("biz.rabbit.username",environment.getProperty("biz.rabbit.username"));
        propertiesConfiguration.setProperty("biz.rabbit.password",environment.getProperty("biz.rabbit.password"));

        return propertiesConfiguration;
    }
}
