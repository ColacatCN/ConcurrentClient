package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientTypeEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DateUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClientFactory {

    private ClientConfig clientConfig;

    private long startTime;

    protected ExecutorService executorService;

    protected CountDownLatch latch;

    protected void initializeClientFactory() throws Exception {
        clientConfig = BeanUtil.getBean(ClientConfig.class);

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
        startTime = DateUtil.now();
    }

    protected void shutdownClientFactory() {
        long timeout = getClientTimeout();
        for (; ; ) {
            if ((DateUtil.now() - startTime) >= timeout) {
                executorService.shutdownNow();
                break;
            }
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

    protected String getClientBaseUrl() {
        return clientConfig.getBaseUrl();
    }

    protected int getClientAmount() {
        return clientConfig.getAmount();
    }

    protected long getClientTimeout() {
        return clientConfig.getTimeout();
    }

    protected String getProxyIP() {
        return clientConfig.getProxy().getHostname();
    }

    protected int getProxyPort() {
        return clientConfig.getProxy().getPort();
    }

    protected String getAppIP() {
        return clientConfig.getApp().getHostname();
    }

    protected int getAppPort() {
        return clientConfig.getApp().getPort();
    }

}
