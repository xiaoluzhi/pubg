package com.tencent.tga.liveplugin.mina.handler;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.auth.auth_cmd_types;
import com.tencent.protocol.tga.auth.auth_subcmd;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.mina.MessageStruct.CSHead;
import com.tencent.tga.liveplugin.mina.MinaManager;
import com.tencent.tga.liveplugin.mina.utils.ByteConvert;
import com.tencent.tga.liveplugin.mina.utils.CHexConver;
import com.tencent.tga.liveplugin.mina.utils.MinaConstants;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.net.encrypt.MinaNetworkEngine;

import okiotga.ByteString;


/**
 * Created by hyqiao on 2017/8/17.
 */

public class RequestUtil {

    private static final String TAG = MinaConstants.TAG+"RequestUtil";

    public static byte[] getRequest(int cmd,int subcmd,int seq,byte[] payload_src){

        byte[] head = new byte[1];
        head[0] = 0x0e;
        TLog.e(TAG, CHexConver.byte2HexStr(head));

        CSHead mCSHead = getCSHead(cmd,subcmd,seq);

        byte[] csHead = mCSHead.toByteArray();
        byte[] csHeadlength = ByteConvert.uintToBytesS(csHead.length);

        byte[] payload_original = payload_src;
        byte[] payload = ByteConvert.compress(payload_original);
        byte[] payloadlength = ByteConvert.uintToBytesS(payload.length);

        byte[] original_data = ByteConvert.concatAll(payloadlength,payload);
        byte[] encrypt_data = MinaNetworkEngine.shareEngine().encrypt(Sessions.getDecryptKey(mCSHead),original_data);
        byte[] encrypt_data_length = ByteConvert.uintToBytesS(encrypt_data.length);

        byte[] end = new byte[1];
        end[0] = 0x0f;

        byte[] request_data = ByteConvert.concatAll(head,csHeadlength,csHead,encrypt_data_length,encrypt_data,end);

        int total_length = request_data.length+4;
        byte[] total_length_data = ByteConvert.uintToBytes(total_length);

        byte[] result = ByteConvert.concatAll(total_length_data,request_data);

        if(Configs.Debug)
            TLog.e(TAG,"result : "+CHexConver.byte2HexStr(result));
        return result;
    }


    public static CSHead getCSHead(int cmd,int subcmd,int seq){
        if((cmd == auth_cmd_types.CMD_AUTH.getValue()) && (subcmd == auth_subcmd.SUBCMD_AUTH_TOKEN.getValue())){
            return getCSHead(cmd,subcmd,seq, MinaManager.mDefaultUserId,"".getBytes());
        }else {
            return getCSHead(cmd,subcmd,seq,Sessions.globalSession().getUserId(),Sessions.globalSession().getAccess_token());
        }
    }
    public static CSHead getCSHead(int cmd,int subcmd,int seq,String userid,byte[] access_token){

        CSHead.Builder b = new CSHead.Builder();
        b.command = cmd;
        b.subcmd = subcmd;
        b.seq = seq;
        b.user_id = PBDataUtils.string2ByteString(userid);
        b.client_type = Configs.CLIENT_TYPE;
        b.head_flag = Request.FLAG_FULL_ENCRYPT| Request.FLAG_PARTIAL_ENCRYPT|Request.FLAG_ZIP_COMPRESS;
        b.client_ver = Configs.plugin_version;
        b.signature = ByteString.of(Sessions.globalSession().getAccess_token());
        b.uin = 2l;
        b.ext = null;
        b.result = 1;
        return b.build();
    }
}
