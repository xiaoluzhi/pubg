package com.tencent.tga.liveplugin.networkutil;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.tga.liveplugin.base.util.ThreadPoolManager;

/**
 * Created by hyqiao on 2016/1/15.
 */
public class SPUtils {

    public static String SPTAG = "live_file";

    public static String accountname = "accountname";
    public static String uuid = "uuid";
    public static String openid = "openid";
    public static String auth_key = "auth_key";
    public static String access_token = "access_token";
    public static String authkeymd5 = "authkeymd5";

    public static String netbroadcastperiod = "netbroadcastperiod";//网络广播间隔，有些手机收到多次广播
    public static String netstate = "netstate";//网络状态

    public static String subscribecount = "subscribecount";//订阅次数

    public static String PopTvLastShowTime = "PopTvLastShowTime";//弹窗上次展示时间

    public static String upload_switch = "upload_switch";//关系链上次同步时间

    public static String screen_tv = "screen_tv";//弹窗内按钮红点的状态

    public static String last_lottery= "last_lottery";//最后一次的抽奖活动ID

    public static String last_lottery_win_id= "last_lottery_win_id";//最后一次的中奖活动ID

    public static String last_lottery_result_list_id= "last_lottery_result_list_id";//最后一次的展示列表的活动ID

    public static String last_lottery_id_value = "last_lottery_id_value";//最后一次的中奖id和已选中的选项

    public static void SPSaveString(final Context context, final String key, final String value){
        ThreadPoolManager.getInstance().executeRunnable(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, value);
                editor.commit();
            }
        });
    }

    public static void SPSaveStringInMain(final Context context, final String key, final String value){
            SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
    }

    /**
    * 默认返回“”
    * @author hyqiao
    * @time 2017/8/15 10:46
    */
    public static String SPGetString(Context context,String key){
        return SPGetString(context,key, "");
    }

    public static String SPGetString(Context context,String key,String default_value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, default_value);
    }


    public static void SPSaveInt(final Context context, final String key, final int value){
        ThreadPoolManager.getInstance().executeRunnable(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(key, value);
                editor.commit();
            }
        });
    }

    public static void SPSaveInt(final Context context, final String key, final int value,boolean isNeedThread){
        if(isNeedThread){
            ThreadPoolManager.getInstance().executeRunnable(new Runnable() {
                @Override
                public void run() {
                    SPSaveIntNoThread(context,key,value);
                }
            });
        }else {
            SPSaveIntNoThread(context,key,value);
        }
    }

    public static void SPSaveIntNoThread(final Context context, final String key, final int value){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SPSaveIntInMain(final Context context, final String key, final int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 默认返回0
     * @author hyqiao
     * @time 2017/8/15 10:46
     */
    public static int SPGetInt(Context context,String key){
        return SPGetInt(context,key, 0);
    }

    public static int SPGetInt(Context context,String key,int default_value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, default_value);
    }


    public static void SPSaveLong(final Context context, final String key, final long value){
        ThreadPoolManager.getInstance().executeRunnable(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(key, value);
                editor.commit();
            }
        });
    }

    /**
     * 默认返回0
     * @author hyqiao
     * @time 2017/8/15 10:46
     */
    public static long SPGetLong(Context context,String key){
        return SPGetLong(context,key, 0);
    }

    public static long SPGetLong(Context context,String key,long default_value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, default_value);
    }


    public static void SPSaveBool(final Context context, final String key, final boolean value){
        ThreadPoolManager.getInstance().executeRunnable(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(key, value);
                editor.commit();
            }
        });
    }

    /**
     * 默认返回false
     * @author hyqiao
     * @time 2017/8/15 10:46
     */
    public static boolean SPGetBool(Context context,String key){
        return SPGetBool(context,key, false);
    }

    public static boolean SPGetBool(Context context,String key,boolean default_value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, default_value);
    }

}
