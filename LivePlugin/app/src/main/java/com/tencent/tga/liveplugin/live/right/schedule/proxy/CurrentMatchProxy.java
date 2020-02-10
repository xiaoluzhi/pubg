package com.tencent.tga.liveplugin.live.right.schedule.proxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.base.util.HttpUtil;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.Sessions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CurrentMatchProxy extends HttpBaseUrlWithParameterProxy<CurrentMatchProxy.Param> {

    private static final String TAG = "CurrentMatchProxy";

    @Override
    protected String getUrl(CurrentMatchProxy.Param param) {
        return HttpUtil.getHttpUrl("getHpjyRoomList") + getParameter(param);
    }

    @Override
    protected byte[] convertParamToPbReqBuf(CurrentMatchProxy.Param param) {

        String data;

        JSONObject j = new JSONObject();
        try {
            j.put("user_id", Sessions.globalSession().getUserId());
            j.put("game_id", Configs.GAME_ID);
            j.put("account_type", UnityBean.getmInstance().accountType);
        } catch (JSONException e) {
            // ignore
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
    protected int parsePbRspBuf(byte[] data, CurrentMatchProxy.Param param) {
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
        //out
    }
}
