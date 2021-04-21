package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataUtilTest {

    @Test
    public void assembleData() {
        byte[] bytes = DataUtil.assembleData(10);
        assertEquals(10, bytes.length);
    }

}