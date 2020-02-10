package com.tencent.tga.liveplugin.mina.proxy;


import com.tencent.protocol.tga.conn.HelloReq;
import com.tencent.protocol.tga.conn.HelloRsp;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.Sessions;

public class HeartBeatProxy extends NetProxy<HeartBeatProxy.Param> {
    public static class Param{
        //out
        public HelloRsp helloRsp;
    }

    @Override
    protected int getCmd() {
        return conn_cmd_types.CMD_CONN.getValue();
    }

    @Override
    protected int getSubcmd() {
        return conn_subcmd.SUBCMD_CONN_HELLO.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        HelloReq.Builder builder = new HelloReq.Builder();
        builder.cur_time = (int)(System.currentTimeMillis()/1000);
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
            param.helloRsp = WireHelper.wire().parseFrom(msg.payload, HelloRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
