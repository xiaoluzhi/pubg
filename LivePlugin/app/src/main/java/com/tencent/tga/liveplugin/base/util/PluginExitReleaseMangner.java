package com.tencent.tga.liveplugin.base.util;

import android.content.Context;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.LivePlugin;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.util.SoftUitl;
import com.tencent.tga.liveplugin.live.title.view.FeedbackDialog;
import com.tencent.tga.liveplugin.mina.MinaManager;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;
import com.tencent.tga.liveplugin.networkutil.broadcast.NetBroadHandeler;
import com.tencent.tga.liveplugin.networkutil.netproxy.LogoutProxy;
import com.tencent.tga.liveplugin.base.notification.NotificationCenter;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.liveplugin.report.VideoMonitorReport;
import com.tencent.tga.net.encrypt.MinaNetworkEngine;

import java.io.UnsupportedEncodingException;

/**
 *插件相关引用的释放
 * Created by hyqiao on 2017/5/4.
 */

public class PluginExitReleaseMangner {
    private static String TAG = "PluginExitReleaseMangner";
    public static void release(Context mContext,String mRoomId){

        TLog.e(TAG,"PluginExitReleaseMangner release");

        releaseNetworkEngine(mContext,mRoomId);

        releaseDialog();

        releaseKeyBoard(mContext);

        releaseImageLoader();

        ThreadPoolManager.getInstance().unInit();

        releaseOther(mContext);

        LiveConfig.mWebViewContext = null;
    }

    /**
    * 有些机型退出有问题,所以手动释放dialog
    * @author hyqiao
    * @time 2017/5/4 17:01
    */
    private static void releaseDialog(){
        try{
            if (FeedbackDialog.dialog_builder != null) {
                FeedbackDialog.dialog_builder.releaseDialog();
            }

        }catch (Exception e){
            TLog.e(TAG,"Dialog release error : "+e.getMessage());
        }
    }


    private static void releaseImageLoader(){
        try{
            ImageLoaderUitl.getmInstance().unInit();
        }catch (Exception e){
            TLog.e(TAG,"ImageLoader release error : "+e.getMessage());
        }
    }

    /**
    * 键盘导致内存溢出
    * @author hyqiao
    * @time 2017/5/4 16:41
    */
    private static void releaseKeyBoard(Context mContext){
        try{
            SoftUitl.fixInputMethodManagerLeak(mContext);
        }catch (Exception e){
            TLog.e(TAG,"SoftUitl release error : "+e.getMessage());
        }
    }


    /**
    * 网络模块相关释放(有先后顺序)：
     * 1、释放前首先进行数据上报
     * 2、数据上报释放
     * 3、等待100ms，为数据上报预留时间
     * 4、长连接登出
     * 5、网络引擎释放
    * @author hyqiao
    * @time 2017/5/4 16:42
    */
    private static void releaseNetworkEngine(final Context mContext,final String mRoomId){
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TLog.e(TAG,"NetConnectLogout begin ");
                    ReportManager.getInstance().reportAllWithoutLimit();

                    //数据上报释放
                    ReportManager.getInstance().unInit();

                    //等待，为数据上报预留时间
                    try {
                        Thread.sleep(20);

                        //长连接登出
                        NetConnectLogout(mContext);

                        Thread.sleep(20);
                        TLog.e(TAG,"NetConnectLogout finish ");

                        //网络引擎释放
                        MinaManager.getInstance().unInit();
                        MinaNetworkEngine.shareEngine().unInit();

                    } catch (Exception e) {
                        //e.printStackTrace();
                        TLog.e(TAG,"NetworkEngine release error : "+e.getMessage());
                    }

                }
            }).start();

        }catch (Exception e){
            TLog.e(TAG,"NetworkEngine release error : "+e.getMessage());
        }
    }
    /**
    * 长连接登出
    * @author hyqiao
    * @time 2017/5/4 15:20
    */
    private static void NetConnectLogout(Context mContext) {
        //长连接登出
        LogoutProxy logoutProxy = new LogoutProxy();
        LogoutProxy.Param logoutProxyParam = new LogoutProxy.Param();

        try {
            logoutProxyParam.openid = new String(Sessions.globalSession().getOpenid(), "utf-8");
            logoutProxyParam.uuid = new String(Sessions.globalSession().getUid(), "utf-8");
            logoutProxyParam.access_token = new String(Sessions.globalSession().getAccess_token(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logoutProxyParam.machine_code = VersionUtil.getMachineCode(mContext);
        logoutProxy.postReqWithOutRepeat(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                TLog.e(TAG, "退出长连接成功 " + code);
            }
            @Override
            public void onFail(int errorCode) {
                TLog.e(TAG, "退出长连接失败 " + errorCode);
            }
        }, logoutProxyParam);
    }

    /**
    * 内存泄漏的释放
    * @author hyqiao
    * @time 2018/4/11 11:10
    */
    private static void releaseOther(Context mContext){
        try{
            VideoMonitorReport.getInstance(mContext).unInit();

            NetBroadHandeler.getInstance().unInit();

            Sessions.globalSession().unInit();

            UserInfo.getInstance().unInit();

            LivePlugin.getInstance().unInit();

            NotificationCenter.defaultCenter().unInit();
        }catch (Exception e){
            TLog.e(TAG,"releaseOther error : "+e.getMessage());
        }
    }
}
