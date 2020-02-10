package com.tencent.tga.liveplugin.live.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tencent.common.log.tga.TLog;

/**
 * Created by lionljwang on 2016/8/2.
 */
public class ResizeLinerlayout extends LinearLayout {

    public ResizeLinerlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        TLog.e(String.format("onSizeChanged w= %s h = %s oldw = %s oldh = %s", w, h, oldw, oldh));
        if (mListener != null && h>oldh && oldh!= 0)
        {
            mListener.OnResizeRelative(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        TLog.e( String.format("onSizeChanged widthMeasureSpec= %s widthMeasureSpec = %s ", widthMeasureSpec, heightMeasureSpec));
    }

    // 监听接口
    private OnResizeRelativeListener mListener;

    public interface OnResizeRelativeListener
    {
        void OnResizeRelative(int w, int h, int oldw, int oldh);
    }

    public void setOnResizeRelativeListener(OnResizeRelativeListener l)
    {
        mListener = l;
    }
}
