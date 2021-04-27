package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.ConfigUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum.TCP;

public class Socks5InitialResponseHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Socks5InitialRequest socks5InitialRequest = new DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH);
        ctx.writeAndFlush(socks5InitialRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialResponse socks5InitialResponse) throws Exception {
        if (socks5InitialResponse.decoderResult().isSuccess()) {
            if (Socks5AuthMethod.NO_AUTH.equals(socks5InitialResponse.authMethod())) {
                Socks5CommandRequest socks5CommandRequest;
                if (TCP.equals(ConfigUtil.getClientProtocol())) {
                    socks5CommandRequest = new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, Socks5AddressType.IPv4, ConfigUtil.getAppIP(), ConfigUtil.getAppPort());
                } else {
                    socks5CommandRequest = new DefaultSocks5CommandRequest(Socks5CommandType.UDP_ASSOCIATE, Socks5AddressType.IPv4, ConfigUtil.getAppIP(), ConfigUtil.getAppPort());
                }
                ctx.writeAndFlush(socks5CommandRequest);
            } else {
                throw new ClientException("Socks5InitialResponse 认证失败");
            }
        } else {
            throw new ClientException("Socks5InitialResponse 解码失败");
        }
    }

}
