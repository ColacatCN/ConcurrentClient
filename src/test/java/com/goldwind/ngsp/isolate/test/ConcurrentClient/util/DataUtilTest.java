package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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

}