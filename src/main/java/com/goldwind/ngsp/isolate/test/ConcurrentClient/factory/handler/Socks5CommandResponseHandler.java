package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class Socks5CommandResponseHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandResponse> {

    private final CountDownLatch latch;

    public Socks5CommandResponseHandler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandResponse socks5CommandResponse) throws Exception {
        if (socks5CommandResponse.decoderResult().isSuccess()) {
            if (Socks5CommandStatus.SUCCESS.equals(socks5CommandResponse.status())) {
                latch.countDown();
                log.info("SOCKS5 建立成功");
            } else {
                throw new ClientException("SOCKS5 建立失败");
            }
        } else {
            throw new ClientException("Socks5CommandResponse 解码失败");
        }
    }

}
