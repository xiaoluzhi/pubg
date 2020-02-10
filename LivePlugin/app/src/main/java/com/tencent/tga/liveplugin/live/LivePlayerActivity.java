package com.tencent.tga.liveplugin.live;

import com.ryg.DLCallBackManager;
import com.ryg.dynamicload.DLBasePluginActivity;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.apngplayer.ApngImageLoader;
import com.tencent.tga.liveplugin.base.LivePlugin;
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.PluginExitReleaseMangner;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.common.broadcast.manager.EventBroadcastMananger;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.common.util.UIAdaptationUtil;
import com.tencent.tga.liveplugin.live.liveView.LiveView;
import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;
import com.tencent.tga.liveplugin.networkutil.SPUtils;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.networkutil.broadcast.NetBroadHandeler;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.util.HashMap;

/**
 * Created by lionljwang on 2016/7/15.
 * <p/>
 * 视频播放界面
 */
public class LivePlayerActivity extends DLBasePluginActivity {

    private static final String TAG = "LivePlayerActivity";
    private UnityBean mUnityBean = UnityBean.getmInstance();
    public LiveView mLiveView;
    private ViewTreeObserver.OnGlobalLayoutListener mRootViewLayoutListener;

    private int mLastRootviewWidth;
    private int mLastRootviewHeight;
    private int mCurrentRootviewWidth;
    private int mCurrentRootviewHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveConfig.mLockSwitch = false;
        LiveConfig.mLiveContext = that;
        //参数的初始化必须在网络初始化之前，否则参数为空，导致长连接异常
        initArgs();
        EventBroadcastMananger.getInstance().registerBroadcast();
        mLiveView = new LiveView(that);
        that.setContentView(mLiveView);
        init(that.getApplication());
        initFont();
        initHideBottomBar();

        initActivityInfo();

