package com.goldwind.ngsp.isolate.test.ConcurrentClient.service;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class IConcurrentClientServiceTest extends ApplicationTests {

    @Autowired
    private IConcurrentClientService concurrentClientService;

    @Test
    public void test() throws Exception {
        concurrentClientService.start();
        TimeUnit.SECONDS.sleep(60);
    }

}
