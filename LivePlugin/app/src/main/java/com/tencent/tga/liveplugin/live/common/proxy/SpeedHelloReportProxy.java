package com.tencent.tga.liveplugin.live.common.proxy;


import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.protocol.tga.online_data.CMD;
import com.tencent.protocol.tga.online_data.OnlineHelloReq;
import com.tencent.protocol.tga.online_data.OnlineHelloRsp;
import com.tencent.protocol.tga.online_data.SUBCMD;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

/**
 * Created by lionljwang on 2016/4/4.
 */
public class SpeedHelloReportProxy extends NetProxy<SpeedHelloReportProxy.Param> {
    public static class Param{
        //in
        public String userid;
        public String openid;
        public Integer client_type;
        public String gameid;
        public Integer areaid;

        public String roomid;
        //out
        public OnlineHelloRsp onlineHelloRsp;
    }

    @Override
    protected int getCmd() {
        return CMD.CMD_ONLINE_DATA.getValue();
    }

    @Override
    protected int getSubcmd() {
        return SUBCMD.SUBCMD_ONLINE_HELLO.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        OnlineHelloReq.Builder builder = new OnlineHelloReq.Builder();

        builder.userid(PBDataUtils.string2ByteString(Sessions.globalSession().getUserId()));
        builder.openid(PBDataUtils.string2ByteString(UnityBean.getmInstance().openid));
        builder.client_type(Configs.CLIENT_TYPE);
        builder.gameid(PBDataUtils.string2ByteString(UnityBean.getmInstance().gameUid));
        builder.areaid(Integer.valueOf(UnityBean.getmInstance().partition));
        builder.roomid(PBDataUtils.string2ByteString(param.roomid));

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
            param.onlineHelloRsp = WireHelper.wire().parseFrom(msg.payload, OnlineHelloRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
