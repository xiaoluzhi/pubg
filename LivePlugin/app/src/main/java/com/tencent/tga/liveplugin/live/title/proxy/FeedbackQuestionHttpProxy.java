package com.tencent.tga.liveplugin.live.title.proxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.HttpUtil;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class FeedbackQuestionHttpProxy extends HttpBaseUrlWithParameterProxy<FeedbackQuestionHttpProxy.Param> {
    private static final String TAG = "FeedbackQuestionHttpProxy";

    @Override
    protected String getUrl(Param param) {
        String url = HttpUtil.getHttpUrl("postHpjyUserFeedback")+getParameter(param);
        TLog.e(TAG,"getUrl : "+url);
        return url;
    }

    @Override
    protected byte[] convertParamToPbReqBuf(Param param) {
        String data;

        JSONObject j = new JSONObject();
        try {
            j.put("GameVersion", UnityBean.getmInstance().gameVersion);
            j.put("GameId",Configs.GAME_ID);
            j.put("MachineCode", VersionUtil.getMachineCode(LiveConfig.mLiveContext));
            j.put("Nick", UnityBean.getmInstance().nikeName);
            j.put("SdkUserId", Sessions.globalSession().getUserId());
            j.put("FeedbackType",param.FeedbackType);
            j.put("FeedbackContent",param.FeedbackContent);
            j.put("FeedbackDetail",param.FeedbackDetail);
            j.put("ContactAccount",param.ContactAccount);
            j.put("Source", "1");
            j.put("Roomid", LiveInfo.mRoomId);
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
        public int FeedbackType;
        public String FeedbackContent;
        public String FeedbackDetail;
        public String ContactAccount;

        public String response;
    }


}
