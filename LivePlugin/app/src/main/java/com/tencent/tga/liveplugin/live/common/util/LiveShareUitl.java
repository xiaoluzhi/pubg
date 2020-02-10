package com.tencent.tga.liveplugin.live.common.util;

import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.SPUtils;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lionljwang on 2016/4/13.
 */
public class LiveShareUitl {

    public static String LIVE_FILE = "live_file";
    private static final String LIVE_DANMU_SHOW = "live_danmu_show";

    private static final String LIVE_DANMU_SIZE = "live_danmu_size";
    private static final String LIVE_DANMU_POSITION = "live_danmu_position";
    private static final String LIVE_DANMU_ALPHA = "live_danmu_alpha";

    private static final String LIVE_DANMU_SETTING_HIS = "live_danmu_setting_his";//有没有弹幕设置操作历史
    private static final String LIVE_DANMU_SETTING_TIME = "live_danmu_setting_time";//有没有弹幕设置操作历史

    /**弹幕位置**/
    public static final int LIVE_DANMU_POSITION_FULL = 0;//全屏弹幕
    public static final int LIVE_DANMU_POSITION_TOP = 1;//上半屏
    public static final int LIVE_DANMU_POSITION_BOTTOM = 2;//下半屏

    private static final String LIVE_WATCH_TIME = "live_watch_time";//
    private static final String LIVE_WATCH_SYS_TIME = "live_watch_sys_time";//

    /**弹幕size*/
    public static final int LIVE_DANMU_SIZE_SMALL = 14;
    public static final int LIVE_DANMU_SIZE_NORMAL = 16;
    public static final int LIVE_DANMU_SIZE_BIG = 18;

    /**弹幕透明度*/
    public static final int LIVE_DANMU_ALPHA_MAX = 255;

    /***
     * 直播播放清晰度
     */
    private static final String LIVE_DEFINE = "live_define";
    /***
     * 视频播放清晰度
     */
    private static final String VIDEO_DEFINE = "video_define";


    private static final String LIVE_LINE_RED = "live_line_red";

