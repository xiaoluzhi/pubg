package com.tencent.tga.liveplugin.live.liveView.gesture;

import android.view.MotionEvent;

public interface IVolumeAndBrightListener {
    void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    void onDown(MotionEvent e);
}
