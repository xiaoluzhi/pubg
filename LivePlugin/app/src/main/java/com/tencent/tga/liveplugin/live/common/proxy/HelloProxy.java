package com.tencent.tga.liveplugin.live.common.proxy;


import com.tencent.protocol.tga.conn.Room_HelloReq;
import com.tencent.protocol.tga.conn.Room_HelloRsp;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

/**
 * Created by lionljwang on 2016/4/4.
 */
public class HelloProxy extends NetProxy<HelloProxy.Param> {
    public static class Param{
        //in
        public String roomId;
        public int flag;
        //out
        public Room_HelloRsp helloRsp;
    }

    @Override
    protected int getCmd() {
        return conn_cmd_types.CMD_CONN.getValue();
    }

    @Override
    protected int getSubcmd() {
        return conn_subcmd.SUBCMD_CONN_ROOM_HELLO.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        Room_HelloReq.Builder builder = new Room_HelloReq.Builder();

        builder.roomid(PBDataUtils.string2ByteString(param.roomId));
        builder.cur_time = (int)(System.currentTimeMillis()/1000);
        builder.room_push_flag(param.flag);
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
            param.helloRsp = WireHelper.wire().parseFrom(msg.payload, Room_HelloRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
