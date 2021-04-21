package com.goldwind.ngsp.isolate.test.ConcurrentClient.enums;

import lombok.Getter;

@Getter
public enum DataTypeEnum {

    FILE("File", "文件数据"),

    BYTE("Byte", "字节数据"),

    ;

    private final String key;

    private final String value;

    DataTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
