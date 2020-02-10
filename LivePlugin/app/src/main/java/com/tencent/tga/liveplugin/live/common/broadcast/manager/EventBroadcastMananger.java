package com.tencent.tga.liveplugin.live.common.broadcast.manager;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.notification.NotificationCenter;
import com.tencent.tga.liveplugin.base.notification.Subscriber;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.proxy.ProxyHolder;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.SPUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;



/**
 * Created by agneswang on 2017/3/14.
 * 通用广播处理类，用于处理通用广播的注册及处理
 */

public class EventBroadcastMananger {

    private static final String TAG = "BroadcastMananger";

    private static EventBroadcastMananger mInstance;

    public synchronized static EventBroadcastMananger getInstance(){
        if (mInstance == null)mInstance = new EventBroadcastMananger();
        return mInstance;
    }

    private EventBroadcastMananger() {
        lastBroadCastNetState = NetUtils.NetWorkStatus(LiveConfig.mLiveContext);
    }

    public synchronized static void release(){
        mInstance = null;
    }

    public void registerBroadcast() {
        try {
            //建立长链接成功
            NotificationCenter.defaultCenter().subscriber(LiveEvent.NetworkEngineUsable.class, mNetworkEngineUsableSubscriber);

            SPUtils.SPSaveLong(LiveConfig.mLiveContext, SPUtils.netbroadcastperiod, System.currentTimeMillis());
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            LiveConfig.mLiveContext.registerReceiver(mNetStateChangeReceiver, mFilter);
        } catch (Exception e) {
            TLog.e(TAG, "registerBroadcast failed");
        }

    }

    public void unRegisterBroadcast() {
        try {
            NotificationCenter.defaultCenter().unsubscribe(LiveEvent.NetworkEngineUsable.class, mNetworkEngineUsableSubscriber);
            if (mNetStateChangeReceiver != null ) {
                LiveConfig.mLiveContext.unregisterReceiver(mNetStateChangeReceiver);
                mNetStateChangeReceiver = null;
            }
        } catch (Throwable e) {
            TLog.e(TAG, "unRegisterBroadcast failed");
        }
    }

    private long mPeriod = 2000;//2000ms内的广播相当于一次
    private long lastBroadCastTime = System.currentTimeMillis();
    private int lastBroadCastNetState = -1;

    private BroadcastReceiver mNetStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    int state = NetUtils.NetWorkStatus(context);
                    TLog.e(TAG, "mNetStateChangeReceiver---" + state);
                    long lastTime = lastBroadCastTime;
                    int lastState = lastBroadCastNetState;

                    lastBroadCastTime = System.currentTimeMillis();
                    lastBroadCastNetState = state;
                    //初始话动态注册后会有一条广播，过滤该广播
                    if (lastState == state && (System.currentTimeMillis() - lastTime) < mPeriod) {
                        TLog.e(TAG, "重复的广播  state = " + state);
                    } else {
                        // 通知
                        PlayViewEvent.netWorkChange(state);
                        NotificationCenter.defaultCenter().publish(new LiveEvent.NetWorkStateChange(state));
                    }
                } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    TLog.e(TAG, "mNetStateChangeReceiver---screen off");
                    PlayViewEvent.stopPlay();
                } else {
                    TLog.e(TAG, "other system broadcast");
                }
            } catch (Exception e) {
                TLog.e(TAG, "handle broadcast exception " + e.getMessage());
            }
        }
    };

    private Subscriber<LiveEvent.NetworkEngineUsable> mNetworkEngineUsableSubscriber = new Subscriber<LiveEvent.NetworkEngineUsable>() {
        @Override
        public void onEvent(LiveEvent.NetworkEngineUsable event) {
            /**建立长连接成功*/
            TLog.e(TAG, "mNetworkEngineUsableSubscriber ");

            /** 后续如果ScheduleFragment部分去除，此处注释即可*/

            PlayViewEvent.init();

            ProxyHolder.getInstance().enterRoom();

            LiveInfo.isReadyToReport = true;

            PlayViewEvent.doPlayViewInitReport();
        }
    };



    /**
     *
     * @param roomInfo
     * @param isUpdateSourceId 只有广播是false，需要判断id，其它不用直接走下面流程
     */
    public void updateSourceIDAndRoomId(RoomInfo roomInfo, boolean isUpdateSourceId) {
        try {
            if (roomInfo == null) return;

            /**当前观看直播，其它比赛广播过来不出来**/
            if (isUpdateSourceId) {
                LiveInfo.mSourceId = roomInfo.getSourceid();
            } else if (LiveInfo.mSourceId != roomInfo.getSourceid() && isUpdateSourceId) {
                return;
            }

            String mRoomId = roomInfo.getRoomid();
            TLog.e(TAG, "reqCurrentMatch 成功 " + mRoomId + "  " + LiveInfo.mRoomId + "  " + isUpdateSourceId);
            if (!TextUtils.equals(LiveInfo.mRoomId, mRoomId) && !TextUtils.isEmpty(mRoomId) && isUpdateSourceId) {
                LiveInfo.mRoomId = mRoomId;
                ProxyHolder.getInstance().setRoomId(mRoomId);
                ProxyHolder.getInstance().enterRoom();
            }
        } catch (Exception e) {
            TLog.e(TAG, "updateRoomInf exception " + e.getMessage());
        }
    }
}
