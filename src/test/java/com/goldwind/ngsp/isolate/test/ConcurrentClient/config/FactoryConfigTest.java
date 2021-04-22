package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class FactoryConfigTest extends ApplicationTests {

    @Autowired
    private FactoryConfig factoryConfig;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private DataConfig dataConfig;

    @Test
    public void test() {
        assertNotNull(factoryConfig);
        log.info(factoryConfig.toString());
        assertNotNull(clientConfig);
        log.info(clientConfig.toString());
        assertNotNull(dataConfig);
        log.info(dataConfig.toString());
    }

}