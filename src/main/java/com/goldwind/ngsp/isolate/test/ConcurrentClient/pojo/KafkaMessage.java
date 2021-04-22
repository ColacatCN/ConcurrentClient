package com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ChannelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessage {

    private long groupId;

    private ChannelTypeEnum channelType;

    private long msgId;

    private String channelId;

    private Date updateTime;

}
