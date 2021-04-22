package com.goldwind.ngsp.isolate.test.ConcurrentClient.dao;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.pojo.KafkaMessage;

public interface KafkaMessageMapper {

    int deleteByPrimaryKey(Long id);

    int insert(KafkaMessage record);

    int insertSelective(KafkaMessage record);

    KafkaMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(KafkaMessage record);

    int updateByPrimaryKey(KafkaMessage record);

}
