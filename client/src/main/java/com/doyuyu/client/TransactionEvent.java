package com.doyuyu.client;

import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.TransactionStatusEnum;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.toList;

@Getter
@Slf4j
class TransactionEvent implements Callable<Object>{

    @Autowired
    private TransactionGroup transactionGroup;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    private FixedChannelPool nettyChannelPool;

    private Object targetClass;
    private Method method;
    private Object[] param;
    private Object result;
    private Long timeout;

    public TransactionEvent(Object targetClass, Method method, Object[] param,Long timeout) {
        this.targetClass = targetClass;
        this.method = method;
        this.param = Objects.isNull(param)?null:param.clone();
        this.timeout = timeout;
    }

    @Override
    public Object call()throws Exception{
        String transactionId = UUID.randomUUID().toString();
        //获取netty channel
        Channel channel = nettyChannelPool.acquire().get();
        log.info("进入线程"+Thread.currentThread().getName());
        //获取事务
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
        //获取事务，将当前事务放入事务组
        transactionGroup.addTransaction(transactionId,transactionStatus);

        //获取事务组
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpRequest = attrs.getRequest();
        String transactionGroupId = httpRequest.getHeader("transactionGroupId");

        if(Objects.isNull(transactionGroupId)){
            transactionGroupId = UUID.randomUUID().toString();
            httpRequest.setAttribute("transactionGroupId",transactionGroupId);
        }

        final String finalTransactionGroupId = transactionGroupId;
        channel.writeAndFlush(RpcRequest.builder()
                .transactionGroupId(finalTransactionGroupId)
                .transactionId(transactionId)
                .transactionStatus(TransactionStatusEnum.JOIN)
                .build());

        Boolean isLast = isLastTransaction();

        try{
            result = method.invoke(targetClass,param);
            if(isLast){
                channel.writeAndFlush(RpcRequest.builder()
                        .transactionGroupId(finalTransactionGroupId)
                        .transactionId(transactionId)
                        .transactionStatus(TransactionStatusEnum.COMMIT)
                        .build());
            }else{
                wait(timeout);
            }
        }catch (Exception e){
            //发送请求，事务组事务回滚
            channel.writeAndFlush(RpcRequest.builder()
                    .transactionGroupId(finalTransactionGroupId)
                    .transactionId(transactionId)
                    .transactionStatus(TransactionStatusEnum.ROLLBACK)
                    .build());
            throw e;
        }

        return result;
    }

    private Boolean isLastTransaction(){
        Boolean isLast = true;
        Set<Annotation> annotationSet = new HashSet<>();
        //遍历当前线程堆栈
        Arrays.asList(Thread.currentThread().getStackTrace()).parallelStream().forEach(
                stackTraceElement -> annotationSet.addAll(Arrays.asList(stackTraceElement.getClass().getAnnotations()))
        );
        Iterator<Annotation> iterator = annotationSet.iterator();
        while (iterator.hasNext()){
            Annotation annotation = iterator.next();
            if(annotation.equals(FeignClient.class)) {
                isLast = false;
                break;
            }
        }
        log.info("是否最后一个事务"+isLast);
        return isLast;
    }
}