package com.doyuyu.client;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 *
 * @author Song
 * @date 2019/4/17
 */
@Configuration
@Slf4j
public class FeignHeadConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
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