        initOppo();
        if (Build.VERSION.SDK_INT >= 28) setCutOutMode();
        stopUE4Bgm();
    }

    @TargetApi(28)
    private void setCutOutMode() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        getWindow().setAttributes(lp);
    }

    private void init(Context context){
        LivePlugin.getInstance().initTXPlayerSdk(context);
        LivePlugin.getInstance().init(context);

        LiveConfig.mWebViewContext  =context;
        ApngImageLoader apngImageLoader = ApngImageLoader.getInstance();
        apngImageLoader.setEnableDebugLog(false);
        apngImageLoader.setEnableVerboseLog(false);
        apngImageLoader.init(DLPluginLayoutInflater.getInstance(context).getContext());
    }

    private void initFont() {
        DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
        if (callBack != null) {
            LiveConfig.mFont = (Typeface) callBack.callback(DLCallBackManager.SDK2Plugin.GET_FONT, null);
        }
    }

    private void initOppo() {
        if (DeviceUtils.isOppo()) {
            mRootViewLayoutListener = () -> {
                try {
                    mCurrentRootviewWidth = mLiveView.getWidth() > mLiveView.getHeight() ? mLiveView.getWidth() : mLiveView.getHeight();
                    mCurrentRootviewHeight = mLiveView.getWidth() > mLiveView.getHeight() ? mLiveView.getHeight() : mLiveView.getWidth();
                    if (mLastRootviewWidth != mCurrentRootviewWidth || mLastRootviewHeight != mCurrentRootviewHeight) {
                        if (null != mLiveView && null != mLiveView.mPlayView) {
                            mLiveView.updateVideoLayoutParams(mCurrentRootviewWidth, mCurrentRootviewHeight);
                            mLastRootviewHeight = mCurrentRootviewHeight;
                            mLastRootviewWidth = mCurrentRootviewWidth;
                        }
                    }
                } catch (Exception e) {

                }
            };
            mLiveView.getViewTreeObserver().addOnGlobalLayoutListener(mRootViewLayoutListener);
        }
    }

    public void initHideBottomBar(){
        try {
            if(!DeviceUtils.hasVirtualBar(LiveConfig.mLiveContext)){
                TLog.e(TAG,"没有虚拟键，不用做全屏设置");
                return;
            }
            setHideBottomBar();
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
                TLog.e(TAG,"onSystemUiVisibilityChange visibility = "+visibility);
                setHideBottomBar();
            });
        }catch (Exception e){
            TLog.e(TAG,"onSystemUiVisibilityChange initHideBottomBar error : "+e.getMessage());
        }
    }

    public void setHideBottomBar(){
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }catch (Exception e){
            TLog.e(TAG,"onSystemUiVisibilityChange setHideBottomBar error : "+e.getMessage());
        }
    }

    /**
     * 弹窗启动的时候需要告诉UE4停止背景音乐
     */
    private void stopUE4Bgm() {
        try {
            DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
            if (callBack != null) {
                TLog.e(TAG, "onDialogShow, callback");
                HashMap map =  new HashMap<String, Object>();
                JSONObject param = new JSONObject();
                param.put("type", "2");
                map.put("param", param.toString());
                callBack.callback(DLCallBackManager.SDK2Plugin.CALL_UE4,map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void resumeUE4Background() {
        try {
            DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
            if (callBack != null) {
                TLog.e(TAG, "onDialogExit, callback");
                HashMap map =  new HashMap<String, Object>();
                JSONObject param = new JSONObject();
                param.put("type", "1");
                map.put("param", param.toString());
                callBack.callback(DLCallBackManager.SDK2Plugin.CALL_UE4,map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (null != mLiveView && null != mRootViewLayoutListener && DeviceUtils.isOppo()) {
                mLiveView.getViewTreeObserver().removeOnGlobalLayoutListener(mRootViewLayoutListener);
            }
            release();

            if (null != mLiveView)
                mLiveView.onDestroy();
            mLiveView = null;
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
            DLPluginLayoutInflater.getInstance(that).releaseDLPluginInflater();
            LiveConfig.mLiveContext = null;
            LiveInfo.destory();
            TGARouter.Companion.getInstance().release();
            resumeUE4Background();
//            Process.killProcess(Process.myPid());
        } catch (Throwable e) {
            TLog.e(TAG, "NetworkEngine 退出异常-->" + e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        TLog.e(TAG, "onStop..");
        if (null != mLiveView)
            mLiveView.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();
        TLog.e(TAG, "onStart..");

    }

    @Override
    public void onResume() {
        super.onResume();
        TLog.e(TAG, "onResume..");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (mLiveView !=null)
            mLiveView.onStart(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    /***
     * 初始化 从sdk传到plugin的参数
     */
    private void initArgs() {
        Intent intent = getIntent();

        if (intent == null) {
            that.finish();
            return;
        }
        mUnityBean.accountType = intent.getIntExtra("AccountType", 0);
        mUnityBean.token = intent.getStringExtra("AccountToken");
        mUnityBean.appid = intent.getStringExtra("appid");
        mUnityBean.areaid = intent.getStringExtra("areaid");
        mUnityBean.openid = intent.getStringExtra("openid");
        try {
            String nickname = intent.getStringExtra("nikeName");
            mUnityBean.nikeName = new String(Base64.decode(nickname.getBytes(), 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUnityBean.avatarUrl = intent.getStringExtra("avatarUrl");
        mUnityBean.gameVersion = intent.getStringExtra("gameVersion");
        mUnityBean.unityVersion = intent.getStringExtra("unityVersion");
        mUnityBean.position = intent.getIntExtra("position", 0);
        mUnityBean.gender = intent.getIntExtra("gender", 0);
        mUnityBean.gameUid = intent.getStringExtra("gameUid");
        mUnityBean.userLevel = intent.getIntExtra("userLevel", 1);
        mUnityBean.serverIps = intent.getStringArrayListExtra("serverIps");
        mUnityBean.partition = intent.getStringExtra("areaid");
        mUnityBean.pop_banner_type = intent.getStringExtra("pop_banner_type");

        byte[] configInfo = intent.getByteArrayExtra("config_info");
        if (configInfo == null || configInfo.length == 0) {
            that.finish();
            return;
        }
        TLog.e(TAG,"configInfo : "+new String(configInfo));

        JSONObject jsonObject = null;
        try {
            LiveShareUitl.LIVE_FILE = SPUtils.SPTAG = "tga_"+mUnityBean.gameUid+mUnityBean.partition;
            LOG.e(TAG,"LiveShareUitl.LIVE_FILE = "+LiveShareUitl.LIVE_FILE );

            jsonObject = new JSONObject(new String(configInfo));
            mUnityBean.tv_name = jsonObject.optString("tv_name");

            mUnityBean.chatCd = jsonObject.optInt("chat_cd", 3);
            mUnityBean.chatCd = mUnityBean.chatCd * 1000;
            mUnityBean.matchGuessUrl = jsonObject.optString("match_guess_url");

            mUnityBean.tab_list = jsonObject.optString("tab_list");
            mUnityBean.open_tab = jsonObject.optInt("open_tab");
            mUnityBean.isUseApplicationContext = jsonObject.optInt("use_app_context", 1) == 1;

            if(mUnityBean.position == 3){//弹窗引流的定位在直播TAB
                mUnityBean.open_tab = DefaultTagID.LIVE;
            }
            UIAdaptationUtil.initCameraHoleList(jsonObject.optString("android_hole_phone"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserInfo.getInstance().setUserInfo(mUnityBean);
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (null != mLiveView) {
            return mLiveView.handleKeyEvent(event);
        }
        return false;
    }

    /**
     * 初始化Activity信息，不要在插件sdk中设置
     */
    private void initActivityInfo() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void release(){
        PluginExitReleaseMangner.release(that.getApplication(), LiveInfo.mRoomId);

        NetBroadHandeler.getInstance().removeBroadcast();

        EventBroadcastMananger.getInstance().unRegisterBroadcast();
    }

}
