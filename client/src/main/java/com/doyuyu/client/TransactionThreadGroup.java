package com.doyuyu.client;

import java.util.Objects;

/**
 *
 * @author Song
 * @date 2019/4/23
 */
public class TransactionThreadGroup extends ThreadGroup{

    private Thread[] threads;

    public TransactionThreadGroup(String name) {
        super(name);
        threads = new Thread[0];
    }

    public void setThreads(Thread[] threads){
        if(Objects.isNull(threads)){
            this.threads = null;
        }else{
            this.threads = threads.clone();
        }
    }

    public Thread[] getThreads(){
        if(Objects.isNull(threads)){
            return null;
        }
        return threads.clone();
    }
}
