package com.tencent.tga.liveplugin.live.liveView.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.SoftReference;

public class VideoPlayerOnGestureListener extends GestureDetector.SimpleOnGestureListener {
    IVolumeAndBrightListener mVideoGestureListener;
    SoftReference<PlayView> playViewSoftReference;
    public VideoPlayerOnGestureListener(PlayView view, IVolumeAndBrightListener listener){
        mVideoGestureListener = listener;
        playViewSoftReference = new SoftReference<>(view);
    }


    public static final int NONE = 0, VOLUME = 1, BRIGHTNESS = 2;
    @ScrollMode
    public int mScrollMode = NONE;

    @Retention(RetentionPolicy.SOURCE)
    private @interface ScrollMode {
    }
    private static String TAG ="VideoPlayerOnGestureListener";
    @Override
    public boolean onDown(MotionEvent e) {
        LOG.e(TAG, "onDown: ");
        //每次按下都重置为NONE
        mScrollMode = NONE;
        if (mVideoGestureListener != null) {
            mVideoGestureListener.onDown(e);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        try {
            if (LiveConfig.mLockSwitch) return true;
            if (playViewSoftReference==null ||playViewSoftReference.get() == null)return true;
            switch (mScrollMode) {
                case NONE:
                    LOG.e(TAG, "NONE: ");
                    if (e1.getX() < playViewSoftReference.get().getWidth() / 2) {
                        mScrollMode = BRIGHTNESS;
                    } else {
                        mScrollMode = VOLUME;
                    }
                    break;
                case VOLUME:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onVolumeGesture(e1, e2, distanceX, distanceY);
                    }
                    LOG.e(TAG, "VOLUME: ");
                    break;
                case BRIGHTNESS:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onBrightnessGesture(e1, e2, distanceX, distanceY);
                    }
                    LOG.e(TAG, "BRIGHTNESS: ");
                    break;
            }
            return true;
        } catch (Exception e) {
            LOG.e(TAG, "onScroll Exception: ");
            return false;
        }
    }


    @Override
    public boolean onContextClick(MotionEvent e) {
        LOG.e(TAG, "onContextClick: ");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        LOG.e(TAG, "onDoubleTap: ");
        if (LiveConfig.mLockSwitch && PlayView.isFullscreen())return true;
        LiveViewEvent.Companion.switchMode(true);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        LOG.e(TAG, "onLongPress: ");
        super.onLongPress(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        LOG.d(TAG, "onDoubleTapEvent: ");
        return super.onDoubleTapEvent(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LOG.e(TAG, "onSingleTapUp: ");
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        LOG.e(TAG, "onFling: ");
        return super.onFling(e1, e2, velocityX, velocityY);
    }


    @Override
    public void onShowPress(MotionEvent e) {
        LOG.e(TAG, "onShowPress: ");
        super.onShowPress(e);
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        PlayViewEvent.dismissDialogs();

        if (NoDoubleClickUtils.isDoubleClickVideo()) {
            if (LiveConfig.mLockSwitch && PlayView.isFullscreen())return true;

            LiveViewEvent.Companion.switchMode(true);
            return true;
        }


        if (PlayView.isFullscreen() ){
            if (playViewSoftReference.get().mPlayerStateView.mLockScreen.getVisibility()== View.VISIBLE&& playViewSoftReference.get().mPlayerController.getVisibility() == View.VISIBLE)
                playViewSoftReference.get().mPlayerStateView.showLockScreen1(View.GONE);
            else
                playViewSoftReference.get().mPlayerStateView.showLockScreenDelayDismiss1(View.VISIBLE);
            if (LiveConfig.mLockSwitch)
                return super.onSingleTapConfirmed(e);
        }

        playViewSoftReference.get().mHandler.removeMessages(PlayView.MSG_HIDE_CONTROL_VIEW);
        if (playViewSoftReference.get().mPlayerController.getVisibility() == View.VISIBLE) {
            if (PlayView.isFullscreen())
                playViewSoftReference.get().mPlayerTitleView.setVisibility(View.GONE, false);
            playViewSoftReference.get().mPlayerController.setVisibility(View.GONE, PlayView.isFullscreen());
            playViewSoftReference.get().mPlayerStateView.unShowDefineChangeTips();
        } else {

            playViewSoftReference.get().delayDismissCtl(2);
            if (PlayView.isFullscreen())
                playViewSoftReference.get().mPlayerTitleView.setVisibility(View.VISIBLE, false);
            playViewSoftReference.get().mPlayerController.setVisibility(View.VISIBLE, PlayView.isFullscreen());
        }

        return super.onSingleTapConfirmed(e);
    }
}
