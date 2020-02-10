package com.tencent.tga.liveplugin.notificenter;

import com.tencent.tga.liveplugin.poptv.LivePlayerPanel;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by lionljwang on 2016/9/12.
 */
public class PluginNotifi {
    private static final String TAG = "PluginNotifi";
    public static void windowPlaer(final Activity activity,final Resources resources,final String content){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LivePlayerPanel livePlayerPanel = new LivePlayerPanel(activity, resources,content);
                livePlayerPanel.luanch();
//                LivePlayerPanelNew livePlayerPanel = new LivePlayerPanelNew(activity, resources,content);
//                livePlayerPanel.launch();
            }
        });

    }



}
