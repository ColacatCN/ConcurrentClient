package com.goldwind.ngsp.isolate.test.ConcurrentClient.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteConfig {

    private String hostname;

    private int port;

}
