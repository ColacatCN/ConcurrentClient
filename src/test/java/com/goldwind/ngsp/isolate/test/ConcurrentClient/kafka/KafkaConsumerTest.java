package com.goldwind.ngsp.isolate.test.ConcurrentClient.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.dao.KafkaMessageMapper;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Ignore
@Component
@Slf4j
public class KafkaConsumerTest extends ApplicationTests {

    @Autowired
    private KafkaMessageMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(60);
    }

    @KafkaListener(topics = {"topic-isolate"})
    public void receiveMsg(String msg) throws JsonProcessingException {
        log.info("msg = {}.", msg);
        KafkaMessage kafkaMessage = objectMapper.readValue(msg, KafkaMessage.class);
        mapper.insertSelective(kafkaMessage);
    }

}
