package com.tencent.tga.liveplugin.live.right.schedule.proxy;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class GetHpjyScheduleListHttpProxy extends HttpBaseUrlWithParameterProxy<GetHpjyScheduleListHttpProxy.Param> {

    private static final String TAG = "GetHpjyScheduleListHttpProxy";

    @Override
    protected String getUrl(Param param) {
        String url = HttpUtil.getHttpUrl("getHpjyScheduleList")+getParameter(param);
        TLog.e(TAG,"getUrl : "+url);
        return url;
    }

    @Override
    protected byte[] convertParamToPbReqBuf(Param param) {
        String data;

        JSONObject j = new JSONObject();
        try {
            //j.put("userid",param.userid);
            //j.put("seasonid", param.seasonid);
            j.put("gameid",param.gameid);

            if (param.stage!=null && !param.stage.equals("")){
                j.put("stage",param.stage);
            }
            j.put("direction",param.direction);
            if (param.direction!=0){
                j.put("last_match_day",param.last_match_day);
            }
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
    protected int parsePbRspBuf(byte[] bytes, Param param) {
        try {
            param.data = new String(bytes,"utf-8");
            JSONObject object = new JSONObject(param.data);
            param.result = object.optInt("result");

            TLog.e(TAG,"data : " + param.data);
            if (param.result!=0){
                param.msg = object.optString("msg");
            }
            param.is_finish = object.optInt("is_finish");
            param.matchDayInfo = object.optString("match_day_list");
            //param.getMultiMatchScheduleListRsp = WireHelper.wire().parseFrom(bytes, GetMultiMatchScheduleListRsp.class);
        } catch (Exception e) {
            //e.printStackTrace();
            TLog.e(TAG,"解析失败 : " + e.getMessage());
            //param.getMultiMatchScheduleListRsp = null;
        }
        return 0;
    }

    public static class Param{

        //in
        //public String userid ;
        //public String seasonid ;
        public String gameid;
        public String stage;//赛事阶段id 不填表示拉取全部赛事
        public int direction;//翻页方向,0当前默认,-1向前,1向后,非0时last_match_day必填
        public int last_match_day;//分页的最近比赛日

        //out
        //public GetMultiMatchScheduleListRsp getMultiMatchScheduleListRsp;
        public String data;
        public int result; //0成功；非0失败；
        public String msg; //错误消息
        public int is_finish;//是否已完成, 0否 1是

        public String matchDayInfo;

    }
}
