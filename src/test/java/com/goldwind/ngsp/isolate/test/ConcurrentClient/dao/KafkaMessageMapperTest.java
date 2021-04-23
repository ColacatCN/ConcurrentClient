package com.goldwind.ngsp.isolate.test.ConcurrentClient.dao;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DateUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaMessageMapperTest extends ApplicationTests {

    @Autowired
    private KafkaMessageMapper mapper;

    @Test
    public void testInsertSelective() {
        KafkaMessage kafkaMessage = new KafkaMessage(1L, "Single", "1", 1L, DateUtil.now());
        mapper.insertSelective(kafkaMessage);
    }

}
