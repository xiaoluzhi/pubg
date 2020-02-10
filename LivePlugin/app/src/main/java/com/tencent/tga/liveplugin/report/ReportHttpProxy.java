package com.tencent.tga.liveplugin.report;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.LivePluginConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Created by hyqiao on 2018/5/4.
 */

public class ReportHttpProxy  extends HttpBaseUrlWithParameterProxy<ReportHttpProxy.Param> {

    private final static String TAG = "ReportHttpProxy";
    public static class Param{
        public Vector<ReportBean> list;
        public String uid;
        public String areaId;
        public String openid;
        public int result = -1;
    }

    @Override
    protected String getUrl(Param param) {
        String url = "";
        try {
            if ( Configs.isUseTestIP)
                url = LivePluginConstant.REPORT_TEST_IP_URL+getParameter(param);
            else
                url = LivePluginConstant.REPORT_DOMAIN_URL+getParameter(param);
        }catch (Exception e){
            TLog.e(TAG,"ReportHttpProxy  getUrl : "+e.getMessage());
        }
        TLog.e(TAG,"getUrl : "+url);
        return url;
    }

    @Override
    protected byte[] convertParamToPbReqBuf(Param param) {
        JSONArray ja = new JSONArray();
        try {
            if(param.list != null && param.list.size()>0){
                for(int i=0;i<param.list.size();i++){
                    ReportBean s = param.list.get(i);
                    JSONObject jo = new JSONObject();
                    jo.put("key",s.getKey());
                    jo.put("value",s.getValue());
                    ja.put(jo);
                }
            }
            TLog.e(TAG, ja.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            TLog.e(TAG,"ReportHttpProxy convertParamToPbReqBuf 数据处理异常 : "+e.getMessage());
        }
        try {
            return ja.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    protected int parsePbRspBuf(byte[] bytes, Param param) {
        try {
            TLog.e(TAG,"ReportHttpProxy  parsePbRspBuf : "+new String(bytes,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(new String(bytes,"utf-8"));
            param.result = jsonObject.optInt("result");
        } catch (Exception e) {
            TLog.e(TAG,"ReportHttpProxy parsePbRspBuf 数据处理异常 : "+e.getMessage());
            param.result = -1;
            return -1;
        }
        return 0;
    }
}
