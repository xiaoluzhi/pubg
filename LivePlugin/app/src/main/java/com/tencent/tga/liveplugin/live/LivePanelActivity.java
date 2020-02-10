package com.tencent.tga.liveplugin.live;

import com.ryg.DLCallBackManager;
import com.ryg.dynamicload.DLBasePluginPopActivity;
import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.LivePlugin;
import com.tencent.tga.liveplugin.base.support.Plugin;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.networkutil.SPUtils;
import com.tencent.tga.liveplugin.poptv.PopTvConstant;
import com.tencent.tga.liveplugin.poptv.bean.PopTvBean;
import com.tencent.tga.liveplugin.poptv.manager.ExitAnimManager;
import com.tencent.tga.liveplugin.poptv.manager.PopStateViewManager;
import com.tencent.tga.liveplugin.poptv.manager.PopTvBannerManager;
import com.tencent.tga.liveplugin.poptv.manager.PopTvLpManager;
import com.tencent.tga.liveplugin.poptv.manager.PopTvPalyerManager;
import com.tencent.tga.liveplugin.report.PopTvReport;
import com.tencent.tga.plugin.R;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by agneswang on 2019/10/09.
 * <p/>
 * 视频播放界面
 */
public class LivePanelActivity extends DLBasePluginPopActivity {

    private static final String TAG = "LivePanelActivity";

    private PopTvBean mPopTvBean;

    private String mMatchInfo = "";

    private ViewGroup mVideoLayout;
    private PopStateViewManager mPopStateViewManager;
    private PopTvPalyerManager mPopTvPalyerManager;//腾讯视频播放器相关
    private ExitAnimManager mExitAnimManager;

    private ImageView close;

