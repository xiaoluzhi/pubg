package com.tencent.tga.liveplugin.poptv.manager;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.poptv.bean.PopTvBean;
import com.tencent.tga.liveplugin.poptv.view.BottomOperateView;
import com.tencent.tga.liveplugin.poptv.view.PopStateView;
import com.tencent.tga.liveplugin.networkutil.NetUtils;

/**
 * Created by hyqiao on 2018/4/9.
 */

public class PopTvBannerManager {

    private final static String TAG = "PopTvPalyerManager";
    private Activity mActivity;
    private Resources mResources;
    private PopTvBean mPopTvBean;
    private PopStateView mPopStateView;
    private RelativeLayout mRlyBottomOperation;
    private Typeface mFont;

    private BottomOperateView mBottomOperateView;

    public PopTvBannerManager(Activity activity, Resources resources, PopTvBean popTvBean, PopStateView popStateView, RelativeLayout rlyBottomOperation) {
        this.mActivity = activity;
        this.mResources = resources;
        this.mPopTvBean = popTvBean;
        this.mPopStateView = popStateView;
        this.mRlyBottomOperation = rlyBottomOperation;
    }

    public void setFont(Typeface font){
        this.mFont = font;
    }

    public void initPopTvBannerManager(){

        mBottomOperateView = new BottomOperateView(mActivity, mResources);
        mBottomOperateView.setFont(mFont);
        mRlyBottomOperation.addView(mBottomOperateView);
        initListener();
    }

    public void getOperateData() {
        TLog.e(TAG, "banner_switch = " + mPopTvBean.banner_switch);
        // TODO: 2018/4/10  如果要设置底部运营位删除这部分代码就好
        if(!TextUtils.isEmpty(mPopTvBean.operation_image) && !TextUtils.isEmpty(mPopTvBean.operation_button) && NetUtils.isNetworkAvailable(mActivity)){
            mBottomOperateView.setData(mPopTvBean.operation_image,mPopTvBean.operation_button);
        }else {
            showDefaultUI();
        }
    }

    private void initListener(){
        mBottomOperateView.setFullScreenClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBottomOperateView.isShowedRedPot()) {
                    mBottomOperateView.setRedPotInVisible();
                }
                onDialogExit(false);
            }
        });

        mBottomOperateView.setShowDefaultUIListener(new BottomOperateView.ShowDefaultUIListener() {
            @Override
            public void showDefaultui() {
                showDefaultUI();
            }
        });
    }

    private int bannerInfoType = 0;
    private int getBannerInfoType(){
        if(!TextUtils.isEmpty(mPopTvBean.operation_button) && !TextUtils.isEmpty(mPopTvBean.operation_button)){
            bannerInfoType = 3;
        }
        return bannerInfoType;
    }

    public interface PopBannerListener{
        void onShowDefaultUI();
        void onDialogExit(boolean b);
    }

    private PopBannerListener mPopBannerListener;

    public void setPopBannerListener(PopBannerListener l){
        this.mPopBannerListener = l;
    }
    private void showDefaultUI(){
        if (mBottomOperateView != null) {
            mBottomOperateView.showDefaultUI();
        }
        if(mPopBannerListener != null){
            mPopBannerListener.onShowDefaultUI();
        }
    }

    private void onDialogExit(boolean isNeedAnim){
        if(mPopBannerListener != null){
            mPopBannerListener.onDialogExit(isNeedAnim);
        }
    }

    public void setCloseClick(View.OnClickListener listener){
        if(mBottomOperateView != null)
            mBottomOperateView.setCloseClick(listener);
    }
}
