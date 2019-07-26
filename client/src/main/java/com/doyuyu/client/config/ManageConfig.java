package com.doyuyu.client.config;

import com.doyuyu.client.NettyClientHandler;
import com.doyuyu.common.RpcDecode;
import com.doyuyu.common.RpcEncode;
import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import feign.RequestInterceptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author songyuxiang
 * @description
 * @date 2019/7/26
 */
@Configuration
@Slf4j
@ComponentScan(value = "com.doyuyu")
public class ManageConfig {

    @Bean
    public ChannelFuture nettyClientFactory(@Autowired PropertiesConfiguration nettyConfiguration) throws InterruptedException {
        //todo 连接池
        return null;
    }

    @Bean
    public Channel nettyChannel(@Autowired ChannelFuture nettyClientFactory){
        return null;
    }

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()){
                String name = headerNames.nextElement();
                if("transactionGroupId".equals(headerNames.nextElement())){
                    String value = request.getHeader(name);
                    requestTemplate.header(name,value);
                    log.info("transactionGroupId:{}",value);
                    break;
                }
            }
        };
    }
}
