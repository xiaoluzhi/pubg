package com.tencent.tga.liveplugin.video.proxy;
import com.tencent.protocol.tga.vod_op.CMD;
import com.tencent.protocol.tga.vod_op.DianZanReq;
import com.tencent.protocol.tga.vod_op.DianZanRsp;
import com.tencent.protocol.tga.vod_op.SUBCMD;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

import java.io.UnsupportedEncodingException;

/**
 * Created by lionljwang on 2016/4/4.
 */
public class AddZanProxy extends NetProxy<AddZanProxy.Param> {
    private static final String TAG = "AddZanProxy";

    public static class Param {
        public String vid;
        public int op;// 1 点赞操作 2 取消点赞
        public DianZanRsp mZanRsp;
    }

    @Override
    protected int getCmd() {
        return CMD.CMD_VOD_OP.getValue();
    }

    @Override
    protected int getSubcmd() {
        return SUBCMD.SUBCMD_DIAN_ZAN.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        DianZanReq.Builder builder = new DianZanReq.Builder();
        try {
            builder.openid(PBDataUtils.string2ByteString(UnityBean.getmInstance().openid));
            builder.userid(PBDataUtils.string2ByteString(new String(Sessions.globalSession().getUid(), "utf-8")));
            builder.nick(PBDataUtils.string2ByteString(UnityBean.getmInstance().nikeName));
            builder.vid(PBDataUtils.string2ByteString(param.vid));
            builder.gameid(PBDataUtils.string2ByteString("hpjy"));
            builder.game_level(UnityBean.getmInstance().userLevel);
            builder.op = param.op;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        byte[] payload = builder.build().toByteArray();

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
            param.mZanRsp = WireHelper.wire().parseFrom(msg.payload, DianZanRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
