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

    private final static Integer readableBytesLength = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < readableBytesLength){
            return;
        }
        //readerIndex 为读取索引,表示已经被读取的字节数
        int beginIndex = in.readerIndex();
        //读四位,消息的长度,通常用4个字节保存
        int length = in.readInt();
        //readableBytes 返回可读的字节数
        if(in.readableBytes() < length){
            in.readerIndex(beginIndex);
            return;
        }

        //decode方法的源码注释如下：This method will be called till either the input has nothing to read.
        // 意思是说：ByteBuf对象的数据没有读完的话，decode方法会一直调用,判断的标准writerIndex > readerIndex
        in.readerIndex(beginIndex + readableBytesLength + length);

        //当decode方法执行完后,会释放bufferIn这个缓冲区,
        // 如果将执行完释放操作的bufferIn传递给下个处理器的话,
        // 一旦下个处理器调用bufferIn的读或者写的方法时,会立刻报出IllegalReferenceCountException异常的。
        // 因此slice操作后,必须加上一个retain操作,让bufferIn的引用计数器加1,这样ByteToMessageDecoder会刀下留人,先不释放bufferIn。
        ByteBuf otherByteBufRef = in.slice(beginIndex, readableBytesLength + length);
        otherByteBufRef.retain();
        out.add(otherByteBufRef);
    }
}
