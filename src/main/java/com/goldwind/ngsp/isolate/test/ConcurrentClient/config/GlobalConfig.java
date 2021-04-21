package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "global-config")
@Configuration
@Data
public class GlobalConfig {

    private ClientConfig clientConfig;

    private DataConfig dataConfig;

}
