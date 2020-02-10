package com.tencent.tga.liveplugin.mina.handler;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.mina.MessageStruct.BroadcastNotice;
import com.tencent.tga.liveplugin.mina.MessageStruct.CSHead;
import com.tencent.tga.liveplugin.mina.MessageStruct.RspNotice;
import com.tencent.tga.liveplugin.mina.interfaces.OnResponseListener;
import com.tencent.tga.liveplugin.mina.utils.ByteConvert;
import com.tencent.tga.liveplugin.mina.utils.CHexConver;
import com.tencent.tga.liveplugin.mina.utils.MinaConstants;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.net.encrypt.MinaNetworkEngine;
import com.tencent.tga.net.mina.core.buffer.IoBuffer;

/**
 * Created by hyqiao on 2017/8/31.
 */

public class MessageHandleUtil {

    private static String TAG = MinaConstants.TAG+"MessageHandleUtil";

    private static final int FLAG_BEGIN = 0x0e;//14
    private static final int FLAG_END = 0x0f;//15

    private static final int LENGTH_LONG_HEAD = 4;//整个包的长度数据所占字节
    private static final int LENGTH_SHORT_HEAD = 2;//cshead、payload的长度数据所占字节
    private static final int LENGTH_FLAG = 1;//flag的长度数据所占字节

    public static void handleReceiveMessage(Object message,OnResponseListener onResponseListener){
        try{
            IoBuffer result = (IoBuffer) message;
            if(Configs.Debug)
                TLog.i(TAG, "i客户端接收到的信息为:" + result.getHexDump());
            if(result != null){
                byte[] b = result.array();
                if(b != null){
                    byte[] total_length_data = new byte[LENGTH_LONG_HEAD];
                    System.arraycopy(b,0,total_length_data,0, LENGTH_LONG_HEAD);
                    int total_length = ByteConvert.bytesToInt(total_length_data);
                    TLog.e(TAG,"result.capacity is : "+result.capacity());

                    int flag_begin = b[LENGTH_LONG_HEAD +LENGTH_FLAG-1];
                    int flag_end = b[total_length-1];
                    if(flag_begin == FLAG_BEGIN && flag_end == FLAG_END){
                        TLog.e(TAG,"flag is right");
                        byte[] cshead_length_data = new byte[LENGTH_SHORT_HEAD];
                        System.arraycopy(b,(LENGTH_LONG_HEAD +LENGTH_FLAG+1)-1,cshead_length_data,0, LENGTH_SHORT_HEAD);
                        int cshead_length = ByteConvert.bytesToIntS(cshead_length_data);
                        byte[] cshead_data = new byte[cshead_length];
                        System.arraycopy(b, LENGTH_LONG_HEAD +LENGTH_FLAG+ LENGTH_SHORT_HEAD,cshead_data,0,cshead_length);
                        CSHead csHead = WireHelper.wire().parseFrom(cshead_data,CSHead.class);
                        TLog.e(TAG,String.format("csHead = %s",csHead.toString()));

                        if((csHead.head_flag & Request.FLAG_BROADCAST) != 0){
                            //广播
                            TLog.e(TAG,"FLAG_BROADCAST");

                            byte[] original_data = new byte[total_length-(LENGTH_LONG_HEAD+LENGTH_FLAG+LENGTH_SHORT_HEAD+cshead_length+LENGTH_FLAG)];
                            System.arraycopy(b,(LENGTH_LONG_HEAD+LENGTH_FLAG+LENGTH_SHORT_HEAD+cshead_length),original_data,0,original_data.length);
                            byte[] payload_data = getPayLoad(original_data,csHead);
                            if(onResponseListener != null){
                                onResponseListener.onBroadcast(new BroadcastNotice(Request.createEncryptRequest(csHead.command,csHead.subcmd,null,null,null,null),
                                        Message.createMessage(csHead.command,csHead.subcmd, Configs.CLIENT_TYPE,csHead.seq,payload_data,null,null, REQ_RESULT_CODE.CODE_SUCCESS)));
                            }
                        }else {
                            //业务请求
                            TLog.e(TAG,"not FLAG_BROADCAST");

                            byte[] original_data = new byte[total_length-(LENGTH_LONG_HEAD+LENGTH_FLAG+LENGTH_SHORT_HEAD+cshead_length+LENGTH_FLAG)];
                            System.arraycopy(b,(LENGTH_LONG_HEAD+LENGTH_FLAG+LENGTH_SHORT_HEAD+cshead_length),original_data,0,original_data.length);
                            byte[] payload_data = getPayLoad(original_data,csHead);
                            if(onResponseListener != null){
                                onResponseListener.onResponse(new RspNotice(REQ_RESULT_CODE.CODE_SUCCESS,
                                        Request.createEncryptRequest(csHead.command,csHead.subcmd,null,null,null,null),
                                        Message.createMessage(csHead.command,csHead.subcmd, Configs.CLIENT_TYPE,csHead.seq,payload_data,null,null,REQ_RESULT_CODE.CODE_SUCCESS)));
                            }
                        }
                    }else {
                        TLog.e(TAG,String.format("flag check failed , flag_begin = %s , flag_end = %s",flag_begin,flag_end));
                    }
                }else {
                    TLog.e(TAG,"response convert to byte error ;b is null");
                }
            }else {
                TLog.e(TAG,"response result is null");
            }
        }catch (Exception e){
            TLog.e(TAG,"parse response error ："+e.getMessage());
        }
    }
    public static byte[] getPayLoad(byte[] src,CSHead cshead){

        byte[] payload_length_encryptdata = new byte[LENGTH_SHORT_HEAD];
        System.arraycopy(src,0,payload_length_encryptdata,0,LENGTH_SHORT_HEAD);

        int payload_encrypt_length = ByteConvert.bytesToIntS(payload_length_encryptdata);

        int data_length = src.length-LENGTH_SHORT_HEAD;
        byte[] payload_original_data = new byte[data_length];
        System.arraycopy(src,LENGTH_SHORT_HEAD,payload_original_data,0,data_length);

        byte[] payload_unencrypt_data = null;
        byte[] payload_data = null;

        if((cshead.head_flag & Request.FLAG_PARTIAL_ENCRYPT) != 0){
            TLog.e(TAG,"FLAG_PARTIAL_ENCRYPT");
            payload_unencrypt_data = getUnEncryptPayloadData(payload_original_data,payload_encrypt_length, Sessions.globalSession().getDecryptKey(cshead));
            payload_data = getUnZipPayload(payload_unencrypt_data,cshead.head_flag,true);
        }else {
            TLog.e(TAG,"not FLAG_PARTIAL_ENCRYPT");
            if((cshead.head_flag & Request.FLAG_FULL_ENCRYPT) != 0){
                TLog.e(TAG,"FLAG_FULL_ENCRYPT");
                payload_unencrypt_data = getUnEncryptPayloadData(payload_original_data,payload_encrypt_length,Sessions.globalSession().getDecryptKey(cshead));
                payload_data = getUnZipPayload(payload_unencrypt_data,cshead.head_flag,true);
            }else {
                TLog.e(TAG,"not FLAG_FULL_ENCRYPT");
                payload_unencrypt_data = new byte[payload_original_data.length];
                System.arraycopy(payload_original_data,0,payload_unencrypt_data,0,payload_original_data.length);
                payload_data = getUnZipPayload(payload_unencrypt_data,cshead.head_flag,false);
            }
        }

        return payload_data;
    }

