package com.goldwind.ngsp.isolate.test.ConcurrentClient;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.IConcurrentClientService;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.service.impl.ConcurrentClientServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.StartupModeEnum.CONCURRENT_CLIENT_MODE;
import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.StartupModeEnum.KAFKA_CLIENT_MODE;

@SpringBootApplication
@MapperScan(basePackages = "com.goldwind.ngsp.isolate.test.ConcurrentClient.dao")
public class Application {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        if (args.length == 0) {
            throw new ClientException("请配置启动参数");
        }

        if (args.length > 1) {
            throw new ClientException("启动参数过多");
        }

        String startupMode = args[0];
        if (CONCURRENT_CLIENT_MODE.getKey().equalsIgnoreCase(startupMode)) {
            IConcurrentClientService clientService = applicationContext.getBean(ConcurrentClientServiceImpl.class);
            clientService.start();
        } else if (KAFKA_CLIENT_MODE.getKey().equalsIgnoreCase(startupMode)) {
            // TODO: 待开发
        } else {
            throw new ClientException("暂时不支持 " + startupMode + " 模式");
        }
    }

}
