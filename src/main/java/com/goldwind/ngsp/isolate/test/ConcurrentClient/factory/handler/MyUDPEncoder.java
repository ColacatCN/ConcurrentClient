package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.ConfigUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.NetUtil;

import java.util.List;

public class MyUDPEncoder extends MessageToMessageEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) {
        ByteBuf input = (ByteBuf) msg;
        ByteBuf output = Unpooled.buffer(input.capacity() + 10);

        String appIP = ConfigUtil.getAppIP();
        int appPort = ConfigUtil.getAppPort();
        byte[] headerBytes = new byte[]{0x00, 0x00, 0x00, 0x01};
        byte[] ipBytes = NetUtil.createByteArrayFromIpAddressString(appIP);
        byte[] portBytes = DataUtil.intToByteArray(appPort);

        output.writeBytes(DataUtil.copyArray(headerBytes, ipBytes, portBytes));
        output.writeBytes(input);
        out.add(output);
    }

}

