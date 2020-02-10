package com.tencent.tga.liveplugin.webview;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.msdk.api.WGPlatform;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.liveView.LiveView;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.plugin.R;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by agneswang on 2017/3/23.
 */

public class WebViewLauncher {

    private static final String TAG = "WebViewLauncher";


    private GameCenterWebView mGameCenterWebView;

    public WebViewLauncher() {
    }

    public void launchGameCenter(LiveView liveView,String url) {
        try {
            if (null == mGameCenterWebView) {
                mGameCenterWebView = (GameCenterWebView) DLPluginLayoutInflater.getInstance(liveView.getContext())
                        .inflate(R.layout.webview_layout, null);
                liveView.addView(mGameCenterWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            if (mGameCenterWebView.getVisibility() != View.VISIBLE) {
                mGameCenterWebView.setVisibility(View.VISIBLE);
            }

            mGameCenterWebView.onCreate(liveView, generateMSDKUrl(url)); //此处不能放在子线程，否则x5浏览器会报错
            LiveViewEvent.Companion.updatePlayViewVisibility(false);
        } catch (Exception e) {
            TLog.e(TAG, "launchGameCenter exception " + e.getMessage());
        }
    }

    public void dismiss() {
        if (mGameCenterWebView.getVisibility() == View.VISIBLE) {
            LiveViewEvent.Companion.updatePlayViewVisibility(true);
            mGameCenterWebView.finish();
            PlayViewEvent.rePlay();
        }

    }

    public static String generateMSDKUrl(String url) {
        try {
            if (url.startsWith("file:///android_asset/")) return url;
            if(!url.contains("?") ){
                url = url + "?";
            }
            String originUrl = String.format(url + "&partition=%s&roleid=%s&areaid=%s", UnityBean.getmInstance().partition, UnityBean.getmInstance().gameUid, UnityBean.getmInstance().areaid);
            String resultUrl = WGPlatform.WGGetEncodeUrl(originUrl) + "&open_in_current_page=true";
            if (originUrl.contains("?") && "?".equals(resultUrl.substring(originUrl.length(), originUrl.length() + 1))) {
                resultUrl = originUrl + "&" + resultUrl.substring(originUrl.length() + 1);
            }
            TLog.e(TAG, "url is " + resultUrl);
            return resultUrl;
        } catch (Throwable e) {
            TLog.e(TAG, "generate MSDK url exception : "+e.getMessage());
            return url + "&open_in_current_page=true";
        }
    }

    public boolean handleKeyEvent(KeyEvent event) {
        if (null != mGameCenterWebView) {
            return mGameCenterWebView.handleKeyEvent(event);
        }
        return false;
    }

    public void notifyWebViewPlayStatus() {
        if (null != mGameCenterWebView)
            mGameCenterWebView.notifyWebViewPlayStatus();
    }

    public void refreshTitle(String title) {
        if (null != mGameCenterWebView) {
            mGameCenterWebView.refreshTitle(title);
        }
    }

}
