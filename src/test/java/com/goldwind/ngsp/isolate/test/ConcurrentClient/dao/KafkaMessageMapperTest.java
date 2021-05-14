package com.goldwind.ngsp.isolate.test.ConcurrentClient.dao;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DateUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Ignore
@Transactional
public class KafkaMessageMapperTest extends ApplicationTests {

    @Autowired
    private KafkaMessageMapper mapper;

    @Test
    public void testInsertSelective() {
        KafkaMessage kafkaMessage = new KafkaMessage(1L, "Single", "1", 1L, DateUtil.now());
        mapper.insertSelective(kafkaMessage);
    }

    @Test
    public void testSelectByGroupId() {
        List<KafkaMessage> kafkaMessageList = mapper.selectByGroupId(-1822537257L);
        assertEquals(692, kafkaMessageList.size());
    }

    @Test
    public void testSelectByGroupIdAndChannelId() {
        List<KafkaMessage> kafkaMessageList = mapper.selectByGroupIdAndChannelId(-1822537257L, "ClientThreadFactory-socket-client-1");
        assertEquals(67, kafkaMessageList.size());
    }

    @Test
    public void testBatchInsert() {
        List<KafkaMessage> kafkaMessageList = new ArrayList<>();
        KafkaMessage kafkaMessage1 = new KafkaMessage(1L, "Single", "1", 1L, DateUtil.now());
        kafkaMessageList.add(kafkaMessage1);
        KafkaMessage kafkaMessage2 = new KafkaMessage(1L, "Single", "2", 2L, DateUtil.now());
        kafkaMessageList.add(kafkaMessage2);
        mapper.batchInsert(kafkaMessageList);
    }

}
