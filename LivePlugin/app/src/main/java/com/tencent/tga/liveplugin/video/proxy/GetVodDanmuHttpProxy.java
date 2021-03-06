package com.tencent.tga.liveplugin.video.proxy;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by hyqiao on 2018/2/5.
 */

public class GetVodDanmuHttpProxy extends HttpBaseUrlWithParameterProxy<GetVodDanmuHttpProxy.Param> {
    private static String TAG = "GetVodDanmuHttpProxy";

    @Override
    protected String getUrl(GetVodDanmuHttpProxy.Param param) {
        String url = HttpUtil.getCommonHttpUrl("getVodDanMu")+getParameter(param);
        TLog.e(TAG,"getUrl : "+url);
        return url;
    }

    @Override
    protected byte[] convertParamToPbReqBuf(GetVodDanmuHttpProxy.Param param) {
        String data;

        JSONObject j = new JSONObject();
        try {
            j.put("vid", param.vid);
            j.put("gameid", "hpjy");
        }catch (JSONException e){}
        data = j.toString();
        TLog.e(TAG, data);

        try {
            return data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    protected int parsePbRspBuf(byte[] bytes, GetVodDanmuHttpProxy.Param param) {
        //解析对象
        try {
            param.response = new String(bytes, "utf-8");
            TLog.e(TAG,"json: " + param.response);

        } catch (Throwable e) {
            TLog.e(TAG, "json :"+e.getMessage());
        }

        return 0;
    }

    public static class Param{
        public String vid ;
        public String response;
    }
}
