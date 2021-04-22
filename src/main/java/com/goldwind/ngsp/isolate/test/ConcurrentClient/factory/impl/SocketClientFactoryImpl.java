package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.consts.ConcurrentClientConst.KAFKA_TOPIC;

@Component
@Slf4j
public class SocketClientFactoryImpl extends AbstractClientFactory {

    @Autowired
    private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    private final List<Socket> socketList = new ArrayList<>();

    @Override
    protected void createClient(String proxyIP, int proxyPort) throws IOException {
        Proxy socks5Proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));
        Socket socket = new Socket(socks5Proxy);
        socket.connect(new InetSocketAddress(clientConfig.getAppIP(), clientConfig.getAppPort()));
        socketList.add(socket);
    }

    @Override
    public void sendMsg() throws Exception {
        initializeClientFactory();
        for (Socket socket : socketList) {
            executorService.submit(() -> {
                for (; ; ) {
                    try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                         DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
                        // 发送数据
                        byte[] bytes = getMsg();
                        if (log.isDebugEnabled()) {
                            log.debug(Thread.currentThread().getName() + "发送数据: " + Arrays.toString(bytes));
                        }
                        outputStream.write(bytes);
                        kafkaTemplate.send(KAFKA_TOPIC, clientConfig.getType().getKey(), new KafkaMessage(DataUtil.getGroupId(), channelType.getKey(), null, DataUtil.getMsgId(bytes), new Date()));

                        // 接收数据
                        byte[] response = new byte[bytes.length];
                        int numOfBytes = inputStream.read(response);
                        if (numOfBytes != -1) {
                            if (log.isDebugEnabled()) {
                                log.debug(Thread.currentThread().getName() + "接收数据: " + Arrays.toString(bytes));
                            }
                            kafkaTemplate.send(KAFKA_TOPIC, clientConfig.getType().getKey(), new KafkaMessage(DataUtil.getGroupId(), channelType.getKey(), null, DataUtil.getMsgId(bytes), new Date()));
                        } else {
                            throw new ClientException("Socket 客户端没有收到响应数据");
                        }
                    }
                }
            });
        }
    }

}
