package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public abstract class AbstractClientFactory {

    @Autowired
    protected ClientConfig clientConfig;

    protected ExecutorService executorService;

    protected CountDownLatch latch;

    protected void initializeClientFactory() throws IOException {
        String clientType = clientConfig.getType().getKey().toLowerCase();
        int amountOfClient = clientConfig.getAmount();
        executorService = new ThreadPoolExecutor(amountOfClient, amountOfClient,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ClientThreadFactory(clientType));
        latch = new CountDownLatch(amountOfClient);

        for (int i = 0; i < amountOfClient; i++) {
            createClient(clientConfig.getProxyIP(), clientConfig.getProxyPort());
        }
    }

    protected abstract void createClient(String proxyIP, int proxyPort) throws IOException;

    public abstract void sendMsg() throws Exception;

}
