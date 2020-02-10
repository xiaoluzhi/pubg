package com.tencent.tga.liveplugin.poptv.manager;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.networkutil.SPUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by hyqiao on 2017/6/14.
 */

public class PopTvLpManager {
    private final static String TAG = "PopTvLpManager";

    /**
     * 初始化UI，根据屏幕尺寸计算相应view的大小
     * @author hyqiao
     * @time 2016/10/11 19:50
     */

    //必须初始化长和宽
    public static int mScreenHeight = 0;
    public static int mScreenWidth = 0;

    //UI效果图的标准手机尺寸
    private final static int PHONE_HEIGHT = 720;
    private final static int PHONE_WIDTH = 1080;
    //弹窗背景图的长宽
    private final static int BG_HEIGHT = 488;
    private final static int BG_WIDTH = 860;
    private final static int BG_MARGIN_TOP = 94;
    //播放器的长宽
    private final static int PLAYER_HEIGHT = 440;
    private final static int PLAYER_WIDTH = 782;
    private final static int PLAYER_MARGIN_TOP = 36;
    //底部运营位的长宽
    private final static int BOTTOM_HEIGHT = 138;
    private final static int BOTTOM_WIDTH = 782;


    public static ViewGroup.MarginLayoutParams  getRootViewLp(ViewGroup root_view){
        ViewGroup.MarginLayoutParams mParamsRootView = (ViewGroup.MarginLayoutParams) root_view.getLayoutParams();
        mParamsRootView.height = ViewGroup.MarginLayoutParams.MATCH_PARENT;
        mParamsRootView.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;//layout_bg的长宽比1032 ：630（大背景图）
        TLog.e(TAG, "mParamsRootView width : " + mParamsRootView.width + "  height : " + mParamsRootView.height);
        return mParamsRootView;
    }

    public static ViewGroup.MarginLayoutParams  getParentViewLp(ViewGroup parent_view){
        ViewGroup.MarginLayoutParams mParamstParentView = (ViewGroup.MarginLayoutParams) parent_view.getLayoutParams();
        mParamstParentView.height = mScreenHeight;
        mParamstParentView.width = mScreenHeight*PLAYER_WIDTH/PLAYER_HEIGHT;//layout_bg的长宽比1032 ：630（大背景图）
        TLog.e(TAG, "mParamsRootView width : " + mParamstParentView.width + "  height : " + mParamstParentView.height);
        return mParamstParentView;
    }


    public static ViewGroup.MarginLayoutParams getmLayoutBgLp(ViewGroup mLayoutBg){
        ViewGroup.MarginLayoutParams mParamsLayoutBg = (ViewGroup.MarginLayoutParams) mLayoutBg.getLayoutParams();
        mParamsLayoutBg.height = mScreenHeight*BG_HEIGHT/PHONE_HEIGHT;
        mParamsLayoutBg.width = mParamsLayoutBg.height*BG_WIDTH/BG_HEIGHT;//layout_bg的长宽比1032 ：630（大背景图）
        mParamsLayoutBg.setMargins(0,mScreenHeight*BG_MARGIN_TOP/PHONE_HEIGHT,0,0);
        TLog.e(TAG, "layout_bg width : " + mParamsLayoutBg.width + "  height : " + mParamsLayoutBg.height);
        return mParamsLayoutBg;
    }

    public static ViewGroup.MarginLayoutParams getmBottomOperationLp(ViewGroup mLayoutBg){
        ViewGroup.MarginLayoutParams mParamsLayoutBg = (ViewGroup.MarginLayoutParams) mLayoutBg.getLayoutParams();
        mParamsLayoutBg.height = mScreenHeight*BOTTOM_HEIGHT/PHONE_HEIGHT;
        mParamsLayoutBg.width = mScreenHeight*PLAYER_HEIGHT/PHONE_HEIGHT*16/9-27;//layout_bg的长宽比1032 ：630（大背景图）
        TLog.e(TAG, "layout_bg width : " + mParamsLayoutBg.width + "  height : " + mParamsLayoutBg.height);
        return mParamsLayoutBg;
    }


