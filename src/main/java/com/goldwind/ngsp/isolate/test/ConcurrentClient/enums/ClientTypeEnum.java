package com.goldwind.ngsp.isolate.test.ConcurrentClient.enums;

import lombok.Getter;

@Getter
public enum ClientTypeEnum {

    NETTY("Netty Client", "Netty 客户端"),

    HTTP("Http Client", "Http 客户端"),

    SOCKET("Socket Client", "Socket 客户端"),

    ;

    private final String key;

    private final String value;

    ClientTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
