package com.goldwind.ngsp.isolate.test.ConcurrentClient.dao;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;

import java.util.List;

public interface KafkaMessageMapper {

    int deleteByPrimaryKey(Long id);

    int insert(KafkaMessage record);

    int insertSelective(KafkaMessage record);

    KafkaMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(KafkaMessage record);

    int updateByPrimaryKey(KafkaMessage record);

    List<KafkaMessage> selectByGroupId(Long groupId);

    List<KafkaMessage> selectByGroupIdAndChannelId(Long groupId, String channelId);

}
