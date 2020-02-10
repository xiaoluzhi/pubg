package com.tencent.tga.liveplugin.live.common.broadcast.manager;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.OperationMsg.OperationMsg;
import com.tencent.protocol.tga.chatMsg.ChatMsg;
import com.tencent.protocol.tga.expressmsg.BusinessType;
import com.tencent.protocol.tga.hpjy_update_roominfo.NotifyLiveOffline;
import com.tencent.protocol.tga.hpjy_update_roominfo.UpdateRoomInfo;
import com.tencent.protocol.tga.room_usernum_notify.RoomUsernumNotify;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveOnLiveListener;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveRoomUpdateListener;
import com.tencent.tga.liveplugin.live.common.broadcast.MsgBroadListener;
import com.tencent.tga.liveplugin.live.common.broadcast.NetBroadListener;
import com.tencent.tga.liveplugin.live.common.broadcast.OnlineBroadListener;
import com.tencent.tga.liveplugin.live.common.util.LimitQueue;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.broadcast.NetBroadHandeler;
import com.tencent.tga.liveplugin.networkutil.netproxy.MinaConnectControl;

import android.text.TextUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by agneswang on 2017/3/14.
 */

public class NetBroadcastMananger {

    private static final String TAG = "PlayBroadcastMananger";

    private static NetBroadcastMananger mInstance;

    public synchronized static NetBroadcastMananger getInstance(){
        if (mInstance == null)mInstance = new NetBroadcastMananger();
        return mInstance;
    }

    public synchronized static void release(){
        mInstance = null;
    }

    private NetBroadcastMananger() {
    }

    public LimitQueue<byte[]> msg = new LimitQueue<>(50);
    public ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());

    public void registerBroadcast() {
        //聊天消息
        NetBroadHandeler.getInstance().setmMsgBroadListener(mMsgBroadListener);
        //竞猜条消息
        NetBroadHandeler.getInstance().setmOnlineBroadListener(mOnlineBroadListener);
        NetBroadHandeler.getInstance().setmLiveOnLiveListener(mLiveOnLiveListener);
        NetBroadHandeler.getInstance().setmNetBroadListener(mNetBroadListener);
        NetBroadHandeler.getInstance().setmLiveRoomUpdateListener(mUpdageRoomInfoListener);
    }

    public void unRegisterBroadcast() {
        NetBroadHandeler.getInstance().setmMsgBroadListener(null);
        NetBroadHandeler.getInstance().setmOnlineBroadListener(null);
        NetBroadHandeler.getInstance().setmLiveOnLiveListener(null);
        NetBroadHandeler.getInstance().setmNetBroadListener(null);
        NetBroadHandeler.getInstance().setmLiveRoomUpdateListener(null);

    }


    private  NetBroadListener  mNetBroadListener = bytes -> {
        TLog.e(TAG, "NetBroadHandeler.getInstance().addBroadcast() 重连");
        new MinaConnectControl(LiveConfig.mLiveContext).connect();
    };

    /**
     * 接受聊天消息，系统广播弹幕消息，管理员消息
     * 根据type区别消息类型
     */
    private int time = 200;//线程等待时间，消息缓冲越多等待时间越短。
    private byte[] types;//缓冲普通消息
    private ChatMsg chatMsg;
    private MsgBroadListener mMsgBroadListener = (bytes, type) -> {
    try {
        if (type == BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.getValue()) {
            proMsg(bytes);
        } else {
            if (type == BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.getValue()){
                proOperationMsg(bytes);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    };

    //普通消息
    public void proMsg(final byte[] bytes){

        msg.offer(bytes);

        threadPool.execute(() -> {
            try {
                while (msg.size() > 0) {
                    time = 201 - msg.size() * 4;//满消息不等待
                    types = msg.poll();
                    if (bytes == null) continue;
                    if (time > 4) Thread.sleep(time);
                    chatMsg = WireHelper.wire().parseFrom(types, ChatMsg.class);
                    if (chatMsg.uid != null && !PBDataUtils.byteString2String(chatMsg.uid).equals(new String(Sessions.globalSession().getUid(), "utf-8"))) {
                        ChatMsgEntity chatMsgEntity = new ChatMsgEntity();

                        chatMsgEntity.roomId =PBDataUtils.byteString2String(chatMsg.room_id);
                        chatMsgEntity.name = PBDataUtils.byteString2String(chatMsg.nick);
                        chatMsgEntity.text = PBDataUtils.byteString2String(chatMsg.text_msg);
                        chatMsgEntity.isSel = false;
                        chatMsgEntity.msgType = BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.getValue();
                        chatMsgEntity.bageId =  PBDataUtils.byteString2String(chatMsg.badge_id);

                        LiveViewEvent.Companion.showTextMsg(chatMsgEntity);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //运营消息
    public void proOperationMsg(byte[] bytes){
        try {
            OperationMsg chatMsg = WireHelper.wire().parseFrom(bytes, OperationMsg.class);
            TLog.e(TAG,"OperationMsg : "+chatMsg.toString());
            if (chatMsg.msg_type !=null)
            {
                ChatMsgEntity chatMsgEntity = new ChatMsgEntity();

                chatMsgEntity.name = PBDataUtils.byteString2String(chatMsg.nick);
                chatMsgEntity.text =  PBDataUtils.byteString2String(chatMsg.text_msg);
                chatMsgEntity.isSel = false;
                chatMsgEntity.nickUrl = PBDataUtils.byteString2String(chatMsg.face_url);
                chatMsgEntity.msgType = BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.getValue();
                chatMsgEntity.subType = chatMsg.msg_type;
                chatMsgEntity.color = PBDataUtils.byteString2String(chatMsg.color);

                if (null != chatMsg.show_type)chatMsgEntity.showType = chatMsg.show_type;
                LiveViewEvent.Companion.showTextMsg(chatMsgEntity);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private LiveOnLiveListener mLiveOnLiveListener = bytes -> {
        try {
            NotifyLiveOffline liveOffline = WireHelper.wire().parseFrom(bytes, NotifyLiveOffline.class);
            PlayViewEvent.offLine(liveOffline.source_id);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    };

    private OnlineBroadListener mOnlineBroadListener = bytes -> {
        try {
            RoomUsernumNotify roomUserNum = WireHelper.wire().parseFrom(bytes, RoomUsernumNotify.class);
            if (roomUserNum != null && !TextUtils.isEmpty(LiveInfo.mRoomId) && LiveInfo.mRoomId.equals(PBDataUtils.byteString2String(roomUserNum.roomid)))
                PlayViewEvent.setOnlineNum(roomUserNum.user_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private LiveRoomUpdateListener mUpdageRoomInfoListener = bytes -> {
        try {
            UpdateRoomInfo info =  WireHelper.wire().parseFrom(bytes, UpdateRoomInfo.class);
            TLog.e(TAG, "mUpdageRoomInfoListener info is " + info);
            if (Integer.valueOf(PBDataUtils.byteString2String(info.sourceid)) == LiveInfo.mSourceId) {
                PlayViewEvent.updatePlayViewInfo(new RoomInfo(
                        PBDataUtils.byteString2String(info.roomid),
                        PBDataUtils.byteString2String(info.vid),
                        Integer.valueOf(PBDataUtils.byteString2String(info.sourceid)),0,
                        PBDataUtils.byteString2String(info.live_title), "", 0),
                        Integer.valueOf(PBDataUtils.byteString2String(info.live_type)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
