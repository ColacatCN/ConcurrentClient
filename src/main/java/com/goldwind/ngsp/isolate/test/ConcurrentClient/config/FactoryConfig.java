package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "factory-config")
@Configuration
@Data
public class FactoryConfig {

    private ClientConfig clientConfig;

    private DataConfig dataConfig;

}
