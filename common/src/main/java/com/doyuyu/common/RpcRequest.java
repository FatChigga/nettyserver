package com.doyuyu.common;

import lombok.*;

/**
 *
 * @author Song
 * @date 2019/4/3
 */

@Getter
@Builder
@AllArgsConstructor
public class RpcRequest {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 事务组ID
     */
    private String transactionGroupId;

    /**
     * 线程ID
     */
    private Long threadId;

    /**
     * 状态
     */
    private TransactionStatusEnum transactionStatus;

    @Override
    public String toString(){
        return "transactionGroupId:"+transactionGroupId+",threadId:"+threadId;
    }
}
