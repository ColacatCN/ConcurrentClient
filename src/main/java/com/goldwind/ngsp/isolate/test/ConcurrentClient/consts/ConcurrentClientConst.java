package com.goldwind.ngsp.isolate.test.ConcurrentClient.consts;

import okhttp3.MediaType;

public class ConcurrentClientConst {

    public static final String HTTP_CLIENT_URL = "http://%s:%d%s";

    public static final MediaType TEXT_PLAIN = MediaType.parse("text/plain");

    public static final String KAFKA_TOPIC = "topic-isolate";

}
