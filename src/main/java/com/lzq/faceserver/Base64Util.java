package com.lzq.faceserver;

import org.apache.tomcat.util.codec.binary.Base64;

public class Base64Util {

    /**
     * 编码字符串
     * @param str
     * @return
     */
    public static String encode(String str){
        if(str==null||str.trim().isEmpty()) return "";
        return Base64.encodeBase64String(str.getBytes());
    }

    /**
     * 解码字符串
     * @param str
     * @return
     */
    public static String decode(String str){
        if(str==null||str.trim().isEmpty()) return "";
        return new String(Base64.decodeBase64(str));
    }
}