package com.goldwind.ngsp.isolate.test.ConcurrentClient.enums;

import lombok.Getter;

@Getter
public enum ChannelTypeEnum {

    SINGLE("Single", "单通道"),

    DUAL("Dual", "双通道"),

    ;

    private final String key;

    private final String value;

    ChannelTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
