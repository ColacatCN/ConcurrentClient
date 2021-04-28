package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.consts.ConcurrentClientConst.HTTP_CLIENT_URL;
import static com.goldwind.ngsp.isolate.test.ConcurrentClient.consts.ConcurrentClientConst.TEXT_PLAIN;

@Component
@Slf4j
public class HttpClientFactoryImpl extends AbstractClientFactory {

    @Autowired
    private KafkaUtil kafkaUtil;

    private final List<OkHttpClient> httpClientList = new ArrayList<>();

    @Override
    protected void createClient() {
        Proxy socks5Proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(getProxyIP(), getProxyPort()));
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(10 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(10 * 60 * 1000, TimeUnit.MILLISECONDS)
                .proxy(socks5Proxy)
                .build();
        httpClientList.add(httpClient);
    }

    @Override
    public void sendMsg() throws Exception {
        initializeClientFactory();

        for (OkHttpClient httpClient : httpClientList) {
            executorService.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] requestBytes = DataUtil.getMsg();
                    Request request = new Request.Builder()
                            .url(String.format(HTTP_CLIENT_URL, getAppIP(), getAppPort(), getClientBaseUrl()))
                            .post(RequestBody.create(TEXT_PLAIN, requestBytes))
                            .build();
                    httpClient.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            log.error(e.getMessage(), e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                log.error("无法访问目标 HTTP 服务, 状态码: {}.", response.code());
                            } else {
                                ResponseBody responseBody;
                                if ((responseBody = response.body()) != null) {
                                    byte[] responseBytes = responseBody.bytes();
                                    kafkaUtil.send(responseBytes);
                                }
                            }
                        }

                    });
                    kafkaUtil.send(requestBytes);
                }
            });
        }

        shutdownClientFactory();
    }

}
