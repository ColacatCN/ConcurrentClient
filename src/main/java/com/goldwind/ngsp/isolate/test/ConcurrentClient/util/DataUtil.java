package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import com.goldwind.ngsp.isolate.test.ConcurrentClient.config.DataConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.DataTypeEnum.BYTE;
import static com.goldwind.ngsp.isolate.test.ConcurrentClient.enums.DataTypeEnum.FILE;

@Component
@Slf4j
public class DataUtil {

    @Autowired
    protected DataConfig dataConfig;

    private final byte[] groupId = getRandomByteArray();

    public byte[] getMsg() {
        byte[] bytes = null;
        if (BYTE.equals(dataConfig.getType())) {
            int dataSize = dataConfig.getSize();
            bytes = assembleData(dataSize);
        } else if (FILE.equals(dataConfig.getType())) {
            String filePath = dataConfig.getPath();
            bytes = assembleData(filePath);
        }
        return bytes;
    }

    public byte[] assembleData(int length) {
        byte[] msgId = getRandomByteArray();
        byte[] payload = getRandomByteArray(length - groupId.length - msgId.length);
        return copyArray(msgId, payload, length);
    }

    public byte[] assembleData(String filePath) {
        byte[] msgId = getRandomByteArray();
        byte[] payload = fileToByteArray(filePath);
        int totalLength = groupId.length + msgId.length + payload.length;
        return copyArray(msgId, payload, totalLength);
    }

    public long getGroupId() {
        return byteArrayToInt(groupId);
    }

    public long getMsgId(byte[] data) {
        byte[] msgId = new byte[4];
        System.arraycopy(data, 4, msgId, 0, 4);
        return byteArrayToInt(msgId);
    }

    private byte[] fileToByteArray(String filePath) {
        byte[] payload = null;
        try (InputStream inputStream = new FileInputStream(filePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            payload = outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return payload;
    }

    private byte[] copyArray(byte[] msgId, byte[] payload, int length) {
        byte[] data = new byte[length];
        System.arraycopy(groupId, 0, data, 0, groupId.length);
        System.arraycopy(msgId, 0, data, groupId.length, msgId.length);
        System.arraycopy(payload, 0, data, groupId.length + msgId.length, payload.length);
        return data;
    }

    private byte[] getRandomByteArray() {
        return getRandomByteArray(4);
    }

    private byte[] getRandomByteArray(int length) {
        byte[] byteArray = new byte[length];
        ThreadLocalRandom.current().nextBytes(byteArray);
        return byteArray;
    }

    private int byteArrayToInt(byte[] byteArray) {
        int value = 0;
        for (int i = 0; i < byteArray.length; i++) {
            int shift = (3 - i) * 8;
            value += (byteArray[i] & 0xFF) << shift;
        }
        return value;
    }

}
