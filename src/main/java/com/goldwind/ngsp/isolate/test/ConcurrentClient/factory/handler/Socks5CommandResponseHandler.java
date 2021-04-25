package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.codec.MyEncoder2;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum.TCP;

@Slf4j
public class Socks5CommandResponseHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandResponse> {

    private final ClientConfig clientConfig = BeanUtil.getBean(ClientConfig.class);

    private final List<Channel> channelList;

    private final CountDownLatch latch;

    public Socks5CommandResponseHandler(List<Channel> channelList, CountDownLatch latch) {
        this.channelList = channelList;
        this.latch = latch;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandResponse socks5CommandResponse) throws Exception {
        if (socks5CommandResponse.decoderResult().isSuccess()) {
            if (Socks5CommandStatus.SUCCESS.equals(socks5CommandResponse.status())) {
                if (TCP.equals(clientConfig.getProtocol())) {
                    channelList.add(ctx.channel());
                    ctx.channel().pipeline().addFirst(new MyEncoder2());
                } else {
                    int bindPort = socks5CommandResponse.bndPort();
                    createUDPClient(bindPort);
                }
                latch.countDown();
                log.info("SOCKS5 建立成功");
            } else {
                throw new ClientException("SOCKS5 建立失败");
            }
        } else {
            throw new ClientException("Socks5CommandResponse 解码失败");
        }
    }

    private void createUDPClient(int bindPort) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addFirst(new MyEncoder2());
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new UniversalHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(clientConfig.getProxyIP(), bindPort).sync();
            channelList.add(channelFuture.channel());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }

}
