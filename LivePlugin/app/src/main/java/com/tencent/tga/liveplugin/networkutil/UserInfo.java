package com.tencent.tga.liveplugin.networkutil;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;

import java.util.ArrayList;

/**
 * Created by hyqiao on 2016/8/1.
 */
public class UserInfo {

    private static String TAG = "UserInfo";
    private static UserInfo baseData = new UserInfo();
    public synchronized static UserInfo getInstance() {
        if (baseData == null)
            baseData = new UserInfo();
        return baseData;
    }

    public void unInit() {
        baseData = null;
    }


    public String mAccount_name = "";
    public int mAccount_type;
    public String mAccess_token = "";
    public String mSdk_appid = "";
    public int mAreaid = 0;
    public String mOpenid = "";
    public String mNickName = "";
    public String mSt_buf = "";
    public String mGameid = "";
    public String mAvatarUrl = "";
    public String mGameVersion = "";
    public String mUnityVersion = "";
    public String mGameUid = "";
    public int mPosition = -1;
    public int userLevel ;

    public ArrayList<String> mIpList = new ArrayList<>();
    public ArrayList<Integer> mPortList = new ArrayList<>();

    public void setUserInfo(UnityBean unityBean){
        this.mAccount_name = unityBean.openid;
        this.mAccount_type = unityBean.accountType;
        this.mAccess_token = unityBean.token;
        this.mSt_buf = unityBean.token;
        this.mSdk_appid = unityBean.appid;
        this.mGameid =  unityBean.gameUid;
        this.mAreaid = Integer.parseInt(unityBean.areaid == null ? "0" : unityBean.areaid);
        this.mOpenid = unityBean.openid;
        this.mNickName = unityBean.nikeName;
        this.mAvatarUrl = unityBean.avatarUrl;
        this.mGameVersion = unityBean.gameVersion;
        this.mUnityVersion = unityBean.unityVersion;
        this.mGameUid = unityBean.gameUid;
        this.mPosition = unityBean.position;
        this.userLevel = unityBean.userLevel;

        if(unityBean.serverIps != null){
            this.mIpList = unityBean.serverIps;
        }

        this.mPortList.add(8000);
        this.mPortList.add(443);
        this.mPortList.add(8080);

        TLog.e(TAG,toString());
    }

    @Override
    public String toString() {
        return String.format("UserInfo accountType = %s token = %s appid = %s areaid = %s openid = %s nikeName = %s avatarUrl= %s gameVersion = %s unityVersion = %s position = %s mGameUid = %s userLevel = %s"
                ,mAccount_type,mAccess_token,mSdk_appid,mAreaid,mOpenid,mNickName,mAvatarUrl,mGameVersion,mUnityVersion,mPosition,mGameUid,userLevel);
    }
}
