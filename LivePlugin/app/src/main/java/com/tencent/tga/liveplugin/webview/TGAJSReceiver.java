package com.tencent.tga.liveplugin.webview;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.JavascriptInterface;

import java.util.ArrayList;

public class TGAJSReceiver {
    private static final String TAG = "TGAJSReceiver";

    boolean isFromSchedule = false;

    public TGAJSReceiver(boolean isFromSchedule) {
        this.isFromSchedule = isFromSchedule;
    }

    @JavascriptInterface
    public void setPageTitle(final String title) {
        if (null != LiveConfig.mLiveContext) {
            LiveConfig.mLiveContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LiveViewEvent.Companion.refreshWebViewTitle(title);
                }
            });
        }
    }

    @JavascriptInterface
    public void launchTgaVideoPlayer(String params) {
        try {
            TLog.e(TAG, "launchTgaVideoPlayer params is " + params);
            JSONObject obj = new JSONObject(params);
            final JSONArray vids = obj.getJSONArray("vids");
            final JSONArray titles = obj.getJSONArray("titles");
            final JSONArray playNums = obj.getJSONArray("playNums");
            final int index = obj.optInt("index");
            final int type = obj.optInt("type"); 
            ArrayList<String> mPlayVids = new ArrayList<>();
            ArrayList<String> mPlayTitles = new ArrayList<>();
            ArrayList<Integer> mPlayNums= new ArrayList<>();
            for (int i =0; i < vids.length(); i++) {
                mPlayVids.add((String)vids.get(i));
            }
            for (int i =0; i < titles.length(); i++) {
                mPlayTitles.add((String)titles.get(i));
            }
            for (int i =0; i < playNums.length(); i++) {
                mPlayNums.add(Integer.valueOf((String)playNums.get(i)));
            }
            if (null != LiveConfig.mLiveContext) {
                LiveConfig.mLiveContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (NoDoubleClickUtils.isDoubleClick())//
                            return;
                            LiveViewEvent.Companion.launchVideoView(isFromSchedule, mPlayVids,mPlayTitles, index, mPlayNums, type ==1);


                    }
                });
            }else{
                TLog.e(TAG, "mLivePlayerView is Null");
            }
        } catch (JSONException e) {
            TLog.e("JSON", "parse json exception");
        }
    }


    String title = "";
    String summary = "";
    String icon = "";
    String url = "";
    String types = "";
    @JavascriptInterface
    public void shareWebPage(String guessShareInfo) {
        try {
            TLog.e(TAG,"guessShareInfo : "+guessShareInfo);
            JSONObject jsonObject = new JSONObject(guessShareInfo);
            title = jsonObject.optString("title");
            summary = jsonObject.optString("summary");
            icon = jsonObject.optString("icon");
            url = jsonObject.optString("url");
            types = jsonObject.optString("types");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (null != LiveConfig.mLiveContext) {
            LiveConfig.mLiveContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShareUtils.shareGuessWebPage(LiveConfig.mLiveContext, title, summary, icon, url, types);
                }
            });
        }
    }


}
