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
public class RpcResponse {
    private String id;

    private Object data;

    private MessageStatusEnum joinStatusEnum;

    private MessageStatusEnum commitStatusEnum;

    private Long threadId;
}
