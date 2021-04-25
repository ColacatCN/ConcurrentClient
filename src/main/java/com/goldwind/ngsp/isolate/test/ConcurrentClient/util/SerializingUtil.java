/**
 * Copyright © 2021 金风科技. All rights reserved.
 *
 * @Title: ConverUtils.java
 * @Prject: socks5-netty-master
 * @Package: netty
 * @Description: 序列化工具类
 * @author: SF
 * @date: 2021年03月01日
 * @version: V1.0
 */
package com.goldwind.ngsp.isolate.test.ConcurrentClient.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @ClassName: SerializingUtil
 * @Description: 序列化工具类
 * @author: SF
 * @date: 2021年03月01日 19:09:20
 */
@SuppressWarnings("all")
@Slf4j
public class SerializingUtil {

    private SerializingUtil() {
    }

    /**
     * 功能简述: 对实体Bean进行序列化操作.
     *
     * @param source 待转换的实体
     * @return 转换之后的字节数组
     * @throws Exception
     */
    public static byte[] serialize(Object source) {
        if (source == null) {
            return null;
        }
        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream objOut = null;
        try {
            byteOut = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(source);
            objOut.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (null != objOut) {
                    objOut.close();
                }
            } catch (IOException e) {
                objOut = null;
            }
        }
        return byteOut.toByteArray();
    }

    /**
     * 功能简述: 将字节数组反序列化为实体Bean.
     *
     * @param source 需要进行反序列化的字节数组
     * @return 反序列化后的实体Bean
     * @throws Exception
     */
    public static Object deserialize(byte[] source) {
        if (source == null) {
            return null;
        }
        ObjectInputStream objIn = null;
        Object retVal = null;
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(source);
            objIn = new ObjectInputStream(byteIn);
            retVal = objIn.readObject();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (null != objIn) {
                    objIn.close();
                }
            } catch (IOException e) {
                objIn = null;
            }
        }
        return retVal;
    }
}