    private PopTvBannerManager mPopTvBannerManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TLog.e(TAG, "onCreate");
        initHideBottomBar();
        initActivityInfo();
        mMatchInfo = getIntent().getStringExtra("pop_info");
        launchLivePanel();
    }

    private void launchLivePanel() {
        TLog.e(TAG, "launchLivePanel");
        try {
            initData();//解析游戏传过来的数据

            if (!PopTvLpManager.isReadyToLaunch(that,mPopTvBean.pop_cd_period)) {
                that.finish();
                return;
            }

            init();

            that.setContentView(DLPluginLayoutInflater.getInstance(that).inflate(R.layout.dialog_live_player, null));

            stopUE4Bgm();

            initUI();

            initBanner();//获取运营位数据

            setListener();

            initTxPlayer();

        } catch (Exception e) {
            TLog.e(TAG, "PluginNotifi windowPlaer strat " + e.getMessage());
        }
    }

    public void initHideBottomBar(){
        try {
            if(!DeviceUtils.hasVirtualBar(that)){
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        exitRelease();
        releaseView();
//        android.os.Process.killProcess(Process.myPid());
    }

    @Override
    public void onStop() {
        super.onStop();
        TLog.e(TAG, "onStop..");
        if (null != mPopTvPalyerManager) mPopTvPalyerManager.stopPlay();
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
        TLog.e(TAG, "onRestart..");
        super.onRestart();
        if (null != mPopTvPalyerManager) mPopTvPalyerManager.startPlay();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        try {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                 finish();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 初始化Activity信息，不要在插件sdk中设置
     */
    private void initActivityInfo() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void init() {

        LivePlugin.getInstance().initTXPlayerSdk(that);//初始化腾讯视频

        ImageLoaderUitl.getmInstance().init(that);//初始化图片加载
        //初始化字体
        DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
        if (callBack != null) {
            mFont = (Typeface) callBack.callback(DLCallBackManager.SDK2Plugin.GET_FONT, null);
        }

        PopTvLpManager.initLogSwitch();
        TLog.e(TAG, "SPTAG : " + SPUtils.SPTAG);
        TLog.e(TAG, "mMatchInfo : " + mMatchInfo);
    }

    private void initData() {
        try {
            mPopTvBean = new PopTvBean(mMatchInfo);
            PopTvLpManager.mScreenHeight = DeviceUtils.getScreenHeight(that);
            PopTvLpManager.mScreenWidth = DeviceUtils.getScreenWidth(that);
            PopTvReport.mBannerInfo = mPopTvBean.pop_banner_type;
            LiveShareUitl.LIVE_FILE = SPUtils.SPTAG = "tga_" + mPopTvBean.mUid + mPopTvBean.mAreaID;
            PopTvConstant.initPopExitBlackList(mPopTvBean.android_pop_exit_black_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***游戏字体*/
    private Typeface mFont = null;
    private ViewGroup mLayoutBg;
    private RelativeLayout parent_view;
    private RelativeLayout root_view;
    private FrameLayout mRlyStateCtr;
    private RelativeLayout mRlyBottomOperation;

    private boolean isPopTvBgSucc = false;

    private void initUI() {
        parent_view = (RelativeLayout) findViewById(R.id.parent_view);
        root_view = (RelativeLayout)findViewById(R.id.root_view);
        mVideoLayout = (ViewGroup)findViewById(R.id.layout_videolayout);
        mRlyStateCtr = (FrameLayout) findViewById(R.id.mRlyStateCtr);
        close = (ImageView) findViewById(R.id.imageview_colse);
        mLayoutBg = (ViewGroup) findViewById(R.id.mLayoutBg);
        mRlyBottomOperation = (RelativeLayout) findViewById(R.id.mRlyBottomOperation);

        root_view.setLayoutParams(PopTvLpManager.getRootViewLp(root_view));
        parent_view.setLayoutParams(PopTvLpManager.getParentViewLp(parent_view));

        mLayoutBg.setLayoutParams(PopTvLpManager.getmLayoutBgLp(mLayoutBg));

        try{
            ImageLoaderUitl.loadImageFromNet(mPopTvBean.mLeagueLogo, new ImageLoaderUitl.ImageLoadSuccess() {
                @Override
                public void loadSucc() {
                    isPopTvBgSucc = true;
                    showNetImage();
                }
            });

            ImageLoaderUitl.loadImageFromNet(mPopTvBean.mPlayerBgUrl, new ImageLoaderUitl.ImageLoadSuccess() {
                @Override
                public void loadSucc() {
                    isPopTvBgSucc = true;
                    showNetImage();
                }
            });
        }catch (Throwable throwable){
            TLog.e(TAG,"ImageLoaderUitl throwable : "+throwable.getMessage());
        }

        mVideoLayout.setLayoutParams(PopTvLpManager.getVideoLayoutLp(mVideoLayout));
        mVideoLayout.setBackgroundResource(R.drawable.player_view_background);

        initPopStateViewManager();

        mRlyBottomOperation.setLayoutParams(PopTvLpManager.getmBottomOperationLp(mRlyBottomOperation));
    }

    private void showNetImage() {
        try {
            if (isPopTvBgSucc && isPopTvBgSucc) {
                ImageLoaderUitl.loadImageForViewGroup(mPopTvBean.mLeagueLogo, parent_view);
                ImageLoaderUitl.loadImageForViewGroup(mPopTvBean.mPlayerBgUrl, mLayoutBg);
            }
        } catch (Exception e) {
            TLog.e(TAG, "showNetImage error : " + e.getMessage());
        }
    }

    private void showDefaultUI() {
        if (that == null)
            return;
        try {
            that.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (close != null) {
                        close.setVisibility(View.GONE);
                    }
                    root_view.postInvalidate();
                }
            });
        } catch (Exception e) {
            TLog.e(TAG, "LivePlayerPanel showDefaultUI error : " + e.getMessage());
        }
    }

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (NoDoubleClickUtils.isDoubleClick()) return;
            finish();
        }
    };

    @Override
    public void finish() {
        super.finish();
        TLog.e(TAG, "finish");
//        exitRelease();
//        releaseView();
    }

    private void setListener() {
        close.setOnClickListener(closeListener);

        mPopStateViewManager.setPlayUnderMobileNetListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMobile();
            }
        });
    }

    /**
     * 弹窗启动的时候需要告诉UE4停止背景音乐
     */
    private void stopUE4Bgm() {
//        try {
//            DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
//            if (callBack != null) {
//                TLog.e(TAG, "onDialogShow, callback");
//                HashMap map =  new HashMap<String, Object>();
//                JSONObject param = new JSONObject();
//                param.put("type", "2");
//                map.put("param", param.toString());
//                callBack.callback(DLCallBackManager.SDK2Plugin.CALL_UE4,map);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void exitRelease() {
        try {
            releaseTxPlayer();
            PopTvReport.getInstance().release();
//
//            DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
//            if (callBack != null) {
//                TLog.e(TAG, "onDialogExit, callback");
//                HashMap map =  new HashMap<String, Object>();
//                JSONObject param = new JSONObject();
//                param.put("type", "1");
//                map.put("param", param.toString());
//                callBack.callback(DLCallBackManager.SDK2Plugin.CALL_UE4,map);
//            }
            TLog.e(TAG, "弹窗资源释放成功");
        } catch (Exception e) {
            TLog.e(TAG, "弹窗资源释放失败");
        }
    }

    private void releaseOther(){
        mFont = null;
        Plugin.getInstance().unInit();
        LivePlugin.getInstance().unInit();
        ImageLoaderUitl.getmInstance().unInit();
    }


    /**
     * 资源释放 否则内存泄漏
     * @author hyqiao
     * @time 2016/11/1 17:14
     */
    private void releaseView() {
        try {
            close.setBackground(null);
            releaseBanner();
            parent_view.setBackground(null);
            mLayoutBg.setBackground(null);
            releasePopStateViewManager();
            releaseExitAnim();
            releaseOther();
        } catch (Exception e) {
            TLog.e(TAG, "releaseView 失败");
            e.printStackTrace();
        }
    }

    public void initPopStateViewManager(){
        mPopStateViewManager = new PopStateViewManager(that,mRlyStateCtr,mFont, DLPluginLayoutInflater.getInstance(that).getContext().getResources(),
                mMatchInfo,mPopTvBean);
        mPopStateViewManager.init();
    }

    public void releasePopStateViewManager(){
        if(mPopStateViewManager != null)
            mPopStateViewManager = null;
    }

    public void initTxPlayer(){
        mPopTvPalyerManager = new PopTvPalyerManager(that,mVideoLayout,mPopStateViewManager.getPopStateView(),mPopTvBean);
        mPopTvPalyerManager.initTxPlayer();
    }

    /**
     * 移动网络点击播放
     * @author hyqiao
     * @time 2018/4/9 16:36
     */
    public void playMobile(){
        if(mPopTvPalyerManager != null)
            mPopTvPalyerManager.playUnderMobile();
    }

    public void releaseTxPlayer(){
        if(mPopTvPalyerManager != null){
            mPopTvPalyerManager.releaseTxPlayer();
            mPopTvPalyerManager = null;
        }
    }

    public void initBanner(){
        mPopTvBannerManager = new PopTvBannerManager(that,DLPluginLayoutInflater.getInstance(that).getContext().getResources(),mPopTvBean,mPopStateViewManager.getPopStateView(),mRlyBottomOperation);
        mPopTvBannerManager.setFont(mFont);
        mPopTvBannerManager.initPopTvBannerManager();
        mPopTvBannerManager.setCloseClick(closeListener);
        mPopTvBannerManager.setPopBannerListener(new PopTvBannerManager.PopBannerListener() {
            @Override
            public void onShowDefaultUI() {
                showDefaultUI();
            }

            @Override
            public void onDialogExit(boolean b) {
                try {
                    finish();
                    DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
                    if (callBack != null) {
                        try {
//                            Map<String, Object> map = new HashMap<String, Object>();
//                            String pop_banner_type = mPopTvBean.pop_banner_type;
//                            map.put("pop_banner_type",pop_banner_type);
//                            callBack.callback(DLCallBackManager.SDK2Plugin.CALL_PLAYER, map);
                            DLIntent intent = new DLIntent("com.tencent.tga.plugin", "com.tencent.tga.liveplugin.live.LivePlayerActivity");
                            intent.putExtra("AccountType", getIntent().getIntExtra("AccountType", 0));
                            intent.putExtra("AccountToken", getIntent().getStringExtra("AccountToken"));
                            intent.putExtra("appid", getIntent().getStringExtra("appid"));
                            intent.putExtra("areaid", getIntent().getStringExtra("areaid"));
                            intent.putExtra("openid", getIntent().getStringExtra("openid"));
                            intent.putExtra("nikeName", getIntent().getStringExtra("nikeName"));
                            intent.putExtra("avatarUrl", getIntent().getStringExtra("avatarUrl"));
                            intent.putExtra("gameVersion", getIntent().getStringExtra("gameVersion"));
                            intent.putExtra("unityVersion", getIntent().getStringExtra("unityVersion"));
                            intent.putExtra("position", 3);
                            intent.putExtra("serverIps", getIntent().getStringArrayListExtra("serverIps"));
                            intent.putExtra("gameUid", getIntent().getStringExtra("gameUid"));
                            intent.putExtra("userLevel", getIntent().getIntExtra("userLevel", 0));
                            intent.putExtra("config_info", getIntent().getByteArrayExtra("config_info"));
                            intent.putExtra("gender",getIntent().getIntExtra("gender", 0));
                            intent.putExtra("pop_banner_type",getIntent().getStringExtra("pop_banner_type"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            int result = DLPluginManager.getInstance(that).startPluginActivity(that, intent);
                            TLog.e(TAG, "start result is " + result);
                        } catch (Exception e) {
                            TLog.e(TAG, "mBottomOperateView onClick error : " + e.getMessage());
                        }
                    }
                }catch (Exception e){
                    TLog.e(TAG,"setPopBannerListener onDialogExit error : "+e.getMessage());
                }
            }
        });
        mPopTvBannerManager.getOperateData();
    }

    public void releaseBanner(){
        mPopTvBannerManager = null;
    }


    public void releaseExitAnim(){
        mExitAnimManager = null;
    }
}
