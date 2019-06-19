package com.doyuyu.server;

/**
 *@description: 业务消息
 *@author: songyuxiang
 *@date: 2019/5/23
 */
public interface BizMessage<M> {
    M getPayload();
}
