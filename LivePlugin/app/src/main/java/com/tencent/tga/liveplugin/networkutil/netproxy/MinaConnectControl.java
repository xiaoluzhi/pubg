package com.tencent.tga.liveplugin.networkutil.netproxy;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.common.AccountType;
import com.tencent.tga.liveplugin.base.notification.NotificationCenter;
import com.tencent.tga.liveplugin.base.util.HexStringUtil;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.mina.MinaManager;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.SPUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;
import com.tencent.tga.liveplugin.networkutil.broadcast.NetBroadHandeler;
import com.tencent.tga.net.encrypt.MinaNetworkEngine;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okiotga.ByteString;


/**
 * Created by hyqiao on 2016/4/6.
 */
public class MinaConnectControl {
    private static String TAG = "MinaConnectControl";

    public static int ACCOUNT_TYPE_QQ = 1;
    public static int ACCOUNT_TYPE_WECHAT = 2;
    public static int ACCOUNT_TYPE_TOURIST = 0;

    private NetConnectListener mNetConnectListener;//长连接回调，根据需要设置，可为空

    private int mConnectCount = 1;//长连接重连2次

    private Context mContext;

    public MinaConnectControl(Context context) {
        this.mContext = context;
        TLog.e(TAG, "MinaConnectControl  create......");
    }
    /**
    * 建立长连接方法
     * 初始化网络引擎设置；判断是否需要认证
    * @author hyqiao
    * @time 2016/9/22 16:36
    */
    public void connect(){
        try {
            if(UserInfo.getInstance().mAccount_name.equals(SPUtils.SPGetString(mContext,SPUtils.accountname))){
                Sessions.globalSession().uuid = SPUtils.SPGetString(mContext, SPUtils.uuid).getBytes("utf-8");
                Sessions.globalSession().openid = SPUtils.SPGetString(mContext, SPUtils.openid).getBytes("utf-8");
                //必须用base64编解码，否则存储前后获得的byte不一致
                Sessions.globalSession().auth_key = Base64.decode(SPUtils.SPGetString(mContext, SPUtils.auth_key), 1);
                Sessions.globalSession().access_token = Base64.decode(SPUtils.SPGetString(mContext, SPUtils.access_token), 1);
                reqConnProxyData(true);
            }else {
                reqAuthProxyData();
            }
        }catch (Exception e){
            TLog.e(TAG,TAG+" connect : "+e.getMessage());
        }
    }

    /**
    * 建立长连接方法,清理了之前的帐号，重新认证
    * @author hyqiao
    * @time 2017/2/20 16:21
    */
    public void clearAccountConnect(){
        SPUtils.SPSaveString(mContext,SPUtils.accountname,"");//清空账户，重新登录
        connect();
    }

    static class AuthProxyHolder {
        AuthProxy authProxy = new AuthProxy();
        AuthProxy.Param param = new AuthProxy.Param();
    }
    private AuthProxyHolder mAuthProxyHolder = new AuthProxyHolder();

