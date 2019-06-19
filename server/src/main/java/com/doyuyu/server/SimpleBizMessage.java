package com.doyuyu.server;

import lombok.Builder;
import lombok.Getter;

/**
 * @author songyuxiang
 * @description
 * @date 2019/5/21
 */

@Builder
@Getter
public class SimpleBizMessage<M> implements BizMessage<M> {

    private M payload;

    @Override
    public M getPayload() {
        return payload;
    }
}
