package com.doyuyu.common;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * @author Song
 * @date 2019/4/3
 */
@Slf4j
public class RpcDecode extends ByteToMessageDecoder{

    private Class<?> targetClass;

    private Integer readableBytesLength = 4;

    public RpcDecode(Class<?> targetClass){
        this.targetClass = targetClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        if(in.readableBytes() < readableBytesLength){
            return;
        }

        in.markReaderIndex();
        int dataLength = in.readInt();

        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object object = JSON.parseObject(data,targetClass);

        list.add(object);
    }
}
