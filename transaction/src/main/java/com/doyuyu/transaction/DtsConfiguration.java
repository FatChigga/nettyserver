package com.doyuyu.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 *
 * @author Song
 * @date 2019/4/12
 */

@Aspect
@Component
public class DtsConfiguration {

    @Autowired
    private TransactionUtil transactionUtil;

    @Pointcut("@annotation(com.doyuyu.transaction.DtsTransaction)")
    public void transactionLog(){}

    @Around("transactionLog()")
    public Object transactionAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;

        Object target = proceedingJoinPoint.getTarget();
        Method method = target.getClass().getMethod(methodSignature.getName(),methodSignature.getParameterTypes());
        Object[] param = proceedingJoinPoint.getArgs();

        Object object = transactionUtil.transact(target,method,param);

        return object;
    }
}
