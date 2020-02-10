package com.tencent.tga.liveplugin.networkutil.netproxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.conn.LoginReq;
import com.tencent.protocol.tga.conn.LoginRsp;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.LivePluginConstant;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

import okiotga.ByteString;


/**
 * Created by hyqiao on 2016/3/25.
 */
public class ConnProxy extends NetProxy<ConnProxy.Param> {
    private static final String TAG ="ConnProxy";
    public static class Param{
        //in
        //out
        public LoginRsp loginRsp;
        public ByteString openid;
        public ByteString uuid;
        public ByteString access_token;
        public ByteString machine_code;
        public int network_type;
    }

    @Override
    protected int getCmd() {
        return conn_cmd_types.CMD_CONN.getValue();
    }

    @Override
    protected int getSubcmd() {
        return conn_subcmd.SUBCMD_CONN_LOGIN.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        LoginReq.Builder builder = new LoginReq.Builder();

        builder.openid = param.openid;
        builder.uuid = param.uuid;
        builder.access_token = param.access_token;
        builder.machine_code = param.machine_code;
        builder.network_type = param.network_type;
        builder.jfgameid = PBDataUtils.string2ByteString(LivePluginConstant.JINGFEN_ID);
        builder.gameid = PBDataUtils.string2ByteString(Configs.GAME_ID);

        LoginReq req = builder.build();
        byte[] payload = req.toByteArray();
        // TODO: 2016/4/7    request执行多次
        // fill signature
        byte[] signature = Sessions.globalSession().access_token;
        Request request = Request.createEncryptRequest(conn_cmd_types.CMD_CONN.getValue(),
                conn_subcmd.SUBCMD_CONN_LOGIN.getValue(),
                payload,
                null,
                null,
                signature);

        TLog.d(TAG, "payload-2-" + req.toString());
        return request;
    }

    @Override
    protected int parsePbRspBuf(Message msg, Param param) {
        try {
            param.loginRsp = WireHelper.wire().parseFrom(msg.payload, LoginRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
            TLog.e(TAG, "ConnProxy 解析失败");
            param.loginRsp = null;
        }
        return 0;
    }

}
