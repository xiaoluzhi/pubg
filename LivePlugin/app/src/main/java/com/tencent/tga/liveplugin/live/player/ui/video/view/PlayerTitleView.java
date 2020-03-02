package com.tencent.tga.liveplugin.live.player.ui.video.view;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo;
import com.tencent.tga.liveplugin.live.common.util.AnimationUtil;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.common.views.RedPotView;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by fluxliu on 2016/8/22.
 *
 */
public class PlayerTitleView extends LinearLayout {

    private TextView mTitle;
    private TextView mOnlineNum;
    private ImageView mOnlineIm;//自定义字体不能居中蛋疼
    private ImageView mTitleIcon;
    public RedPotView mLiveLineSelect;//直播流选择
    private TextView mLiveLineText;
    public boolean isClickRedPot =false;
    private DecimalFormat df = new DecimalFormat("0.0");
    public PlayerTitleView(Context context) {
        super(DLPluginLayoutInflater.getInstance(context).getContext());
    }

    public PlayerTitleView(Context context, AttributeSet attrs) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs);
    }

    public PlayerTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs, defStyleAttr);
    }


    public static PlayerTitleView newInstance(Context context){
        PlayerTitleView view = (PlayerTitleView) DLPluginLayoutInflater.getInstance(context).inflate(R.layout.view_player_title,null);
        view.init();
        return view;
    }

    private void init(){
        df.setRoundingMode(RoundingMode.HALF_UP);
        mTitle =  findViewById(R.id.match_title);

        mOnlineNum =  findViewById(R.id.online_num_tv);
        mOnlineIm =  findViewById(R.id.online_num_im);
        mTitleIcon =  findViewById(R.id.match_title_icon);
        mLiveLineSelect = findViewById(R.id.live_line_select);
        mLiveLineText = findViewById(R.id.live_line_text);
        findViewById(R.id.live_line_container).setOnClickListener(view -> PlayViewEvent.liveLineClick());
        mOnlineNum.setTypeface(LiveConfig.mFont);
        mLiveLineText.setTypeface(LiveConfig.mFont);
        setBackgroundColor(0x4c0F121C);
        setPadding(0,0,0,0);
    }

    public int mLineSize = 1;

    public void setSizeAndVivibility(List<ChannelInfo> list){
        if (list == null && list.size()==0) return;
        mLineSize = list.size();
        boolean isMultiRoom = false;
        for(ChannelInfo info : list) {
            if (1 == info.getPlay_type() && info.getRoom_list().size() > 1) {
                isMultiRoom = true;
                break;
            }
        }
        boolean isShow = mLineSize>1 || mLineSize == 1 && list.get(0).getRoom_list().size() > 1; //只有一个频道，有多个房间也显示
        mLiveLineSelect.setVisibility(isShow ? VISIBLE : GONE);
        mLiveLineText.setVisibility(isShow ? VISIBLE : GONE);
//        if (!isClickRedPot) {
            if (isMultiRoom && LiveShareUitl.isLiveLineRed(getContext())) {
                mLiveLineSelect.setRadius(2);
                mLiveLineSelect.setRedPotMargin(1, 0);
                mLiveLineSelect.setTipOn(true);
                PlayViewEvent.showLiveLineTips();
            }  else
                mLiveLineSelect.setTipOn(false);
//        }
    }

    public void switchMode(boolean isFull){
        setPadding(0,0,0,0);
        if (isFull){
            mOnlineNum.setTextColor(0xFFFFFFFF);
            mOnlineIm.setImageResource(R.drawable.online_num_fullscreen_icon);
            setBackground(null);
            setBackgroundResource(R.drawable.live_top_bg);
        }else {
            mOnlineNum.setTextColor(0xffBDBDBD);
            mOnlineIm.setImageResource(R.drawable.online_num_not_fullscreen_icon);
            setBackground(null);
            setBackgroundColor(0x4c0F121C);
        }
    }


    public void setVisibility(int visibility,boolean full) {
        if (getVisibility() == visibility) return;
        if (visibility == VISIBLE){
            setVisibility(visibility);
            startAnimation(AnimationUtil.topIn(getContext()));
        }else{

            startAnimation(AnimationUtil.topOut(getContext(), new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            }));
        }

    }

    public void setTitle(String title,int type){
        if (type == 1)
        {
            mTitle.setText(title);
            mTitleIcon.setImageResource(R.drawable.title_live_icon);
        }else if (type == 2){
            mTitle.setText(title);
            mTitleIcon.setImageResource(R.drawable.title_video_icon);
        }else if (type == 3){
            mTitle.setText(title);
            mTitleIcon.setImageResource(R.drawable.title_progam_icon);
        }

    }

    public void setmOnlineNum(int num)
    {
        if (null != mOnlineNum)
        {
            ((View)mOnlineNum.getParent()).setVisibility(VISIBLE);
            mOnlineNum.setText(getOnlineStr(num));
        }
    }
    public String getOnlineStr(int num) {
        String string = "";
        if(num<1)
        {
            string = "1";
        }else if (num<=9999)
        {
            string = num+"";
        }else if (num<=99999999)
        {
            string = df.format(num/10000.0)+"万";
        }else if (num>99999999)
        {
            string = df.format(num/100000000.0)+"亿";
        }
        return string;
    }

}
