package com.tencent.tga.liveplugin.networkutil.broadcast;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.conn.conn_cmd_types;
import com.tencent.protocol.tga.conn.conn_subcmd;
import com.tencent.protocol.tga.expressmsg.BusinessType;
import com.tencent.protocol.tga.expressmsg.ExpressMsg;
import com.tencent.protocol.tga.expressmsg.PushNotify;
import com.tencent.tga.base.net.BroadcastHandler;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveOnLiveListener;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveRoomUpdateListener;
import com.tencent.tga.liveplugin.live.common.broadcast.MsgBroadListener;
import com.tencent.tga.liveplugin.live.common.broadcast.NetBroadListener;
import com.tencent.tga.liveplugin.live.common.broadcast.OnlineBroadListener;
import com.tencent.tga.liveplugin.mina.MinaManager;

import okiotga.ByteString;


/**
 * Created by lionljwang on 2016/4/26.
 */
public class NetBroadHandeler implements BroadcastHandler {
    private static final String TAG = "NetBroadHandeler";

    private MsgBroadListener mMsgBroadListener;
    private NetBroadListener mNetBroadListener;
    private LiveRoomUpdateListener mLiveRoomUpdateListener;
    private OnlineBroadListener mOnlineBroadListener;
    private LiveOnLiveListener mLiveOnLiveListener;

    public void setmMsgBroadListener(MsgBroadListener mMsgBroadListener) {
        this.mMsgBroadListener = mMsgBroadListener;
    }

    public void setmNetBroadListener(NetBroadListener mNetBroadListener) {
        this.mNetBroadListener = mNetBroadListener;
    }

    public void setmLiveRoomUpdateListener(LiveRoomUpdateListener mLiveRoomUpdateListener){
        this.mLiveRoomUpdateListener = mLiveRoomUpdateListener ;
    }

    public void setmOnlineBroadListener(OnlineBroadListener mOnlineBroadListener) {
        this.mOnlineBroadListener = mOnlineBroadListener;
    }

    public void setmLiveOnLiveListener(LiveOnLiveListener mLiveOnLiveListener) {
        this.mLiveOnLiveListener = mLiveOnLiveListener;
    }

    private static volatile NetBroadHandeler mInstance;

    private NetBroadHandeler(){}

    public static synchronized NetBroadHandeler getInstance(){
        if (mInstance == null)
        {
            mInstance  = new NetBroadHandeler();
        }
        return mInstance;
    }

    public void unInit(){
        mInstance = null;
    }


    @Override
    public boolean match(int command, int subcmd, int seq) {
        if (command == conn_cmd_types.CMD_CONN.getValue()  && subcmd == conn_subcmd.SUBCMD_CONN_PUSH.getValue())
        {
            return  true;
        }
        return false;
    }

    @Override
    public void onBroadcast(Message msg) {

        if (msg==null||msg.payload ==null)
            return;
        try {
            PushNotify pushNotify = WireHelper.wire().parseFrom(msg.payload, PushNotify.class);
//            TLog.e(TAG,"pushNotify : "+pushNotify.toString());
            for (ByteString byteString : pushNotify.body)
            {
                ExpressMsg expressMsg = WireHelper.wire().parseFrom(byteString.toByteArray(), ExpressMsg.class);
                if (expressMsg == null || expressMsg.business_type == null || expressMsg.content ==  null)
                    break;

//                TLog.e(TAG,"expressMsg.business_type : "+expressMsg.business_type);
                if (expressMsg.business_type  == BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.getValue()
                        ||expressMsg.business_type  == BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.getValue()
                        ||expressMsg.business_type  == BusinessType.BUSINESS_TYPE_BOX_CRITICAL_MSG.getValue())
                {
                    if (mMsgBroadListener !=null  ){
                        mMsgBroadListener.onMsg(expressMsg.content.toByteArray(),expressMsg.business_type);
                    }
                }else if (expressMsg.business_type  == BusinessType.BUSINESS_TYPE_RELOGIN_NOTIFY.getValue()){
                    if (mNetBroadListener !=null){
                        mNetBroadListener.onMsg(expressMsg.content.toByteArray());
                    }
                }else if(expressMsg.business_type  == BusinessType.BUSINESS_TYPE_HPJY_UPDATE_ROOM_INFO.getValue()){

                    if (mLiveRoomUpdateListener !=null && expressMsg.content !=null)
                    {
                        mLiveRoomUpdateListener.onMsg(expressMsg.content.toByteArray());
                    }
                } else if (expressMsg.business_type  == BusinessType.BUSINESS_TYPE_ROOM_USERNUM_NOTIFY.getValue()){
                    if (mOnlineBroadListener !=null){
                        mOnlineBroadListener.onMsg(expressMsg.content.toByteArray());
                    }
                }else if (expressMsg.business_type  == BusinessType.BUSINESS_TYPE_HPJY_NOTIFY_LIVE_OFFLINE.getValue()){
                    if (mLiveOnLiveListener !=null){
                        mLiveOnLiveListener.onMsg(expressMsg.content.toByteArray());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, "match error : "+e.getMessage());
        }
    }


    public void addBroadcast()
    {
        removeBroadcast();
        MinaManager.getInstance().addBroadcastHandler(this);

    }
    public void removeBroadcast()
    {
        MinaManager.getInstance().removeBroadcastHandler(this);
    }
}
