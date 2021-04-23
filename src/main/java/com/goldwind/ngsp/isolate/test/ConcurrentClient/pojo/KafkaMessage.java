package com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessage {

    private Long groupId;

    private String channelType;

    private String channelId;

    private Long msgId;

    private Long updateTime;

}
