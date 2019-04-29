package com.doyuyu.client;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Song
 * @date 2019/4/12
 */

@Aspect
@Component
public class DtsAopConfiguration {

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Pointcut("@annotation(DtsTransaction)")
    public void transactionLog(){}

    @Around("transactionLog()")
    public Object transactionAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;

        Object target = proceedingJoinPoint.getTarget();
        Method method = target.getClass().getMethod(methodSignature.getName(),methodSignature.getParameterTypes());
        DtsTransaction dtsTransaction = method.getAnnotation(DtsTransaction.class);
        Object[] param = proceedingJoinPoint.getArgs();

        TransactionEvent transactionEvent =
                new TransactionEvent(target,method,param,dtsTransaction.timeout());

        Future future = threadPoolExecutor.submit(transactionEvent);

        return future.get();
    }
}
