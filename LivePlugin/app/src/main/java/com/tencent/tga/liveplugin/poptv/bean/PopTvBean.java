package com.tencent.tga.liveplugin.poptv.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by hyqiao on 2017/10/31.
 */

public class PopTvBean {

    public String mOpenID = "";
    public int mAreaID = 0;
    public String mLeagueLogo = "";
    public String mPlayerBgUrl = "";
    public String mUid = "";

    public int banner_switch;

    public String mVid = "";

    public String operation_image = "";
    public String operation_button = "";

    public JSONArray mCarIdListArray;
    public JSONArray mTrackIdListArray;

    public long pop_cd_period = 0;
    public int popup_window_match;
    public String pop_banner_type = "0";
    public String android_pop_exit_black_list = "";

    public PopTvBean(String mContent) {

        JSONObject mJsonobject = null;
        try {
            mJsonobject = new JSONObject(mContent);
            mOpenID = mJsonobject.optString("openid");
            mAreaID = mJsonobject.optInt("areaid");
            mVid = mJsonobject.optString("vid");
            mLeagueLogo = mJsonobject.optString("bk_image");
            mPlayerBgUrl = mJsonobject.optString("player_frame");
            mUid = mJsonobject.optString("uid");

            banner_switch = mJsonobject.optInt("banner_switch");

            operation_image = mJsonobject.optString("operation_image");
            operation_button = mJsonobject.optString("operation_button");

            pop_banner_type = TextUtils.isEmpty(operation_image)||TextUtils.isEmpty(operation_button)?"0":"3";

            pop_cd_period = mJsonobject.optLong("pop_cd_period");

            popup_window_match = mJsonobject.optInt("popup_window_match");

            android_pop_exit_black_list = mJsonobject.optString("android_pop_exit_black_list");

            Log.e("pop_cd_period","pop_cd_period : "+pop_cd_period);
            String playInfo_str = mJsonobject.optString("playInfo");
            if(!TextUtils.isEmpty(playInfo_str)){
                JSONObject jsonObject_playInfo = new JSONObject(playInfo_str);
                mCarIdListArray = jsonObject_playInfo.optJSONArray("carIds");
                mTrackIdListArray = jsonObject_playInfo.optJSONArray("trackIds");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
