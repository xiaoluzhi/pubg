package com.tencent.tga.liveplugin.live.gift.proxy;


import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.base.util.HttpUtil;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by lionljwang on 2016/4/4.
 */
public class TreasureBoxListProxy extends HttpBaseUrlWithParameterProxy<TreasureBoxListProxy.Param> {
    private static final String TAG = "TreasureBoxListProxy";

    public static class Param{
        //out
        public String response;
    }

    @Override
    protected String getUrl(Param param) {
        return HttpUtil.getHttpUrl("getHpjyTreasureBoxList") + getParameter(param);
    }

    @Override
    protected byte[] convertParamToPbReqBuf(Param param) {
        String data;

        JSONObject j = new JSONObject();
        try {
            j.put("mcode", VersionUtil.getMachineCode(LiveConfig.mLiveContext));
        } catch (JSONException e) {
            // ignore
            e.printStackTrace();
        }
        data = j.toString();
        LOG.e(TAG, "req treasure data =" + data);
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
            LOG.e(TAG, "treasure response =" + param.response);
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.e(TAG, "json :" + e.getMessage());
        }
        return 0;
    }
}
