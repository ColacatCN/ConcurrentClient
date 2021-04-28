package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.RemoteConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler.Socks5CommandResponseHandler;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler.Socks5InitialResponseHandler;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler.UniversalHandler;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.ConfigUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.KafkaUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ClientEncoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponseDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum.TCP;

@Component
@Slf4j
public class NettyClientFactoryImpl extends AbstractClientFactory {

    @Autowired
    private KafkaUtil kafkaUtil;

    private final List<Channel> channelList = new ArrayList<>();

    @Override
    protected void createClient() {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            // 1. 编码器
                            pipeline.addFirst(Socks5ClientEncoder.DEFAULT);

                            // 2. 解码处理 Socks5InitialResponse
                            pipeline.addLast(new Socks5InitialResponseDecoder());
                            pipeline.addLast(new Socks5InitialResponseHandler());

                            // 3. 解码处理 Socks5CommandResponse
                            pipeline.addLast(new Socks5CommandResponseDecoder());
                            pipeline.addLast(new Socks5CommandResponseHandler(channelList, latch));

                            // 4. 通用解码器和 handler
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new UniversalHandler());
                        }
                    });
            bootstrap.connect(getProxyIP(), getProxyPort()).sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendMsg() throws Exception {
        initializeClientFactory();
        latch.await();

        for (Channel channel : channelList) {
            executorService.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] bytes = DataUtil.getMsg();

                    if (TCP.equals(getClientProtocol())) {
                        channel.writeAndFlush(bytes);
                    } else {
                        RemoteConfig remoteConfig;
                        if ((remoteConfig = ConfigUtil.getRemoteConfig()) != null) {
                            DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(bytes),
                                    new InetSocketAddress(remoteConfig.getHostname(), remoteConfig.getPort()),
                                    new InetSocketAddress(ConfigUtil.getRandomPort()));
                            channel.writeAndFlush(datagramPacket);
                        }
                    }

                    String channelId = channel.id().asLongText();
                    kafkaUtil.send(bytes, channelId);
                }
            });
        }

        shutdownClientFactory();
    }

}
