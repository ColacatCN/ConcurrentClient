package com.goldwind.ngsp.isolate.test.ConcurrentClient.service;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl.ConcurrentClientServiceImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Ignore
public class IConcurrentClientServiceTest extends ApplicationTests {

    @Test
    public void test() throws Exception {
        IClientService clientService = BeanUtil.getBean(ConcurrentClientServiceImpl.class);
        clientService.start();
        TimeUnit.SECONDS.sleep(60);
    }

}
