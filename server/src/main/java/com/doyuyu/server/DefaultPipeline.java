package com.doyuyu.server;

import com.doyuyu.common.RpcRequest;
import com.doyuyu.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author songyuxiang
 * @description
 * @date 2019/6/20
 */
@Component("pipeline")
@Scope("prototype")
@Slf4j
public class DefaultPipeline implements Pipeline,ApplicationContextAware,InitializingBean{

    private static final Handler DEFAULT_HANDLER = new Handler() {};

    public ApplicationContext context;

    /** 创建一个头结点和尾节点，这两个节点内部没有做任何处理，只是默认的将每一层级的链往下传递，
    这里头结点和尾节点的主要作用就是用于标志整个链的首尾，所有的业务节点都在这两个节点中间 **/
    private HandlerContext head;
    private HandlerContext tail;

    private Object object;
    private RpcRequest rpcRequest;
    private ChannelHandlerContext ctx;

    public DefaultPipeline(Object object,ChannelHandlerContext ctx){
        this.ctx = ctx;
        this.object = object;
    }

    @Override
    public Pipeline taskReceived() {
        log.info("receive message from client:{}",rpcRequest.toString());
        if(object instanceof RpcRequest){
            this.rpcRequest = (RpcRequest)object;
        }else{
            throw new TransactionServerException("message type is wrong");
        }
        return this;
    }

    @Override
    public Pipeline taskFiltered() {
        log.info("filter message from client:{}",rpcRequest.toString());
        if(Objects.isNull(rpcRequest)){
            throw new TransactionServerException("RpcRequest is null");
        }
        if(Objects.isNull(rpcRequest.getTransactionStatus())){
            throw new TransactionServerException("TransactionStatus is null");
        }
        if(Objects.isNull(rpcRequest.getThreadId())){
            throw new TransactionServerException("ThreadId is null");
        }
        if(Objects.isNull(rpcRequest.getTransactionGroupId())){
            throw new TransactionServerException("TransactionGroupId is null");
        }
        return this;
    }

    @Override
    public Pipeline taskExecuted() {
        HandlerContext.invokeTaskExecuted(head,rpcRequest);
        return this;
    }

    @Override
    public Pipeline afterCompletion() {
        HandlerContext.invokeAfterCompletion(head);
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        head = newContext(DEFAULT_HANDLER);
        tail = newContext(DEFAULT_HANDLER);
        head.next = tail;
        tail.prev = head;
    }

    /** 用于往Pipeline中添加节点的方法，读者朋友也可以实现其他的方法用于进行链的维护 **/
    void addLast(Handler handler) {
        HandlerContext handlerContext = newContext(handler);
        tail.prev.next = handlerContext;
        handlerContext.prev = tail.prev;
        handlerContext.next = tail;
        tail.prev = handlerContext;
    }

    /** 使用默认的Handler初始化一个HandlerContext **/
    private HandlerContext newContext(Handler handler) {
        HandlerContext context = this.context.getBean(HandlerContext.class);
        context.handler = handler;
        context.setChannelHandlerContext(ctx);
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
