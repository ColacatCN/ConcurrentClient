package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientTypeEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClientFactory {

    private final ClientConfig clientConfig = BeanUtil.getBean(ClientConfig.class);

    protected ExecutorService executorService;

    protected CountDownLatch latch;

    protected void initializeClientFactory() throws Exception {
        String clientType = getClientType().getKey().toLowerCase();
        int amountOfClient = getClientAmount();
        executorService = new ThreadPoolExecutor(amountOfClient, amountOfClient,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ClientThreadFactory(clientType));
        latch = new CountDownLatch(amountOfClient);

        for (int i = 0; i < amountOfClient; i++) {
            createClient();
        }
    }

    protected abstract void createClient() throws Exception;

    public abstract void sendMsg() throws Exception;

    protected ClientTypeEnum getClientType() {
        return clientConfig.getType();
    }

    protected ClientProtocolEnum getClientProtocol() {
        return clientConfig.getProtocol();
    }

    protected int getClientAmount() {
        return clientConfig.getAmount();
    }

    protected String getProxyIP() {
        return clientConfig.getProxyIP();
    }

    protected int getProxyPort() {
        return clientConfig.getProxyPort();
    }

    protected String getAppIP() {
        return clientConfig.getAppIP();
    }

    protected int getAppPort() {
        return clientConfig.getAppPort();
    }

}
