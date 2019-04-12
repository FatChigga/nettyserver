package com.doyuyu.transaction;

import com.doyuyu.client.NettyClient;
import com.doyuyu.common.RpcRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;

/**
 *
 * @author Song
 * @date 2019/4/12
 */

@Component
public class TransactionUtil {

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Autowired
    NettyClient nettyClient;

    public Object transact(Object targetClass,Method method,Object[] param) throws Exception{
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            Object result = method.invoke(targetClass,param);

            platformTransactionManager.commit(transactionStatus);

            return result;
        }catch (Exception e){
            platformTransactionManager.rollback(transactionStatus);
            throw e;
        }
    }
}
