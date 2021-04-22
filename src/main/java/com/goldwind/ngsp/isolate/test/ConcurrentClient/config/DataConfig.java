package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.DataTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "factory-config.data-config")
@Configuration
@Data
public class DataConfig {

    private DataTypeEnum type;

    private String path;

    private int size;

}
