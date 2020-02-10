package com.tencent.tga.liveplugin.base.view;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by agneswang on 2017/8/21.
 */

public class BasePopWindow extends PopupWindow {

    public View mAnchor;
    public ViewGroup root;
    public Context mContext;

    private static int FULL_SCREEN_SYSTEMUI_VISIBILITY = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    public BasePopWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        mAnchor = contentView;
        initHideBottomBar();
    }


    public BasePopWindow(View anchor, boolean isCancelOutside, PopupWindow.OnDismissListener listener) {
        super(anchor.getContext());
        mAnchor = anchor;
        mContext = DLPluginLayoutInflater.getInstance(anchor.getContext()).getContext();
        setAttributes(isCancelOutside, listener);
    }

    private void setAttributes(boolean isCancelOutside, PopupWindow.OnDismissListener listener) {
        ColorDrawable cd = new ColorDrawable(0x000000);
        setBackgroundDrawable(cd);
        update();
        setTouchable(true);
        setOutsideTouchable(isCancelOutside);
        setFocusable(isCancelOutside);
        setOnDismissListener(listener);
    }

    public void setLayout(int layoutId) {
        root =  (ViewGroup) DLPluginLayoutInflater.getInstance(mAnchor.getContext()).inflate(layoutId, null);
        setContentView(root);
        root.setOnClickListener(null);

        initHideBottomBar();
    }

    public void show(int w,int h,int paddingRight,int paddingBottom ) {
        try {
            setWidth(w);
            setHeight(h);
            showAtLocation(mAnchor, Gravity.BOTTOM | Gravity.RIGHT, paddingRight, paddingBottom);
        } catch (Exception e) {
            TLog.e("BasePopWindow", "show excetion " + e.getMessage());
        }
    }


    public void showLeft(int w, int h, int paddingLeft, int paddingBottom) {
        try {
            setWidth(w);
            setHeight(h);
            showAtLocation(mAnchor, Gravity.BOTTOM | Gravity.LEFT, paddingLeft, paddingBottom);
        }catch (Exception e) {
            TLog.e("BasePopWindow", "show excetion " + e.getMessage());
        }
    }

     public void showInPlayViewRightAndBottom(PlayView view, int w) {
        if (PlayView.isFullscreen()) {
            show(w, view.getLayoutParams().height, 0, 0);
        } else {
            show(w, view.getHeight() -view.mPlayerTitleView.getHeight(), view.getRootView().getWidth()-view.getWidth()-
            (int) mContext.getResources().getDimension(R.dimen.video_scrollow_view_margin_left), (int) mContext.getResources().getDimension(R.dimen.video_scrollow_view_margin));
        }
    }

    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
            TLog.e("BasePopWindow", "dismiss excetion " + e.getMessage());
        }
    }

    public void initHideBottomBar(){
        try {
            if(!DeviceUtils.hasVirtualBar(mAnchor.getContext())){
                TLog.e("BasePopWindow","没有虚拟键，不用做全屏设置");
                return;
            }
            setHideBottomBar();
//            getContentView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    if (visibility != FULL_SCREEN_SYSTEMUI_VISIBILITY) setHideBottomBar();
//                }
//            });
        }catch (Exception e){
            TLog.e("BasePopWindow","onSystemUiVisibilityChange initHideBottomBar error : "+e.getMessage());
        }
    }

    public void setHideBottomBar(){
        try {
            TLog.e("BasePopWindow","setHideBottomBar visibility = "+ FULL_SCREEN_SYSTEMUI_VISIBILITY);
            getContentView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }catch (Exception e){
            TLog.e("BasePopWindow","onSystemUiVisibilityChange setHideBottomBar error : "+e.getMessage());
        }
    }
}
