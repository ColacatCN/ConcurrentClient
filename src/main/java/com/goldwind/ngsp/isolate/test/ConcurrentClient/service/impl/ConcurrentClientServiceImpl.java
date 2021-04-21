package com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.ClientConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.DataConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl.HttpClientFactoryImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl.NettyClientFactoryImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl.SocketClientFactoryImpl;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.IConcurrentClientService;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.BeanUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.DataTypeEnum.BYTE;

@Service
public class ConcurrentClientServiceImpl implements IConcurrentClientService {

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private DataConfig dataConfig;

    @Override
    public void start() throws Exception {
        AbstractClientFactory clientFactory;
        switch (clientConfig.getType()) {
            case HTTP:
                clientFactory = BeanUtil.getBean(HttpClientFactoryImpl.class);
                break;
            case SOCKET:
                clientFactory = BeanUtil.getBean(SocketClientFactoryImpl.class);
                break;
            default:
                clientFactory = BeanUtil.getBean(NettyClientFactoryImpl.class);
                break;
        }

        byte[] bytes;
        if (BYTE.equals(dataConfig.getType())) {
            int dataSize = dataConfig.getSize();
            bytes = DataUtil.assembleData(dataSize);
        } else {
            String filePath = dataConfig.getPath();
            bytes = DataUtil.assembleData(filePath);
        }
        clientFactory.sendMsg(bytes);
    }

}
