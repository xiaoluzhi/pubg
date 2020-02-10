package com.tencent.tga.liveplugin.mina.MessageStruct;

import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;

/**
 * Created by hyqiao on 2017/8/23.
 */

public class RspNotice {
    public Request request;
    public Message msg;
    public int type;

    public RspNotice(int type,Request request,Message msg){
        this.request = request;
        this.msg = msg;
        this.type = type;
    }
}
