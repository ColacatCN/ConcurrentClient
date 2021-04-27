package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "factory-config.client-config")
@Configuration
@Data
public class ClientConfig {

    private ClientTypeEnum type;

    private ClientProtocolEnum protocol;

    private String baseUrl;

    private int amount;

    private RemoteConfig proxy;

    private RemoteConfig app;

}
