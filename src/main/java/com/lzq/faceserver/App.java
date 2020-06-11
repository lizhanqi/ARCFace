package com.lzq.faceserver;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;

public class App {


    public static void main(String[] args) throws Exception {


        //人脸特征保存
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        byteArrayOutputStream.write(b, 0, b.length);
        System.out.println(   Base64.encodeBase64String(byteArrayOutputStream.toByteArray()));




        String name = Thread.currentThread().getName();
        System.out.println("入口线程"+name);
        UIFace uiFace = new UIFace();
        uiFace.start();
    }
}
