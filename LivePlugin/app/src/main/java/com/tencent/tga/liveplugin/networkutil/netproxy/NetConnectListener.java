package com.tencent.tga.liveplugin.networkutil.netproxy;

/**
 * Created by hyqiao on 2017/12/4.
 */

public interface NetConnectListener {
    void onSucc(int mLoginType);

    void onFail(String errMsg);
}
