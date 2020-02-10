package com.tencent.tga.liveplugin.live.common.util;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.tencent.common.log.tga.TLog;

import java.lang.reflect.Field;

/**
 * Created by lionljwang on 2016/8/1.
 */
public class SoftUitl {
    /**
     * 显示软键盘
     */
    public static void showSoftKeyBroad(Context context, View editText) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // only will trigger it if no physical keyboard is open
        mgr.showSoftInput(editText, 0);

    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeyBroad(Context context, View editText) {
        if (context == null || editText == null) {
            return;
        }
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void fixInputMethodManagerLeak(Context destContext) {

        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        //String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView","mLastSrvView"};
        String [] arr = new String[]{"mLastSrvView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0;i < arr.length;i ++) {
            String param = arr[i];
            try{
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    TLog.e("SoftUitl",""+v_get.getContext());
                    TLog.e("SoftUitl",""+destContext);

                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                        TLog.e("SoftUitl", "fixInputMethodManagerLeak clear");
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        TLog.e("SoftUitl", "fixInputMethodManagerLeak break, context is not suitable, get_context=" + v_get.getContext() + " dest_context=" + destContext);
                        //break;
                    }
                }
            }catch(Throwable t){
                //t.printStackTrace();
                TLog.e("SoftUitl","getDeclaredField 兼容异常，有些手机方法不能找到，机型为："+ Build.MODEL);
            }
        }
    }
}
