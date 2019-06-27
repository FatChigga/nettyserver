package com.doyuyu.server;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/24
 */
public class HandlerBeanProcessor implements BeanPostProcessor,ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof DefaultPipeline){
            DefaultPipeline pipeline = (DefaultPipeline)bean;
            Map<String,Handler> map = applicationContext.getBeansOfType(Handler.class);
            map.forEach((name,handler) -> pipeline.addLast(handler));
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
