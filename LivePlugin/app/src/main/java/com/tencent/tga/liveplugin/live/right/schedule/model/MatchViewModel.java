package com.tencent.tga.liveplugin.live.right.schedule.model;


import android.content.Context;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.protocol.tga.matchsubscribe.EnterType;
import com.tencent.tga.liveplugin.base.mvp.BaseModelInter;
import com.tencent.tga.liveplugin.base.mvp.BasePresenter;
import com.tencent.tga.liveplugin.live.right.schedule.proxy.MatchSubscribeProxy;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.Sessions;

/**
 * Created by hyqiao on 2017/4/4.
 */

public class MatchViewModel extends BaseModelInter {

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    public MatchSubscribeProxy matchSubscribeProxy = new MatchSubscribeProxy();
    public MatchSubscribeProxy.Param matchSubscribeProxyParam = new MatchSubscribeProxy.Param();

    public void doSubscribe(Context context, NetProxy.Callback callback, String match_date, String match_id, int op){

        try {
            matchSubscribeProxyParam.user_id = new String(Sessions.globalSession().getUid(), "utf-8");
            matchSubscribeProxyParam.openid = new String(Sessions.globalSession().getOpenid(), "utf-8");
            matchSubscribeProxyParam.match_id = match_id;
            matchSubscribeProxyParam.operation_type = op;
            matchSubscribeProxyParam.entry_type = EnterType.HpjyNormal.getValue();
            matchSubscribeProxyParam.game_id = Configs.GAME_ID;
            matchSubscribeProxy.postReq(callback,matchSubscribeProxyParam);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
