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

    private String id;

    private Object data;

    @Override
    public String toString(){
        return "id:"+id+",Object:"+data.toString();
    }
}
