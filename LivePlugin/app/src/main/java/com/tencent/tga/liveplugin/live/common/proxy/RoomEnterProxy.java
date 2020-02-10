package com.tencent.tga.liveplugin.live.common.proxy;


import com.tencent.protocol.tga.conn.Room_EnterReq;
import com.tencent.protocol.tga.conn.Room_EnterRsp;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.base.util.WireHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created by lionljwang on 2016/4/3.
 */
public class RoomEnterProxy extends NetProxy<RoomEnterProxy.Param> {
    public static class Param{
        //in
        public String roomId;
        //out
        public Room_EnterRsp roomEnterRsp;
    }

    @Override
    protected int getCmd() {
        return conn_cmd_types.CMD_CONN.getValue();
    }

    @Override
    protected int getSubcmd() {
        return conn_subcmd.SUBCMD_CONN_ROOM_ENTER.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        Room_EnterReq.Builder builder = new Room_EnterReq.Builder();

        builder.roomid(PBDataUtils.string2ByteString(param.roomId));
        try {
            builder.userid(PBDataUtils.string2ByteString(new String(Sessions.globalSession().getUid(), "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        builder.nick(PBDataUtils.string2ByteString( UnityBean.getmInstance().nikeName));

        Room_EnterReq req = builder.build();
        byte[] payload = req.toByteArray();

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
            param.roomEnterRsp = WireHelper.wire().parseFrom(msg.payload, Room_EnterRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
