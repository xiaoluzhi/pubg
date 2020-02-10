package com.tencent.tga.liveplugin.live.common.proxy;

import com.tencent.protocol.tga.conn.Room_LeaveReq;
import com.tencent.protocol.tga.conn.Room_LeaveRsp;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

/**
 * Created by lionljwang on 2016/4/3.
 */
public class RoomExitProxy extends NetProxy<RoomExitProxy.Param> {
    public static class Param{
        //in
        public String roomId;

        public int leaType;
        //out
        public Room_LeaveRsp roomLeaveRsp;

    }

    @Override
    protected int getCmd() {
        return conn_cmd_types.CMD_CONN.getValue();
    }

    @Override
    protected int getSubcmd() {
        return conn_subcmd.SUBCMD_CONN_ROOM_LEAVE.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        Room_LeaveReq.Builder builder = new Room_LeaveReq.Builder();
        builder.roomid(PBDataUtils.string2ByteString(param.roomId));


        builder.leave_type = param.leaType;
        builder.user_nick(PBDataUtils.string2ByteString(UnityBean.getmInstance().nikeName));
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
            param.roomLeaveRsp = WireHelper.wire().parseFrom(msg.payload, Room_LeaveRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
