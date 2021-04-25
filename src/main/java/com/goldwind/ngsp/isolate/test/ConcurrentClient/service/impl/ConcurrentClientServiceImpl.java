package com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl.HttpClientFactoryImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl.NettyClientFactoryImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl.SocketClientFactoryImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.IClientService;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConcurrentClientServiceImpl implements IClientService {

    @Autowired
    private ClientConfig clientConfig;

    @Override
    public void start() throws Exception {
        AbstractClientFactory clientFactory;
        switch (clientConfig.getType()) {
            case HTTP:
                clientFactory = BeanUtil.getBean(HttpClientFactoryImpl.class);
                break;
            case SOCKET:
                clientFactory = BeanUtil.getBean(SocketClientFactoryImpl.class);
                break;
            default:
                clientFactory = BeanUtil.getBean(NettyClientFactoryImpl.class);
                break;
        }

        log.info("成功启动 ConcurrentClientService");
        clientFactory.sendMsg();
    }

}
