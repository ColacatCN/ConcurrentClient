package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ChannelTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "factory-config.channel-config")
@Configuration
@Data
public class ChannelConfig {

    private ChannelTypeEnum type;

}
