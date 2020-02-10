package com.tencent.tga.liveplugin.poptv;

import com.ryg.DLCallBackManager;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.LivePlugin;
import com.tencent.tga.liveplugin.base.support.Plugin;
import com.tencent.tga.liveplugin.base.support.PluginLifecycle;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.ThreadPoolManager;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.networkutil.SPUtils;
import com.tencent.tga.liveplugin.poptv.bean.PopTvBean;
import com.tencent.tga.liveplugin.poptv.manager.ExitAnimManager;
import com.tencent.tga.liveplugin.poptv.manager.PopStateViewManager;
import com.tencent.tga.liveplugin.poptv.manager.PopTvBannerManager;
import com.tencent.tga.liveplugin.poptv.manager.PopTvLpManager;
import com.tencent.tga.liveplugin.poptv.manager.PopTvPalyerManager;
import com.tencent.tga.liveplugin.report.PopTvReport;
import com.tencent.tga.plugin.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lionljwang on 2016/9/14.
 */
public class LivePlayerPanel {
    private static final String TAG = "LivePlayerPanel";

    private Activity mActivity;
    private Resources mResources;
    private PopTvBean mPopTvBean;

    private String mMatchInfo = "";

    private ViewGroup mVideoLayout;
    private PopStateViewManager mPopStateViewManager;
    private PopTvPalyerManager mPopTvPalyerManager;//腾讯视频播放器相关
    private ExitAnimManager mExitAnimManager;

    private PluginLifecycle pluginLifecycle = new PluginLifecycle();
    private FloatPanel floatPanel;

    private ImageView close;

    private PopTvBannerManager mPopTvBannerManager;
    public LivePlayerPanel(Activity activity, Resources resources, String content) {
        mActivity = activity;
        mResources = resources;
        mMatchInfo = content;
    }

    public void luanch() {
        try {
            if(NoDoubleClickUtils.isDoubleClick()){
                return;
            }

            initData();//解析游戏传过来的数据

            if (!PopTvLpManager.isReadyToLaunch(mActivity,mPopTvBean.pop_cd_period)) {
                return;
            }

            init();

            floatPanel = new FloatPanel(mActivity);
            Plugin.getInstance().mFloatPanel = floatPanel;
            XmlResourceParser xmlResourceParser = mResources.getLayout(R.layout.dialog_live_player);

            ViewGroup view = (ViewGroup) DLPluginLayoutInflater.getInstance(mActivity).inflate(xmlResourceParser, null);

            if (view == null)
                return;

            floatPanel.setContentView(view);

            floatPanel.showPanel();

            stopUE4Bgm();

            initUI(view);

            initBanner();//获取运营位数据

            setListener();

            initTxPlayer();

            regSDKCallback();
        } catch (Exception e) {
            LOG.e(TAG, "PluginNotifi windowPlaer strat " + e.getMessage());
        }
    }

    private void init() {

        LivePlugin.getInstance().initTXPlayerSdk(mActivity);//初始化腾讯视频

        mActivity.getApplication().registerActivityLifecycleCallbacks(pluginLifecycle);//activity生命周期依赖

        ImageLoaderUitl.getmInstance().init(mActivity);//初始化图片加载

        PopTvLpManager.initLogSwitch();
        TLog.e(TAG, "SPTAG : " + SPUtils.SPTAG);
        TLog.e(TAG, "mMatchInfo : " + mMatchInfo);
    }