    /**
    * 长连接的网络认证：获取用户的基本信息(不用每次认证，用户信息缓存本地)
    * @author hyqiao
    * @time 2016/9/22 16:38
    */
    private void reqAuthProxyData(){
        TLog.e(TAG, "AuthProxy......");

        mAuthProxyHolder.param.account_name = PBDataUtils.string2ByteString(UserInfo.getInstance().mAccount_name);
        mAuthProxyHolder.param.account_type = UserInfo.getInstance().mAccount_type;
        mAuthProxyHolder.param.access_token = PBDataUtils.string2ByteString(UserInfo.getInstance().mAccess_token);
        mAuthProxyHolder.param.sdk_appid = PBDataUtils.string2ByteString(UserInfo.getInstance().mSdk_appid);
        mAuthProxyHolder.param.st_buf = PBDataUtils.string2ByteString(UserInfo.getInstance().mSt_buf);

        mAuthProxyHolder.param.client_type = Configs.CLIENT_TYPE;
        mAuthProxyHolder.param.clientip = PBDataUtils.string2ByteString(NetUtils.getLocalIPAddress());
        TLog.e(TAG,"getLocalIPAddress--->"+NetUtils.getLocalIPAddress());
        mAuthProxyHolder.param.mcode = PBDataUtils.string2ByteString(VersionUtil.getMachineCode(mContext));
        mAuthProxyHolder.param.client_time = (int)(System.currentTimeMillis()/1000);

        mAuthProxyHolder.param.live_token = PBDataUtils.string2ByteString("123");//主播工具端使用，此处无用
        mAuthProxyHolder.param.gameid = PBDataUtils.string2ByteString(UserInfo.getInstance().mGameid);
        mAuthProxyHolder.param.areaid = Integer.valueOf(UnityBean.getmInstance().partition);
        mAuthProxyHolder.param.openid = PBDataUtils.string2ByteString(UserInfo.getInstance().mOpenid);
        String phoneName = Build.MODEL;
        TLog.e(TAG,"当前机型:"+phoneName);
        mAuthProxyHolder.param.device_info = PBDataUtils.string2ByteString(phoneName);
        mAuthProxyHolder.param.game_uid = PBDataUtils.string2ByteString(UserInfo.getInstance().mGameUid);

        mAuthProxyHolder.authProxy.postReq(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                if (code == 0) {
                    if (mAuthProxyHolder.param.authTokenRsp != null
                            && mAuthProxyHolder.param.authTokenRsp.result != null
                            && mAuthProxyHolder.param.authTokenRsp.result == 0) {
                        Sessions.globalSession().authorized = true;

                        Sessions.globalSession().uuid = mAuthProxyHolder.param.authTokenRsp.uuid.toByteArray();
                        Sessions.globalSession().openid = mAuthProxyHolder.param.authTokenRsp.openid.toByteArray();
                        Sessions.globalSession().auth_key = mAuthProxyHolder.param.authTokenRsp.auth_key.toByteArray();
                        Sessions.globalSession().access_token = mAuthProxyHolder.param.authTokenRsp.access_token.toByteArray();
                        Sessions.globalSession().token_expires = mAuthProxyHolder.param.authTokenRsp.token_expires;

                        SPUtils.SPSaveString(mContext, SPUtils.accountname, UserInfo.getInstance().mAccount_name);
                        SPUtils.SPSaveString(mContext, SPUtils.uuid, PBDataUtils.byteString2String(mAuthProxyHolder.param.authTokenRsp.uuid));
                        SPUtils.SPSaveString(mContext, SPUtils.openid, PBDataUtils.byteString2String(mAuthProxyHolder.param.authTokenRsp.openid));
                        SPUtils.SPSaveString(mContext, SPUtils.auth_key, Base64.encodeToString(Sessions.globalSession().auth_key, 1));
                        SPUtils.SPSaveString(mContext, SPUtils.access_token, Base64.encodeToString(Sessions.globalSession().access_token, 1));

                        TLog.e(TAG, "AuthProxy--->" + mAuthProxyHolder.param.authTokenRsp.result + "  " + "认证成功");

                        reqConnProxyData(false);
                    } else {
                        TLog.e(TAG, "AuthProxy--->" + "  " + "网络认证失败");
                        //textAuth.setText("Auth result: " + mProxyHolder.param.authTokenRsp.result);
                        if (mNetConnectListener != null) {
                            mNetConnectListener.onFail("网络认证失败");
                        }
                    }
                } else {
                    TLog.e(TAG, "AuthProxy  result code -->" + code);
                }
            }

            @Override
            public void onFail(int errorCode) {
                TLog.e(TAG, "AuthProxy网络认证失败超时-->" + errorCode);
                if (mNetConnectListener != null) {
                    mNetConnectListener.onFail("网络认证失败超时");
                }
            }
        }, mAuthProxyHolder.param);
    }

    //建立长链接
    static class ConnProxyHolder {
        ConnProxy connProxy = new ConnProxy();
        ConnProxy.Param param = new ConnProxy.Param();
    }
    private ConnProxyHolder mConnProxyHolder = new ConnProxyHolder();

    /**
    * 登录协议，建立长连接
     * isCheckUserkey 是否校验userkey，防止被破坏的数据请求(不认证直接登录的需要校验)
    * @author hyqiao
    * @time 2016/9/22 16:40
    */
    private void reqConnProxyData(boolean isCheckUserkey){
        TLog.e(TAG, "ConnProxy......");

        byte[] openid = Sessions.globalSession().openid;
        byte[] uuid = Sessions.globalSession().uuid;
        byte[] access_token = Sessions.globalSession().access_token;

        if(openid == null || uuid == null || access_token == null){
            if (mNetConnectListener != null) {
                mNetConnectListener.onFail("建立长连接失败");
            }
            return;
        }

        try {
            TLog.d(TAG, "openid-->" + openid.length + "  " + new String(openid,"utf-8"));
            TLog.d(TAG, "uuid-->" + uuid.length + "  " + new String(uuid,"utf-8"));
            TLog.d(TAG, "access_token-->" + access_token.length + "  " + new String(access_token,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }


        /*****************更换认证票据*********************/
        byte[] stkey_bytes = null;
        try {
            stkey_bytes = Configs.PB_KEY.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(stkey_bytes == null){
            return;
        }
        byte[] authkey_bytes = Sessions.globalSession().auth_key;//Sessions.globalSession().auth_key;
        if(authkey_bytes == null){
            TLog.e(TAG,"userkey_bytes is null");
            SPUtils.SPSaveString(mContext,SPUtils.accountname,"");//清空账户，退出重进重新登录
            return ;
        }
        byte[] userkey_bytes = MinaNetworkEngine.shareEngine().decrypt(stkey_bytes, authkey_bytes);

        if (null == userkey_bytes) {
            TLog.e(TAG,"userkey_bytes is null");
            SPUtils.SPSaveString(mContext,SPUtils.accountname,"");//清空账户，退出重进重新登录
            return ;
        }else {
            TLog.e(TAG,"authkey_bytes  :  "+ HexStringUtil.bytesToHexString(authkey_bytes));
            TLog.e(TAG,"userkey_bytes  :  "+ HexStringUtil.bytesToHexString(userkey_bytes));

            if(isCheckUserkey){
                TLog.e(TAG,"authkey_bytes check");
                if(!TextUtils.isEmpty(SPUtils.SPGetString(mContext,SPUtils.authkeymd5)) &&
                        !HexStringUtil.bytesToHexString(authkey_bytes).equals(SPUtils.SPGetString(mContext,SPUtils.authkeymd5))){
                    TLog.e(TAG,"authkey_bytes not equal");
                    TLog.e(TAG,"authkey_bytes1 : "+HexStringUtil.bytesToHexString(authkey_bytes));
                    TLog.e(TAG,"authkey_bytes2 : "+SPUtils.SPGetString(mContext,SPUtils.authkeymd5));

                    SPUtils.SPSaveString(mContext,SPUtils.accountname,"");//清空账户，下次重新登录
                    // TODO: 2017/2/14 如果 auth_key 或者userkey被破坏，导致登录失败,暂时返回
                    return;
                }
            }else {
                TLog.e(TAG,"authkey_bytes not check");
                SPUtils.SPSaveString(mContext,SPUtils.authkeymd5,HexStringUtil.bytesToHexString(authkey_bytes));
            }
        }

        Sessions.globalSession().decrypt_key = userkey_bytes;

        mConnProxyHolder.param.openid = ByteString.of(openid, 0, openid.length);
        mConnProxyHolder.param.uuid = ByteString.of(uuid, 0, uuid.length);
        mConnProxyHolder.param.access_token = ByteString.of(access_token, 0, access_token.length);
        mConnProxyHolder.param.machine_code = PBDataUtils.string2ByteString(VersionUtil.getMachineCode(mContext));
        mConnProxyHolder.param.network_type = NetUtils.getReportNetStatus(mContext);


        TLog.e(TAG,"connProxy begin");
        mConnProxyHolder.connProxy.postReqWithSignature(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                if (mConnProxyHolder.param.loginRsp == null) {
                    TLog.e(TAG, "ConnProxy loginRsp-->" + null);
                    if (mNetConnectListener != null) {
                        mNetConnectListener.onFail("建立长连接失败");
                    }
                    return;
                }
                TLog.e(TAG, "ConnProxy loginRsp-->" + mConnProxyHolder.param.loginRsp.toString());
                if (code == 0) {
                    if (mConnProxyHolder.param.loginRsp.result != null && mConnProxyHolder.param.loginRsp.result == 0) {
                        TLog.e(TAG, "ConnProxy rspbody result = " + mConnProxyHolder.param.loginRsp.result);

                        //Hello包需要的判断
                        Sessions.globalSession().connected = true;

                        TLog.e(TAG, "ConnProxy--->Account_type:" + UserInfo.getInstance().mAccount_type + "  建立长连接成功");
                        //通知页面更新数据
                        NotificationCenter.defaultCenter().publish(new LiveEvent.NetworkEngineUsable());
                        if (mNetConnectListener != null) {
                            if (UserInfo.getInstance().mAccount_type == AccountType.AccountType_QQ.getValue()) {
                                mNetConnectListener.onSucc(ACCOUNT_TYPE_QQ);
                            } else if (UserInfo.getInstance().mAccount_type == AccountType.AccountType_WeChat.getValue()) {
                                mNetConnectListener.onSucc(ACCOUNT_TYPE_WECHAT);
                            } else {
                                mNetConnectListener.onSucc(ACCOUNT_TYPE_TOURIST);
                            }
                        }

                        NetBroadHandeler.getInstance().addBroadcast();

                    } else if (mConnProxyHolder.param.loginRsp.result != null && mConnProxyHolder.param.loginRsp.result == 2) {
                        //票据过期时重新auth
                        try {
                            clearAccountConnect();
                            TLog.e(TAG, "ConnProxy--->" + mConnProxyHolder.param.loginRsp.result + "  " + "认证过期以游客身份登录");
                        }catch (Exception e){
                            TLog.e(TAG,"票据过期时重新auth exception : "+e.getMessage());
                        }
                    } else if (mConnProxyHolder.param.loginRsp.result != null && mConnProxyHolder.param.loginRsp.result == 3) {
                        TLog.e(TAG, "ConnProxy--->" + mConnProxyHolder.param.loginRsp.result + "  " + "服务器满载，返回一批新的IP地址，重新建立长连接");
                        if (mConnProxyHolder.param.loginRsp.connsvr_ip != null && mConnProxyHolder.param.loginRsp.connsvr_ip.size() > 0) {
                                List<String> list = new ArrayList<>();
                                try {
                                    for (Integer i : mConnProxyHolder.param.loginRsp.connsvr_ip) {
                                        list.add(NetUtils.intToIp(i).trim());
                                        TLog.e(TAG, "connsvr_ip : " + NetUtils.intToIp(i).trim());
                                    }

                                    UserInfo.getInstance().mIpList.clear();
                                    UserInfo.getInstance().mIpList.addAll(list);

                                    if(UserInfo.getInstance().mIpList != null && UserInfo.getInstance().mIpList.size() > 0){
                                        MinaManager.getInstance().reConnectSocket(UserInfo.getInstance().mIpList,UserInfo.getInstance().mPortList);
                                    }

                                } catch (Exception e) {
                                    TLog.e(TAG, "满载重连失败 -->" + e.getMessage());
                                }
                        }
                    } else {
                        //上报建立长链接失败
                        //textAuth.setText("Auth result: " + mConnProxyHolder.param.loginRsp.result);
                        TLog.e(TAG, "ConnProxy--->" + "  " + "建立长连接失败");
                        if (mNetConnectListener != null) {
                            mNetConnectListener.onFail("建立长连接失败");
                        }
                    }
                }
            }

            @Override
            public void onFail(int errorCode) {
                TLog.e(TAG, "ConnProxy请求失败-->" + errorCode);

                mConnectCount++;
                if (mConnectCount == 2) {
                    connect();
                }
                if (mNetConnectListener != null) {
                    mNetConnectListener.onFail("建立长连接超时");
                }
            }
        }, mConnProxyHolder.param);
    }

    public void setNetConnectListener(NetConnectListener l){
        this.mNetConnectListener = l;
    }
}
