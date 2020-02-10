package com.tencent.tga.liveplugin.live.gift.proxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.hpjy_treasure.CMD;
import com.tencent.protocol.tga.hpjy_treasure.ReceiveTreasureBoxReq;
import com.tencent.protocol.tga.hpjy_treasure.ReceiveTreasureBoxRsp;
import com.tencent.protocol.tga.hpjy_treasure.SUBCMD;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.UserInfo;

import android.os.Build;

import java.util.Random;

public class ReceiveTreasureBoxProxy extends NetProxy<ReceiveTreasureBoxProxy.Param> {
    private static String TAG = "ReceiveTreasureBoxProxy";

    public static class Param{
        public String boxid;
        public int server_time;
        public String user_name;
        public int level;
        public ReceiveTreasureBoxRsp rsp;
    }

    @Override
    protected int getCmd() {
        return CMD.CMD_HPJY_TREASURE.getValue();
    }

    @Override
    protected int getSubcmd() {
        return SUBCMD.SUBCMD_RECEIVE_TREASURE_BOX.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        ReceiveTreasureBoxReq.Builder builder = new ReceiveTreasureBoxReq.Builder();

        builder.user_id = PBDataUtils.string2ByteString(Sessions.globalSession().getUserId());
        builder.boxid = PBDataUtils.string2ByteString(param.boxid);
        builder.server_time = param.server_time;
        builder.time_stamp = PBDataUtils.string2ByteString(System.currentTimeMillis()/1000+"");
        builder.random_value = PBDataUtils.string2ByteString(new Random(65535).nextInt()+"");
        builder.user_name = PBDataUtils.string2ByteString(param.user_name);
        builder.level = param.level;
        builder.client_type = Configs.CLIENT_TYPE;
        builder.areaid = Integer.valueOf(UserInfo.getInstance().mAreaid);
        builder.area = UserInfo.getInstance().mAccount_type == 3 ? 3 : UserInfo.getInstance().mAccount_type == 1 ? 2 : 1;
        builder.platid = 1;
        builder.mcode = PBDataUtils.string2ByteString(Build.MODEL);
        builder.openid = PBDataUtils.string2ByteString(UserInfo.getInstance().mOpenid);

        byte[] payload = builder.build().toByteArray();
        TLog.e(TAG,"convertParamToPbReqBuf : "+ builder.build().toString());
        // fill signature
        byte[] signature = Sessions.globalSession().access_token;
        Request request = Request.createEncryptRequest(getCmd(),
                getSubcmd(),
                payload,
                null,
                null,
                signature);
        return request;
    }

    @Override
    protected int parsePbRspBuf(Message msg, Param param) {
        try {
            param.rsp = WireHelper.wire().parseFrom(msg.payload, ReceiveTreasureBoxRsp.class);
            TLog.e(TAG,"parsePbRspBuf : "+param.rsp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
