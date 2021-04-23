package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler.Socks5CommandResponseHandler;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler.Socks5InitialResponseHandler;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.handler.UniversalHandler;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.KafkaUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.socksx.v5.Socks5ClientEncoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponseDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class NettyClientFactoryImpl extends AbstractClientFactory {

    @Autowired
    private KafkaUtil kafkaUtil;

    @Autowired
    private DataUtil dataUtil;

    private final List<Channel> channelList = new ArrayList<>();

    @Override
    protected void createClient(String proxyIP, int proxyPort) {
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
                            pipeline.addFirst(new ByteArrayEncoder());

                            // 2. 解码处理 Socks5InitialResponse
                            pipeline.addLast(new Socks5InitialResponseDecoder());
                            pipeline.addLast(new Socks5InitialResponseHandler(clientConfig.getAppIP(), clientConfig.getAppPort()));

                            // 3. 解码处理 Socks5CommandResponse
                            pipeline.addLast(new Socks5CommandResponseDecoder());
                            pipeline.addLast(new Socks5CommandResponseHandler(latch));

                            // 4. 通用解码器和 handler
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new UniversalHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(proxyIP, proxyPort).sync();
            channelList.add(channelFuture.channel());
            log.info("Succeed to connect to inner proxy {}:{}.", proxyIP, proxyPort);
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
                for (; ; ) {
                    byte[] bytes = dataUtil.getMsg();
                    if (log.isDebugEnabled()) {
                        log.debug(Thread.currentThread().getName() + " 发送数据: " + Arrays.toString(bytes));
                    }
                    channel.writeAndFlush(bytes);
                    String channelId = channel.id().asLongText();
                    kafkaUtil.send(bytes, channelId);
                }
            });
        }
    }

}
