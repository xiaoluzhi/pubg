package com.tencent.tga.liveplugin.live.gift.presenter;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.imageloader.core.assist.FailReason;
import com.tencent.tga.imageloader.core.listener.ImageLoadingListener;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.gift.model.TimerGiftModel;
import com.tencent.tga.liveplugin.live.gift.proxy.TreasureBoxListProxy;
import com.tencent.tga.liveplugin.live.gift.view.TimerGiftListView;
import com.tencent.tga.liveplugin.live.gift.view.TimerGiftView;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.networkutil.VersionUtil;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

public class TimerGiftPresenter extends BaseFrameLayoutPresenter<TimerGiftView, TimerGiftModel> implements View.OnClickListener {

    private static final String TAG = "TimerGiftPresenter";

    public String mBoxIconUrl;
    public String mBoxIconLightUrl;

    @Override
    public TimerGiftModel getModel() {
        if (modle == null) {
            modle = new TimerGiftModel(this);
        }
        return modle;
    }

    private static final int UPDATE_TIMER = 1;
    int minute,second,leftTime;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case UPDATE_TIMER:
                    update();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick())
            return;
        switch (v.getId()) {
            case R.id.gift_container:
                if (getView().mTimerGiftListView  == null)
                {
                    getView().mTimerGiftListView = new TimerGiftListView(getView().getContext());
                    getView().mTimerGiftListView.mSystemTime = getView().mSystime;
                    getView().mTimerGiftListView.mBoxs = getView().mBoxs;
                    getView().mTimerGiftListView.mDismiss = new TimerGiftListView.Dismiss() {
                        @Override
                        public void onDismiss() {
                            if (getView().isReceiveable()||getView().getNextTime() - getView().mWatchTime>0) {
                                getView().undismiss();
                            }
                        }
                    };
                }
                getView().mTimerGiftListView.show(getView().mParent, getView().mWatchTime);
                getView().dismiss();
                ReportManager.getInstance().commonReportFun("TVUserWatchMissionClick", false,"");
                break;
            default:
                break;
        }
    }

    public void initIconUrl() {
        String json = ConfigInfo.getmInstance().getStringConfig(ConfigInfo.BOX_ICON);
        try {
            JSONObject j = new JSONObject(json);
            mBoxIconLightUrl = j.optString("lighting-icon");
            mBoxIconUrl = j.optString("unlighting-icon");
        } catch (Exception e) {
            TLog.e(TAG, e.getMessage());
        }
    }

    private void update(){
        if (getView() == null)
        {
            stopUpdate();
            return;
        }
        getView().mWatchTime ++;
        leftTime = getView().getNextTime() - getView().mWatchTime;

        minute =  leftTime / 60;
        second =  leftTime % 60;
        if (second == 0)
        {
            LiveShareUitl.saveLiveWatchTime(getView().getContext(),getView().mWatchTime, true);
        }

        if (getView().isReceiveable())
        {
            getView().mGiftIcon.setTextColor(0xFFFFC951);
            getView().mGiftIcon.setText("可领取");
            if (TextUtils.isEmpty(mBoxIconLightUrl)) {
                getView().mGiftImage.setImageResource(R.drawable.gift_icon_able);
            } else {
                ImageLoaderUitl.loadimage(mBoxIconLightUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        getView().mGiftImage.setImageResource(R.drawable.gift_icon_able);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                         if (null != loadedImage) {
                             getView().mGiftImage.setImageBitmap(loadedImage);
                         }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }

        }
        else if (leftTime>0)
        {
            getView().mGiftIcon.setTextColor(0xFFEAECEF);
            getView().mGiftIcon.setText(String.format("%02d:%02d", minute, second));
            if (TextUtils.isEmpty(mBoxIconUrl)) {
                getView().mGiftImage.setImageResource(R.drawable.gift_icon_timer);
            } else {
                ImageLoaderUitl.loadimage(mBoxIconUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        getView().mGiftImage.setImageResource(R.drawable.gift_icon_timer);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (null != loadedImage) {
                            getView().mGiftImage.setImageBitmap(loadedImage);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
        if (leftTime>0) {
            timerUpdate(true);

        }else if (getView().mBoxs!=null&& getView().mBoxs.size()>0){//已完成
            getView().mWatchTime = getView().mBoxs.get(getView().mBoxs.size()-1).recv_time;
                LiveShareUitl.saveLiveWatchTime(getView().getContext(),getView().mWatchTime, true);
        }

        if (getView().mTimerGiftListView  != null && getView().mTimerGiftListView.isShown() )
            getView().mTimerGiftListView.update(getView().mWatchTime);
    }


    public boolean isOpenFlag = true;
    public void timerUpdate(boolean delay){
        if (isOpenFlag){
            mHandler.removeMessages(UPDATE_TIMER);
            mHandler.sendEmptyMessageDelayed(UPDATE_TIMER, delay ? 1000 : 0);
        }
    }

    public void stopUpdate(){
        isOpenFlag = false;
        mHandler.removeMessages(UPDATE_TIMER);
    }


    public void releaseTimerGiftPresenter(){
        if(mHandler!=null)
            mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public TreasureBoxListProxy proxy = new TreasureBoxListProxy();
    public TreasureBoxListProxy.Param param = new TreasureBoxListProxy.Param();

    public void reqGiftList(){
        proxy.postReq(LiveConfig.mLiveContext, new HttpBaseUrlWithParameterProxy.Callback() {
            @Override
            public void onSuc(int code) {
                TLog.e(TAG, "reqGiftList 成功 :" + param.response);
                try {
                    JSONObject rsp = new JSONObject(param.response);
                    if (0 == rsp.optInt("result")) {
                        JSONArray giftlist = rsp.getJSONArray("boxes");
                        if (giftlist.length() != 4) return;
                        int serverTime = rsp.optInt("server_time");
                        getView().show(giftlist, serverTime);
                        if(Configs.Debug)
                            LOG.e(TAG, "reqGiftList onSuc"+ giftlist.length());
                    } else {
                        ReportManager.getInstance().commonReportFun("TreasureBoxDisplay", false,"0", VersionUtil.getMachineCode(getView().getContext()), UserInfo.getInstance().mOpenid);
                    }
                } catch (Exception e) {
                    LOG.e(TAG,"reqGiftList exc"+e);
                }
            }

            @Override
            public void onFail(int errorCode) {
                LOG.e(TAG,"reqGiftList onFail : "+errorCode);
                ReportManager.getInstance().commonReportFun("TreasureBoxDisplay", false,"0", VersionUtil.getMachineCode(getView().getContext()), UserInfo.getInstance().mOpenid);
            }
        }, param);
    }
}
