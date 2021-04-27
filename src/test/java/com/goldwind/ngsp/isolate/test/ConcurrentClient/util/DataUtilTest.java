package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

@Slf4j
public class DataUtilTest extends ApplicationTests {

    @Test
    public void testAssembleData() {
        byte[] bytes = DataUtil.getMsg();
        assertEquals(1024, bytes.length);
    }

    @Test
    public void testGetGroupId() {
        long groupId1 = DataUtil.getGroupId();
        long groupId2 = DataUtil.getGroupId();
        assertEquals(groupId1, groupId2);
    }

    @Test
    public void testGetMsgId() {
        byte[] bytes = DataUtil.getMsg();
        long msgId = DataUtil.getMsgId(bytes);
        log.info("msgId = {}.", msgId);
    }

    @Test
    public void testDeserializePort() {
        byte[] bytes = DataUtil.portToByteArray(6060);
        log.info(Arrays.toString(bytes));

        int port = byteArrayToPort(new byte[]{bytes[0], bytes[1]});
        log.info("port = {}.", port);
    }

    private static int byteArrayToPort(byte[] byteArray) {
        int value = 0;
        for (int i = 0; i < byteArray.length; i++) {
            int shift = (1 - i) * 8;
            value += (byteArray[i] & 0xFF) << shift;
        }
        return value;
    }

}