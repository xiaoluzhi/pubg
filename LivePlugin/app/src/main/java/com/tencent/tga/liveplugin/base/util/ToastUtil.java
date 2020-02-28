package com.tencent.tga.liveplugin.base.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.plugin.R;

import java.lang.reflect.Method;

/**
 * Created by hyqiao on 2016/10/28.
 */
public class ToastUtil {
    public static void show(Context mContext,String msg){
        show(mContext,msg,Toast.LENGTH_SHORT);
    }

    public static void showLong(Context mContext,String msg){
        show(mContext,msg,Toast.LENGTH_LONG);
    }

    public static Toast toast;
    public static void show(Context mContext,CharSequence msg,int type){
        Context context = DLPluginLayoutInflater.getInstance(mContext).getContext();
        if(toast == null){
            toast = new Toast(context);
        }

        TextView tv = new TextView(context);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor("#FFFFC951"));
        tv.setText(msg);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setBackgroundResource(R.drawable.toast_bg);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(40,15,40,15);
        toast.setView(tv);
        toast.setDuration(type);
        toast.setGravity(Gravity.CENTER,getNavigationBarHeight(mContext)/2,0);
        toast.show();
    }

    public static void show(Context mContext,View view){
        Context context = DLPluginLayoutInflater.getInstance(mContext).getContext();
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }


    public static void cancel(){
        if (toast!=null){
            toast.cancel();
            toast = null;
        }
    }

}
