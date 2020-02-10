package com.tencent.tga.liveplugin.networkutil.netproxy;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.auth.AuthTokenReq;
import com.tencent.protocol.tga.auth.AuthTokenRsp;
import com.tencent.protocol.tga.auth.auth_cmd_types;
import com.tencent.protocol.tga.auth.auth_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.base.util.WireHelper;

import java.io.IOException;

import okiotga.ByteString;

/**
 * Created by hyqiao on 2016/3/25.
 */
public class AuthProxy extends NetProxy<AuthProxy.Param> {
    private static final String TAG ="AuthProxy";
    public static class Param{
        //in
        //out
        public AuthTokenRsp authTokenRsp;
        public ByteString account_name;
        public Integer account_type;
        public Integer client_type;
        public ByteString access_token;
        //public Integer st_type;
        public ByteString st_buf;
        public ByteString clientip;
        public ByteString mcode;
        public Integer client_time;
        public ByteString live_token;
        public ByteString gameid;
        public Integer areaid;
        public ByteString openid;
        public ByteString device_info;
        public ByteString sdk_appid;
        public ByteString game_uid;
    }

    @Override
    protected int getCmd() {
        return auth_cmd_types.CMD_AUTH.getValue();
    }

    @Override
    protected int getSubcmd() {
        return auth_subcmd.SUBCMD_AUTH_TOKEN.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        AuthTokenReq.Builder builder = new AuthTokenReq.Builder();

        try{
            builder.account_name = param.account_name;
            builder.account_type = param.account_type;
            builder.client_type = param.client_type;
            builder.access_token = param.access_token;
            //builder.st_type = param.st_type;//微信的APPID包含字母，因此增加一个新的参数sdk_appid
            builder.st_buf = param.st_buf;
            builder.clientip = param.clientip;
            builder.mcode = param.mcode;
            builder.client_time = param.client_time;
            builder.live_token = param.live_token;
            builder.gameid = param.gameid;
            builder.areaid = param.areaid;
            builder.game_openid = param.openid;
            builder.device_info = param.device_info;
            builder.sdk_appid = param.sdk_appid;
            builder.game_uid = param.game_uid;

            AuthTokenReq req = builder.build();
            byte[] payload = req.toByteArray();
            Request request = Request.createEncryptRequest(auth_cmd_types.CMD_AUTH.getValue(),
                    auth_subcmd.SUBCMD_AUTH_TOKEN.getValue(),
                    payload,
                    null,
                    null,
                    null);
            TLog.e(TAG,"convertParamToPbReqBuf success : "+req.toString());
            return request;
        }catch (Exception e){
            TLog.e(TAG,"convertParamToPbReqBuf   :  "+e.getMessage());
            return null;
        }

        // Sessions.globalSession().sigKey);

    }

    @Override
    protected int parsePbRspBuf(Message msg, Param param) {
        try {
            TLog.e(TAG,"parsePbRspBuf result");
            param.authTokenRsp = WireHelper.wire().parseFrom(msg.payload, AuthTokenRsp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
