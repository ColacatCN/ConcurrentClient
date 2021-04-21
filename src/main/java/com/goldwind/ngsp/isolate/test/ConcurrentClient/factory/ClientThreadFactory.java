package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThreadFactory implements ThreadFactory {

    private final String groupName;

    private final AtomicInteger nextId = new AtomicInteger(1);

    public ClientThreadFactory(String groupName) {
        this.groupName = "ClientThreadFactory-" + groupName + "-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = groupName + nextId.getAndIncrement();
        return new Thread(runnable, threadName);
    }

}
