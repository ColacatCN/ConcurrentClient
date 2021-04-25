package com.goldwind.ngsp.isolate.test.ConcurrentClient.enums;

import lombok.Getter;

@Getter
public enum ClientProtocolEnum {

    TCP("TCP", "TCP 协议"),

    UDP("UDP", "UDP 协议"),

    ;

    private final String key;

    private final String value;

    ClientProtocolEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
