package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.DataConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ChannelTypeEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.DataTypeEnum.BYTE;

@Component
public abstract class AbstractClientFactory {

    @Autowired
    protected ClientConfig clientConfig;

    @Autowired
    protected DataConfig dataConfig;

    @Value("${global-config.channel-config.type}")
    protected ChannelTypeEnum channelType;

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

    protected byte[] getMsg() {
        byte[] bytes;
        if (BYTE.equals(dataConfig.getType())) {
            int dataSize = dataConfig.getSize();
            bytes = DataUtil.assembleData(dataSize);
        } else {
            String filePath = dataConfig.getPath();
            bytes = DataUtil.assembleData(filePath);
        }
        return bytes;
    }

    protected abstract void createClient(String proxyIP, int proxyPort) throws IOException;

    public abstract void sendMsg() throws Exception;

}
