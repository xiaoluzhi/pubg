package com.tencent.tga.liveplugin.networkutil;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.mina.MinaManager;
import com.tencent.tga.net.encrypt.MinaMessageHandler;

import static com.tencent.tga.liveplugin.networkutil.NetProxy.Callback.ERROR_CODE_UNREACHABLENET;
import static com.tencent.tga.liveplugin.networkutil.NetProxy.Callback.ERROR_CONN_EXCEPTION;
import static com.tencent.tga.liveplugin.networkutil.NetProxy.Callback.ERROR_NOT_CONN;

/**
 * Created by lionljwang on 2016/3/25.
 */
public abstract class NetProxy<Param> {
    private static final String TAG = "NetProxy";

    private int mTimeOutPeriod = 10000;//超时时间设置
    public interface Callback {
        int CODE_SUCCESS = 0;
        int ERROR_CODE_SERVER_ERROR = -1;
        int ERROR_CODE_TIMEOUT = -2;
        int ERROR_CODE_UNREACHABLENET = -3;

        int ERROR_NOT_CONN = -4;
        int ERROR_CONN_EXCEPTION = -5;
        int ERROR_UNINIT = -6;

        void onSuc(int code);
        void onFail(int errorCode);
    }

    protected abstract int getCmd();

    protected abstract int getSubcmd();

    protected abstract Request convertParamToPbReqBuf(Param param);

    protected abstract int parsePbRspBuf(Message msg, Param param);

    public void postReq(final Callback callback, final Param param){
        postReq_new(callback,param,0);
    }


    public void postReq_new(final Callback callback, final Param param,final int index) {
        TLog.e(TAG,String.format("postReq index = %s ; getCmd = %s ; getSubcmd = %s",index,getCmd(),getSubcmd()));
        try{
            int ret = MinaManager.getInstance().sendRequest(getCmd(), getSubcmd(),
                    convertParamToPbReqBuf(param).payload,
                    new MinaMessageHandler() {
                        @Override
                        public void OnOtherError(int type) {
                            TLog.i(TAG, "NetProxy--网络访问返回错误码--" + type);
                            callback.onFail(type);
                        }

                        @Override
                        public boolean match(int command, int subcmd, int seq) {
                            return false;
                        }

                        @Override
                        public void onMessage(Request request, Message msg) {
                            if(msg!=null){
                                if (msg.result ==0) {
                                    TLog.e(TAG, "getCmd() = " + getCmd() + " getSubcmd() = " + getSubcmd() + " msg.sequenceNumber = " + msg.sequenceNumber);
                                    try {
                                        parsePbRspBuf(msg, param);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    callback.onSuc(Callback.CODE_SUCCESS);
                                } else {
                                    //ReportHelp.reportNetError("2",getCmd()+"",getSubcmd()+"",msg.sequenceNumber+"");
                                    callback.onFail(Callback.ERROR_CODE_SERVER_ERROR);
                                    TLog.i(TAG, "NetProxy--网络访问返回错误码--" + msg.result);
                                }
                            }

                        }

                        @Override
                        public void onTimeout(Request request) {
                            TLog.e(TAG, "onTimeout.....");
                            // TODO: 2016/7/27   应用层重连逻辑 ，需要context
                            if (LiveConfig.mLiveContext ==null )return;

                            if (NetUtils.isNetworkAvailable(LiveConfig.mLiveContext))
                            {
                                if(index == 0){
                                    postReq_new(callback,param,1);
                                }else{
                                    callback.onFail(Callback.ERROR_CODE_TIMEOUT);
                                }

                            }else{
                                callback.onFail(ERROR_CODE_UNREACHABLENET);
                            }
                        }
                    },mTimeOutPeriod);

            if(ret == ERROR_CODE_UNREACHABLENET ||
                    ret == ERROR_CONN_EXCEPTION ||
                    ret == ERROR_NOT_CONN){
                TLog.e(TAG,"netproxy error ret = "+ret);
                //网络无效、没有连接、连接异常立即返回
                callback.onFail(ret);
            }
        }catch (Exception e){
            TLog.e(TAG, "NetProxy-postReq-网络访问失败-" + e.getMessage());
        }
    }

    public void postReqWithOutRepeat(final Callback callback, final Param param){
        postReq_new(callback,param,1);
    }

    public void postReqWithSignature(final Callback callback, final Param param) {
        postReq_new(callback,param,1);
    }

}
