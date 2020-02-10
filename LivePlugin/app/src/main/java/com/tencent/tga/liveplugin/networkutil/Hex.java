package com.tencent.tga.liveplugin.networkutil;

/**
 * Created by wonlangwu on 2015/12/8.
 */

public abstract class Hex {

    private final static char[]	HEX_DIGITS	= { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 把字节数组转换成十六进制字符串
     *
     * 请使用{@link #bytesToHexes(byte[])}
     *
     * @param data 要转换的字节数组
     * @return 返回转换后的字符串
     */
    @Deprecated
    public static String bytesToHexString(byte[] data) {

        // 如果字节数组为空则直接返回空
        if (data == null || data.length == 0) {
            return null;
        }

        // byte数组转换成字符串后的长度是原来字节数组的一倍
        char[] chars = new char[data.length << 1];

        // 循环字节把字节的高位和低位拆开放入一个新的数组当中
        byte b = 0;
        int j =0;
        for (int i=0; i<data.length; i++) {
            b = data[i];
            chars[j++] =  (char) ((b & 0xf0) >> 4); // 高位
            chars[j++] = (char) (b & 0x0f); // 低位
        }

        // 把字节转换成16进制的字符并拼凑成16进制的字符串
        StringBuffer buffer = new StringBuffer();
        for (int k=0; k<chars.length; k++) {
            buffer.append(Integer.toHexString(chars[k]));
        }
        return buffer.toString();
    }

    /**
     * 把十六进制的字符串还原成二进制的字节数组
     *
     * 弃用，请使用{@link #hexesToBytes(String)}
     *
     * @param hexString 十六进制的字符串
     * @return 返回还原后的字节数组
     */
    @Deprecated
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.trim().length() == 0) {
            return null;
        }

        // 还原后的字节数组长度是字符串的一半
        byte[] bytes = new byte[hexString.length() >> 1];

        //  每次取两个字符转换成数字后，分别作为字节的高位和低位重新组合成一个字节
        int j=0;
        for (int i=0; i<hexString.length(); i++) {
            // 取第一个字符作为字节的高位
            char c1 = hexString.charAt(i);
            byte gw = (byte) Character.digit(c1, 16); // 高位

            // 取第二个字节作为字节的低位
            i++;
            char c2 = hexString.charAt(i);
            byte dw = (byte) Character.digit(c2, 16);

            // 高位左移4位后和低位做与运算组合成字节
            bytes[j] = (byte) ((gw << 4) | dw);

            // 组合成一个字节后指针加1
            j++;
        }

        // 返回转换后的字节数组
        return bytes;
    }

    /**
     * 将指定字节转换成十六进制字符串
     *
     * @param b 待转换字节
     * @return 返回转换后的字符串
     */
    public static String byteToHexDigits(byte b)
    {
        int n = b;
        if (n < 0)
            n += 256;

        int d1 = n / 16;
        int d2 = n % 16;

        return "" + HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 将指定字节数组转换成十六进制字符串
     *
     * @param bytes 待转换的字节数组
     * @return 返回转换后的字符串
     */
    public static String bytesToHexes(byte[] bytes)
    {
        if (bytes == null || bytes.length == 0)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
        {
            sb.append(byteToHexDigits(bytes[i]));
        }
        return sb.toString();
    }
    /**
     * 十六进制字符转换为整数
     *
     * @param hex 小写十六进制字符
     * @return 返回整数
     */
    public static int hexToInteger(char hex) {
        if (hex >= HEX_DIGITS[16])
            return hex - HEX_DIGITS[16] + 10;
        else if (hex >= HEX_DIGITS[10])
            return hex - HEX_DIGITS[10] + 10;
        else
            return hex - HEX_DIGITS[0];
    }

    /**
     * 十六进制字符串转换为字节数组
     *
     * @param hexes 十六进制字符串
     * @return 返回字节数组
     */
    public static byte[] hexesToBytes(String hexes)
    {
        if (hexes == null || hexes.length() == 0)
            return null;

        int slen = hexes.length();
        int len = (slen + 1)/ 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++)
        {
            char c = hexes.charAt(2 * i);
            int val = hexToInteger(c);
            val *= 16;
            if (2 * i + 1 < slen) {
                c = hexes.charAt(2 * i + 1);
                val += hexToInteger(c);
            }

            bytes[i] = (byte)(val & 0xff);

        }
        return bytes;
    }
}