    /**
     * 获取弹幕显示状态
     *
     * @param context
     * @return
     */
    public static boolean isShowDanmu(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetBool(context,LIVE_DANMU_SHOW,true);
        }
    }

    /**
     * 保存弹幕显示状态
     *
     * @param context
     * @return
     */
    public static void saveDanmuState(Context context, boolean isShow) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveBool(context,LIVE_DANMU_SHOW,isShow);
        }

    }
    public static String getCurDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(new Date());
    }

    /**
     * 弹幕位置
     *
     * @param context
     * @return
     */
    public static void saveLiveDanmuPosition(Context context, int position) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context, LIVE_DANMU_POSITION, position);
        }

    }

    /**
     * 弹幕位置
     *
     * @param context
     * @return
     */
    public static int getLiveDanmuPosition(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context, LIVE_DANMU_POSITION, LIVE_DANMU_POSITION_FULL);
        }

    }

    /**
     * 弹幕大小
     *
     * @param context
     * @return
     */
    public static void saveLiveDanmuSize(Context context, int size) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context, LIVE_DANMU_SIZE, size);
        }

    }

    /**
     * 弹幕大小
     *
     * @param context
     * @return
     */
    public static int getLiveDanmuSize(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context,LIVE_DANMU_SIZE, LIVE_DANMU_SIZE_NORMAL);
        }
    }

    /**
     * 弹幕大小
     *
     * @param context
     * @return
     */
    public static void saveLiveDanmuAlpha(Context context, int alpha) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context,LIVE_DANMU_ALPHA, alpha);
        }

    }

    /**
     * 弹幕大小
     *
     * @param context
     * @return
     */
    public static int getLiveDanmuAlpha(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context, LIVE_DANMU_ALPHA, 179);
        }
    }


    /**
     * 清晰度
     *
     * @param context
     * @return
     */
    public static String getVideoDefine(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetString(context, VIDEO_DEFINE, "shd");
        }
    }

    /**
     * 保存清晰度
     *
     * @param context
     * @return
     */
    public static void saveVideoTips(Context context, String define) {
        if (NetUtils.NetWorkStatus(context) == NetUtils.WIFI_NET) {
            synchronized (LiveShareUitl.class) {
                SPUtils.SPSaveString(context,VIDEO_DEFINE, define);
            }
        }

    }

    /**
     * 清晰度
     *
     * @param context
     * @return
     */
    public static String getLiveDefine(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetString(context,LIVE_DEFINE, "hd");

        }
    }

    /**
     * 保存清晰度
     *
     * @param context
     * @return
     */
    public static void saveLiveTips(Context context, String define) {
        //if (NetUtils.NetWorkStatus(context) == NetUtils.WIFI_NET) {
            synchronized (LiveShareUitl.class) {
                SPUtils.SPSaveString(context,LIVE_DEFINE, define);
            }
       // }

    }

    /**
     *
     * @param context
     * @return
     */
    public static void saveLiveLineRed(Context context,String ids) {
            synchronized (LiveShareUitl.class) {
                SPUtils.SPSaveString(context,LIVE_LINE_RED, getCurDay());
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean isLiveLineRed(Context context) {
        synchronized (LiveShareUitl.class) {
            String newId = getCurDay();
            String oldId = SPUtils.SPGetString(context, LIVE_LINE_RED, "");
            return (!TextUtils.equals(oldId, newId));
        }
    }

    public static void saveLiveDanmuSettingHis(Context context) {
        LiveConfig.isShowDanmuSet = true;
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveBool(context, LIVE_DANMU_SETTING_HIS, true);
        }

    }

    public static boolean getLiveDanmuSettingHis(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetBool(context, LIVE_DANMU_SETTING_HIS, false);
        }
    }

    public static void saveLiveDanmuSettingTime(Context context) {
        int time = getLiveDanmuSettingTime(context)+1;
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context, LIVE_DANMU_SETTING_TIME, time);
        }

    }

    public static int getLiveDanmuSettingTime(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context, LIVE_DANMU_SETTING_TIME, 0);
        }
    }

    public static int getLiveWatchTime(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context, LIVE_WATCH_TIME, 0);
        }
    }

    public static void initLiveWatchTime(Context context){
        String string = getLiveSysTime(context);
        if (!TextUtils.equals(string,getCurDay()))
        {
            saveLiveSysTime(context);
            saveLiveWatchTime(context,0, false);
        }
    }


    public static void saveLiveSysTime(Context context) {
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveString(context,LIVE_WATCH_SYS_TIME, getCurDay());
        }
    }

    public static String getLiveSysTime(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetString(context, LIVE_WATCH_SYS_TIME, "");
        }
    }


    public static void saveLiveWatchTime(Context context,int watchTime, boolean isNeedThread) {
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context, LIVE_WATCH_TIME, watchTime, isNeedThread);
        }
    }

    /**
     * 点播弹幕位置
     *
     * @param context
     * @return
     */
    public static void saveVideoDanmuPosition(Context context, int position) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context, "video_danmu_position", position,true);
        }

    }

    /**
     * 点播弹幕位置
     *
     * @param context
     * @return
     */
    public static int getVideoDanmuPosition(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context, "video_danmu_position", LIVE_DANMU_POSITION_FULL);
        }

    }


    /**
     * 点播弹幕大小
     *
     * @param context
     * @return
     */
    public static void saveVidewDanmuSize(Context context, int size) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context, "video_danmu_size", size,true);
        }

    }

    /**
     * 点播弹幕大小
     *
     * @param context
     * @return
     */
    public static int getVideoDanmuSize(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context,"video_danmu_size", LIVE_DANMU_SIZE_NORMAL);
        }
    }

    /**
     * 弹幕大小
     *
     * @param context
     * @return
     */
    public static void saveVideoDanmuAlpha(Context context, int alpha) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveInt(context,"video_danmu_alpha", alpha,true);
        }

    }

    /**
     * 弹幕大小
     *
     * @param context
     * @return
     */
    public static int getVideoDanmuAlpha(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetInt(context, "video_danmu_alpha", 179);
        }
    }

    /**
     * 获取弹幕显示状态
     *
     * @param context
     * @return
     */
    public static boolean isShowVideoDanmu(Context context) {
        synchronized (LiveShareUitl.class) {
            return SPUtils.SPGetBool(context,"video_danmu_show",true);
        }
    }

    /**
     * 保存弹幕显示状态
     *
     * @param context
     * @return
     */
    public static void saveVideoDanmuState(Context context, boolean isShow) {
        saveLiveDanmuSettingHis(context);
        synchronized (LiveShareUitl.class) {
            SPUtils.SPSaveBool(context,"video_danmu_show",isShow);
        }

    }

}
