package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.codec;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.socksx.v5.Socks5Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyEncoder2 extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        if (msg instanceof Socks5Message) {
            ctx.writeAndFlush(msg);
        } else {

            byte[] body = SerializingUtil.serialize(msg);

            int dataLength = body.length;

            out.writeInt(dataLength);

            out.writeBytes(body);
        }
    }

}
