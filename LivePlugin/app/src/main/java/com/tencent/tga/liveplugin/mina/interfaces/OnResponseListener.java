package com.tencent.tga.liveplugin.mina.interfaces;

import com.tencent.tga.liveplugin.mina.MessageStruct.BroadcastNotice;
import com.tencent.tga.liveplugin.mina.MessageStruct.ConnectTypeNotice;
import com.tencent.tga.liveplugin.mina.MessageStruct.RspNotice;

/**
 * Created by hyqiao on 2017/11/30.
 */

public interface OnResponseListener {
    void onResponse(RspNotice rspNotice);//业务请求回调
    void onBroadcast(BroadcastNotice broadcastNotice);//广播回调
    void onConnectType(ConnectTypeNotice connectTypeNotice);//Socket状态回调
}
