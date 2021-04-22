package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class SocketClientFactoryImpl extends AbstractClientFactory {

    private final List<Socket> socketList = new ArrayList<>();

    @Override
    protected void createClient(String proxyIP, int proxyPort) throws IOException {
        Proxy socks5Proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));
        Socket socket = new Socket(socks5Proxy);
        socket.connect(new InetSocketAddress(clientConfig.getAppIP(), clientConfig.getAppPort()));
        socketList.add(socket);
    }

    @Override
    public void sendMsg(Object msg) throws Exception {
        initializeClientFactory();
        for (Socket socket : socketList) {
            executorService.submit(() -> {
                for (; ; ) {
                    try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                         DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
                        byte[] bytes = (byte[]) msg;
                        if (log.isDebugEnabled()) {
                            log.debug(Thread.currentThread().getName() + ": " + Arrays.toString(bytes));
                        }
                        // TODO: 埋点
                        outputStream.write(bytes);

                        byte[] response = new byte[bytes.length];
                        int numOfBytes = inputStream.read(response);
                        if (numOfBytes != -1) {
                            // TODO: 埋点
                        }
                    }
                }
            });
        }
    }

}
