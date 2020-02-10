package com.tencent.tga.liveplugin.base;

import com.loopj.android.tgahttp.Configs.Configs;
import com.ryg.utils.LOG;
import com.tencent.qqlive.multimedia.TVKSDKMgr;
import com.tencent.tga.liveplugin.base.task.AppInitializeTaskExecutor;

import android.content.Context;
import android.text.TextUtils;


/**
 * Created by lionljwang on 2016/7/15.
 */
public class LivePlugin {

    private static LivePlugin mInstance;

    private LivePlugin(){

    }

    public static synchronized LivePlugin getInstance(){
        if (mInstance == null){
            mInstance = new LivePlugin();
        }
        return mInstance;
    }

    private static boolean isInist = false;

    /***
     * 初始化腾讯视频播放器
     * @param context
     */
    public void initTXPlayerSdk(Context context){
        if (context == null)
            return;

        if (isInist)
            return;

        isInist = true;


        if (Configs.isTXPlayerLog)
            TVKSDKMgr.setDebugEnable(true);
        else
            TVKSDKMgr.setDebugEnable(false);
        String packageName = context.getPackageName();

        if (TextUtils.isEmpty(packageName) ||LivePluginConstant.PUBG_PACKAGE_FORMAL.equals(packageName)){
            TVKSDKMgr.initSdk(context, LivePluginConstant.TX_PLAYER_KEY_FORMAL, "");
        }else if (LivePluginConstant.PUBG_PACKAGE_DEMO.equals(packageName)) {
            TVKSDKMgr.initSdk(context, LivePluginConstant.TX_PLAYER_KEY_DEMO, "");
        }else if (LivePluginConstant.PUBG_PACKAGE_TIYAN.equals(packageName)) {
            TVKSDKMgr.initSdk(context, LivePluginConstant.TX_PLAYER_KEY_TIYAN, "");
        } else {
            TVKSDKMgr.initSdk(context, LivePluginConstant.TX_PLAYER_KEY_FORMAL, "");
        }
        LOG.d("LivePlugin","packageName ....===="+packageName);
    }


    public void init(Context context){
        AppInitializeTaskExecutor executor = new AppInitializeTaskExecutor(context);
        executor.execute();
    }

    public void unInit(){
        mInstance = null;
    }
}
