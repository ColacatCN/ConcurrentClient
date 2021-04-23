package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.KafkaUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class UniversalHandler extends SimpleChannelInboundHandler<byte[]> {

    private final KafkaUtil kafkaUtil = BeanUtil.getBean(KafkaUtil.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] bytes) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(Thread.currentThread().getName() + " 接收数据: " + Arrays.toString(bytes));
        }
        kafkaUtil.send(bytes);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        throw new ClientException("channel 已关闭");
    }

}