    private void initData() {
        try {
            mPopTvBean = new PopTvBean(mMatchInfo);
            PopTvLpManager.mScreenHeight = DeviceUtils.getScreenHeight(mActivity);
            PopTvLpManager.mScreenWidth = DeviceUtils.getScreenWidth(mActivity);
            PopTvReport.mBannerInfo = mPopTvBean.pop_banner_type;
            LiveShareUitl.LIVE_FILE = SPUtils.SPTAG = "tga_" + mPopTvBean.mUid + mPopTvBean.mAreaID;
            PopTvConstant.initPopExitBlackList(mPopTvBean.android_pop_exit_black_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void regSDKCallback() {
        ThreadPoolManager.getInstance().executeRunnable(new Runnable() {
            @Override
            public void run() {
                DLCallBackManager.Plugin2SDK callback = new DLCallBackManager.Plugin2SDK() {
                    @Override
                    public Object callback(int i, final Map<String, Object> map) {
                        TLog.e(TAG, "callback fired " + i);
                        if (i == 7) {
                            if(null != floatPanel) floatPanel.dismissPopupWindow();
                        }
                        return null;
                    }
                };
                DLCallBackManager.setPluginCallback(callback);
            }
        });

    }

    /***游戏字体*/
    private Typeface mFont = null;
    private ViewGroup mLayoutBg;
    private RelativeLayout parent_view;
    private RelativeLayout root_view;
    private FrameLayout mRlyStateCtr;
    private RelativeLayout mRlyBottomOperation;

    private boolean isPopTvBgSucc = false;

    private void initUI(ViewGroup view) {
        parent_view = (RelativeLayout) view.findViewById(R.id.parent_view);
        root_view = (RelativeLayout) view.findViewById(R.id.root_view);
        mVideoLayout = (ViewGroup) view.findViewById(R.id.layout_videolayout);
        mRlyStateCtr = (FrameLayout) view.findViewById(R.id.mRlyStateCtr);
        close = (ImageView) view.findViewById(R.id.imageview_colse);
        mLayoutBg = (ViewGroup) view.findViewById(R.id.mLayoutBg);
        mRlyBottomOperation = (RelativeLayout) view.findViewById(R.id.mRlyBottomOperation);

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
        mVideoLayout.setBackground(mResources.getDrawable(R.drawable.player_view_background));

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
        if (mActivity == null)
            return;
        try {
            mActivity.runOnUiThread(new Runnable() {
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
            if (floatPanel != null)
//                floatPanel.onDialogExit(true);
                floatPanel.dismissPopupWindow();
        }
    };

    private void setListener() {
        close.setOnClickListener(closeListener);

        mPopStateViewManager.setPlayUnderMobileNetListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMobile();
            }
        });


        floatPanel.setFloatPanelListener(new FloatPanel.FloatPanelListener() {
            @Override
            public void onDismiss(boolean isNeedAnim) {
                if (NoDoubleClickUtils.isDoubleClick()) {
                    return;
                }
                exitRelease();

                if (isNeedAnim) {
                    //if (PopTvConstant.isExitAnimReady()) {
                    if (false) {//暂时不要退出动画
                        exitAnimation();
                        TLog.e(TAG, "floatPanel with anim");
                    } else {
                        releaseView();
                    }
                } else {
                    releaseView();
                    TLog.e(TAG, "floatPanel without anim");
                }
            }

        });
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

    private void exitRelease() {
        try {
            releaseTxPlayer();
            PopTvReport.getInstance().release();

            mActivity.getApplication().unregisterActivityLifecycleCallbacks(pluginLifecycle);
            DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
            if (callBack != null) {
                TLog.e(TAG, "onDialogExit, callback");
                HashMap map =  new HashMap<String, Object>();
                JSONObject param = new JSONObject();
                param.put("type", "1");
                map.put("param", param.toString());
                callBack.callback(DLCallBackManager.SDK2Plugin.CALL_UE4,map);
            }
            TLog.e(TAG, "弹窗资源释放成功");
        } catch (Exception e) {
            TLog.e(TAG, "弹窗资源释放失败");
        }
    }

    private void releaseOther(){
        mFont = null;
        mActivity = null;
        mResources = null;
        floatPanel = null;
        Plugin.getInstance().unInit();
        LivePlugin.getInstance().unInit();
        ImageLoaderUitl.getmInstance().unInit();
        DLCallBackManager.setPluginCallback(null);
    }


    private synchronized void exitAnimation() {
        initExitAnim();
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
            floatPanel.onDialogDestroy();
            releaseOther();
        } catch (Exception e) {
            TLog.e(TAG, "releaseView 失败");
        }
    }

    public void initPopStateViewManager(){
        mPopStateViewManager = new PopStateViewManager(mActivity,mRlyStateCtr,mFont,mResources,mMatchInfo,mPopTvBean);
        mPopStateViewManager.init();
    }

    public void releasePopStateViewManager(){
        if(mPopStateViewManager != null)
            mPopStateViewManager = null;
    }

    public void initTxPlayer(){
        mPopTvPalyerManager = new PopTvPalyerManager(mActivity,mVideoLayout,mPopStateViewManager.getPopStateView(),mPopTvBean);
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
        mPopTvBannerManager = new PopTvBannerManager(mActivity,mResources,mPopTvBean,mPopStateViewManager.getPopStateView(),mRlyBottomOperation);
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
                    floatPanel.dismissPopupWindow();
                    DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
                    if (callBack != null) {
                        try {
                            Map<String, Object> map = new HashMap<String, Object>();
                            String pop_banner_type = mPopTvBean.pop_banner_type;
                            map.put("pop_banner_type",pop_banner_type);
                            callBack.callback(DLCallBackManager.SDK2Plugin.CALL_PLAYER, map);
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

    public void initExitAnim(){
        if(mExitAnimManager != null){
            return;
        }
        mExitAnimManager = new ExitAnimManager(parent_view,root_view,mVideoLayout);
        mExitAnimManager.startExitAnimation();
        mExitAnimManager.setExitAnimFinishListener(new ExitAnimManager.ExitAnimFinishListener() {
            @Override
            public void onFinish() {
                releaseView();
            }
        });
    }

    public void releaseExitAnim(){
        mExitAnimManager = null;
    }
}
