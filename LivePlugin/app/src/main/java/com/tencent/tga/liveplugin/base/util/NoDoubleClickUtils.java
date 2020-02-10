package com.tencent.tga.liveplugin.base.util;

/**
 * Created by fluxliu on 2016/8/25.
 */
public class NoDoubleClickUtils {
    private static long lastClickTime;
    private final static int SPACE_TIME = 500;
    public final static int SWITCH_SPACE_TIME = 1500;

    private static long lastClickVideoTime;
    private static long lastSwitchTime;

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickTime >
                SPACE_TIME) {
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        lastClickTime = currentTime;
        return isClick2;
    }

    public synchronized static boolean isDoubleClick(int period) {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickTime >
                period) {
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        lastClickTime = currentTime;
        return isClick2;
    }

    public synchronized static boolean isDoubleClickVideo() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickVideoTime > SPACE_TIME ) { //两次点击事件不超过500ms说明双击
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        if (currentTime - lastSwitchTime < SWITCH_SPACE_TIME) { //两次switch时间不能小于1.5s
            isClick2 = false;
        }
        lastClickVideoTime = currentTime;
        if (isClick2) lastSwitchTime = currentTime;
        return isClick2;
    }

}
