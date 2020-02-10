package com.tencent.tga.liveplugin.base.support;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by lionljwang on 2016/9/20.
 */
public class PluginLifecycle implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "PluginLifecycle";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        TLog.e(TAG, "PluginLifecycle onActivityCreated.....");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        TLog.e(TAG, "PluginLifecycle onActivityStarted.....");
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
        TLog.e(TAG, "PluginLifecycle onActivityPaused.....");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        try {
            if (Configs.Debug)
                TLog.e(TAG, "PluginLifecycle onActivityStopped....."+activity.getComponentName().getClassName());
            if (Plugin.getInstance().mFloatPanel != null)
                Plugin.getInstance().mFloatPanel.hide();
            if (Plugin.getInstance().mLivePanel != null)
                Plugin.getInstance().mLivePanel.finish();
        }catch (Exception t){

        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        TLog.e(TAG, "PluginLifecycle onActivitySaveInstanceState.....");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        TLog.e(TAG, "PluginLifecycle onActivityDestroyed.....");

    }


}
