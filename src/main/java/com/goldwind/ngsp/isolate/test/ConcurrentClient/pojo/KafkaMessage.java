package com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KafkaMessage {

    private Long id;

    private Long groupId;

    private String channelType;

    private String channelId;

    private Long msgId;

    private Long updateTime;

    public KafkaMessage(Long groupId, String channelType, String channelId, Long msgId, Long updateTime) {
        this.groupId = groupId;
        this.channelType = channelType;
        this.channelId = channelId;
        this.msgId = msgId;
        this.updateTime = updateTime;
    }

}
