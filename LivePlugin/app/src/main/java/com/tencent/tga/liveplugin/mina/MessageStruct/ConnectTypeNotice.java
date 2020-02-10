package com.tencent.tga.liveplugin.mina.MessageStruct;

/**
 * Created by hyqiao on 2017/8/30.
 */

public class ConnectTypeNotice {

    public interface ConnectType{
        int CONNECT_SUCC = 1;
        int CONNECT_FAIL = -1;
        int DISCONNECT = -2;
    }
    public int type;
    public ConnectTypeNotice(int type) {
        this.type = type;
    }
}
