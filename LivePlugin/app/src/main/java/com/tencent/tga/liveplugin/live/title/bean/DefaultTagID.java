package com.tencent.tga.liveplugin.live.title.bean;

import java.util.ArrayList;

/**
 * Created by hyqiao on 2017/5/23.
 */

public class DefaultTagID {
    public static int CURRENT_SELECTION = DefaultTagID.LIVE;

    public final static int LIVE = 1;

    public final static String LIVE_DEFAULT_NAME = "直播";

    public static ArrayList<TitleTagBean> getDefaultTitleTagBeanList(){
        ArrayList<TitleTagBean> defaultList = new ArrayList<>();
        defaultList.add(new TitleTagBean(LIVE_DEFAULT_NAME ,LIVE, "default", "", true, false));
        return defaultList;
    }
}
