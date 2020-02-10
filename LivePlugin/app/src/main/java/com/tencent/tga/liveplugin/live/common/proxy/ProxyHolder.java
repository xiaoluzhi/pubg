package com.tencent.tga.liveplugin.live.common.proxy;

import android.os.Handler;
import android.text.TextUtils;

import com.loopj.android.tgahttp.Configs.Configs;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.right.event.RightViewEvent;
import com.tencent.tga.liveplugin.live.title.TitleView;
import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.Sessions;

/**
 * Created by lionljwang on 2016/4/4.
 */
public class ProxyHolder {

    private static final String TAG = "ProxyHolder";
    private static volatile  ProxyHolder instance;


    public synchronized static ProxyHolder getInstance(){
        if (instance == null)
            instance = new ProxyHolder();
        return instance;
    }


    public ProxyHolder()
    {

        roomEnterProxy = new RoomEnterProxy();
        roomEnterProxyParam = new RoomEnterProxy.Param();


        helloProxy = new HelloProxy();
        helloProxyParam = new HelloProxy.Param();


        speedHelloReportProxy = new SpeedHelloReportProxy();
        speedHelloReportProxyParam = new SpeedHelloReportProxy.Param();


        roomExitProxy = new RoomExitProxy();
        roomExitProxyParam = new RoomExitProxy.Param();

    }

    public void setRoomId(String roomid){
        roomEnterProxyParam.roomId = roomid;
        helloProxyParam.roomId = roomid;
        roomExitProxyParam.roomId = roomid;
    }



    //进房
    public RoomEnterProxy roomEnterProxy ;
    public RoomEnterProxy.Param roomEnterProxyParam;

    //退房
    public RoomExitProxy roomExitProxy;
    public RoomExitProxy.Param roomExitProxyParam;

    //hello
    public HelloProxy helloProxy;
    public HelloProxy.Param helloProxyParam;

    //这个给研发那边用的。 我们原来的房间心跳要改造成累计时长在线要改基础服务。这个成本有点高， 所以再报一份
    public SpeedHelloReportProxy speedHelloReportProxy;
    public SpeedHelloReportProxy.Param speedHelloReportProxyParam;

    /**
     * 0暂时退房-同时退大厅和包厢,1永久退房-只退包厢,2只退大厅不退包厢
     * @param type
     * @param flag clearn chat room id no not
     */
    public void exitRoom(final int type,final boolean flag){
        if (TextUtils.isEmpty(LiveInfo.mRoomId))return;

        roomExitProxyParam.leaType = type;
        roomExitProxy.postReqWithOutRepeat(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                if (Configs.Debug)
                    LOG.e("exitRoom","退出房间成功 " + flag+"  "+type+" ");
            }

            @Override
            public void onFail(int errorCode) {
                TLog.e("退出房间失败 " + errorCode);
            }
        }, roomExitProxyParam);
    }


    public void countOnlinePeriod(String roomid){
        if(TextUtils.isEmpty(roomid)){
            TLog.e("countOnlinePeriod","roomid is null");
            return;
        }
        TLog.e("countOnlinePeriod","countOnlinePeriod roomid is : "+roomid);
        try {
            speedHelloReportProxyParam.roomid = roomid;
            speedHelloReportProxy.postReq(new NetProxy.Callback() {
                @Override
                public void onSuc(int code) {
                    try {
                        if(speedHelloReportProxyParam.onlineHelloRsp != null){
                            TLog.e("countOnlinePeriod","countOnlinePeriod onSuc  : "+speedHelloReportProxyParam.onlineHelloRsp.toString());
                        }
                    }catch (Exception e){
                        TLog.e("countOnlinePeriod","countOnlinePeriod result error : "+e.getMessage());
                    }
                }
                @Override
                public void onFail(int errorCode) {
                    TLog.e("countOnlinePeriod","countOnlinePeriod onFail errorCode : "+errorCode);
                }
            },speedHelloReportProxyParam);
        }catch (Exception e){
            TLog.e("countOnlinePeriod","countOnlinePeriod error : "+e.getMessage());
        }
    }

    /***
     * hello 失败次数  连续失败三次就不发
     */
    private int helloFailTimes = 0;
    public static int reqHelloTime = 10*1000;
    private Handler mHandler = new Handler();

    private void reqDelayedHello(){
        mHandler.removeCallbacks(mHelloRunnable);
        mHandler.postDelayed(mHelloRunnable, reqHelloTime);
    }

    public void reqHello() {
        ProxyHolder.getInstance().helloProxyParam.flag = 0;
        ProxyHolder.getInstance().helloProxy.postReq(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                if (Configs.Debug)
                    TLog.e(TAG, String.format("reqHello 成功  mRoomId = %s uid = %s flag = %d",  LiveInfo.mRoomId, Sessions.globalSession().getUserId(),ProxyHolder.getInstance().helloProxyParam.flag));
                if (ProxyHolder.getInstance().helloProxyParam.helloRsp != null) {
                    if (ProxyHolder.getInstance().helloProxyParam.helloRsp.hello_timespan != null) {
                        reqHelloTime = ProxyHolder.getInstance().helloProxyParam.helloRsp.hello_timespan;
                    }
                }
                helloFailTimes = 0;
                reqDelayedHello();

            }

            @Override
            public void onFail(int errorCode) {
                TLog.d(TAG, "reqHello失败 " + errorCode);
                if (helloFailTimes > 3) {
                    stopHello();
                    return;
                }else {
                    reqDelayedHello();
                }
                helloFailTimes++;

            }
        }, ProxyHolder.getInstance().helloProxyParam);


        ProxyHolder.getInstance().countOnlinePeriod(LiveInfo.mRoomId);
    }

    public void stopHello() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mHelloRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private Runnable mHelloRunnable = () -> reqHello();


    public void enterRoom() {
        if (TextUtils.isEmpty(ProxyHolder.getInstance().roomEnterProxyParam.roomId)) return;

        if (TitleView.mCurrentSelection != DefaultTagID.LIVE) return;
        ProxyHolder.getInstance().roomEnterProxy.postReq(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                TLog.e(LiveConfig.TAG, "进入房间成功 " + ProxyHolder.getInstance().roomEnterProxyParam.roomId);
                com.tencent.tga.liveplugin.live.common.proxy.ProxyHolder.getInstance().reqHello();
                RightViewEvent.Companion.addSysMsg();
            }

            @Override
            public void onFail(int errorCode) {
                TLog.d(LiveConfig.TAG, "进入房间失败 $errorCode");
            }
        }, ProxyHolder.getInstance().roomEnterProxyParam);
    }
}
