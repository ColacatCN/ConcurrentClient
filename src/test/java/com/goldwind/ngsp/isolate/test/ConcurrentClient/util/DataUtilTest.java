package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class DataUtilTest extends ApplicationTests {

    @Autowired
    private DataUtil dataUtil;

    @Test
    public void assembleData() {
        byte[] bytes = dataUtil.assembleData(10);
        assertEquals(10, bytes.length);
    }

}