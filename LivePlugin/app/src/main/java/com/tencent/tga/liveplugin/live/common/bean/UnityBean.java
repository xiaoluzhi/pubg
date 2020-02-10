package com.tencent.tga.liveplugin.live.common.bean;

import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;

import java.util.ArrayList;

/**
 * Created by lionljwang on 2016/8/4.
 */
public class UnityBean {

    public int accountType;//qq 或者微信

    public String token = "";

    public String appid= "";

    public String areaid= "";

    public String openid= "";

    public String nikeName= "";

    public String avatarUrl= "";

    public String gameVersion= "";

    public String unityVersion= "";

    public String gameUid = "";

    public String matchGuessUrl = "";

    public int position;

    public int gender;

    public String partition;

    public ArrayList<String> serverIps = new ArrayList<>();

    public int chatCd  = 3000;

    public int userLevel;

    public String tv_name = "";

    public String tab_list;//顶部显示TAG的ID
    public int open_tab = DefaultTagID.LIVE;

    public String pop_banner_type = "0";

    public boolean isUseApplicationContext = true;
    private UnityBean(){

    }

    private static volatile UnityBean mInstance = null;

    public synchronized static UnityBean getmInstance(){
        if (mInstance == null)
            mInstance = new UnityBean();
        return mInstance;
    }



    public int getArea(){
        int area;
        area = (UnityBean.getmInstance().accountType == 1) ? 1 : 3;
        return area;
    }
}
