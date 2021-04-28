package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.codec;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyDecoder2 extends ByteToMessageDecoder {

    private static final int HEAD_LENGTH = 4;

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if (in.readableBytes() < HEAD_LENGTH) {
            return;
        }

        in.markReaderIndex();

        int dataLength = in.readInt();

        if (dataLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[dataLength];
        in.readBytes(body);

        Object o = SerializingUtil.deserialize(body);
        out.add(o);
    }

}
