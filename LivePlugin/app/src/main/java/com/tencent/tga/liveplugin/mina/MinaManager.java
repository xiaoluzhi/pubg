package com.tencent.tga.liveplugin.mina;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.base.net.BroadcastHandler;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.mina.MessageStruct.BroadcastNotice;
import com.tencent.tga.liveplugin.mina.MessageStruct.ConnectTypeNotice;
import com.tencent.tga.liveplugin.mina.MessageStruct.RspNotice;
import com.tencent.tga.liveplugin.mina.handler.MinaWrapHandler;
import com.tencent.tga.liveplugin.mina.handler.REQ_RESULT_CODE;
import com.tencent.tga.liveplugin.mina.interfaces.OnResponseListener;
import com.tencent.tga.liveplugin.mina.interfaces.SocketSuccListener;
import com.tencent.tga.liveplugin.mina.proxy.HeartBeatProxy;
import com.tencent.tga.liveplugin.mina.utils.MinaConstants;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;
import com.tencent.tga.net.encrypt.MinaMessageHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hyqiao on 2017/8/22.
 */

public class MinaManager {
    private final static String TAG = MinaConstants.TAG+"MinaManager";
    private static MinaManager mMinaManager;
    private Context mContext;
    private MinaThread minaThread;
    private int seq_num = 0;
    private ConcurrentHashMap<String,MinaWrapHandler> messageHandlerHashMap;
    private List<BroadcastHandler> mBroadcastHandlers;
    public static String mDefaultUserId = "";

    private boolean hasInit = false;

    ArrayList<String> mIpList;
    ArrayList<Integer> mPortList;
    private Handler handler;
    private final static int TIMEOUT_INTERVAL = 5000;
    private final static int HELLO_INTERVAL = 270*1000;
    public synchronized static MinaManager getInstance(){
        if(mMinaManager == null){
            mMinaManager = new MinaManager();
        }
        return mMinaManager;
    }

    public void Init(Context context,ArrayList<String> iplist,ArrayList<Integer> portlist){
        this.mContext = context;
        this.mIpList = iplist;
        this.mPortList = portlist;

        TLog.e(TAG,"MinaManager init : "+hasInit);
        if(hasInit){
            return;
        }

        hasInit = true;
        Looper looper = Looper.getMainLooper();
        handler = new Handler(looper);

        messageHandlerHashMap = new ConcurrentHashMap();
        mBroadcastHandlers = Collections.synchronizedList(new ArrayList());

        mDefaultUserId = VersionUtil.getMachineCode(mContext);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);


