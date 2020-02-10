package com.tencent.tga.liveplugin.live.gift;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemBean;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemState;
import com.tencent.tga.plugin.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GiftItemNew extends RelativeLayout {
    private static String TAG = "GiftItemNew";
    private Context mContext;
    private TextView mIvGiftItemName;
    private ImageView mIvGiftItemPrize;
    private ImageView mIvGiftItemState;
    private TextView mTvGiftItemRemainTime;

    private android.os.Handler mHandler;


    public GiftItemBean mData = new GiftItemBean();
    //时间
    private int leftTime;
    private int minute;
    private int second;

    private boolean isDoAnim = true;


    public GiftItemNew(Context context) {
        super(context);
    }

    public GiftItemNew(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    private void init() {
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.gift_item_view, this);

        mIvGiftItemName = findViewById(R.id.mIvGiftItemName);
        mIvGiftItemPrize = findViewById(R.id.mIvGiftItemPrize);
        mIvGiftItemState = findViewById(R.id.mIvGiftItemState);
        mTvGiftItemRemainTime = findViewById(R.id.mTvGiftItemRemainTime);
        if(null != LiveConfig.mFont) {
            mIvGiftItemName.setTypeface(LiveConfig.mFont);
            mTvGiftItemRemainTime.setTypeface(LiveConfig.mFont);
        }
        isDoAnim = ConfigInfo.getmInstance().getConfig(ConfigInfo.BOX_ANIM_SWITCH);
    }

    public void setmData(GiftItemBean box) {
        mData = box;
        mIvGiftItemName.setText(box.name);
        ImageLoaderUitl.loadimage(box.pic_url,mIvGiftItemPrize);
        setCannotReceiveWithOutTime();
    }

    public boolean updateGift(int mWatchTime, View flag,View img) {
        leftTime = mData.recv_time - mWatchTime;
        minute = leftTime / 60;
        second = leftTime % 60;

        if (leftTime > 0) {
            setCannotReceiveWithTime(String.format("%02d:%02d", minute, second));
            flag.setBackgroundResource(R.drawable.flag_disable);
            return true;//不需要更新其它item
        } else {
            if (mData.recv_state == GiftItemState.STATE_RECEVIEING ) {
                setCanReceive(img);
            } else if (mData.recv_state == GiftItemState.STATE_RECEVIED ) {
                setHasReceiveD(img);
            }
            flag.setBackgroundResource(R.drawable.flag_enable);

        }
        return false;
    }

    public void setCanReceive(View img){
        clearData();
        if (isDoAnim) startAnim();
        mIvGiftItemState.setBackgroundResource(R.drawable.icon_box_can_receive);
        switch (img.getId()){
            case R.id.gift_list1:
            case R.id.gift_list2:
                img.setBackgroundResource(R.drawable.icon_box_can_receive_light_bg1);
                break;
            case R.id.gift_list3:
            case R.id.gift_list4:
                img.setBackgroundResource(R.drawable.icon_box_can_receive_light_bg2);
                break;

        }
    }

    public void setHasReceiveD(View img){
        clearData();
        if (isDoAnim) stopAnim();
        mIvGiftItemState.setBackgroundResource(R.drawable.icon_box_has_received);
        switch (img.getId()){
            case R.id.gift_list1:
            case R.id.gift_list2:
                img.setBackgroundResource(R.drawable.icon_box_can_receive_bg2);
                break;
            case R.id.gift_list3:
            case R.id.gift_list4:
                img.setBackgroundResource(R.drawable.icon_box_can_receive_bg);
                break;

        }
    }

    public void setCannotReceiveWithTime(String time){
        clearData();
        mTvGiftItemRemainTime.setText(time);
    }

    public void setCannotReceiveWithOutTime(){
        clearData();
    }

    private void clearData(){
        mIvGiftItemState.setBackground(null);
        mTvGiftItemRemainTime.setText("");
    }

    /**
     * 判断是否可领取
     *
     * @param mWatchTime
     * @return
     */
    public boolean isReceiveable(int mWatchTime) {
        if (mData.recv_state == GiftItemState.STATE_RECEVIEING && mData.recv_time <= mWatchTime)
            return true;
        return false;
    }

    private float angle1 = 8;//最大角度
    private float angle2 = angle1/2;

    int period1 = 100;//最大角度的对应时间
    int period2 = period1/2;

    int anim_period = 200;//两个动画之间的间隔

    AnimatorSet animatorSet = new AnimatorSet();
    private void startAnim() {
        try {
            TLog.e(TAG, "startAnim");
            if (mHandler == null) {
                mHandler = new android.os.Handler(Looper.getMainLooper());
            }
            ObjectAnimator ra1 = ObjectAnimator.ofFloat(mIvGiftItemPrize, "rotation", 0f, -angle1);
            ra1.setDuration(period1);

            ObjectAnimator ra2 = ObjectAnimator.ofFloat(mIvGiftItemPrize, "rotation", -angle1, angle1);
            ra2.setDuration(period1 * 2);

            ObjectAnimator ra3 = ObjectAnimator.ofFloat(mIvGiftItemPrize, "rotation", angle1, -angle2);
            ra3.setDuration(period1 + period2);

            ObjectAnimator ra4 = ObjectAnimator.ofFloat(mIvGiftItemPrize, "rotation", -angle2, angle2);
            ra4.setDuration(period2 * 2);

            ObjectAnimator ra5 = ObjectAnimator.ofFloat(mIvGiftItemPrize, "rotation", angle2, 0);
            ra5.setDuration(period2);

            animatorSet.playSequentially(ra1, ra2, ra3, ra4, ra5);
            animatorSet.start();
            animatorSet.removeAllListeners();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    try {
                        if (mHandler == null)
                            return;
                        mHandler.postDelayed(runnable, anim_period);
                    } catch (Exception e) {
                        TLog.e(TAG, "GiftItem repeat anim 避免空指针");
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    Runnable runnable  = new Runnable() {
        @Override
        public void run() {
//            TLog.e(TAG,TAG+" is repeating");
            animatorSet.start();
        }
    };
    private void stopAnim(){
        try {
            TLog.e(TAG,"stopAnim");
            animatorSet.end();
            animatorSet.cancel();
            if(mHandler != null){
                mHandler.removeCallbacksAndMessages(null);
            }
            //mHandler.removeCallbacks(runnable);
        }catch (Exception e){
            TLog.e(TAG,"stopAnim error : "+e.getMessage());
        }
    }

    public void releaseView(){
        stopAnim();
        mHandler = null;
    }

}
