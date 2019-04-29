package com.doyuyu.client;

import com.doyuyu.common.RpcRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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

@Getter
class TransactionEvent implements Callable<Object>{

    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private TransactionThreadGroup transactionThreadGroup;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    private Object targetClass;
    private Method method;
    private Object[] param;
    private Object result;
    private Boolean isSuccess;
    private Long timeout;

    public TransactionEvent(Object targetClass, Method method, Object[] param,Long timeout) {
        this.targetClass = targetClass;
        this.method = method;
        this.param = Objects.isNull(param)?null:param.clone();
        this.timeout = timeout;
    }

    @Override
    public Object call()throws Exception{
        //获取事务
        TransactionStatus transactionStatus =
                platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

        result = method.invoke(targetClass,param);
        isSuccess = true;

        //获取线程组，将当前线程放入线程组
        List<Thread> threads = Arrays.asList(transactionThreadGroup.getThreads());
        threads.add(Thread.currentThread());
        transactionThreadGroup.setThreads(threads.stream().toArray(Thread[]::new));

        //遍历当前线程堆栈
        Arrays.asList(Thread.currentThread().getStackTrace()).parallelStream().forEach(
                stackTraceElement -> Arrays.asList(
                        stackTraceElement.getClass().getAnnotations()
                ).parallelStream().forEach(
                        annotation -> {
                            if(annotation.equals(DtsTransaction.class)) {
                                //获取事务组
                                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                                HttpServletRequest httpRequest = attrs.getRequest();
                                String transactionGroupId = httpRequest.getHeader("transactionGroupId");
                                if(Objects.isNull(transactionGroupId)){
                                    transactionGroupId = UUID.randomUUID().toString();
                                    httpRequest.setAttribute("transactionGroupId",transactionGroupId);
                                }

                                //发送请求，事务未结束
                                nettyClient.getChannel()
                                        .writeAndFlush(RpcRequest.builder()
                                                .transactionGroupId(transactionGroupId)
                                                .threadId(Thread.currentThread().getId())
                                                .build());

                                try {
                                    wait(timeout);
                                } catch (InterruptedException e) {
                                    platformTransactionManager.rollback(transactionStatus);
                                }

                                platformTransactionManager.commit(transactionStatus);

                            }
                        }
                )
        );

        return result;
    }
}