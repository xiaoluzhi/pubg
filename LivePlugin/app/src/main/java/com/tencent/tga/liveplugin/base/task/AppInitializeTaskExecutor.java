package com.tencent.tga.liveplugin.base.task;

import android.content.Context;
import android.os.Environment;

import com.loopj.android.tgahttp.Configs.Configs;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.mina.MinaManager;
import com.tencent.tga.liveplugin.mina.interfaces.SocketSuccListener;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.networkutil.netproxy.MinaConnectControl;
import com.tencent.tga.liveplugin.networkutil.netproxy.NetConnectListener;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.net.encrypt.MinaNetworkEngine;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hyqiao on 2016/4/6.
 */
public class AppInitializeTaskExecutor extends ConcurrentTaskExecutor {
    public Context mContext;

    public AppInitializeTaskExecutor(Context context) {
        this.mContext = context;
        addDefaultTask();
    }

    private void addDefaultTask() {
        addTask(new LogInitializeTask());
        addTask(new ReportInitializeTask(mContext));
        addTask(new MinaNetworkInitializeTask(mContext));
        addTask(new ImageLoaderInitializeTask(mContext));
    }

    public static String mLogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "tencent" + File.separator + "tga" + File.separator + "liveplugin" + File.separator + "log";
    class LogInitializeTask extends Task {
        public final static String tag = "LogInitializeTask";

        @Override
        protected void run() {
            TLog.d(tag, "LogInitializeTask.......");
            if(Configs.Debug){
                TLog.enableFileAppender(true,mLogPath);
                TLog.enableDebug(true);
            }
        }
    }

    class ReportInitializeTask extends Task {
        public final static String tag = "ReportInitializeTask";
        private Context mContext;

        public ReportInitializeTask(Context context) {
            mContext = context;
        }
        @Override
        protected void run() {
            TLog.d(tag, "ReportInitializeTask.......");
            ReportManager.getInstance().init(mContext);
        }
    }


    class ImageLoaderInitializeTask extends WrappedContextTask {
        public final static String tag = "imageloader";

        private Context mContext;

        public ImageLoaderInitializeTask(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void run() {
            ImageLoaderUitl.getmInstance().init(mContext);
        }
    }

    /**
     * 网络
     */
    private boolean isFirst = true;
    class MinaNetworkInitializeTask extends WrappedContextTask {
        public static final String tag = "MinaNetworkInitializeTask";
        private Context mContext;

        public MinaNetworkInitializeTask(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void run() {
            TLog.e(tag, "MinaNetworkInitializeTask  run");
            MinaNetworkEngine.init();

            if(Configs.isUseTestIP){
                ArrayList<String> list = new ArrayList<>();
                UserInfo.getInstance().mIpList.clear();
                list.add(Configs.test_ip);
                UserInfo.getInstance().mIpList.addAll(list);
            }
            TLog.e(tag, "UserInfo.getInstance().mIpList : Configs.test_ip "+Configs.test_ip);
            if(UserInfo.getInstance().mIpList == null || UserInfo.getInstance().mIpList.size() == 0){
                TLog.e(tag, "UserInfo.getInstance().mIpList : null");
                UserInfo.getInstance().mIpList.addAll(Configs.ip_list);
            }

            MinaManager.getInstance().Init(mContext,UserInfo.getInstance().mIpList,UserInfo.getInstance().mPortList);
            MinaManager.getInstance().connectSocket(new SocketSuccListener() {
                @Override
                public void onSucc() {
                    TLog.e(tag,"MinaManager connectSocket onSucc");
                    MinaConnectControl minaConnectControl = new MinaConnectControl(mContext);
                    minaConnectControl.connect();
                    minaConnectControl.setNetConnectListener(new NetConnectListener() {
                        @Override
                        public void onSucc(int mLoginType) {
                            TLog.e(tag,"MinaConnectControl onSucc : "+mLoginType);
                            if(isFirst){
                                isFirst = false;
                                reportEnterTVInfo();
                            }
                        }

                        @Override
                        public void onFail(String errMsg) {
                            TLog.e(tag,"MinaConnectControl onFail : "+errMsg);
                        }
                    });
                }
            });
            TLog.e(tag, "MinaNetworkInitializeTask  init  success");
        }
    }

    public static void reportEnterTVInfo(){
        ReportManager.getInstance().report_EnterTVInfo(UserInfo.getInstance().mPosition);
    }
}








