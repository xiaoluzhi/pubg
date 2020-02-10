package com.tencent.tga.liveplugin.live.common.proxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.base.util.HttpUtil;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;

import java.io.UnsupportedEncodingException;

/**
* 拉取配置
* @author hyqiao
* @time 2019/4/1 19:32
*/
public class UpdateProxy extends HttpBaseUrlWithParameterProxy<UpdateProxy.Param> {

    private static final String TAG = "UpdateProxy";

    @Override
    protected String getUrl(Param param) {
        return HttpUtil.getHttpUrl("getHpjyCfg") + getParameter(param);
    }

    @Override
    protected byte[] convertParamToPbReqBuf(Param param) {

        String data;

        JSONObject j = new JSONObject();
        try {
            j.put("config_key", param.reqKeyJSONArray);
            j.put("uid", UnityBean.getmInstance().gameUid);
            j.put("ver", String.valueOf(Configs.plugin_version));
            j.put("os_ver", String.valueOf(Build.VERSION.SDK_INT));
            j.put("model", Build.MODEL);
            j.put("machine_code", Build.MANUFACTURER);
            j.put("client_type", Configs.CLIENT_TYPE);
            j.put("area_id", UnityBean.getmInstance().areaid);
            j.put("sourceid", Configs.SOURCE_ID);
            j.put("openid", UnityBean.getmInstance().openid);
            j.put("account_type", String.valueOf(UnityBean.getmInstance().accountType));
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
    protected int parsePbRspBuf(byte[] data, Param param) {
        try {
            param.response = new String(data, "utf-8");
            LOG.e(TAG, "json :" + param.response);
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.e(TAG, "json :" + e.getMessage());
        }

        return 0;
    }

    public static class Param {
        public JSONArray reqKeyJSONArray = new JSONArray();
        public String response = "";
        //out
    }
}
