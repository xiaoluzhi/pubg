package com.tencent.tga.liveplugin.live.player.proxy;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.chat.RoomChatReq;
import com.tencent.protocol.tga.chat.RoomChatRsp;
import com.tencent.protocol.tga.chat.chat_cmd_types;
import com.tencent.protocol.tga.chat.chat_subcmd;
import com.tencent.protocol.tga.chatMsg.ChatMsg;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

import java.io.UnsupportedEncodingException;

import okiotga.ByteString;


/**
 * Created by lionljwang on 2016/4/4.
 */
public class ChatProxy extends NetProxy<ChatProxy.Param> {
    private static final String TAG ="ChatProxy";
    public static class Param{
        //in
        public String roomId;
        public String text;
        public int seq;
        public String nick;
        public int msgType = 0;
        public int hotwordId;
        //out
        public RoomChatRsp chatRsp;

        public Param(String roomId,String text,int seq){
            this.roomId = roomId;
            this.text = text;
            this.seq = seq;
        }
    }

    @Override
    protected int getCmd() {
        return chat_cmd_types.CMD_CHAT.getValue();
    }

    @Override
    protected int getSubcmd() {
        return chat_subcmd.SUBCMD_HPJY_ROOM_CHAT.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        RoomChatReq.Builder builder = new RoomChatReq.Builder();
        try {
            builder.openid(PBDataUtils.string2ByteString(UnityBean.getmInstance().openid));
            builder.roomid(PBDataUtils.string2ByteString(param.roomId));
            builder.userid(PBDataUtils.string2ByteString(new String(Sessions.globalSession().getUid(), "utf-8")));
            builder.nick_name(PBDataUtils.string2ByteString(param.nick));
            builder.gamelevel(UnityBean.getmInstance().userLevel);

           ChatMsg.Builder chatMsg = new ChatMsg.Builder();
            chatMsg.room_id(PBDataUtils.string2ByteString(param.roomId));
           chatMsg.text_msg(PBDataUtils.string2ByteString(param.text));
           chatMsg.date_time = (int)(System.currentTimeMillis()/1000);
           chatMsg.seq = param.seq;
            chatMsg.hot_word_id = param.hotwordId;
            chatMsg.uid(PBDataUtils.string2ByteString(new String(Sessions.globalSession().getUid(), "utf-8")));
            chatMsg.badge_id(PBDataUtils.string2ByteString(""));
            chatMsg.nick(PBDataUtils.string2ByteString(param.nick));
           //增加mAreaid
            chatMsg.area_id(Integer.valueOf(UnityBean.getmInstance().partition));
            TLog.d(TAG, chatMsg.seq + " onBroadcast chatMsg.build().toByteArray().length = " + chatMsg.build().toByteArray().length);

            chatMsg.msg_type = param.msgType;

           builder.chat_msg(ByteString.of(chatMsg.build().toByteArray()));
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
            param.chatRsp = WireHelper.wire().parseFrom(msg.payload, RoomChatRsp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
