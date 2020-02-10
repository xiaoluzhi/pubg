package com.tencent.tga.liveplugin.mina.handler;

/**
 * Created by hyqiao on 2017/11/28.
 */

public interface REQ_RESULT_CODE {
    int CODE_SUCCESS = 0;
    int ERROR_CODE_SERVER_ERROR = -1;
    int ERROR_CODE_TIMEOUT = -2;
    int ERROR_CODE_UNREACHABLENET = -3;

    int ERROR_NOT_CONN = -4;
    int ERROR_CONN_EXCEPTION = -5;
    int ERROR_UNINIT = -6;
}
