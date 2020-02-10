package com.tencent.tga.liveplugin.live;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by lionljwang on 2017/7/17.
 */
public class LiveConfig {

    public static String TAG = "TGAPlugin";

    /**
     * 是否显示过弹幕提示
     */
    public static boolean isShowDanmuSet = false;

    public static Activity mLiveContext;

    public static Context mWebViewContext;//高版本Android24，webview会加载Activity的res（html中包含视频播放的链接），掉线版本用的that，会找不到资源

    public static boolean mLockSwitch = false;

    public static Typeface mFont;

    public static boolean isPlayOnMobileNet = false;
}
