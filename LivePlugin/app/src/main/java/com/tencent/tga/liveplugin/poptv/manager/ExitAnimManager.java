package com.tencent.tga.liveplugin.poptv.manager;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.tencent.common.log.tga.TLog;

/**
 * Created by hyqiao on 2018/4/9.
 */

public class ExitAnimManager {
    private final static String TAG = "ExitAnimManager";

    private RelativeLayout parent_view;
    private RelativeLayout root_view;
    private ViewGroup mVideoLayout;

    private Handler handler;
    private int mCount = 0;

    public ExitAnimManager(RelativeLayout parent_view, RelativeLayout root_view, ViewGroup mVideoLayout) {
        this.parent_view = parent_view;
        this.root_view = root_view;
        this.mVideoLayout = mVideoLayout;
    }


    public void startExitAnimation() {
        try {

            if (null != mVideoLayout) {
                mVideoLayout.setVisibility(View.GONE);
                mVideoLayout.removeAllViews();
            }
            refreshView();

            AnimationSet animationSet = new AnimationSet(true);

            Animation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
            Animation scaleAnimation = new ScaleAnimation(1.0f, 0.05f, 1.0f, 0.05f);
            //scaleAnimation.setRepeatMode(Animation.REVERSE);
            //Animation translateAnimation = new TranslateAnimation(0, PopTvLpManager.mScreenWidth, 0, PopTvLpManager.mScreenHeight * 4.0f / 5.0f);
            Animation translateAnimation = new TranslateAnimation(0, 0, 0, PopTvLpManager.mScreenHeight * 1.0f / 4.0f);

            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(translateAnimation);

            animationSet.setDuration(700);

            parent_view.startAnimation(animationSet);
            //parent_view.setVisibility(View.GONE);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    TLog.e(TAG, "onAnimationStart");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    TLog.e(TAG, "onAnimationEnd");
                    handler = null;
                    doFinish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    TLog.e(TAG, "onAnimationRepeat");
                }
            });
        } catch (Exception e) {
            TLog.e(TAG, "do dismiss anim exception : " + e.getMessage());
        }
    }

    private void refreshView(){
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable,100);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                mCount++;
                if (root_view != null) {
                    root_view.postInvalidate();
                }
                TLog.e(TAG,"refreshView : "+mCount);
                if(mCount <10 && handler != null){
                    handler.postDelayed(runnable,100);
                }
            }catch (Exception e){
                TLog.e(TAG,"refreshView error : "+e.getMessage());
            }
        }
    };
    private ExitAnimFinishListener mExitAnimFinishListener;
    public interface ExitAnimFinishListener{
        void onFinish();
    }

    public void setExitAnimFinishListener(ExitAnimFinishListener l){
        this.mExitAnimFinishListener = l;
    }
    public void doFinish(){
        if(mExitAnimFinishListener != null){
            mExitAnimFinishListener.onFinish();
        }
    }
}