    public static ViewGroup.MarginLayoutParams getVideoLayoutLp(ViewGroup mVideoLayout){
        ViewGroup.MarginLayoutParams  mParamsVideoLayout = (ViewGroup.MarginLayoutParams) mVideoLayout.getLayoutParams();
        int videoHeight = mScreenHeight*PLAYER_HEIGHT/PHONE_HEIGHT;
        mParamsVideoLayout.height = videoHeight;
        //mParamsVideoLayout.width = mParamsVideoLayout.height*PLAYER_WIDTH/PLAYER_HEIGHT;//mVideoLayout的长宽比840 ：490(播放器)
        mParamsVideoLayout.width = videoHeight*16/9-9;//mVideoLayout的长宽比16 ：9,-27校正边上透明(比例不对会有透明)
        int player_margin_top = mScreenHeight*PLAYER_MARGIN_TOP/PHONE_HEIGHT;
        mParamsVideoLayout.setMargins(0, player_margin_top, 0, 0);
        TLog.e(TAG, "mParamsVideoLayout width : " + mParamsVideoLayout.width + "  height : " + mParamsVideoLayout.height);
        return mParamsVideoLayout;
    }

    public static ViewGroup.MarginLayoutParams getDanmaLayoutLp(View mVideoLayout, int h){
        ViewGroup.MarginLayoutParams  mParamsVideoLayout = (ViewGroup.MarginLayoutParams) mVideoLayout.getLayoutParams();
        int videoHeight = mScreenHeight*PLAYER_HEIGHT/PHONE_HEIGHT;
        mParamsVideoLayout.height = videoHeight-h;
        //mParamsVideoLayout.width = mParamsVideoLayout.height*PLAYER_WIDTH/PLAYER_HEIGHT;//mVideoLayout的长宽比840 ：490(播放器)
        mParamsVideoLayout.width = videoHeight*16/9-27;//mVideoLayout的长宽比16 ：9,-27校正边上透明(比例不对会有透明)
        int player_margin_top = mScreenHeight*PLAYER_MARGIN_TOP/PHONE_HEIGHT+ h;
        mParamsVideoLayout.setMargins(0, player_margin_top, 0, 0);
        TLog.e(TAG, "mParamsVideoLayout width : " + mParamsVideoLayout.width + "  height : " + mParamsVideoLayout.height);
        return mParamsVideoLayout;
    }


    public static ViewGroup.MarginLayoutParams getUserHeroLp(ImageView imageView){
        ViewGroup.MarginLayoutParams  mParamsVideoLayout = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        int videoHeight = mScreenHeight*90/PHONE_HEIGHT;
        mParamsVideoLayout.height = videoHeight;
        mParamsVideoLayout.width = videoHeight*6;
        mParamsVideoLayout.setMargins(0, 0, 0, 25);
        TLog.e(TAG, "mParamsVideoLayout width : " + mParamsVideoLayout.width + "  height : " + mParamsVideoLayout.height);
        return mParamsVideoLayout;
    }


    /**
    * 弹窗弹出频率控制
    * @author hyqiao
    * @time 2017/6/21 10:27
    */
    public static boolean isReadyToLaunch(Context mActivity,long period){
        try{
//            if(NetUtils.NetWorkStatus(mActivity) != NetUtils.WIFI_NET){
//                if(Configs.Debug){
//                    Log.e(TAG,"not ready to launch poptv , current network state is : "+NetUtils.NetWorkStatus(mActivity));
//                }
//                return false;
//            }

            long periodLimit = 2*60*60*1000;//2小时内只能弹出一次
            if(period>0){
                periodLimit = period * 1000;
            }
            long lastShowTime = SPUtils.SPGetLong(mActivity,SPUtils.PopTvLastShowTime);
            long currentTime = System.currentTimeMillis();
            if((currentTime-lastShowTime)>periodLimit){
                SPUtils.SPSaveLong(mActivity,SPUtils.PopTvLastShowTime,currentTime);
                return true;
            }else {
                if(Configs.Debug){
                    Log.e(TAG,"not ready to launch poptv , current time interval is(s) : "+(currentTime-lastShowTime)/1000);
                }
                return false;
            }

        }catch (Exception e){
            Log.e(TAG,"isReadyToLaunch error : "+e.getMessage());
            return false;
        }
    }

    /**
     * 初始化日志，避免先弹窗的时候不打印日志
     * @author hyqiao
     * @time 2017/1/12 11:43
     */
    public static void initLogSwitch(){
        if(Configs.Debug){
            TLog.enableFileAppender(true, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "tencent" + File.separator + "tga" + File.separator + "liveplugin"+ File.separator + "log");
            TLog.enableDebug(true);
        }
    }
}
