package com.tencent.tga.liveplugin.live.gift.presenter;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.hpjy_treasure.ReceiveItem;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.gift.GiftFloatTipPopWindowView;
import com.tencent.tga.liveplugin.live.gift.GiftReceiveResultPopWindowView;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemBean;
import com.tencent.tga.liveplugin.live.gift.model.TimerGiftListModel;
import com.tencent.tga.liveplugin.live.gift.view.TimerGiftListView;
import com.tencent.tga.plugin.R;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimerGiftListPresenter extends BaseFrameLayoutPresenter<TimerGiftListView,TimerGiftListModel> implements View.OnClickListener {

    private static String TAG = "TimerGiftListPresenter";
    private static final int UPDATE_TIMER = 1;
    private static final int LONG_PRESS_TIPS = 2;

    private TimerGiftListModel mTimerGiftListModel;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case UPDATE_TIMER:
                    break;
                case LONG_PRESS_TIPS:

                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public TimerGiftListModel getModel() {
        if (mTimerGiftListModel == null)
            mTimerGiftListModel = new TimerGiftListModel(this);
        return mTimerGiftListModel;
    }

    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick())
            return;
        TLog.e(TAG,"onClick : "+v.getId());
        switch (v.getId()) {
            case R.id.gift_list_close:
                getView().close();
                break;
            default:
                break;
        }
    }


    private long actionDownTime = 0;
    private static int PERIOD = 350;
    private int current_down_id = -1;

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                if(event.getAction() == MotionEvent.ACTION_DOWN ){
                    if(current_down_id == -1){
                        current_down_id = v.getId();
                        actionDownTime = System.currentTimeMillis();

                        mHandler.postDelayed(showTipRunnable,PERIOD);
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP ){
                    if(current_down_id == v.getId()){
                        mHandler.removeCallbacks(showTipRunnable);
                        if((System.currentTimeMillis() - actionDownTime)<PERIOD){
                            receivePrize(v);
                        }else {
                            hideTipPopWindow();
                        }
                        current_down_id = -1;
                    }

                }
            }catch (Exception e){
                TLog.e(TAG,"onTouchListener error : "+e.getMessage());
            }
            return true;
        }
    };


    private void receivePrize(View v){
        TLog.e(TAG,"receivePrize");
        switch (v.getId()) {
            case R.id.gift_list1:
                if (getView().mGift1.isReceiveable(getView().mWatchTime)) {
                    getModel().reveive(getView().mBoxs.get(0).boxid,getView().mBoxs.get(0).level, getView().mSystemTime,1);
                }
                break;
            case R.id.gift_list2:
                if (getView().mGift2.isReceiveable(getView().mWatchTime)){
                    getModel().reveive(getView().mBoxs.get(1).boxid,getView().mBoxs.get(1).level,getView().mSystemTime,2);
                }
                break;
            case R.id.gift_list3:
                if (getView().mGift3.isReceiveable(getView().mWatchTime)){
                    getModel().reveive(getView().mBoxs.get(2).boxid,getView().mBoxs.get(2).level,getView().mSystemTime,3);
                }
                break;
            case R.id.gift_list4:
                if (getView().mGift4.isReceiveable(getView().mWatchTime)){
                    getModel().reveive(getView().mBoxs.get(3).boxid,getView().mBoxs.get(3).level,getView().mSystemTime,4);
                }
                break;
            default:
                break;
        }
    }

    Runnable showTipRunnable = new Runnable() {
        @Override
        public void run() {
            showTipPopWindow(current_down_id);
        }
    };
    BasePopWindow popupWindowTip;
    private void showTipPopWindow(int viewID){
        TLog.e(TAG,"showTipPopWindow");
        try {
            switch (viewID) {
                case R.id.gift_list1:
                    showTipPopWindow(getView().mBoxs.get(0), -120);
                    break;
                case R.id.gift_list2:
                    showTipPopWindow(getView().mBoxs.get(1), -58);
                    break;
                case R.id.gift_list3:
                    showTipPopWindow(getView().mBoxs.get(2), 25);
                    break;
                case R.id.gift_list4:
                    showTipPopWindow(getView().mBoxs.get(3), 120);
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private void showTipPopWindow(GiftItemBean giftItemBean, final int offsetX){
        try {
            if(popupWindowTip != null && popupWindowTip.isShowing()){
                return;
            }
            final GiftFloatTipPopWindowView giftFloatTipPopWindowView = new GiftFloatTipPopWindowView(getView().getContext());
            giftFloatTipPopWindowView.setData(giftItemBean);
            popupWindowTip = new BasePopWindow(giftFloatTipPopWindowView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,false);
            popupWindowTip.setFocusable(true);
            popupWindowTip.setOutsideTouchable(false);
            popupWindowTip.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //不展示获取不了view的高度，先不显示的展示获取高度
            giftFloatTipPopWindowView.setVisibility(View.INVISIBLE);
            popupWindowTip.showAtLocation(getView().mParent ,Gravity.CENTER, DeviceUtils.dip2px(getView().getContext(),offsetX), DeviceUtils.dip2px(getView().getContext(),-80));

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int popWindowHeight = giftFloatTipPopWindowView.getHeight()/2;
                        int offset_y = 0-popWindowHeight-DeviceUtils.dip2px(getView().getContext(),35);
                        popupWindowTip.dismiss();
                        giftFloatTipPopWindowView.setVisibility(View.VISIBLE);
                        popupWindowTip.showAtLocation(getView().mParent ,Gravity.CENTER, DeviceUtils.dip2px(getView().getContext(),offsetX), offset_y);
                    }catch (Exception e){

                    }
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"showTipPopWindow error : "+e.getMessage());
        }
    }

    public void hideTipPopWindow(){
        TLog.e(TAG,"hideTipPopWindow");
        try {
            if(popupWindowTip != null && popupWindowTip.isShowing()){
                popupWindowTip.dismiss();
                popupWindowTip = null;
            }
        }catch (Exception e){
            TLog.e(TAG,"hideTipPopWindow error : "+e.getMessage());
        }
    }

    private HashMap<String,BasePopWindow> hashMapReciveResult = new HashMap<>();
    public synchronized void showReciveResultTip(final String boxid , List<ReceiveItem> recv_box){
        try {
            TLog.e(TAG,"showReciveResultTip : "+boxid);
            if(hashMapReciveResult == null){
                hashMapReciveResult = new HashMap<>();
            }
            BasePopWindow basePopWindow = hashMapReciveResult.get(boxid);
            if(basePopWindow == null){
                GiftReceiveResultPopWindowView giftReceiveResultPopWindowView = new GiftReceiveResultPopWindowView(getView().getContext());
                giftReceiveResultPopWindowView.setData(recv_box);
                basePopWindow = new BasePopWindow(giftReceiveResultPopWindowView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,false);

                hashMapReciveResult.put(boxid,basePopWindow);
                giftReceiveResultPopWindowView.setOnCloseListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(hashMapReciveResult.get(boxid) != null && hashMapReciveResult.get(boxid).isShowing()){
                            hashMapReciveResult.get(boxid).dismiss();
                            hashMapReciveResult.remove(boxid);
                        }
                    }
                });
            }else {
                return;
            }
            hashMapReciveResult.get(boxid).setFocusable(true);
            hashMapReciveResult.get(boxid).setOutsideTouchable(false);
            hashMapReciveResult.get(boxid).setBackgroundDrawable(new ColorDrawable(0x40000000));
            hashMapReciveResult.get(boxid).showAtLocation(getView().mParent ,Gravity.CENTER, 0, 0);
        }catch (Exception e){
            TLog.e(TAG,"showReciveResultTip error : "+e.getMessage());
        }
    }


    public void hideReciveResultWindow(){
        TLog.e(TAG,"hideReciveResultWindow");
        try {
            if(hashMapReciveResult == null){
                return;
            }
            List<String> list = new ArrayList<>(hashMapReciveResult.keySet());
            for(int i = 0;list != null && i<list.size();i++){
                String key = list.get(i);
                BasePopWindow basePopWindow = hashMapReciveResult.get(key);
                if(basePopWindow != null && basePopWindow.isShowing()){
                    basePopWindow.dismiss();
                }
                hashMapReciveResult.remove(key);
            }
            hashMapReciveResult = null;
        }catch (Exception e){
            TLog.e(TAG,"hideTipPopWindow error : "+e.getMessage());
        }
    }


}