        lastBroadCastTime = System.currentTimeMillis();
        lastBroadCastNetState = NetUtils.NetWorkStatus(mContext);
        mContext.registerReceiver(mNetStateChangeReceiver, mFilter);
    }


    public void unInit(){
        TLog.e(TAG,"MinaManager uninit : "+hasInit);
        if(!hasInit){
            return;
        }

        hasInit = false;

        if(handler != null){
            handler.removeCallbacks(checkTimeOutRunnable);
            handler.removeCallbacks(helloRunable);
            handler = null;
        }
        
        if(minaThread != null){
            minaThread.release();
            minaThread = null;
        }

        if(messageHandlerHashMap != null){
            messageHandlerHashMap.clear();
            messageHandlerHashMap = null;
        }

        if(mBroadcastHandlers != null){
            mBroadcastHandlers.clear();
            mBroadcastHandlers = null;
        }

        try{
            mContext.unregisterReceiver(mNetStateChangeReceiver);
        }catch (Exception e){
            TLog.e(TAG,"unregisterReceiver err0r : "+e.getMessage());
        }


        mContext = null;

        mMinaManager = null;
    }

    private OnResponseListener onResponseListener = new OnResponseListener() {
        @Override
        public void onResponse(final RspNotice rspNotice) {
            if (Configs.Debug)
                TLog.e(TAG,"mRspNotice receive : "+rspNotice.msg.toString());
            if(handler != null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleRspNotice(rspNotice);
                    }
                });
            }
        }

        @Override
        public void onBroadcast(final BroadcastNotice broadcastNotice) {
            if(handler != null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleBroadcastNotice(broadcastNotice);
                    }
                });
            }
        }

        @Override
        public void onConnectType(final ConnectTypeNotice connectTypeNotice) {
            if(handler != null){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleConnectTypeNotice(connectTypeNotice);
                    }
                });
            }
        }
    };

    private SocketSuccListener mSocketSuccListener;
    /**
    * 通知应用层已经建立socket
    * @author hyqiao
    * @time 2017/11/29 12:00
    */
    public void connectSocket(SocketSuccListener listener){
        this.mSocketSuccListener = listener;
        connectSocket();
    }

    /**
     * 需要重新换IP的时候调用
     * @author hyqiao
     * @time 2017/11/29 11:45
     */
    public void reConnectSocket(ArrayList<String> ipList,ArrayList<Integer> portList){
        this.mIpList = ipList;
        this.mPortList = portList;
        reConnectSocket();
    }

    private void connectSocket(){
        if(handler != null){
            handler.postDelayed(checkTimeOutRunnable,TIMEOUT_INTERVAL);
            handler.postDelayed(helloRunable,HELLO_INTERVAL);
        }
        createSocketThread();
    }

    /**
     * 需要重新建立Socket链接
     * @author hyqiao
     * @time 2017/11/29 15:17
     */
    private void reConnectSocket(){
        createSocketThread();
    }

    private void createSocketThread() {
        try {
            if(minaThread != null){
                minaThread.release();
                minaThread = null;
            }
            minaThread = new MinaThread(mIpList,mPortList);
            minaThread.setOnResponseListener(onResponseListener);
            minaThread.start();
        }catch (Exception e){
            TLog.e(TAG,"createSocketThread error : "+e.getMessage());
        }
    }


    private synchronized void handleRspNotice(final RspNotice event){
        if(event == null || handler == null){
            return;
        }
        try{
            if (Configs.Debug)
                TLog.e(TAG,"mRspNotice receive : "+event.msg.toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(messageHandlerHashMap == null){
                            return;
                        }
                        MinaWrapHandler minaWrapHandler = messageHandlerHashMap.get(getMessageHandlerKey(event.msg.command,event.msg.subcmd,event.msg.sequenceNumber));
                        if(event.type == REQ_RESULT_CODE.CODE_SUCCESS){
                            if(minaWrapHandler != null){
                                minaWrapHandler.getHandler().onMessage(event.request,event.msg);
                            }
                        }else if(event.type == REQ_RESULT_CODE.ERROR_CODE_TIMEOUT){
                            if(minaWrapHandler != null){
                                minaWrapHandler.getHandler().onTimeout(event.request);
                            }
                        }else {
                            if(minaWrapHandler != null){
                                minaWrapHandler.getHandler().OnOtherError(event.type);
                            }
                        }
                        if(messageHandlerHashMap != null){
                            messageHandlerHashMap.remove(getMessageHandlerKey(event.msg.command,event.msg.subcmd,event.msg.sequenceNumber));
                        }
                    }catch (Exception e){
                        TLog.e(TAG,"handleRspNotice inner error : "+e.getMessage());
                    }
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"handleRspNotice error : "+e.getMessage());
        }
    }


    private synchronized void handleBroadcastNotice(final BroadcastNotice event) {
        if(event == null || handler == null){
            return;
        }
        try{
            if (Configs.Debug)
                TLog.e(TAG,"mBroadcastNotice receive : "+event.msg.toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(mBroadcastHandlers != null && mBroadcastHandlers.size() != 0){
                            for(int i = 0;i<mBroadcastHandlers.size();i++){
                                if(mBroadcastHandlers.get(i).match(event.request.command,event.msg.subcmd,0)){
                                    mBroadcastHandlers.get(i).onBroadcast(event.msg);
                                }
                            }
                        }
                    }catch (Exception e){
                        TLog.e(TAG,"handleBroadcastNotice inner error : "+e.getMessage());
                    }
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"handleBroadcastNotice error : "+e.getMessage());
        }
    }


    private synchronized void handleConnectTypeNotice(final ConnectTypeNotice event) {
        if(event == null || handler == null){
            return;
        }
        try{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(event.type == ConnectTypeNotice.ConnectType.CONNECT_SUCC){
                            if(messageHandlerHashMap != null){
                                messageHandlerHashMap.clear();
                            }
                            if(mSocketSuccListener != null){
                                mSocketSuccListener.onSucc();
                            }
                            TLog.e(TAG,"socket connect success");
                            // TODO: 2017/8/31 重新验证登录
                        }else if(event.type == ConnectTypeNotice.ConnectType.CONNECT_FAIL){
                            TLog.e(TAG,"socket connect fail");
                        }else if(event.type == ConnectTypeNotice.ConnectType.DISCONNECT){
                            TLog.e(TAG,"socket disconnect");
                        }
                    }catch (Exception e){
                        TLog.e(TAG,"handleConnectTypeNotice inner error : "+e.getMessage());
                    }

                }
            });
        }catch (Exception e){
            TLog.e(TAG,"handleConnectTypeNotice error : "+e.getMessage());
        }
    }


    private long lastBroadCastTime = System.currentTimeMillis();
    private int lastBroadCastNetState = -1;

    private final static int BROADCAST_INTERVAL = 1000;
    private BroadcastReceiver mNetStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    int state = NetUtils.NetWorkStatus(context);
                    TLog.e(TAG, "mNetStateChangeReceiver---" + state);
                    long lastTime = lastBroadCastTime;
                    int lastState = lastBroadCastNetState;

                    lastBroadCastTime = System.currentTimeMillis();
                    lastBroadCastNetState = state;
                    //初始话动态注册后会有一条广播，过滤该广播
                    if (lastState == state && (System.currentTimeMillis() - lastTime) < BROADCAST_INTERVAL) {
                        TLog.e(TAG, "重复的广播  state = " + state);
                    } else {
                        TLog.e(TAG,"normal广播  state = " + state);
                        if (state == NetUtils.MOBILE_NET || state == NetUtils.WIFI_NET) {
                            reConnectSocket();
                        }
                    }
                }
            } catch (Exception e) {
                TLog.e(TAG, "handle broadcast exception " + e.getMessage());
            }
        }
    };

    private String getMessageHandlerKey(int command, int subcmd, int seq){
        return String.format("%s%s%s",command,subcmd,seq);
    }
    public synchronized int sendRequest(int command, int subcmd, byte[] payload, MinaMessageHandler handler, int expiredTime)
    {
        if(!hasInit){
            return REQ_RESULT_CODE.ERROR_UNINIT;
        }
        seq_num++;
        //网络无效直接返回
        if(!NetUtils.isNetworkAvailable(mContext)){
            return REQ_RESULT_CODE.ERROR_CODE_UNREACHABLENET;
        }
        messageHandlerHashMap.put(getMessageHandlerKey(command,subcmd,seq_num),new MinaWrapHandler(handler,System.currentTimeMillis(),command,subcmd,seq_num));
        int result = sendRequest(command, subcmd, seq_num, payload);
        if(result == REQ_RESULT_CODE.ERROR_CONN_EXCEPTION || result == REQ_RESULT_CODE.ERROR_NOT_CONN ){
            messageHandlerHashMap.remove(getMessageHandlerKey(command,subcmd,seq_num));
            return result;
        }
        return result;
    }
    private int sendRequest(int command, int subcmd, int seq, byte[] payload){
        return minaThread.sendRequest(command, subcmd,seq,payload);
    }

    public void addBroadcastHandler(BroadcastHandler broadcastHandler){
        if(mBroadcastHandlers == null){
            mBroadcastHandlers = new ArrayList<>();
        }
        mBroadcastHandlers.add(broadcastHandler);
    }

    public void removeBroadcastHandler(BroadcastHandler broadcastHandler){
        if(mBroadcastHandlers != null){
            mBroadcastHandlers.remove(broadcastHandler);
        }
    }

    private Runnable helloRunable = new Runnable() {
        @Override
        public void run() {
            sendHelloReq();
            if(handler != null){
                handler.postDelayed(helloRunable,HELLO_INTERVAL);
            }
            
        }
    };
    HeartBeatProxy heartBeatProxy = new HeartBeatProxy();
    HeartBeatProxy.Param mHeartBeatProxyParam = new HeartBeatProxy.Param();
    private void sendHelloReq(){
        heartBeatProxy = new HeartBeatProxy();
        mHeartBeatProxyParam = new HeartBeatProxy.Param();
        heartBeatProxy.postReq(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                TLog.e(TAG,"testHeartBeat succ");
                if(mHeartBeatProxyParam.helloRsp != null){
                    TLog.e(TAG,"testHeartBeat : "+mHeartBeatProxyParam.helloRsp.toString());
                }
            }
            @Override
            public void onFail(int errorCode) {
                TLog.e(TAG,"testHeartBeat onFail");
            }
        },mHeartBeatProxyParam);
    }
    
    private Runnable checkTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                checkTimeOut();
                if(handler != null){
                    handler.postDelayed(checkTimeOutRunnable,TIMEOUT_INTERVAL);
                }
            }catch (Exception e){
                TLog.e(TAG,"checkTimeOutRunnable error : "+e.getMessage());
            }
        }
    };
    
    private void checkTimeOut(){
        try {
            Iterator entries = messageHandlerHashMap.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String)entry.getKey();
                MinaWrapHandler handler = (MinaWrapHandler)entry.getValue();
                if((System.currentTimeMillis()-handler.getTimestamp())>MinaThread.REQ_TIMEOUT_PERIOD){
                    handler.getHandler().onTimeout(Request.createEncryptRequest(handler.command,handler.getSubcmd(),null,null,null,null));
                    messageHandlerHashMap.remove(key);
                }
            }
        }catch (Exception e){
            TLog.e(TAG,"checkTimeOut error : "+e.getMessage());
        }
    }
}
