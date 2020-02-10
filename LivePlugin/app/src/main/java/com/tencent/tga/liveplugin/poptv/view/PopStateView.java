package com.tencent.tga.liveplugin.poptv.view;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.plugin.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by hyqiao on 2017/6/14.
 */

public class PopStateView extends RelativeLayout {
    private static String TAG = "PopStateView";
    private Context mContext;
    private Resources mResources;

    private String mPopBk = "";

    //网络状态
    private LinearLayout mLlyMobileNet;
    private ImageView mIvMobileNet;
    private TextView mIvMobileNetText;

    //视频加载动画
    private LinearLayout mAnimContainer;
    private ImageView mAnimView;
    private TextView mAnimTips;
    private AnimationDrawable mAnim;

    //队伍比赛信息
    private boolean has_current_match = false;
    private RelativeLayout mRlyTitle;
    private String mLeagueTitle = "";//放在配图中
    private String mLeftName = "";
    private String mRightName = "";
    private String urlleft = "";
    private String urlright = "";
    private String mCenterInfo = "";
    private RelativeLayout mRlyRootView;
    private RelativeLayout mRlyLeftIconBg,mRlyRightIconBg;
    private ImageView mIvLeftIcon,mIvRightIcon,mIvVideoType;
    private TextView mTvCenterInfo,mTvLeftName,mTvRightName;

    public PopStateView(Context context, Resources resources) {
        super(context);
        mContext = context;
        mResources = resources;
        init();
    }

    private void init(){
        XmlResourceParser xmlTitleview = mResources.getLayout(R.layout.poptv_state_layout);
        DLPluginLayoutInflater.getInstance(mContext).inflate(xmlTitleview,this);

        mRlyRootView = (RelativeLayout) findViewById(R.id.mRlyRootView);
        mLlyMobileNet = (LinearLayout) findViewById(R.id.mLlyMobileNet);
        mIvMobileNet = (ImageView) findViewById(R.id.mIvMobileNet);
        mIvMobileNetText = (TextView) findViewById(R.id.mIvMobileNetText);


        mAnimContainer = (LinearLayout) findViewById(R.id.anim_container);
        mAnimView = (ImageView) findViewById(R.id.anim_loading);
        mAnimTips = (TextView) findViewById(R.id.anim_text);
        mAnim = (AnimationDrawable) mAnimView.getDrawable();


        mRlyTitle = (RelativeLayout) findViewById(R.id.mRlyTitle);
        mTvLeftName = (TextView) findViewById(R.id.mTvLeftName);
        mTvCenterInfo = (TextView) findViewById(R.id.mTvCenterInfo);
        mTvRightName = (TextView) findViewById(R.id.mTvRightName);
        mRlyLeftIconBg = (RelativeLayout) findViewById(R.id.mRlyLeftIconBg);
        mRlyRightIconBg = (RelativeLayout) findViewById(R.id.mRlyRightIconBg);
        mIvLeftIcon = (ImageView) findViewById(R.id.mIvLeftIcon);
        mIvRightIcon = (ImageView) findViewById(R.id.mIvRightIcon);
        mIvVideoType = (ImageView) findViewById(R.id.mIvVideoType);
    }

    public void setTeamData(String mMatchInfo){
        JSONObject mJsonobject = null;
        try {
            mJsonobject = new JSONObject(mMatchInfo);
            TLog.e(TAG,"mMatchInfo : "+mMatchInfo);
            has_current_match = mJsonobject.optBoolean("has_current_match",false);
            mLeagueTitle = mJsonobject.optString("league_title");//标题
            mLeftName = mJsonobject.optString("host_team_name");//"Estar";
            mRightName = mJsonobject.optString("guest_team_name");//"TGA";
            urlleft = mJsonobject.optString("host_team_logo");
            urlright = mJsonobject.optString("guest_team_logo");
            int mLeftScore = mJsonobject.optInt("host_team_score");
            int mRightScore = mJsonobject.optInt("guest_team_score");
            mPopBk = mJsonobject.optString("pop_bk").trim();

            mCenterInfo = mLeftScore+" : "+mRightScore;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(has_current_match){
            mRlyTitle.setVisibility(VISIBLE);
        }else {
            mRlyTitle.setVisibility(GONE);
        }

        mTvLeftName.setText(mLeftName);
        mTvCenterInfo.setText(mCenterInfo);
        mTvRightName.setText(mRightName);
        ImageLoaderUitl.loadRoundImageForImageView(urlleft, mIvLeftIcon);
        ImageLoaderUitl.loadRoundImageForImageView(urlright, mIvRightIcon);
    }

    public void setFont(Typeface font){
        if(font!=null){
            mTvLeftName.setTypeface(font);
            mTvCenterInfo.setTypeface(font);
            mTvRightName.setTypeface(font);
            mIvMobileNetText.setTypeface(font);
            mAnimTips.setTypeface(font);
        }
    }

    public void hideTeamInfo(){
        if(mRlyTitle != null){
            mRlyTitle.setVisibility(GONE);
        }
    }
    public void showNetTips(){
        if(mLlyMobileNet != null){
            mLlyMobileNet.setVisibility(VISIBLE);
        }
    }

    public void hideNetTips(){
        if(mLlyMobileNet != null){
            mLlyMobileNet.setVisibility(GONE);
        }

        hidenMobileNetBg();
    }

    public void showMobileNetBg(){
        if(mRlyRootView != null){
            mRlyRootView.setVisibility(VISIBLE);
            ImageLoaderUitl.loadImageForViewGroup(mPopBk,mRlyRootView);
        }
    }

    public void hidenMobileNetBg(){
        if(mRlyRootView != null){
            mRlyRootView.setVisibility(GONE);
            mRlyRootView.setBackground(null);
        }
    }

    public void showLoading(){
        if(mAnimContainer != null){
            mAnimContainer.setVisibility(VISIBLE);
        }
        if(mAnim != null){
            mAnim.start();
        }
    }

    public void setOnClickPlay(OnClickListener listener){
        if(mIvMobileNet != null){
            mIvMobileNet.setOnClickListener(listener);
        }
    }

    public void hideLoading(){
        if(mAnimContainer != null){
            mAnimContainer.setVisibility(GONE);
        }
        if(mAnim != null){
            mAnim.stop();
        }
    }


    public void releaseView(){
        setBackground(null);
        mIvMobileNet.setImageDrawable(null);
        mAnimView.setImageDrawable(null);

        mRlyTitle.setBackground(null);
        mRlyLeftIconBg.setBackground(null);
        mRlyRightIconBg.setBackground(null);
        mIvLeftIcon.setImageDrawable(null);
        mIvRightIcon.setImageDrawable(null);
        mIvVideoType.setImageDrawable(null);
    }
}
