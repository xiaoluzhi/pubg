package com.tencent.tga.liveplugin.webview;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.msdk.webview.JsBridge;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.liveView.LiveView;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.plugin.R;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by agneswang on 2017/3/23.
 */

public class GameCenterWebView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "GameCenterWebView";

    public WebView mWebView;
    public TextView mWebTitle;
    public ImageView mBackBtn;

    public Context mContext;
    private String mRootUrl;
    public String mCurrentUrl;

    public GameCenterWebView(Context context) {
        super(context);
    }

    public GameCenterWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameCenterWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onCreate(LiveView callback, String url) {
        try {
            if (UnityBean.getmInstance().isUseApplicationContext) {
                TLog.w(TAG, "Init user application context");
                mContext = LiveConfig.mWebViewContext.getApplicationContext();
            } else {
                mContext = callback.getContext();
            }
            mRootUrl = url;
            if (null != mWebView && indexOfChild(mWebView) != -1) {
                removeView(mWebView);
            }
            mWebView = new MyWebView(mContext);

            ViewGroup.MarginLayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.topMargin = DeviceUtils.dip2px(getContext(), 30);
            addView(mWebView, 0, params);
            mBackBtn = (ImageView) findViewById(R.id.webview_back_icon);
            mWebTitle = (TextView) findViewById(R.id.webview_content_title);
            mWebTitle.setTypeface(LiveConfig.mFont);
            mBackBtn.setOnClickListener(this);
            mWebTitle.setOnClickListener(this);
            if (mWebView == null) {
                TLog.w(TAG, "Fail to instance webview!!!");
                return;
            } else {
                initWebView();
                mWebView.loadUrl(url);
            }
        } catch (Throwable e) {
            TLog.e(TAG, "init gamecenter webview excpetion");
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void initWebView() {
        if (null == mWebView) return;
        mWebView.setBackgroundColor(0xff0b1423);
        WebChromeClient client = new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (JsBridge.canResolved(message)) {
                    JsBridge.parseMessage(message);
                }

                result.confirm();
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
                Intent i = new Intent("android.intent.action.GET_CONTENT");
                i.addCategory("android.intent.category.OPENABLE");
                i.setType("*/*");

                try {
                    LiveConfig.mLiveContext.startActivityForResult(Intent.createChooser(i, "上传文件"), 0);
                } catch (ActivityNotFoundException var6) {
                    var6.printStackTrace();
                }

            }

            public Bitmap getDefaultVideoPoster() {
                try {
                    Bitmap bitmap = BitmapFactory.decodeResource(DLPluginLayoutInflater.getInstance(LiveConfig.mLiveContext).getContext().getResources(),
                            R.drawable.ic_media_video_poster);
                    return bitmap;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                try {
                    //2.MidasH5调用原生支付转发
//                    if (APMidasPayAPI.h5PayHookX5(LiveConfig.mLiveContext, mWebView, s, s1, jsResult) == 0) {
//                        jsResult.cancel();
//                        return true;
//                    }
                    //如果游戏的webview要接受JS的alert方法，直接return super的方法
                    //如果不接受，直接return true
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }
                return super.onJsAlert(webView, s, s1, jsResult);
            }
        };
        mWebView.setWebChromeClient(client);

        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint({"NewApi"})
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                TLog.d(TAG, "loading url:" + url);
                if (!url.startsWith("weixin:") && !url.startsWith("mqqapi:") && !url.startsWith("native:")) {
//                    if (!url.contains("open_in_current_page=true")) {
//                        view.loadUrl(url);
//                        return true;
//                    } else {
//                        return false;
//                    }
                    return false;
                } else {
                    try {
                        Intent intent = Intent.parseUri(url, 1);
                        intent.addCategory("android.intent.category.BROWSABLE");
                        intent.setComponent(null);
                        if (Build.VERSION.SDK_INT >= 15) {
                            intent.setSelector(null);
                        }

                        if (null != LiveConfig.mLiveContext) LiveConfig.mLiveContext.startActivity(intent);
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }

                    return true;
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (TextUtils.isEmpty(url)) return;
                    TLog.d(TAG, "onPageFinished " + view.getTitle() + " url is " + url);
//                    try {
//                        //1.初始化MidasH5
//                        APMidasPayAPI.h5PayInitX5(LiveConfig.mLiveContext,mWebView);
//                    }catch (Throwable throwable){
//                        throwable.printStackTrace();
//                    }
                    mCurrentUrl = url;
                    if (url.contains("open_in_current_page=true")) {
                        changeBackForwordBtnState(true);
                        return;
                    }
                    changeBackForwordBtnState(false);

                } catch (Exception e) {
                    TLog.e(TAG, "onPageFinished exception");
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                try {
                    if (TextUtils.isEmpty(url)) return;
                    mCurrentUrl = url;
                    dismissVideoPlayView();
                    TLog.d(TAG, "onPageStarted  url is " + "title is " + view.getTitle() + " url is " + url);
                } catch (Exception e) {
                    TLog.e(TAG, "show title exception " + e.getMessage());
                }
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Uri e = Uri.parse(url);
                    Intent intent = new Intent("android.intent.action.VIEW", e);
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException var9) {
                    TLog.e("default browser is uninstalled!");
                }

            }
        });
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibility");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
        }
        mWebView.addJavascriptInterface(new TGAJSReceiver(false), "TGAJSReceiver");

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setDefaultTextEncodingName("utf-8");
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        if (Build.VERSION.SDK_INT >= 11) {
            webSetting.setDisplayZoomControls(false);
        }

        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(9223372036854775807L);
        webSetting.setAppCachePath(mContext.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(mContext.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(mContext.getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        CookieSyncManager.createInstance(mContext);
        CookieSyncManager.getInstance().sync();
        if (mWebView.getX5WebViewExtension() != null) {
            TLog.d(TAG, "Using Tbs webview core");
            TLog.d(TAG, "Tbs core version : " + WebView.getTbsCoreVersion(mContext));
            TLog.d(TAG, "Tbs sdk version : " + WebView.getTbsSDKVersion(mContext));
        } else {
            TLog.d(TAG, "Using System webview core");
        }

        TLog.d(TAG, "Tbs useragent : " + webSetting.getUserAgentString());
    }


    private void changeBackForwordBtnState(boolean isDismiss) {
        mBackBtn.setVisibility(View.VISIBLE);
        mWebTitle.setVisibility(View.VISIBLE);
        if ((null != mWebView && !mWebView.canGoBack()) || isDismiss) {
            mBackBtn.setVisibility(View.GONE);
            mWebTitle.setVisibility(View.GONE);
        }
    }

    public void refreshTitle(String title) {
        TLog.d(TAG, "refreshTitle title is " + title + " mCurrentUrls is " + mCurrentUrl);
        if ((null != mCurrentUrl && mCurrentUrl.contains("open_in_current_page=true")))
            return;
        mBackBtn.setVisibility(View.VISIBLE);
        mWebTitle.setVisibility(View.VISIBLE);
        mWebTitle.setText(title);
    }

    public void onClick(View v) {
        try {
            int containerId = v.getId();
            if (containerId == R.id.webview_back_icon) {
                if (null != mCurrentUrl && mCurrentUrl.equals(mRootUrl) || (null != mWebView && !mWebView.canGoBack())) {
                    finish();
                    LiveConfig.mLiveContext.finish();
                } else {
                    if (null != mWebView) {
                        mWebView.goBack();
                        mBackBtn.setVisibility(View.GONE);
                        mWebTitle.setVisibility(View.GONE);
                        mWebTitle.setText("");
                    }
                    dismissVideoPlayView();
                }
            }
        }catch (Exception e){
            TLog.e(TAG,"onClick : "+e.getMessage());
        }

    }

    public void finish() {
        try {
            if (null != mWebView) {
                if (indexOfChild(mWebView) != -1) {
                    removeView(mWebView);
                }
                mWebView.stopLoading();
                mWebView.getSettings().setJavaScriptEnabled(false);
                mWebView.clearHistory();
                mWebView.removeAllViews();
                mWebView.destroy();
            }
            mWebView = null;
            setVisibility(View.GONE);
        } catch (Throwable e) {
            TLog.e(TAG, "finish webview exception " + e.getMessage());
        }
    }
    private void dismissVideoPlayView() {
        LiveViewEvent.Companion.videoFinish();
    }

    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP && null != mWebView && mWebView.canGoBack()) {
            mWebView.goBack();
            mBackBtn.setVisibility(View.GONE);
            mWebTitle.setVisibility(View.GONE);
            mWebTitle.setText("");
            dismissVideoPlayView();
            return true;
        }
        return false;
    }

    public void notifyWebViewPlayStatus() {
        if (null != mWebView) {
            mWebView.loadUrl("javascript:startToPlay()");
        }
    }
}

