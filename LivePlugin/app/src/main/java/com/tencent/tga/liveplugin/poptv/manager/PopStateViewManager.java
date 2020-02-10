package com.tencent.tga.liveplugin.poptv.manager;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import com.tencent.tga.liveplugin.poptv.bean.PopTvBean;
import com.tencent.tga.liveplugin.poptv.view.PopStateView;
import com.tencent.tga.liveplugin.networkutil.NetUtils;

/**
 * Created by hyqiao on 2018/4/9.
 */

public class PopStateViewManager {

    private final static String TAG = "PopStateViewManager";
    private PopStateView mPopStateView;

    private Activity mActivity;
    private FrameLayout mRlyStateCtr;
    private Typeface mFont;
    private Resources mResources;
    private String mMatchInfo;
    private PopTvBean mPopTvBean;

    public PopStateViewManager(Activity activity, FrameLayout rlyStateCtr, Typeface font, Resources resources, String matchInfo, PopTvBean popTvBean) {
        this.mActivity = activity;
        this.mRlyStateCtr = rlyStateCtr;
        this.mFont = font;
        this.mResources = resources;
        this.mMatchInfo = matchInfo;
        this.mPopTvBean = popTvBean;
    }

    public void init(){
        initUI();

        setNetUI();

        initListener();
    }

    public PopStateView getPopStateView(){
        return mPopStateView;
    }
    private void initUI(){
        mRlyStateCtr.setLayoutParams(PopTvLpManager.getVideoLayoutLp(mRlyStateCtr));
        mPopStateView = new PopStateView(mActivity, mResources);
        mPopStateView.setTeamData(mMatchInfo);
        if (mFont != null) {
            mPopStateView.setFont(mFont);
        }
        mRlyStateCtr.addView(mPopStateView);
    }

    private void setNetUI(){
        if (NetUtils.NetWorkStatus(mActivity) == NetUtils.MOBILE_NET) {
            mPopStateView.showNetTips();
            mPopStateView.showMobileNetBg();
        } else {
            mPopStateView.hideNetTips();
        }

        if (NetUtils.NetWorkStatus(mActivity) != NetUtils.MOBILE_NET) {
            mPopStateView.showLoading();
        } else {
            if (mPopStateView != null) {
                mPopStateView.hideLoading();
            }
        }

        new Handler(mActivity.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPopStateView != null)
                            mPopStateView.hideTeamInfo();//INVISIBLE 会导致UI异常 背影变黑
                    }
                });
            }
        }, 6000);
    }

    private void initListener(){
        mPopStateView.setOnClickPlay(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopStateView.hideNetTips();
                playUnderMobileNet(view);
            }
        });
    }

    public void playUnderMobileNet(View view){
        if(playerListener != null){
            playerListener.onClick(view);
        }
    }
    private View.OnClickListener playerListener;
    public void setPlayUnderMobileNetListener(View.OnClickListener l){
        this.playerListener = l;
    }
}
