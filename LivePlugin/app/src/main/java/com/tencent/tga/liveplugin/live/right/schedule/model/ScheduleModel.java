package com.tencent.tga.liveplugin.live.right.schedule.model;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.tga.liveplugin.base.mvp.BaseModelInter;
import com.tencent.tga.liveplugin.base.mvp.BasePresenter;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.right.schedule.proxy.GetHpjyScheduleListHttpProxy;
import com.tencent.tga.liveplugin.live.right.schedule.proxy.MultiMatchScheduleListProxy;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.UserInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by lionljwang on 2017/3/30.
 */
public class ScheduleModel extends BaseModelInter {

    public static int FORWARD = -1;
    public static int BACKWARD = 1;
    public static int DEFAULT_VALUE = 0;
    public void reqMatchList(NetProxy.Callback callback, int date, int direction){
        //callback.onSuc(1);
        //callback.onFail(-1);
        mProxyHolder.multiMatchScheduleListProxyParam.page_match_day = date;
        mProxyHolder.multiMatchScheduleListProxyParam.direction = direction;
        mProxyHolder.multiMatchScheduleListProxyParam.account_type = UnityBean.getmInstance().accountType;
        if(!TextUtils.isEmpty(UserInfo.getInstance().mGameid) && Sessions.globalSession().uuid != null){
            mProxyHolder.multiMatchScheduleListProxyParam.game_id = UserInfo.getInstance().mGameid;
            try {
                mProxyHolder.multiMatchScheduleListProxyParam.user_id = new String(Sessions.globalSession().uuid,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mProxyHolder.multiMatchScheduleListProxy.postReq(callback,mProxyHolder.multiMatchScheduleListProxyParam);
        }else {
            callback.onFail(-1);//参数为空
        }
    }

    public void reqMatchList(Context context,HttpBaseUrlWithParameterProxy.Callback callback, int date, int direction){
        //callback.onSuc(1);
        //callback.onFail(-1);
        try {
            mProxyHolder.getHpjyScheduleListHttpProxyParam.last_match_day = date;
            mProxyHolder.getHpjyScheduleListHttpProxyParam.direction = direction;
            if(!TextUtils.isEmpty(UserInfo.getInstance().mGameid) && Sessions.globalSession().uuid != null){
                mProxyHolder.getHpjyScheduleListHttpProxyParam.userid = new String(Sessions.globalSession().getUid(), "utf-8");
                mProxyHolder.getHpjyScheduleListHttpProxy.postReq(context,callback,mProxyHolder.getHpjyScheduleListHttpProxyParam);
            }else {
                callback.onFail(-1);//参数为空
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ProxyHolder mProxyHolder = new ProxyHolder();

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    public static class ProxyHolder {

        public MultiMatchScheduleListProxy multiMatchScheduleListProxy = new MultiMatchScheduleListProxy();
        public MultiMatchScheduleListProxy.Param multiMatchScheduleListProxyParam = new MultiMatchScheduleListProxy.Param();

        public GetHpjyScheduleListHttpProxy getHpjyScheduleListHttpProxy = new GetHpjyScheduleListHttpProxy();
        public GetHpjyScheduleListHttpProxy.Param getHpjyScheduleListHttpProxyParam = new GetHpjyScheduleListHttpProxy.Param();
    }
}
