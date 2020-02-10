package com.tencent.tga.liveplugin.live.common.broadcast;

import com.tencent.tga.liveplugin.live.common.bean.RoomInfo;

/**
 * Created by lionljwang on 2016/2/26.
 */
public class LiveEvent {

    /**
     * 发送聊天消息
     */
    public static class SendMsg {

        public String msg;

        public boolean isSend;

        public boolean isHotword;

        public int hotwordId;




        public SendMsg(String msg,boolean isSend, boolean isHotword, int hotwordId){
            this.msg = msg;

            this.isSend = isSend;

            this.isHotword = isHotword;

            this.hotwordId = hotwordId;
        }

    }


    /**
     * 网络状态变化
     */
    public static class NetWorkStateChange {
        public int state;
        public NetWorkStateChange(int state){
            this.state = state;
        }
    }

    /**
     * 长连接建立成功
     */
    public static class NetworkEngineUsable {
        public NetworkEngineUsable(){
        }
    }

    /**
     * 网络状态变化
     */
    public static class FightMsg {
        public String msg;
        public FightMsg(String msg){
            this.msg = msg;
        }
    }


    /**
     * 切换多路直播，为null表示切到默认kpl房间直播
     */
    public static class LiveLineChange {
        public RoomInfo channelInfo;
        public int playType;
        public LiveLineChange(RoomInfo channelInfo, int playType){
            this.channelInfo = channelInfo;
            this.playType = playType;
        }
    }

    public static class LockSreen {
        public boolean isLock;
        public LockSreen(boolean isLock){
            this.isLock = isLock;
        }
    }



    public static class PlayerResume {
        public PlayerResume(){}
    }
}
