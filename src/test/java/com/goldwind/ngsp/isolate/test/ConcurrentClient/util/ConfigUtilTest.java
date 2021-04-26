package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ConfigUtilTest extends ApplicationTests {

    @Test
    public void test() {
        ClientTypeEnum clientType = ConfigUtil.getClientType();
        log.info(clientType.getKey());

        ClientProtocolEnum clientProtocol = ConfigUtil.getClientProtocol();
        log.info(clientProtocol.getKey());

        int randomPort = ConfigUtil.getRandomPort();
        log.info("randomPort = {}.", randomPort);
    }

}
