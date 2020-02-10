package com.tencent.tga.liveplugin.live.liveView.gesture;

import android.app.Service;
import android.media.AudioManager;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.ryg.utils.LOG;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.util.BrightnessHelper;
import com.tencent.tga.liveplugin.live.player.PlayView;

import java.lang.ref.SoftReference;

public class VolumeAndBrightListener implements IVolumeAndBrightListener {
    private static String TAG ="VolumeAndBrightListener";
    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    public SoftReference<PlayView> playViewSoftReference;

    public  VolumeAndBrightListener(PlayView playView) {
        playViewSoftReference = new SoftReference<>(playView);
        //初始化获取音量属性
        mAudioManager = (AudioManager) playView.getContext().getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(playView.getContext());

        //下面这是设置当前APP亮度的方法配置
        mWindow = (LiveConfig.mLiveContext).getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;
    }

    @Override
    public void onDown(MotionEvent e) {
        //每次按下的时候更新当前亮度和音量，还有进度
        if (mAudioManager == null) return;

        oldProgress = newProgress;
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1) {
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LOG.d(TAG, "onVolumeGesture: value" );
        if (playViewSoftReference ==  null || playViewSoftReference.get() == null)return;
        if (mAudioManager == null) return;

        int value = playViewSoftReference.get().getHeight() / maxVolume;
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);

        LOG.d(TAG, "onVolumeGesture: value" + value);

        LOG.d(TAG, "onVolumeGesture: newVolume " + newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
        if (volumeProgress > 0) {
            playViewSoftReference.get().mPlayerStateView.voiceIsOpen(true);
        } else {
            playViewSoftReference.get().mPlayerStateView.voiceIsOpen(false);
        }
        playViewSoftReference.get().mPlayerStateView.setProgress(volumeProgress);
        playViewSoftReference.get().mPlayerStateView.show();
    }


    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //下面这是设置当前APP亮度的方法
        LOG.d(TAG, "onBrightnessGesture: old" + brightness);
        if (playViewSoftReference ==  null || playViewSoftReference.get() == null)return;
        float newBrightness = (e1.getY() - e2.getY()) / playViewSoftReference.get().getHeight();
        newBrightness += brightness;

        LOG.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        playViewSoftReference.get().mPlayerStateView.setProgress((int) (newBrightness * 100));
        playViewSoftReference.get().mPlayerStateView.updateBrightUi();
        playViewSoftReference.get().mPlayerStateView.show();
    }
}
