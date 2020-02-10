package com.tencent.tga.liveplugin.live;


import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lionljwang on 2017/7/17.
 * 主要存一些后台返回的数据
 */
public class LiveInfo {

    /***当前roomid**/
    public static String mRoomId = "";
    /**当前直播vid vid**/
    public static String mLiveid = "";
    /**当前资源id*/
    public static int mSourceId = 0;
    public static String mMatchId = "";
    public static  boolean isReadyToReport = false;
    public static boolean isStopPLay = false;
    public static List<ChannelInfo> mChannelInfos = new ArrayList<>();


    public static void destory()
    {
        mRoomId = "";
        mLiveid = "";
        mSourceId = 0;
        mMatchId = "";
        isReadyToReport = false;
        isStopPLay = false;
        mChannelInfos.clear();
        mChannelInfos = null;
    }
}
