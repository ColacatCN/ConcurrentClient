package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.RemoteConfig;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.ConfigUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.KafkaUtil;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum.TCP;

@Component
@Slf4j
public class SocketClientFactoryImpl extends AbstractClientFactory {

    @Autowired
    private KafkaUtil kafkaUtil;

    private final List<Socket> socketList = new ArrayList<>();

    private final List<DatagramSocket> datagramSocketList = new ArrayList<>();

    @Override
    protected void createClient() throws Exception {
        if (TCP.equals(getClientProtocol())) {
            createTCPClient();
        } else {
            createUDPClient();
        }
    }

    @Override
    public void sendMsg() throws Exception {
        initializeClientFactory();
        if (TCP.equals(getClientProtocol())) {
            sendTCPMsg();
        } else {
            sendUDPMsg();
        }
    }

    private void createTCPClient() throws IOException {
        Proxy socks5Proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(getProxyIP(), getProxyPort()));
        Socket socket = new Socket(socks5Proxy);
        socket.connect(new InetSocketAddress(getAppIP(), getAppPort()));
        socketList.add(socket);
    }

    private void createUDPClient() throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(getProxyIP(), getProxyPort()));
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
            boolean initialResult = sendSocks5InitialRequest(inputStream, outputStream);
            if (initialResult && sendSocks5CommandRequest(inputStream, outputStream)) {
                DatagramSocket datagramSocket = new DatagramSocket(0);
                datagramSocketList.add(datagramSocket);
            }
        }
    }

    private boolean sendSocks5InitialRequest(DataInputStream inputStream, DataOutputStream outputStream) throws IOException, ClientException {
        byte[] initialRequest = new byte[]{0x05, 0x01, 0x00};
        outputStream.write(initialRequest);

        boolean result = false;
        byte[] initialResponse = new byte[2];
        int numOfBytes = inputStream.read(initialResponse);
        if (numOfBytes != -1) {
            byte authMethod = initialResponse[1];
            if (Socks5AuthMethod.NO_AUTH.byteValue() == authMethod) {
                log.info("SOCKS5 握手成功");
                result = true;
            } else {
                throw new ClientException("SOCKS5 握手失败");
            }
        }

        return result;
    }

    private boolean sendSocks5CommandRequest(DataInputStream inputStream, DataOutputStream outputStream) throws IOException, ClientException {
        byte[] commandRequest = assembleSocks5CommandRequest(getAppIP(), getAppPort());
        outputStream.write(commandRequest);

        boolean result = false;
        byte[] commandResponse = new byte[10];
        int numOfBytes = inputStream.read(commandResponse);
        if (numOfBytes != -1) {
            byte commandStatus = commandResponse[1];
            if (Socks5CommandStatus.SUCCESS.byteValue() == commandStatus) {
                byte addressType = commandResponse[3];
                if (Socks5AddressType.IPv4.byteValue() == addressType) {
                    RemoteConfig remoteConfig = RemoteConfig.builder()
                            .hostname(NetUtil.bytesToIpAddress(commandResponse, 4, 4))
                            .port(6060)
                            .build();
                    if (ConfigUtil.compareAndSetRemoteConfig(remoteConfig)) {
                        log.info("bndAddr = {}, bndPort = {}.", remoteConfig.getHostname(), remoteConfig.getPort());
                    }
                } else if (Socks5AddressType.IPv6.byteValue() == addressType) {
                    // TODO: 待开发
                }
                log.info("SOCKS5 建立成功");
                result = true;
            } else {
                throw new ClientException("SOCKS5 建立失败");
            }
        }

        return result;
    }

    private void sendTCPMsg() {
        for (Socket socket : socketList) {
            executorService.submit(() -> {
                try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                     DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
                    for (; ; ) {
                        byte[] request = DataUtil.getMsg();
                        if (log.isDebugEnabled()) {
                            log.debug(Thread.currentThread().getName() + " 发送数据: " + Arrays.toString(request));
                        }
                        outputStream.write(request);
                        kafkaUtil.send(request);

                        byte[] response = new byte[request.length];
                        int numOfBytes = inputStream.read(response);
                        if (numOfBytes != -1) {
                            if (log.isDebugEnabled()) {
                                log.debug(Thread.currentThread().getName() + " 接收数据: " + Arrays.toString(response));
                            }
                            kafkaUtil.send(response);
                        } else {
                            throw new ClientException("Socket 客户端没有收到响应数据");
                        }
                    }
                }
            });
        }
    }

    private void sendUDPMsg() {
        for (DatagramSocket datagramSocket : datagramSocketList) {
            executorService.submit(() -> {
                for (; ; ) {
                    byte[] requestBody = DataUtil.getMsg();
                    RemoteConfig remoteConfig;
                    if ((remoteConfig = ConfigUtil.getRemoteConfig()) != null) {
                        try {
                            byte[] udpPackage = assembleUDPPackage(requestBody);
                            DatagramPacket requestPacket = new DatagramPacket(udpPackage,
                                    udpPackage.length,
                                    new InetSocketAddress(remoteConfig.getHostname(), remoteConfig.getPort()));
                            datagramSocket.send(requestPacket);
                            kafkaUtil.send(requestBody);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            });
        }
    }

    private byte[] assembleSocks5CommandRequest(String appIP, int appPort) {
        byte[] header = new byte[]{
                0x05, 0x03, 0x00, 0x01
        };
        byte[] ip = NetUtil.createByteArrayFromIpAddressString(appIP);
        byte[] port = DataUtil.intToByteArray(appPort);
        return DataUtil.copyArray(header, ip, port);
    }

    private byte[] assembleUDPPackage(byte[] body) {
        byte[] fixedContent = new byte[]{0x00, 0x00, 0x00, 0x01};
        byte[] appIP = NetUtil.createByteArrayFromIpAddressString(ConfigUtil.getAppIP());
        byte[] appPort = DataUtil.intToByteArray(ConfigUtil.getAppPort());

        byte[] header = DataUtil.copyArray(fixedContent, appIP, appPort);
        return DataUtil.copyArray(header, body, new byte[]{});
    }

}
