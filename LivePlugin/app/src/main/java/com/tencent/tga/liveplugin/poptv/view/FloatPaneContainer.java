package com.tencent.tga.liveplugin.poptv.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;

/**
 * Created by lionljwang on 2016/9/20.
 */
public class FloatPaneContainer extends FrameLayout {
    private static final String TAG = "FloatPaneContainer";


    public FloatPaneContainer(Context context) {
        super(context);
    }

    public void setContentView(int layoutResID) {
        removeAllViews();
        DLPluginLayoutInflater.getInstance(getContext()).inflate(layoutResID, this);
    }

    public void setContentView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
        }
        setContentView(view, params);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        removeAllViews();
        addView(view, params);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        TLog.e(TAG, "dispatchKeyEvent....");
        return super.dispatchKeyEvent(event);
    }
}
