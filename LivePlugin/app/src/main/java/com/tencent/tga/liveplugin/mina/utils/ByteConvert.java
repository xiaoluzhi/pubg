package com.tencent.tga.liveplugin.mina.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by hyqiao on 2017/8/17.
 */

public class ByteConvert {
    // 以下 是整型数 和 网络字节序的  byte[] 数组之间的转换
    public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8  & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    public static void longToBytes( long n, byte[] array, int offset ){
        array[7+offset] = (byte) (n & 0xff);
        array[6+offset] = (byte) (n >> 8 & 0xff);
        array[5+offset] = (byte) (n >> 16 & 0xff);
        array[4+offset] = (byte) (n >> 24 & 0xff);
        array[3+offset] = (byte) (n >> 32 & 0xff);
        array[2+offset] = (byte) (n >> 40 & 0xff);
        array[1+offset] = (byte) (n >> 48 & 0xff);
        array[0+offset] = (byte) (n >> 56 & 0xff);
    }

    public static long bytesToLong( byte[] array )
    {
        return ((((long) array[ 0] & 0xff) << 56)
                | (((long) array[ 1] & 0xff) << 48)
                | (((long) array[ 2] & 0xff) << 40)
                | (((long) array[ 3] & 0xff) << 32)
                | (((long) array[ 4] & 0xff) << 24)
                | (((long) array[ 5] & 0xff) << 16)
                | (((long) array[ 6] & 0xff) << 8)
                | (((long) array[ 7] & 0xff) << 0));
    }

    public static long bytesToLong( byte[] array, int offset )
    {
        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8)
                | (((long) array[offset + 7] & 0xff) << 0));
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static void intToBytes( int n, byte[] array, int offset ){
        array[3+offset] = (byte) (n & 0xff);
        array[2+offset] = (byte) (n >> 8 & 0xff);
        array[1+offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    public static int bytesToInt(byte b[]) {
        return    b[3] & 0xff
                | (b[2] & 0xff) << 8
                | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;
    }

    public static int bytesToIntS(byte b[]) {
        return    b[1] & 0xff
                | (b[0] & 0xff) << 8;
    }


    public static int bytesToInt(byte b[], int offset) {
        return    b[offset+3] & 0xff
                | (b[offset+2] & 0xff) << 8
                | (b[offset+1] & 0xff) << 16
                | (b[offset] & 0xff) << 24;
    }

    public static byte[] uintToBytes( long n )
    {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    public static byte[] uintToBytesS( long n )
    {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);

        return b;
    }

    public static byte[] uintToBytesSForEn( long n )
    {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);

        return b;
    }



    public static void uintToBytes( long n, byte[] array, int offset ){
        array[3+offset] = (byte) (n );
        array[2+offset] = (byte) (n >> 8 & 0xff);
        array[1+offset] = (byte) (n >> 16 & 0xff);
        array[offset]   = (byte) (n >> 24 & 0xff);
    }

    public static long bytesToUint(byte[] array) {
        return ((long) (array[3] & 0xff))
                | ((long) (array[2] & 0xff)) << 8
                | ((long) (array[1] & 0xff)) << 16
                | ((long) (array[0] & 0xff)) << 24;
    }

    public static long bytesToUint(byte[] array, int offset) {
        return ((long) (array[offset+3] & 0xff))
                | ((long) (array[offset+2] & 0xff)) << 8
                | ((long) (array[offset+1] & 0xff)) << 16
                | ((long) (array[offset]   & 0xff)) << 24;
    }

    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void shortToBytes(short n, byte[] array, int offset ) {
        array[offset+1] = (byte) ( n       & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static short bytesToShort(byte[] b){
        return (short)( b[1] & 0xff
                |(b[0] & 0xff) << 8 );
    }

    public static short bytesToShort(byte[] b, int offset){
        return (short)( b[offset+1] & 0xff
                |(b[offset]    & 0xff) << 8 );
    }

    public static byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) ( n       & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void ushortToBytes(int n, byte[] array, int offset ) {
        array[offset+1] = (byte) ( n       & 0xff);
        array[offset] = (byte)   ((n >> 8) & 0xff);
    }

    public static int bytesToUshort(byte b[]) {
        return    b[1] & 0xff
                | (b[0] & 0xff) << 8;
    }

    public static int bytesToUshort(byte b[], int offset) {
        return    b[offset+1] & 0xff
                | (b[offset]   & 0xff) << 8;
    }

    public static byte[] ubyteToBytes( int n ){
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static void ubyteToBytes( int n, byte[] array, int offset ){
        array[0] = (byte) (n & 0xff);
    }

    public static int bytesToUbyte( byte[] array ){
        return array[0] & 0xff;
    }

    public static int bytesToUbyte( byte[] array, int offset ){
        return array[offset] & 0xff;
    }
    // char 类型、 float、double 类型和 byte[] 数组之间的转换关系还需继续研究实现。


    /***
     * 压缩Zip
     *
     * @param data
     * @return
     */
    public static byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
    /***
     * 解压Zip
     *
     * @param data
     * @return
     */
    public static byte[] unZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }


    /**
     * 压缩
     *
     * @param data
     *            待压缩数据
     * @return byte[] 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    /**
     * 压缩
     *
     * @param data
     *            待压缩数据
     *
     * @param os
     *            输出流
     */
    public static void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);

        try {
            dos.write(data, 0, data.length);

            dos.finish();

            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压缩
     *
     * @param data
     *            待压缩的数据
     * @return byte[] 解压缩后的数据
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    /**
     * 解压缩
     *
     * @param is
     *            输入流
     * @return byte[] 解压缩后的数据
     */
    public static byte[] decompress(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
        try {
            int i = 1024;
            byte[] buf = new byte[i];

            while ((i = iis.read(buf, 0, i)) > 0) {
                o.write(buf, 0, i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return o.toByteArray();
    }


    public static byte[] concatAll(byte[] first, byte[]... rest) {

        int totalLength = first.length;

        for (byte[] array : rest) {
            totalLength += array.length;
        }


        byte[] result = Arrays.copyOf(first, totalLength);

        int offset = first.length;

        for (byte[] array : rest) {

            System.arraycopy(array, 0, result, offset, array.length);

            offset += array.length; }

        return result;
    }
}
