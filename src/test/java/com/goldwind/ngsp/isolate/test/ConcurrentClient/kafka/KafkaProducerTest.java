package com.goldwind.ngsp.isolate.test.ConcurrentClient.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.ApplicationTests;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.TimeUnit;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.consts.ConcurrentClientConst.KAFKA_TOPIC;

@Slf4j
public class KafkaProducerTest extends ApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws JsonProcessingException, InterruptedException {
        kafkaTemplate.send(KAFKA_TOPIC, mapper.writeValueAsString(new KafkaMessage()))
                .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error(throwable.getMessage(), throwable);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> stringKafkaMessageSendResult) {
                        log.info("sendResult = {}.", stringKafkaMessageSendResult);
                    }
                });
        TimeUnit.SECONDS.sleep(60);
    }

}
