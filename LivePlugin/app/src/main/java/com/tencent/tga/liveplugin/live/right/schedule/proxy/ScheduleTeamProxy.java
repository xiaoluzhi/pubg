package com.tencent.tga.liveplugin.live.right.schedule.proxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.base.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by WeiLong on 2020/1/16.
 */

public class ScheduleTeamProxy extends HttpBaseUrlWithParameterProxy<ScheduleTeamProxy.Param> {

    private static final String TAG = "ScheduleTeamProxy";

    @Override
    protected String getUrl(ScheduleTeamProxy.Param param) {
        return HttpUtil.getHttpUrl("getHpjyTeamByMatchid") + getParameter(param);
    }

    @Override
    protected byte[] convertParamToPbReqBuf(ScheduleTeamProxy.Param param) {

        String data;

        JSONObject j = new JSONObject();
        try {
            j.put("gameid", Configs.GAME_ID);
            j.put("matchid",param.matchid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data = j.toString();
        LOG.e(TAG, "req config data =" + data);
        try {
            return data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    protected int parsePbRspBuf(byte[] data, ScheduleTeamProxy.Param param) {
        try {
            param.response = new String(data, "utf-8");
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.e(TAG, "json :" + e.getMessage());
        }

        return 0;
    }

    public static class Param {
        public String response = "";
        public String matchid="";
    }
}

