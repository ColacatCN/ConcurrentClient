package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ChannelConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.RemoteConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ChannelTypeEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientTypeEnum;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigUtil {

    private static final ClientConfig CLIENT_CONFIG = BeanUtil.getBean(ClientConfig.class);

    private static final ChannelConfig CHANNEL_CONFIG = BeanUtil.getBean(ChannelConfig.class);

    private static final AtomicReference<RemoteConfig> REMOTE_CONFIG_ATOMIC_REFERENCE = new AtomicReference<>();

    public static ClientTypeEnum getClientType() {
        return CLIENT_CONFIG.getType();
    }

    public static ClientProtocolEnum getClientProtocol() {
        return CLIENT_CONFIG.getProtocol();
    }

    public static String getAppIP() {
        return CLIENT_CONFIG.getApp().getHostname();
    }

    public static int getAppPort() {
        return CLIENT_CONFIG.getApp().getPort();
    }

    public static ChannelTypeEnum getChannelType() {
        return CHANNEL_CONFIG.getType();
    }

    public static RemoteConfig getRemoteConfig() {
        return REMOTE_CONFIG_ATOMIC_REFERENCE.get();
    }

    public static boolean compareAndSetRemoteConfig(RemoteConfig remoteConfig) {
        return REMOTE_CONFIG_ATOMIC_REFERENCE.compareAndSet(null, remoteConfig);
    }

    public static int getRandomPort() {
        int randomPort = 0;
        while (randomPort < 5000) {
            randomPort = ThreadLocalRandom.current().nextInt(65535);
        }
        return randomPort;
    }

}
