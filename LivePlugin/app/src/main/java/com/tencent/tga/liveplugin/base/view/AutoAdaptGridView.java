package com.tencent.tga.liveplugin.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;

public class AutoAdaptGridView extends GridView {

    public AutoAdaptGridView(Context context) {
        super(context);
    }

    public AutoAdaptGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAdaptGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        // 这几行代码比较重要
        if(getLayoutParams().height == AbsListView.LayoutParams.WRAP_CONTENT){
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        }else{
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}