package com.tencent.tga.liveplugin.mina.MessageStruct;

import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;

/**
 * Created by hyqiao on 2017/8/28.
 */

public class BroadcastNotice {
    public Request request;
    public Message msg;

    public BroadcastNotice(Request request,Message msg){
        this.request = request;
        this.msg = msg;
    }
}
