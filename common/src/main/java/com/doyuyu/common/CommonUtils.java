package com.doyuyu.common;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @author songyuxiang
 * @description
 * @date 2019/8/29
 */
@Slf4j
public class CommonUtils {

    public static Object Byte2TargetClassMethod(ByteBuf byteBuf, Class<?> targetClass){
        int length = byteBuf.readInt();
        byte[] head = new byte[4];
        byteBuf.readBytes(head);
        String headString = new String(head);
        log.info("receive server message:{}",headString);
        byte[] body = new byte[length - 4];
        byteBuf.readBytes(body);
        String bodyString = new String(body);
        Object object = JSONObject.parseObject(bodyString, targetClass);
        return object;
    }

}
