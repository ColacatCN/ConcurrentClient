package com.goldwind.ngsp.isolate.test.ConcurrentClient.dao;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class KafkaMessageMapperTest extends ApplicationTests {

    @Autowired
    private KafkaMessageMapper mapper;

    @Test
    public void testInsertSelective() {
        KafkaMessage kafkaMessage = new KafkaMessage(1L, "Single", "1", 1L, new Date());
        mapper.insertSelective(kafkaMessage);
    }

}
