package com.doyuyu.common;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
public class RpcEncode extends MessageToByteEncoder {

    private Class<?> targetClass;

    public RpcEncode(Class<?> targetClass){
        this.targetClass = targetClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf out) throws Exception {
        if(targetClass.isInstance(message)){
            byte[] bytes = JSON.toJSONBytes(message);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
