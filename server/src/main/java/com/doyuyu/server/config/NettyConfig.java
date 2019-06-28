package com.doyuyu.server.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/27
 */

@Configuration
public class NettyConfig {

    @Bean
    public PropertiesConfiguration nettyConfiguration(){
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        
        return propertiesConfiguration;
    }
}