    public static byte[] getUnEncryptPayloadData(byte[] src,int encrypt_length,byte[] encrypt_key){
        if(src.length < encrypt_length){
            TLog.e(TAG,"payload length is less encrypted data length");
            return null;
        }else if(src.length == encrypt_length){//全加密
            byte[] encrypt_data = new byte[encrypt_length];
            System.arraycopy(src,0,encrypt_data,0,encrypt_length);
            return MinaNetworkEngine.shareEngine().decrypt(encrypt_key,encrypt_data);
        }else {//部分加密
            byte[] encrypt_data = new byte[encrypt_length];
            System.arraycopy(src,0,encrypt_data,0,encrypt_length);
            byte[] unencrypt_data = new byte[src.length-encrypt_length];

            System.arraycopy(src,encrypt_length,unencrypt_data,0,src.length-encrypt_length);

            byte[] decrypt_data = MinaNetworkEngine.shareEngine().decrypt(encrypt_key,encrypt_data);
            if(decrypt_data!=null)
                if(Configs.Debug)
                    TLog.e(TAG,"decrypt_data : "+CHexConver.byte2HexStr(decrypt_data));
            else
                TLog.e(TAG,"decrypt_data is null");
            return ByteConvert.concatAll(decrypt_data,unencrypt_data);
        }
    }

    public static byte[] getUnZipPayload(byte[] src,int flag,boolean isDecrypt){
        byte[] data = null;
        if((flag & Request.FLAG_ZIP_COMPRESS) != 0){
            TLog.e(TAG,"FLAG_ZIP_COMPRESS");
            byte[] unZipSrc = new byte[src.length-LENGTH_SHORT_HEAD];
            System.arraycopy(src,LENGTH_SHORT_HEAD,unZipSrc,0,unZipSrc.length);

            byte[] uncompress_data = ByteConvert.decompress(unZipSrc);

            data = new byte[uncompress_data.length];
            System.arraycopy(uncompress_data,0,data,0,data.length);

        }else {
            TLog.e(TAG,"not FLAG_ZIP_COMPRESS : "+isDecrypt);
            if(isDecrypt){//not need zip,but need decrypt
                data = new byte[src.length-LENGTH_SHORT_HEAD];
                System.arraycopy(src,LENGTH_SHORT_HEAD,data,0,src.length-LENGTH_SHORT_HEAD);
            }else {//not need zip and not need decrypt, outside has removed 2 byte(body length)
                data = new byte[src.length];
                System.arraycopy(src,0,data,0,src.length);
            }
        }
        if(Configs.Debug)
            TLog.e(TAG,"data : "+CHexConver.byte2HexStr(data));
        return data;
    }
}
