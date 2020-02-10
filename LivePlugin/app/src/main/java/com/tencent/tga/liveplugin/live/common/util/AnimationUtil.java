package com.tencent.tga.liveplugin.live.common.util;

import android.content.Context;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.tencent.tga.plugin.R;


/**
 * Created by lionljwang on 2016/3/7.
 *
 *视频ui相关动画
 */
public class AnimationUtil {
    private static final String TAG = AnimationUtil.class.getSimpleName();


    /**
     * 从顶部滑入动画
     * @param c
     * @return
     */
    public static Animation topIn(Context c) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.video_top_in);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }

    /***
     * 从顶部滑出动画
     * @param c
     * @return
     */
    public static Animation topOut(Context c,Animation.AnimationListener listener) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.video_top_out);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        a.setAnimationListener(listener);
        return a;
    }

    /***
     * 底部滑入动画
     * @param c
     * @return
     */
    public static Animation bottomIn(Context c) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.video_bottom_in);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }

    /**
     * 底部滑出动画
     * @param c
     * @return
     */
    public static Animation bottomOut(Context c) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.video_bottom_out);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }

    /**
     * 底部滑出动画
     * @param c
     * @return
     */
    public static Animation bottomOut(Context c, Animation.AnimationListener listener) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.video_bottom_out);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        a.setAnimationListener(listener);
        return a;
    }

    public static long currentAnimationTimeMillis() {
        return SystemClock.uptimeMillis();
    }

    /***
     * 右侧滑入动画
     * @param c
     * @return
     */
    public static Animation rightIn(Context c) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.right_in);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }


    public static Animation rightOut(Context c,Animation.AnimationListener listener) {
        Animation a;
        a = AnimationUtils.loadAnimation(c, R.anim.right_out);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        a.setAnimationListener(listener);
        return a;
    }

    /**
     * 位移动画
     * @return
     */
    public static Animation translate(int startX, int endX, int startY,  int endY, Animation.AnimationListener listener) {
        Animation a;
        a = new TranslateAnimation(startX, endX, startY, endY);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        a.setAnimationListener(listener);
        a.setDuration(200);
        return a;
    }

}
