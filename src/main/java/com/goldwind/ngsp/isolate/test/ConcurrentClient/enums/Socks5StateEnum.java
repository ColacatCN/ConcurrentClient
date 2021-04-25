package com.goldwind.ngsp.isolate.test.ConcurrentClient.enums;

import lombok.Getter;

@Getter
public enum Socks5StateEnum {

    INIT("Socks5 Init", "Socks5 请求握手"),

    SUCCESS("Socks5 Success", "Socks5 请求命令"),

    FAIL("Socks5 Fail", "Socks5 请求失败"),

    ;

    private final String key;

    private final String value;

    Socks5StateEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
