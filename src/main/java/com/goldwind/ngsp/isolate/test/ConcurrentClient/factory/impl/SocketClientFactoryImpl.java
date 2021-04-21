package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketClientFactoryImpl extends AbstractClientFactory {

    @Override
    protected void createClient(String proxyIP, int proxyPort) {

    }

    @Override
    public void sendMsg(Object msg) throws Exception {

    }

}
