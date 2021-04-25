/**
 * Copyright © 2021 金风科技. All rights reserved.
 *
 * @Title: MyEncoder.java
 * @Prject: socks5-netty-master
 * @Package: netty
 * @Description: 自定义编码器
 * @author: SF
 * @date: 2021年03月01日
 * @version: V1.0
 */
package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.codec;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.socksx.v5.Socks5Message;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: MyEncoder
 * @Description: 自定义编码器
 * @author: SF
 * @date: 2021年03月01日 19:02:15
 */
@Slf4j
public class MyEncoder2 extends MessageToByteEncoder<Object> {

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error accour
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        if (msg instanceof Socks5Message) {
            ctx.writeAndFlush(msg);
        } else {

            // 由于反向隔离是接收到完整到字节数组之后，写入到E文件中，没有进行反序列化操作，所以，这里在把数据往外发送时，也不需要序列化操作。
            byte[] body = SerializingUtil.serialize(msg);

            //读取消息的长度
            int dataLength = body.length;

            //先将消息长度写入，也就是消息头
            out.writeInt(dataLength);

            //消息体中包含我们要发送的数据
            out.writeBytes(body);
        }
    }
}
