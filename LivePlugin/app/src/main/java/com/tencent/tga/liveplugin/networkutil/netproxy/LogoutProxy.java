package com.tencent.tga.liveplugin.networkutil.netproxy;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.conn.LogoutReq;
import com.tencent.protocol.tga.conn.LogoutRsp;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.base.util.WireHelper;

/**
 * Created by hyqiao on 2016/11/30.
 */

public class LogoutProxy extends NetProxy<LogoutProxy.Param> {
    private static final String TAG ="LogoutProxy";
    public static class Param{
        //in
        //out
        public LogoutRsp logoutRsp;
        public String openid;
        public String uuid;
        public String access_token;
        public String machine_code;
    }


    @Override
    protected int getCmd() {
        return conn_cmd_types.CMD_CONN.getValue();
    }

    @Override
    protected int getSubcmd() {
        return conn_subcmd.SUBCMD_CONN_LOGOUT.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        LogoutReq.Builder builder = new LogoutReq.Builder();

        builder.openid = PBDataUtils.string2ByteString(param.openid);
        builder.uuid = PBDataUtils.string2ByteString(param.uuid);
        builder.access_token = PBDataUtils.string2ByteString(param.access_token);
        builder.machine_code = PBDataUtils.string2ByteString(param.machine_code);

        LogoutReq req = builder.build();
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

        return request;
    }

    @Override
    protected int parsePbRspBuf(Message msg, Param param) {
        try {
            param.logoutRsp = WireHelper.wire().parseFrom(msg.payload, LogoutRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
            TLog.e(TAG, "LogoutProxy 解析失败");
            param.logoutRsp = null;
        }
        return 0;
    }
}
