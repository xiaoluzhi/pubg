package com.tencent.tga.liveplugin.video.ui;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.common.util.AnimationUtil;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.common.util.UIAdaptationUtil;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 *
 */
public class VideoPlayerController extends LinearLayout{
    private static final String TAG ="VideoPlayerController";

    public ToggleButton mDanmuOper;
    public TextView mEditText;
    public TextView mHot;
    public ImageView mVideoPause;
    public TextView mVideoDefin;

    public ImageView mDanmuSetting;

    public SeekBar mSeekBar;//进度条
    private TextView mPlayTime;//播放时间
    private TextView mAllTime;//总时长

    public TextView mSelect;//选集

    private static final int MAX_PROGRESS = 1000;//进度条


    public VideoPlayerController(Context context) {
        super(DLPluginLayoutInflater.getInstance(context).getContext());
        initViews();
    }

    public VideoPlayerController(Context context, AttributeSet attrs) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs);
        initViews();
    }

    public VideoPlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs, defStyleAttr);
    }

    private void initViews(){
        try {
            DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.video_player_controller,this);
            mDanmuSetting = (ImageView) findViewById(R.id.danmu_setting);

            mDanmuOper = (ToggleButton) findViewById(R.id.danmu_oper);

            mEditText = (TextView) findViewById(R.id.edit_text);
            //mEditText.setTypeface(LiveView.mFont);
            mEditText.setHintTextColor(0xff4a4949);
            setDanumIcon(LiveShareUitl.isShowDanmu(getContext()));
            mVideoDefin = (TextView) findViewById(R.id.define_select);
            //mVideoDefin.setTypeface(LiveView.mFont);
            mSelect = (TextView) findViewById(R.id.video_select);
            mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
            mSeekBar.setMax(MAX_PROGRESS);
            mSeekBar.setProgress(0);
            mPlayTime = (TextView) findViewById(R.id.play_time);
            mAllTime = (TextView) findViewById(R.id.all_time);
            mVideoPause = (ImageView) findViewById(R.id.video_pause);
            mDanmuOper.setChecked(LiveShareUitl.isShowVideoDanmu(getContext()));
            if(UIAdaptationUtil.hasCameraHole()) {
                LayoutParams layoutParams = (LayoutParams) mVideoPause.getLayoutParams();
                layoutParams.leftMargin = DeviceUtils.dip2px(getContext(), 20);
                mVideoPause.setLayoutParams(layoutParams);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void initOnClickListener(OnClickListener listener){
        mVideoPause.setOnClickListener(listener);
        mDanmuOper.setOnClickListener(listener);
        mDanmuSetting.setOnClickListener(listener);
        mEditText.setOnClickListener(listener);
    }

    public void setVisibility(int visibility,boolean isFullScreen) {
        if (getVisibility() == visibility) return;

        if (isFullScreen){
            mEditText.setVisibility(VISIBLE);
        }else {
            mEditText.setVisibility(GONE);
        }

        if (visibility == VISIBLE){
            setVisibility(visibility);
            startAnimation(AnimationUtil.bottomIn(getContext()));
        }else{
            startAnimation(AnimationUtil.bottomOut(getContext(), new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    LOG.e(TAG,"onAnimationEnd");
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            }));
        }
    }

    public void updateViews(boolean isShowSelect) {
        mSelect.setVisibility(isShowSelect ? View.VISIBLE : View.GONE);
    }

    public void setPauseIcon(int resId) {
        mVideoPause.setImageResource(resId);
    }

    public void setDanumIcon(final boolean isShow){
        if (mDanmuOper !=null){
            mDanmuOper.setChecked(isShow);
        }

    }

    public void setmEditText(String editText){
        if (mEditText ==null) return;

        if (editText ==null || "".equals(editText)) {
            mEditText.setText("");
            mEditText.setHint(R.string.hint_live_chat);
        }else
            mEditText.setText(editText);
    }


    public void setAllTime(String text) {
        mAllTime.setText(text);
    }

    public void setPlayTime(String text) {
        mPlayTime.setText(text);
    }


    public void setDefine(String text) {
        mVideoDefin.setText(text);
    }

}

