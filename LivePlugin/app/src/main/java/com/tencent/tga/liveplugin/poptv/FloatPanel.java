package com.tencent.tga.liveplugin.poptv;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.poptv.view.FloatPaneContainer;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;


/**
 * Created by lionljwang on 2016/9/20.
 */
public class FloatPanel {
    private static String TAG = "FloatPanel";
    private Context mContext;

    private Handler mUIHandler;

    private FloatPaneContainer mViewContainer;

    private PopupWindow popWindow;

    private FloatPanelListener mFloatPanelListener;

    public FloatPanel(Context context){
        mContext = context;
        mUIHandler = new Handler(Looper.getMainLooper());
        mViewContainer = new FloatPaneContainer(context);
        popWindow = new BasePopWindow(mViewContainer, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,false);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        popWindow.setClippingEnabled(false);
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                onDialogExit(false);
                popWindow = null;
            }
        });
    }

    public void setFloatPanelListener(FloatPanelListener floatPanelListener){
        mFloatPanelListener = floatPanelListener;
    }

    public void setContentView(int layoutResID) {
        mViewContainer.setContentView(layoutResID);
    }
    public void setContentView(View contentView) {
        mViewContainer.setContentView(contentView);
    }

    public void runOnUIThread(Runnable action) {
        if (action == null)
            return;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            mUIHandler.post(action);
        }
    }

    public void showPanel(){
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                showInner();
            }
        });
    }


    public void hide() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                onHide();
            }
        });
    }

    private void showInner() {
        if (mViewContainer == null) {
            return;
        }
        mViewContainer.setVisibility(View.VISIBLE);
        popWindow.showAtLocation(((Activity)mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void dismissPopupWindow() {
        if (null != popWindow && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }


    public void onDialogExit(boolean isNeedAnim) {
        TLog.e(TAG,"onDialogExit isNeedAnim : "+isNeedAnim);
        mFloatPanelListener.onDismiss(isNeedAnim);
        if(!isNeedAnim){
            onDialogDestroy();
        }
    }

    public void onDialogDestroy(){
        try{
            mFloatPanelListener = null;
            mViewContainer = null;
            mUIHandler= null;
            mContext = null;
            TAG = null;
        }catch (Exception e){
            TLog.e(TAG, String.format("onDialogExit = %s cur thread", e.getMessage()));
        }
    }

    public void onHide() {
        if (popWindow == null || !popWindow.isShowing()) {
            return;
        }
        try{
            popWindow.dismiss();
        }catch (Exception e){
            TLog.e(TAG, String.format("onDestory = %s cur thread", e.getMessage()));
        }

    }

    public interface FloatPanelListener{
        void onDismiss(boolean isNeedAnim);
    }

}
