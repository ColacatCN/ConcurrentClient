package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateUtil {

    public static long now() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

}
