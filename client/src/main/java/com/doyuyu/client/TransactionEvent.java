package com.doyuyu.client;

import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.TransactionStatusEnum;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

import static java.util.stream.Collectors.toList;

@Getter
@Slf4j
class TransactionEvent implements Callable<Object>{

    @Autowired
    private Channel channel;

    @Autowired
    private TransactionThreadGroup transactionThreadGroup;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    private Object targetClass;
    private Method method;
    private Object[] param;
    private Object result;
    /**是否最后一个事务事件*/
    private Boolean isEnd;
    private Long timeout;

    public TransactionEvent(Object targetClass, Method method, Object[] param,Long timeout) {
        this.targetClass = targetClass;
        this.method = method;
        this.param = Objects.isNull(param)?null:param.clone();
        this.timeout = timeout;
    }

    @Override
    public Object call()throws Exception{
        log.info("进入线程"+Thread.currentThread().getName());
        //获取事务
        TransactionStatus transactionStatus =
                platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

        isEnd = true;

        //获取线程组，将当前线程放入线程组
        List<Thread> threads = Arrays.stream(transactionThreadGroup.getThreads()).collect(toList());
        threads.add(Thread.currentThread());
        transactionThreadGroup.setThreads(threads.stream().toArray(Thread[]::new));

        //获取事务组
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpRequest = attrs.getRequest();
        String transactionGroupId = httpRequest.getHeader("transactionGroupId");

        if(Objects.isNull(transactionGroupId)){
            transactionGroupId = UUID.randomUUID().toString();
            httpRequest.setAttribute("transactionGroupId",transactionGroupId);
        }

        final String finalTransactionGroupId = transactionGroupId;

        //遍历当前线程堆栈
        Arrays.asList(Thread.currentThread().getStackTrace()).parallelStream().forEach(
                stackTraceElement -> Arrays.asList(
                        stackTraceElement.getClass().getAnnotations()
                ).parallelStream().forEach(
                        annotation -> {
                            if(annotation.equals(FeignClient.class)) {
                                isEnd = false;

                                //发送请求，事务未结束
                                channel.writeAndFlush(RpcRequest.builder()
                                        .transactionGroupId(finalTransactionGroupId)
                                        .transactionId(Thread.currentThread().getId())
                                        .transactionStatus(TransactionStatusEnum.JOIN)
                                        .build());
                            }
                        }
                )
        );

        log.info("是否最后一个线程"+isEnd);

        try{
            result = method.invoke(targetClass,param);
        }catch (Exception e){
            //发送请求，事务组事务回滚
            channel.writeAndFlush(RpcRequest.builder()
                    .transactionGroupId(finalTransactionGroupId)
                    .transactionId(Thread.currentThread().getId())
                    .transactionStatus(TransactionStatusEnum.ROLLBACK)
                    .build());

            platformTransactionManager.rollback(transactionStatus);

            throw e;
        }

        if(isEnd){
            //发送请求，事务结束
            channel.writeAndFlush(RpcRequest.builder()
                    .transactionGroupId(finalTransactionGroupId)
                    .transactionId(Thread.currentThread().getId())
                    .transactionStatus(TransactionStatusEnum.COMMIT)
                    .build());
        }

        try {
            wait(timeout);
        } catch (InterruptedException e) {
            platformTransactionManager.rollback(transactionStatus);
        }

        platformTransactionManager.commit(transactionStatus);

        return result;
    }
}