package com.tencent.tga.liveplugin.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by lionljwang on 2017/10/11.
 */
public class RootRelativeLayout extends RelativeLayout {

    public RootRelativeLayout(Context context) {
        super(context);
    }

    public RootRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RootRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
