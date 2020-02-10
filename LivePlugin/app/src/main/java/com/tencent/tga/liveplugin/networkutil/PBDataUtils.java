package com.tencent.tga.liveplugin.networkutil;


import okiotga.ByteString;

/**
 * Created by xuepingyang on 2015/5/11.
 *
 * PB数据工具，主要是数据类型转换
 */
public class PBDataUtils {

    public static ByteString string2ByteString(String msg) {
        if(msg==null) {
            msg = "";
        }
        return ByteString.encodeUtf8(msg);
    }

    /**
     * ByteString转化为String
     */
    public static String byteString2String(ByteString msg) {
        if(msg==null)
            return "";
        return msg.utf8();
    }

    /**
     * Integer转化为int
     */
    public static int integer2int(Integer msg) {
        if(msg==null)
            return 0;
        return msg.intValue();
    }

    /**
     * Long转化为long
     */
    public static long Long2long(Long msg) {
        if(msg==null)
            return 0;
        return msg.longValue();
    }

}
