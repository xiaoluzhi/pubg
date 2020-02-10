package com.tencent.tga.liveplugin.live.gift.view;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutView;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemBean;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemState;
import com.tencent.tga.liveplugin.live.gift.presenter.TimerGiftPresenter;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TimerGiftView extends BaseFrameLayoutView<TimerGiftPresenter> {

    private static final String TAG ="TimerGiftView";

    public TimerGiftPresenter mTimerGiftPresenter;

    public ImageView mGiftImage;
    public TextView mGiftIcon;

    public ViewGroup mParent;

    public int mWatchTime ;
    public int mNextTime;//下次领取时间

    public int mSystime;
    public ArrayList<GiftItemBean> mBoxs = new ArrayList<>();

    public TimerGiftListView mTimerGiftListView ;


    public TimerGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static TimerGiftView getInstance(Context context, ViewGroup parent) {
        TimerGiftView timerGiftView = (TimerGiftView) DLPluginLayoutInflater.getInstance(context).inflate(R.layout.mvp_timer_gift_view,null);
        timerGiftView.mParent = parent;
        timerGiftView.initView();
        return timerGiftView;
    }

    @Override
    protected TimerGiftPresenter getPresenter() {
        if (mTimerGiftPresenter == null) mTimerGiftPresenter = new TimerGiftPresenter();
        return mTimerGiftPresenter;
    }

    private void initView(){
        mGiftImage = (ImageView) findViewById(R.id.gift_image);
        mGiftIcon = (TextView) findViewById(R.id.gift_icon);
        mGiftIcon.setTypeface(LiveConfig.mFont);
        setOnClickListener(mTimerGiftPresenter);
    }

    public void show(JSONArray box_list, int serverTime){
        mSystime = serverTime;

        for (int i = 0; i < box_list.length(); i++) {
            try {
                mBoxs.add(new GiftItemBean(box_list.getJSONObject(i).optString("boxid"),
                        box_list.getJSONObject(i).optString("pic_url"),
                        box_list.getJSONObject(i).optString("thumb"),
                        box_list.getJSONObject(i).optString("tip"),
                        box_list.getJSONObject(i).optInt("recv_state"),
                        box_list.getJSONObject(i).optInt("require_time_ms"),
                        box_list.getJSONObject(i).optInt("level"),
                        box_list.getJSONObject(i).optString("name")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DeviceUtils.dip2px(getContext(), 60), DeviceUtils.dip2px(getContext(), 60));
        params.bottomMargin = DeviceUtils.dip2px(getContext(), 50);
        params.rightMargin = DeviceUtils.dip2px(getContext(), 5);
        params.gravity = Gravity.RIGHT|Gravity.BOTTOM;
        if (isReceiveable()||(getNextTime() -mWatchTime)>0)
        {
            mParent.addView(this,params);
            ReportManager.getInstance().commonReportFun("TreasureBoxDisplay", false,"1", VersionUtil.getMachineCode(getContext()), UserInfo.getInstance().mOpenid);
        } else {
            ReportManager.getInstance().commonReportFun("TreasureBoxDisplay", false,"0", VersionUtil.getMachineCode(getContext()), UserInfo.getInstance().mOpenid);
        }

        LiveShareUitl.initLiveWatchTime(getContext());
        mWatchTime = LiveShareUitl.getLiveWatchTime(getContext());
        mTimerGiftPresenter.isOpenFlag = true;
        mTimerGiftPresenter.timerUpdate(false);
    }


    public void onStop() {
        LOG.e(TAG, "onstop .....");
        stopTimer();
        setVisibility(GONE);

    }

    public void onStart() {
        LOG.d(TAG, "onStart .....");
        if (getNextTime() - mWatchTime >0 || isReceiveable())
        {
            mTimerGiftPresenter.isOpenFlag = true;
            mTimerGiftPresenter.timerUpdate(true);
            if(mTimerGiftListView != null && mTimerGiftListView.isShowing()){
                setVisibility(GONE);
            }else {
                if(hasAllReceived()){
                    setVisibility(GONE);
                }else {
                    setVisibility(VISIBLE);
                }
            }
        }else {
            mTimerGiftPresenter.isOpenFlag = false;
            mTimerGiftPresenter.stopUpdate();
            setVisibility(GONE);
        }

    }

    public void saveWatchTime(){
        LiveShareUitl.saveLiveWatchTime(getContext(), mWatchTime,true);
    }

    public void stopTimer(){
        mTimerGiftPresenter.stopUpdate();
        saveWatchTime();
    }


    public void initData(){
        mTimerGiftPresenter.reqGiftList();
        mTimerGiftPresenter.initIconUrl();
    }


    /**
     * 是否有可以领取礼包
     * @return
     */
    public boolean isReceiveable(){

        for (GiftItemBean bean : mBoxs) {
            if ((bean.recv_state == GiftItemState.STATE_RECEVIEING) && bean.recv_time <=mWatchTime)
                return true;
        }
        return  false;
    }

    /**
    * 已经全部领取
    * @author hyqiao
    * @time 2018/6/20 16:58
    */
    public boolean hasAllReceived(){
        try {
            if(mBoxs == null || mBoxs.size() == 0){
                return true;
            }
            for(int i = 0;i<mBoxs.size();i++){
                if(mBoxs.get(i).recv_state != GiftItemState.STATE_RECEVIED){
                    return false;
                }
            }
        }catch (Exception e){
            TLog.e(TAG,"hasAllReceived error : "+e.getMessage());
        }
        return true;
    }

    public int getNextTime(){

        for (GiftItemBean bean : mBoxs) {
            if ((bean.recv_state == GiftItemState.STATE_RECEVIEING) && bean.recv_time>mWatchTime)
            {
                mNextTime = bean.recv_time;
                return  mNextTime;
            }
        }

        return  mNextTime;
    }


    public void dismiss(){
        setVisibility(View.GONE);
    }

    public void undismiss(){
        if(hasAllReceived()){
            setVisibility(GONE);
        }else {
            setVisibility(VISIBLE);
        }
    }

    public void releaseTimerGiftView(){
        try {
            if(mTimerGiftPresenter != null){
                mTimerGiftPresenter.releaseTimerGiftPresenter();
            }
        }catch (Exception e){
            TLog.e(TAG,"releaseTimerGiftView error : "+e.getMessage());
        }
    }
}
