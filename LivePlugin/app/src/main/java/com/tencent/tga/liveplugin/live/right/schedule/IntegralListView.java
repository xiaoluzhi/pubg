package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class IntegralListView extends ListView {
    public IntegralListView(Context context) {
        super(context);
    }

    public IntegralListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntegralListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(expandSpec, heightMeasureSpec);
    }
}
