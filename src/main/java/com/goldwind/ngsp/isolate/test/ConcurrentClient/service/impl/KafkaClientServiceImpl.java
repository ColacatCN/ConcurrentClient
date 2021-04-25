package com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.dao.KafkaMessageMapper;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.IClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaClientServiceImpl implements IClientService {

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private KafkaMessageMapper kafkaMessageMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start() {
        kafkaListenerEndpointRegistry.start();
        log.info("成功启动 KafkaClientService");
    }

    @KafkaListener(topics = {"topic-isolate"}, autoStartup = "false")
    public void receiveMsg(String msg) throws JsonProcessingException {
        if (log.isDebugEnabled()) {
            log.debug("msg = {}.", msg);
        }
        KafkaMessage kafkaMessage = objectMapper.readValue(msg, KafkaMessage.class);
        kafkaMessageMapper.insertSelective(kafkaMessage);
    }

}
