package com.tencent.tga.liveplugin.base.support;

import com.tencent.tga.liveplugin.poptv.FloatPanel;
import com.tencent.tga.liveplugin.poptv.LivePlayerPanelNew;


/**
 * Created by lionljwang on 2016/9/20.
 */
public class Plugin {
    private static volatile Plugin mInstance ;

    public FloatPanel mFloatPanel;

    public LivePlayerPanelNew mLivePanel;


    public static synchronized Plugin getInstance(){
        if (mInstance ==null)
        {
            mInstance = new Plugin();
        }
        return  mInstance;
    }

    private Plugin(){
    }

    public void unInit(){
        mFloatPanel = null;
        mInstance = null;
    }
}
