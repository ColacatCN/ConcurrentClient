package com.goldwind.ngsp.isolate.test.ConcurrentClient.enums;

import lombok.Getter;

@Getter
public enum StartupModeEnum {

    CONCURRENT_CLIENT_MODE("Concurrent", "Concurrent 客户端模式"),

    KAFKA_CLIENT_MODE("Kafka", "Kafka 客户端模式"),

    ;

    private final String key;

    private final String value;

    StartupModeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
