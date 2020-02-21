package com.tencent.tga.liveplugin.live.right.schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.liveplugin.webview.MyWebView;
import com.tencent.tga.liveplugin.webview.TGAJSReceiver;
import com.tencent.tga.liveplugin.webview.WebViewLauncher;
import com.tencent.tga.plugin.R;

/**
 * Created by hyqiao on 2018/1/30.
 */

public class ScoreRankWebView extends FrameLayout {

    private final static String TAG = "SchduleWebView";
    private Context mContext;
    private TextView mTvRankTitle;
    private ImageView mIvRankBack;
    private WebView mWvRank;
    private FrameLayout mWvRankContent;
    private String jump_url;
    private ViewGroup mParent;

    public ScoreRankWebView(Context context, ViewGroup parent, String jump_url) {
        super(context);
        this.jump_url = jump_url;
        mParent = parent;
        mCurrentUrl = jump_url;
        DLPluginLayoutInflater.getInstance(context).inflate(R.layout.dialog_schdulerank, this);
        this.mContext = context;
        initView();
        setListener();
        initData();
        if (mParent.indexOfChild(this) == -1) mParent.addView(this);
    }

    private void initView() {
        mTvRankTitle = (TextView) findViewById(R.id.mTvRankTitle);
        mIvRankBack = (ImageView) findViewById(R.id.mIvRankBack);
        mWvRankContent = (FrameLayout) findViewById(R.id.mWvRankContent);
        mTvRankTitle.setTypeface(LiveConfig.mFont);
    }

    private void setListener() {
        mIvRankBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void initData() {
        try {
            doH5Handle();
        }catch (Exception e){
            TLog.e(TAG,"initData error :"+e.getMessage());
        }
    }


    private void doH5Handle(){
        if (null == mWvRank) {
            if (UnityBean.getmInstance().isUseApplicationContext) {
                mWvRank = new MyWebView(LiveConfig.mWebViewContext.getApplicationContext());
            } else {
                mWvRank = new MyWebView(LiveConfig.mWebViewContext);
            }
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mWvRank.setLayoutParams(lp);
            mWvRankContent.addView(mWvRank);
            initWebView(mWvRank);
        }
        mWvRank.loadUrl(WebViewLauncher.generateMSDKUrl(jump_url));
    }


    private void initWebView(final WebView mWebView) {
        if (null == mWebView) return;
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint({"NewApi"})
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                TLog.d(TAG, "loading url:" + url);
                if (!url.startsWith("weixin:") && !url.startsWith("mqqapi:") && !url.startsWith("native:") ) {
//                    if (!url.contains("open_in_current_page=true")) {
//                        view.loadUrl(url);
//                        return true;
//                    } else {
//                        return false;
//                    }
                    return false;
                } else {
                    return true;
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (TextUtils.isEmpty(url)) return;
                    mCurrentUrl = url;
                    TLog.d(TAG, "onPageFinished  url is " + url);
                } catch (Exception e) {
                    TLog.e(TAG, "onPageFinished exception");
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                try {
                    if (TextUtils.isEmpty(url)) return;
                    TLog.d(TAG, "onPageStarted " + view.getTitle() + " url is " + url);
                } catch (Exception e) {
                    TLog.e(TAG, "show title exception");
                }
            }
        });
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                TLog.e("ANDROID_LAB", "TITLE=" + title);
                if(!TextUtils.isEmpty(title)) mTvRankTitle.setText(title);
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

        };
        // 设置setWebChromeClient对象
        mWebView.setWebChromeClient(wvcc);
        mWebView.addJavascriptInterface(new TGAJSReceiver(true), "TGAJSReceiver");
        if (Build.VERSION.SDK_INT >= 11) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibility");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
        }

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

    @Override
    protected void onAttachedToWindow() {
        TLog.e(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
        LiveInfo.isStopPLay = true;
        PlayViewEvent.stopPlay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TLog.e(TAG, "onDetachedFromWindow");
        try {
            if (null != mWvRank) {
                if (mWvRankContent.indexOfChild(mWvRank) != -1) {
                    mWvRankContent.removeView(mWvRank);
                }
                mWvRank.stopLoading();
                mWvRank.getSettings().setJavaScriptEnabled(false);
                mWvRank.clearHistory();
                mWvRank.removeAllViews();
                mWvRank.destroy();
            }
            mWvRank = null;
        } catch (Throwable e) {
            TLog.e(TAG, "finish webview exception " + e.getMessage());
        }

    }

    public void dismiss() {
        ReportManager.getInstance().commonReportFun("TVUserChangeRightTab", true, "2", "1");
        setVisibility(View.GONE);
        LiveInfo.isStopPLay = false;
        PlayViewEvent.rePlay();
    }

    String mCurrentUrl = "";
}
