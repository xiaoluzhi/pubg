package com.tencent.tga.liveplugin.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by agneswang on 2017/3/15.
 * 基类，用于保存一些全局房间信息，供子类访问
 */

public abstract class LiveBaseView extends FrameLayout {


    public LiveBaseView(Context context) {
        super(context);
    }

    public LiveBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void onCreate() {}
    public void onStop() {}
    public void onStart(boolean isReal) {}
    public void onDestroy() {}


}
