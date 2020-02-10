package com.tencent.tga.liveplugin.live.gift.view;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.hpjy_treasure.ReceiveItem;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutView;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.gift.GiftItemNew;
import com.tencent.tga.liveplugin.live.gift.GiftProcess;
import com.tencent.tga.liveplugin.live.gift.GiftStrokeProcess;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemBean;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemState;
import com.tencent.tga.liveplugin.live.gift.presenter.TimerGiftListPresenter;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TimerGiftListView extends BaseFrameLayoutView<TimerGiftListPresenter> {
    private static String TAG = "TimerGiftListView";

    private TimerGiftListPresenter mTimerGiftListPresenter;

    public View mRootView;
    public TextView mGiftIcon;
    public TextView mTime1,mTime2,mTime3,mTime4;
    public GiftItemNew mGift1,mGift2,mGift3,mGift4;
    public GiftProcess mGiftProcess;
    public GiftStrokeProcess mGiftStrokeProcess;
    public BasePopWindow popWindow;

    public ArrayList<GiftItemBean> mBoxs;
    public int mSystemTime;
    public int mWatchTime;

    public Dismiss mDismiss;

    public interface Dismiss{
        void onDismiss();
    }

    public TimerGiftListView(Context context) {
        super(context);
        initView();
    }

    @Override
    protected TimerGiftListPresenter getPresenter() {
        if (mTimerGiftListPresenter == null)
            mTimerGiftListPresenter = new TimerGiftListPresenter();
        return mTimerGiftListPresenter;
    }

    private void initView(){

        mRootView = DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.mvp_timer_gift_list_view,null);
        mRootView.findViewById(R.id.gift_list_close).setOnClickListener(getPresenter());
        mGiftIcon = (TextView) mRootView.findViewById(R.id.gift_icon);
        mGiftProcess = (GiftProcess) mRootView.findViewById(R.id.gift_process);
        mGiftStrokeProcess = (GiftStrokeProcess) mRootView.findViewById(R.id.gift_stroke_process);

        mGift1 = (GiftItemNew) mRootView.findViewById(R.id.gift_list1);
        mGift2 = (GiftItemNew) mRootView.findViewById(R.id.gift_list2);
        mGift3 = (GiftItemNew) mRootView.findViewById(R.id.gift_list3);
        mGift4 = (GiftItemNew) mRootView.findViewById(R.id.gift_list4);

        mGift1.setOnTouchListener(getPresenter().onTouchListener);
        mGift2.setOnTouchListener(getPresenter().onTouchListener);
        mGift3.setOnTouchListener(getPresenter().onTouchListener);
        mGift4.setOnTouchListener(getPresenter().onTouchListener);


        mTime1 = (TextView) mRootView.findViewById(R.id.gift_time1);
        mTime2 = (TextView) mRootView.findViewById(R.id.gift_time2);
        mTime3 = (TextView) mRootView.findViewById(R.id.gift_time3);
        mTime4 = (TextView) mRootView.findViewById(R.id.gift_time4);

        initFont();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = DeviceUtils.dip2px(getContext(),507);
        params.height = DeviceUtils.dip2px(getContext(),245);
        params.gravity = Gravity.CENTER;
        addView(mRootView, params);
        setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
    }

    public void initFont(){
        mTime1.setTypeface(LiveConfig.mFont);
        mTime2.setTypeface(LiveConfig.mFont);
        mTime3.setTypeface(LiveConfig.mFont);
        mTime4.setTypeface(LiveConfig.mFont);
        ((TextView) mRootView.findViewById(R.id.gift_tips)).setTypeface(LiveConfig.mFont);

    }

    public ViewGroup mParent;
    public void show(ViewGroup parent, int watchTime)
    {
        this.mParent = parent;
        mWatchTime = watchTime;

        if (popWindow == null)
        {
            popWindow = new BasePopWindow(this, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,false);
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(false);
            popWindow.setBackgroundDrawable(new ColorDrawable(0xB2000000));
            setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    popWindow.setHideBottomBar();
                }
            });

            if (mDismiss !=null)
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        TLog.e("TimerGiftListView","TimerGiftListView onDismiss");
                        mDismiss.onDismiss();
                    }
                });
        }

        if (!popWindow.isShowing())
        {
            popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
        setData();
        update(watchTime);
    }
    public boolean isShowing(){
        if(popWindow != null && popWindow.isShowing()){
            return true;
        }
        return false;
    }

    public void setData(){
        try {
            mGiftProcess.per1 = mBoxs.get(0).recv_time;
            mGiftProcess.per2 = mBoxs.get(1).recv_time;
            mGiftProcess.per3 = mBoxs.get(2).recv_time;
            mGiftProcess.per4 = mBoxs.get(3).recv_time;
            mGiftProcess.setProcess(mWatchTime);
            mTime1.setText(getTime(mGiftProcess.per1));
            mTime2.setText(getTime(mGiftProcess.per2));
            mTime3.setText(getTime(mGiftProcess.per3));
            mTime4.setText(getTime(mGiftProcess.per4));

            mGift1.setmData(mBoxs.get(0));
            mGift2.setmData(mBoxs.get(1));
            mGift3.setmData(mBoxs.get(2));
            mGift4.setmData(mBoxs.get(3));
            update();//更新一次刷新UI
        } catch (Exception e) {
            TLog.e(TAG, e.getMessage());
        }
    }

    public void receiveBoxId(String id, List<ReceiveItem> recv_box){
        TLog.e(TAG,"receiveBoxId : "+id);
        if (id.equals(mBoxs.get(0).boxid))
        {
            mBoxs.get(0).recv_state = GiftItemState.STATE_RECEVIED;
            getPresenter().showReciveResultTip(id,recv_box);
            mGift1.updateGift(mWatchTime, findViewById(R.id.flag1),mGift1);
        }
        else if (id.equals(mBoxs.get(1).boxid)){
            mBoxs.get(1).recv_state = GiftItemState.STATE_RECEVIED;
            getPresenter().showReciveResultTip(id,recv_box);
            mGift2.updateGift(mWatchTime,findViewById(R.id.flag2),mGift2);
        }else if (id.equals(mBoxs.get(2).boxid)){
            mBoxs.get(2).recv_state = GiftItemState.STATE_RECEVIED;
            getPresenter().showReciveResultTip(id,recv_box);
            mGift3.updateGift(mWatchTime,findViewById(R.id.flag3),mGift3);
        } else if (id.equals(mBoxs.get(3).boxid)){
            mBoxs.get(3).recv_state = GiftItemState.STATE_RECEVIED;
            getPresenter().showReciveResultTip(id,recv_box);
            mGift4.updateGift(mWatchTime,findViewById(R.id.flag4),mGift4);
        }

    }

    public void update(){
       boolean flag = mGift1.updateGift(mWatchTime,findViewById(R.id.flag1),mGift1);
       if (!flag)flag = mGift2.updateGift(mWatchTime,findViewById(R.id.flag2),mGift2);
       if (!flag)flag =  mGift3.updateGift(mWatchTime,findViewById(R.id.flag3),mGift3);
       if (!flag)mGift4.updateGift(mWatchTime,findViewById(R.id.flag4),mGift4);
    }

    public String getTime(int time){
        if (time<=60)
        {
            return time+"秒";
        }else if (time<=60*60)
        {
            if (time%60 == 0) {
                return time / 60 + "分钟";
            } else {
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                return decimalFormat.format(time/60.0) + "分钟";
            }
        }else {
            if (time%3600 == 0) {
                return time / 3600 + "小时";
            } else {
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                return decimalFormat.format(time/3600.0) + "小时";
            }
        }
    }



    public void update(int watchTime){
        mWatchTime = watchTime;
        update();
        mGiftProcess.setProcess(watchTime);
    }


    public void close(){
        try {
            popWindow.dismiss();
            getPresenter().hideTipPopWindow();
            getPresenter().hideReciveResultWindow();
        }catch (Exception e){
            TLog.e("TimerGiftListView","TimerGiftListView close error : "+e.getMessage());
        }
    }


}
