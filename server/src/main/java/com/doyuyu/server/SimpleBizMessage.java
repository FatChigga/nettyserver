package com.doyuyu.server;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/21
 */
public class SimpleBizMessage<M> implements BizMessage<M> {

    private M payload;

    @Override
    public M getPayload() {
        return payload;
    }
}
