package com.doyuyu.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 *
 * @author Song
 * @date 2019/4/23
 */

@Configuration
public class DtsConfiguration {
    @Bean
    public TransactionThreadGroup transactionThreadGroup(){
        return new TransactionThreadGroup("Dts");
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadFactory tractionThreadFactory
                = new ThreadFactoryBuilder().setNameFormat("traction-pool-%d").build();

        RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
            throw new RejectedExecutionException("Task"+r.toString()+"reject from"+executor.toString());
        };

        return new ThreadPoolExecutor(
                20,
                100,
                0L,
                 TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                tractionThreadFactory,
                rejectedExecutionHandler);
    }
}
