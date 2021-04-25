package com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.impl;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.Socks5StateEnum;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.exception.ClientException;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.factory.AbstractClientFactory;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.DataUtil;
import com.goldwind.ngsp.isolate.test.ConcurrentClient.util.KafkaUtil;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.ClientProtocolEnum.TCP;
import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.Socks5StateEnum.INIT;
import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.Socks5StateEnum.SUCCESS;

@Component
@Slf4j
public class SocketClientFactoryImpl extends AbstractClientFactory {

    @Autowired
    private KafkaUtil kafkaUtil;

    private final List<Socket> socketList = new ArrayList<>();

    private final List<DatagramSocket> datagramSocketList = new ArrayList<>();

    private Socks5StateEnum socks5State = INIT;

    @Override
    protected void createClient() throws Exception {
        if (TCP.equals(getClientProtocol())) {
            Proxy socks5Proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(getProxyIP(), getProxyPort()));
            Socket socket = new Socket(socks5Proxy);
            socket.connect(new InetSocketAddress(getAppIP(), getAppPort()));
            socketList.add(socket);
        } else {
            createUDPClient(getProxyIP(), getProxyPort());
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
                    byte[] request = DataUtil.getMsg();
                    DatagramPacket requestPacket = new DatagramPacket(request, request.length, new InetSocketAddress(getAppIP(), getAppPort()));
                    if (log.isDebugEnabled()) {
                        log.debug(Thread.currentThread().getName() + " 发送数据: " + Arrays.toString(requestPacket.getData()));
                    }
                    datagramSocket.send(requestPacket);
                    kafkaUtil.send(request);

                    byte[] response = new byte[request.length];
                    DatagramPacket responsePackage = new DatagramPacket(response, response.length);
                    datagramSocket.receive(requestPacket);
                    if (log.isDebugEnabled()) {
                        log.debug(Thread.currentThread().getName() + " 接收数据: " + Arrays.toString(responsePackage.getData()));
                    }
                    kafkaUtil.send(response);
                }
            });
        }
    }

    private void createUDPClient(String proxyIP, int proxyPort) throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(proxyIP, proxyPort));
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
            byte[] request;
            byte[] response;
            int numOfBytes;
            for (; ; ) {
                switch (socks5State) {
                    case INIT:
                        request = new byte[]{0x05, 0x01, 0x00};
                        outputStream.write(request);
                        response = new byte[2];
                        numOfBytes = inputStream.read(response);
                        if (numOfBytes != -1) {
                            byte authMethod = response[1];
                            if (Socks5AuthMethod.NO_AUTH.byteValue() == authMethod) {
                                log.info("SOCKS5 握手成功");
                                socks5State = SUCCESS;
                                request = assembleSocks5CommandRequest(getAppIP(), getAppPort());
                                outputStream.write(request);
                            } else {
                                throw new ClientException("SOCKS5 握手失败");
                            }
                        }
                    case SUCCESS:
                        response = new byte[16];
                        numOfBytes = inputStream.read(response);
                        if (numOfBytes != -1) {
                            byte commandStatus = response[1];
                            if (Socks5CommandStatus.SUCCESS.byteValue() == commandStatus) {
                                log.info("SOCKS5 建立成功");
                                socks5State = INIT;
                                break;
                            } else {
                                throw new ClientException("SOCKS5 建立失败");
                            }
                        }
                }
            }
        }
    }

    private byte[] assembleSocks5CommandRequest(String appIP, int appPort) {
        byte[] header = new byte[]{
                0x05, 0x01, 0x00, 0x01
        };
        byte[] ip = NetUtil.createByteArrayFromIpAddressString(appIP);
        byte[] port = DataUtil.intToByteArray(appPort);
        return DataUtil.copyArray(header, ip, port);
    }

}
