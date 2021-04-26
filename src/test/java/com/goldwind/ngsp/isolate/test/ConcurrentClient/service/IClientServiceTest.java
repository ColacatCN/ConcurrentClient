package com.goldwind.ngsp.isolate.test.ConcurrentClient.service;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl.ConcurrentClientServiceImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl.KafkaClientServiceImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class IClientServiceTest extends ApplicationTests {

    @Test
    public void testConcurrentClientServiceImpl() throws Exception {
        IClientService clientService = BeanUtil.getBean(ConcurrentClientServiceImpl.class);
        clientService.start();
        TimeUnit.SECONDS.sleep(300);
    }

    @Test
    public void testKafkaClientServiceImpl() throws Exception {
        IClientService clientService = BeanUtil.getBean(KafkaClientServiceImpl.class);
        clientService.start();
        TimeUnit.SECONDS.sleep(300);
    }

}
