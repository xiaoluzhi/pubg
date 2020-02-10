package com.tencent.tga.liveplugin.live.player.ui.video.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKNetVideoInfo;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.common.util.AnimationUtil;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.common.util.UIAdaptationUtil;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;


/**
 *
 */
public class PlayerController extends LinearLayout {
    private static final String TAG ="PlayerController";

    public ImageView mSwitch;
    public ToggleButton mDanmuOper;
    public TextView mEditText;
    public TextView mHot;
    public ImageView mPause;
    public TextView mVideoDefin;
    public LiveDefineView mDefineView;
    public ImageView mDanmuSetting;
    private View mDiv;

    private ArrayList<ControllerStateChangeListener> mVisibilityListeners = new ArrayList<ControllerStateChangeListener>();

    public PlayerController(Context context) {
        super(DLPluginLayoutInflater.getInstance(context).getContext());
    }

    public PlayerController(Context context, AttributeSet attrs) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs);
    }

    public PlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs, defStyleAttr);
    }

    public static PlayerController newInstance( PlayView view){
        PlayerController controller = (PlayerController) DLPluginLayoutInflater.getInstance(view.getContext()).inflate(R.layout.view_player_controller,null);
        controller.initViews();
        return controller;
    }

    public void attatch( PlayView playView){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.video_bottom_height));
        params.gravity = Gravity.BOTTOM;
        playView.addView(this, playView.getChildCount(), params);
    }

    private void initViews(){

        mPause =  findViewById(R.id.live_pause);

        try {
            if(UIAdaptationUtil.hasCameraHole()) {
                LinearLayout.LayoutParams layoutParams = (LayoutParams) mPause.getLayoutParams();
                layoutParams.leftMargin = DeviceUtils.dip2px(getContext(), 30);
                mPause.setLayoutParams(layoutParams);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mSwitch =  findViewById(R.id.switch_mode);

        mDanmuSetting =  findViewById(R.id.danmu_setting);

        mDanmuOper = findViewById(R.id.danmu_oper);

        mDiv = findViewById(R.id.div);

        mEditText =  findViewById(R.id.edit_text);
        mHot =  findViewById(R.id.edit_hot);
        setDanumIcon(LiveShareUitl.isShowDanmu(getContext()));
        mVideoDefin = findViewById(R.id.define_select);
        mVideoDefin.setOnClickListener(view -> PlayViewEvent.videfineClick());
    }

    public interface ControllerStateChangeListener {
        void onVisibilityChanged(PlayerController controller,int visibility, boolean isFullScreen);
    }


    public void setVisibility(int visibility,boolean isFullScreen) {
        if (getVisibility() == visibility) return;
        if (isFullScreen){
            mHot.setVisibility(VISIBLE);
            mEditText.setVisibility(VISIBLE);
        }else {
            mHot.setVisibility(GONE);
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
        if (null != mVisibilityListeners && mVisibilityListeners.size() > 0) {
            for (ControllerStateChangeListener listener : mVisibilityListeners) {
                listener.onVisibilityChanged(this, visibility, isFullScreen);
            }
        }
    }

    public void switchMode(boolean isFullscreen){
        if (isFullscreen) {
            mSwitch.setImageResource(R.drawable.switch_mode_to_notfullscreen);
            mHot.setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.VISIBLE);
            mDiv.setVisibility(VISIBLE);
        } else {
            mSwitch.setImageResource(R.drawable.switch_mode_to_fullscreen);
            mHot.setVisibility(View.GONE);
            mEditText.setVisibility(View.GONE);
            mDiv.setVisibility(GONE);
        }
    }

    public void setDanumIcon(final boolean isShow){
        if (mDanmuOper !=null){
            mDanmuOper.setChecked(isShow);
        }

    }

    public void dismissDialogs() {
        if (null != mDefineView && mDefineView.isShowing()) {
            mDefineView.dismiss();
        }
    }



    public void setCurDefine(final TVKNetVideoInfo.DefnInfo curDefine){
        if (curDefine ==null || TextUtils.isEmpty(curDefine.getDefn()))
            return;
        String defineTxt = "高清";
        if (null != curDefine.getDefnName() && curDefine.getDefnName().length() >=2) {
            defineTxt = curDefine.getDefnName().trim().substring(0, 2);
        }

        setSharpnessIcon(defineTxt);
    }

    /**
    * 设置清晰度图标
    * @author hyqiao
    * @time 2018/4/30 16:03
    */
    private void setSharpnessIcon(String name){
        if (TextUtils.isEmpty(name) || mVideoDefin == null)
            return;
        if(name.equals("标清")){
            mVideoDefin.setText("标清");
        }else if(name.equals("高清")){
            mVideoDefin.setText("高清");
        } else if(name.equals("超清")){
            mVideoDefin.setText("超清");
        }else if(name.equals("蓝光")){
            mVideoDefin.setText("蓝光");
        }
    }

    public void setmEditText(String editText){
        if (mEditText ==null) return;

        if (editText ==null || "".equals(editText))
        {
            mEditText.setText("");
            mEditText.setHint(R.string.hint_live_chat);
        }else
            mEditText.setText(editText);
    }

    public void setOnpauseView(){
        post(() -> mPause.setImageDrawable((getResources().getDrawable(R.drawable.control_icon_playing))));

    }
    public void setOnStartView(){
        post(() -> mPause.setImageDrawable((getResources().getDrawable(R.drawable.control_icon_stop))));

    }

}
