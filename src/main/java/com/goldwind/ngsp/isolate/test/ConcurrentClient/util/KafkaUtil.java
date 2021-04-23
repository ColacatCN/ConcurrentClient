package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ChannelTypeEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.consts.ConcurrentClientConst.KAFKA_TOPIC;

@Component
@Slf4j
public class KafkaUtil {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private ClientConfig clientConfig;

    @Value("${factory-config.channel-config.type}")
    protected ChannelTypeEnum channelType;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(byte[] bytes) throws JsonProcessingException {
        send(bytes, null);
    }

    public void send(byte[] bytes, String channelId) throws JsonProcessingException {
        KafkaMessage kafkaMessage;
        if (channelId != null) {
            kafkaMessage = new KafkaMessage(dataUtil.getGroupId(), channelType.getKey(), channelId, dataUtil.getMsgId(bytes), DateUtil.now());
        } else {
            kafkaMessage = new KafkaMessage(dataUtil.getGroupId(), channelType.getKey(), Thread.currentThread().getName(), dataUtil.getMsgId(bytes), DateUtil.now());
        }

        kafkaTemplate.send(KAFKA_TOPIC, clientConfig.getType().getKey(), objectMapper.writeValueAsString(kafkaMessage))
                .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error("Kafka 消息发送失败: " + throwable.getMessage(), throwable);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> sendResult) {
                        if (log.isDebugEnabled()) {
                            log.debug(sendResult.toString());
                        }
                    }

                });
    }

}
