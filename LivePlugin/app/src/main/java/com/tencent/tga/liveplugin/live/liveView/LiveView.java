package com.tencent.tga.liveplugin.live.liveView;

import com.ryg.DLCallBackManager;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseView;
import com.tencent.tga.liveplugin.base.notification.NotificationCenter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.TimeUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.broadcast.manager.EventBroadcastMananger;
import com.tencent.tga.liveplugin.live.common.broadcast.manager.NetBroadcastMananger;
import com.tencent.tga.liveplugin.live.common.proxy.ProxyHolder;
import com.tencent.tga.liveplugin.live.common.util.BrightnessHelper;
import com.tencent.tga.liveplugin.live.gift.view.TimerGiftView;
import com.tencent.tga.liveplugin.live.liveView.presenter.LiveViewPresenter;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.live.right.LiveRightContainer;
import com.tencent.tga.liveplugin.live.right.chat.ChatView;
import com.tencent.tga.liveplugin.live.right.schedule.SchduleWebView;
import com.tencent.tga.liveplugin.live.right.schedule.ScoreRankWebView;
import com.tencent.tga.liveplugin.live.title.TitleView;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.liveplugin.video.VideoPlayView;
import com.tencent.tga.liveplugin.webview.WebViewLauncher;
import com.tencent.tga.plugin.R;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by agneswang on 2017/3/13.
 * 根View，处理一些通用消息，例如进房退房，hello包发送，font初始化，callback注册等
 */

public class LiveView extends BaseView<LiveViewPresenter>  implements PlayView.VideoGestureListener {

    private static final String TAG = "LiveView";

    @Override
    public LiveViewPresenter getPresenter() {
        if (presenter == null) {
            presenter = new LiveViewPresenter();
        }
        return presenter;
    }

    public TitleView mTitleView;
    public LiveRightContainer mLiveRightContainer;
    public PlayView mPlayView;
    public RelativeLayout mRootView;
    private ImageView mBackIcon;
    public RelativeLayout mContentLayout;

    public VideoPlayView mVideoPlayView;

    private long beginTime; //进电视台的时间
    private long period; //电视台总在线时长
    public WebViewLauncher mWebviewLauncher;

    public static int mVideoLayoutWidth, mVideoLayoutHeight;
    public int mScrollPaddingTop, mScrollPaddingLeft, mScrollPaddingRight, mScrollPaddingBottom;

    public LiveView(Context context) {
        super(context);
        TLog.e(TAG, "init activity");
        initViews();
    }

    public void initViews() {
        try {
            DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.live_player_view, this);

            mRootView = findViewById(R.id.live_player_root);
            mBackIcon = findViewById(R.id.back_icon);
            mBackIcon.setOnClickListener(getPresenter());
            mContentLayout = findViewById(R.id.live_player_content_layout);
            mLiveRightContainer = findViewById(R.id.layout_right_content);
            mPlayView = findViewById(R.id.live_player_play);
            mPlayView.init();
            mPlayView.setVideoGestureListener(this);

            mTitleView = findViewById(R.id.live_player_title);

            initChildView();

            onStart(false);
            initPlayUI();

            initSetting();
        } catch (Exception e) {
            TLog.e(TAG, "init LivePlayerView exception" + e.getMessage());
        }
    }

    public void initChildView() {
        mTitleView.onCreate();
    }

    public void launchConfigTAB(String matchUrl) {
        TLog.e(TAG, "顶部TAB点击");
        if (null == mWebviewLauncher) mWebviewLauncher = new WebViewLauncher();
        mWebviewLauncher.launchGameCenter(this, matchUrl);
    }

    public void disMissConfigTAB() {

        if (null != mWebviewLauncher) {
            mWebviewLauncher.dismiss();
        }
    }

    public void refreshWebViewTitle(String title) {
        if (null != mWebviewLauncher) {
            mWebviewLauncher.refreshTitle(title);
        }
    }

    public void onViewStop() {
        TLog.d(TAG, "onViewStop isReal is ");
        try {
            mTitleView.onStop();
            if (mPlayView != null) mPlayView.onStop();
            if (null != getTimerGiftView()) getTimerGiftView().onStop();
            ProxyHolder.getInstance().stopHello();
            ProxyHolder.getInstance().exitRoom(0, false);

            if (null != mVideoPlayView) {
                mVideoPlayView.onStop();
            }
        } catch (Exception e) {
            TLog.e(TAG, "onViewStop exception");
        }

    }

    public void onStop() {
        try {
            onViewStop();
            NetBroadcastMananger.getInstance().unRegisterBroadcast();
            period = System.currentTimeMillis() - beginTime;
            ReportManager.getInstance().commonReportFun(
                    "TVUserStayTime",
                    false,
                    NetUtils.getLocalIPAddress(),
                    TimeUtils.getCurrentTime(),
                    TimeUtils.getCurrentTime(beginTime),
                    period / 1000 + "",
                    TimeUtils.getCurrentTime());
        } catch (Exception e) {
            TLog.e(TAG, "onStop exception" + e.getMessage());
        }
    }


    /**
     * @param isReal true表示是UnityActivity onStart调用，false表明是电视台初始化
     */
    public void onStart(boolean isReal) {
        LOG.e(TAG, "onStart isReal is " + isReal);
        try {
            NetBroadcastMananger.getInstance().registerBroadcast();

            if (mTitleView !=null)mTitleView.onStart(isReal);

            if (mPlayView != null) mPlayView.onStart(isReal);

            if (null != mVideoPlayView) {
                mVideoPlayView.onStart(isReal);
            }

            if (null != getTimerGiftView()) getTimerGiftView().onStart();

            if (isReal) {
                ProxyHolder.getInstance().enterRoom(); //只有UnityActivity onstart才执行进房操作，进入电视台由广播执行进房操作
            }
            beginTime = System.currentTimeMillis();
        } catch (Exception e) {
            TLog.e(TAG, "onstart exception" + e.getMessage());
        }
    }


    public void onDestroy() {
        TLog.d(TAG, "onDestroy");
        try {

            mTitleView.onDestroy();

            if (mPlayView != null) mPlayView.onDestroy();

            if (null != mVideoPlayView) {
                mVideoPlayView.onDestroy();
            }

            mPlayView = null;
            if (null != getTimerGiftView() && null != getTimerGiftView().mTimerGiftListView) {
                getTimerGiftView().mTimerGiftListView.close();
            }
            if (null != getTimerGiftView()) getTimerGiftView().releaseTimerGiftView();
            if (NetBroadcastMananger.getInstance().threadPool != null) {
                NetBroadcastMananger.getInstance().threadPool.shutdown();
            }
            try {
                //游戏想统计观赛时长，加个观赛任务
                HashMap map = new HashMap<String, Object>();
                map.put("type", 1);
                DLCallBackManager.getCallBack().callback(DLCallBackManager.SDK2Plugin.CALL_UNITY, map);
                DLCallBackManager.setPluginCallback(null);
            } catch (Exception e) {
                TLog.e(TAG, "DLCallBackManager release error : " + e.getMessage());
            }

            NotificationCenter.defaultCenter().clearAllSubscribers();//通知清理
            ConfigInfo.getmInstance().clear();
            NetBroadcastMananger.release();
            EventBroadcastMananger.release();
            TLog.e(TAG, "liveview onDestroy finish");
        } catch (Throwable e) {
            TLog.e(TAG, "onDestroy 退出异常-->" + e.getMessage());
        }
    }

    public TimerGiftView getTimerGiftView() {
        try {
            return mLiveRightContainer.getMChatView().mTimerGiftView;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return true;
    }

    public boolean handleKeyEvent(KeyEvent event) {
        try {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (mPlayView.isFullscreen()) {
                    PlayViewEvent.dismissDialogs();
                    switchMode(true);
                    return true;
                }
                if (null != mVideoPlayView) {
                    mVideoPlayView.finish();
                    return true;
                }

                if (null != scheduleWebView && indexOfChild(scheduleWebView) != -1) {
                    scheduleWebView.dismiss();
                    return true;
                }

                if (null != scoreRankWebView && indexOfChild(scoreRankWebView) != -1) {
                    scoreRankWebView.dismiss();
                    return true;
                }
                if (null != mWebviewLauncher) {
                    boolean result = mWebviewLauncher.handleKeyEvent(event);
                    if (result) return true;
                }

            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void updatePlayViewVisibility(boolean isShow) {
        try {
            if (isShow) {
                if (mPlayView.mPlayerLayout.indexOfChild(mPlayView.mTVKPlayerManager.mVideoViewCasted) == -1) {
                    mPlayView.mPlayerLayout.addView(mPlayView.mTVKPlayerManager.mVideoViewCasted);
                }
                if (mPlayView.mPlayerStateView.mDanmakuView != null)
                    mPlayView.mPlayerStateView.mDanmakuView.setVisibility(View.VISIBLE);
                if (!LiveConfig.isPlayOnMobileNet && NetUtils.getReportNetStatus(getContext()) == NetUtils.MOBILE_NET
                        && mPlayView.mPlayerStateView.isShowNotWifiPlay()) {
                    if (mPlayView.mTVKPlayerManager.mVideoPlayer != null)
                        mPlayView.mTVKPlayerManager.mVideoPlayer.stop();
                }
                if (null != getTimerGiftView())
                    getTimerGiftView().onStart();

            } else {
                if (mPlayView.mPlayerStateView.mDanmakuView != null)
                    mPlayView.mPlayerStateView.mDanmakuView.setVisibility(View.GONE);
                if (mPlayView.mPlayerLayout.indexOfChild(mPlayView.mTVKPlayerManager.mVideoViewCasted) != -1) {
                    mPlayView.mPlayerLayout.removeView(mPlayView.mTVKPlayerManager.mVideoViewCasted);
                }

                if (null != mLiveRightContainer.getMChatView().mHotWordDialog) {
                    mLiveRightContainer.getMChatView().mHotWordDialog.dismiss();
                }

                if (null != getTimerGiftView())
                    getTimerGiftView().onStop();
            }

        } catch (Exception e) {
            TLog.e(TAG, "updatePlayViewVisibility exception" + e.getMessage());
        }
    }

    private SchduleWebView scheduleWebView;
    public void showSchedule() {
        if (null == scheduleWebView) {
            scheduleWebView = new SchduleWebView(getContext(), this, ConfigInfo.getmInstance().getStringConfig(ConfigInfo.SCHEDULE_H5_URL));
        } else {
            scheduleWebView.setVisibility(View.VISIBLE);
        }
    }

    private ScoreRankWebView scoreRankWebView;
    public void showRankWebView() {
        if (null == scoreRankWebView) {
            scoreRankWebView = new ScoreRankWebView(getContext(), this, ConfigInfo.getmInstance().getStringConfig(ConfigInfo.SCORE_RANK_URL));
        } else {
            scoreRankWebView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理发送弹幕请求
     */
    public void handleSendMsg(final LiveEvent.SendMsg event) {
        if (event.isSend) {
            getPresenter().send(event.msg, event.isHotword, event.hotwordId);
            mPlayView.mPlayerController.setmEditText(null);
            getChatView().setmEditText(null);
        } else if (mPlayView.mPlayerController != null) {
            mPlayView.mPlayerController.setmEditText(event.msg);
            getChatView().setmEditText(event.msg);
        }
    }

    /**
     * 视频切换全屏半屏isNeedReport 为false只是为了刷新UI，不需要做太多操作，不然视频可能有黑边
     * 视频播放的时候为false
     */
    public void switchMode(boolean isNeedReport) {
        try {
            RelativeLayout.LayoutParams params = (LayoutParams) mPlayView.getLayoutParams();
            int screenW = Math.max(mRootView.getMeasuredWidth(), mRootView.getMeasuredHeight());
            int screenH = Math.min(mRootView.getMeasuredWidth(), mRootView.getMeasuredHeight());
            if (!PlayView.isFullscreen()) {
                PlayView.mCurrentVideoMode = PlayView.VIDEO_MODE_FULLSCREEN;
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mContentLayout.getLayoutParams();
                params1.setMargins(0, 0, 0, 0);
                params.height = screenH;
                params.width = screenW;
                mPlayView.setPadding(0, 0, 0, 0);
                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mPlayView.mPlayerLayout.getLayoutParams();
                params2.topMargin = 0;
                mPlayView.mPlayerLayout.setLayoutParams(params2);

                mPlayView.setBackgroundColor(Color.BLACK);

                if (isNeedReport && ReportManager.getInstance().hasInit()) {
                    ReportManager.getInstance().report_ScreenNum(LiveInfo.mRoomId, 2);
                }
                mPlayView.mPlayerTitleView.setVisibility(View.GONE);
                mPlayView.unShowDefinChangeTips();
            } else {
                PlayView.mCurrentVideoMode = PlayView.VIDEO_MODE_PREVIEW;
                params.height = mVideoLayoutHeight;
                params.width = mVideoLayoutWidth;
                mPlayView.setPadding(3, 0, 3, 3);

                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) mPlayView.mPlayerLayout.getLayoutParams();
                params2.topMargin = mPlayView.mPlayerTitleView.getHeight();
                mPlayView.mPlayerLayout.setLayoutParams(params2);

                mPlayView.setBackgroundColor(Color.TRANSPARENT);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mContentLayout.getLayoutParams();
                params1.setMargins(mScrollPaddingLeft, mScrollPaddingTop, 0, mScrollPaddingBottom);
                if (isNeedReport && ReportManager.getInstance().hasInit()) {
                    ReportManager.getInstance().report_ScreenNum(LiveInfo.mRoomId, 1);
                }
                mPlayView.mPlayerController.clearAnimation();
                mPlayView.mPlayerController.setTranslationY(0);
                mPlayView.mPlayerTitleView.setVisibility(View.VISIBLE);

            }
            mPlayView.setLayoutParams(params);
            if (isNeedReport)
            {
                mPlayView.delayDismissCtl(7);
                if (mPlayView.mPlayerStateView  !=null )mPlayView.mPlayerStateView.switchScreen(PlayView.isFullscreen());

                if (null != mPlayView.mPlayerTitleView ){
                    mPlayView.mPlayerTitleView.switchMode(PlayView.isFullscreen());
                }

                if (null != mPlayView.mPlayerController)
                    mPlayView.mPlayerController.switchMode(PlayView.isFullscreen());

                if (null != mLiveRightContainer.getMChatView().mHotWordDialog) {
                    mLiveRightContainer.getMChatView().mHotWordDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPlayUI() {
        post(() -> {
            try {
                int screenW = Math.max(mRootView.getMeasuredWidth(), mRootView.getMeasuredHeight());
                int screenH = Math.min(mRootView.getMeasuredWidth(), mRootView.getMeasuredHeight());

                int ScrollviewH = screenH - (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin_top);
                int ScrollviewW = screenW - (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin_left) - (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin_right);

                int videoLayoutHeight = ScrollviewH;
                int listWidth = mLiveRightContainer.getWidth();
                int videoWidth = ScrollviewW - listWidth - DeviceUtils.px2dip(getContext(), 4) - (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin_left) - (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin_right);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mPlayView.getLayoutParams();
                params.height = videoLayoutHeight;
                params.width = videoWidth;
                mPlayView.setLayoutParams(params);
                mPlayView.setBackgroundColor(Color.TRANSPARENT);
                mPlayView.setPadding(3, 0, 3, 3);

                mVideoLayoutHeight = videoLayoutHeight;
                mVideoLayoutWidth = videoWidth;
                mScrollPaddingRight = (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin);
                mScrollPaddingLeft = mScrollPaddingRight;
                mScrollPaddingBottom = mScrollPaddingRight;
                mScrollPaddingTop = (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_scrollow_view_margin_top);

                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mPlayView.mPlayerLayout.getLayoutParams();
                params2.topMargin = (int) mPlayView.getContext().getResources().getDimension(R.dimen.video_top_height);
                mPlayView.mPlayerLayout.setLayoutParams(params2);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mContentLayout.getLayoutParams();
                params1.setMargins(mScrollPaddingLeft, mScrollPaddingTop, 0, mScrollPaddingBottom);
                mContentLayout.setLayoutParams(params1);

                params = (ViewGroup.MarginLayoutParams) mLiveRightContainer.getLayoutParams();
                params.height = videoLayoutHeight;
                params.width = listWidth;
                mLiveRightContainer.setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateVideoLayoutParams(int width, int height) {
        if (PlayView.isFullscreen()) {
            ViewGroup.LayoutParams params = mPlayView.mTVKPlayerManager.mVideoViewCasted.getLayoutParams();
            params.height = height;
            params.width = width;
            mPlayView.mTVKPlayerManager.mVideoViewCasted.setLayoutParams(params);
        } else {
            initPlayUI();
        }
    }

    public ChatView getChatView() {
        return mLiveRightContainer.getMChatView();
    }

    public void launchVideoView(boolean isFromVideoList, ArrayList vids, ArrayList vidTitles, int index, ArrayList commentNums, boolean isPlayBack) {
        VideoPlayView.launch(this, getContext(), isFromVideoList, vids, vidTitles,  index, commentNums, isPlayBack);
    }

    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    protected void initSetting() {
        //初始化获取音量属性
        mAudioManager = (AudioManager)mPlayView.mContext.getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(mPlayView.mContext);

        //下面这是设置当前APP亮度的方法配置
        mWindow = ( LiveConfig.mLiveContext ).getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;
    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //下面这是设置当前APP亮度的方法
        LOG.d(TAG, "onBrightnessGesture: old" + brightness);
        float newBrightness;
        if (null != mVideoPlayView) {
            newBrightness = (e1.getY() - e2.getY()) / mVideoPlayView.getHeight() ;
        } else {
            newBrightness = (e1.getY() - e2.getY()) / mPlayView.getHeight();
        }
        newBrightness += brightness;

        LOG.d(TAG, "onBrightnessGesture: new" + newBrightness);
        if (newBrightness < 0){
            newBrightness = 0;
        }else if (newBrightness > 1){
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        if (null != mVideoPlayView) {
            mVideoPlayView.setProgress((int) (newBrightness * 100));
            mVideoPlayView.updateBrightUi();
            mVideoPlayView.show();
        } else {
            mPlayView.mPlayerStateView.setProgress((int) (newBrightness * 100));
            mPlayView.mPlayerStateView.updateBrightUi();
            mPlayView.mPlayerStateView.show();
        }
    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mAudioManager == null)return;

        int value;
        if (null != mVideoPlayView) {
            value = mVideoPlayView.getHeight() / maxVolume;
        } else {
            value = mPlayView.getHeight() / maxVolume;
        }
        int newVolume = (int) ((e1.getY() - e2.getY())/value + oldVolume);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVolume,0);

        LOG.d(TAG, "onVolumeGesture: value" + value);
        LOG.d(TAG, "onVolumeGesture: newVolume "+ newVolume);

        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume/Float.valueOf(maxVolume) *100);
        if (null != mVideoPlayView) {
            if (volumeProgress > 0) {
                mVideoPlayView.voiceIsOpen(true);
            } else {
                mVideoPlayView.voiceIsOpen(false);
            }
            mVideoPlayView.setProgress(volumeProgress);
            mVideoPlayView.show();
        } else {
            if (volumeProgress > 0) {
                mPlayView.mPlayerStateView.voiceIsOpen(true);
            } else {
                mPlayView.mPlayerStateView.voiceIsOpen(false);
            }
            mPlayView.mPlayerStateView.setProgress(volumeProgress);
            mPlayView.mPlayerStateView.show();
        }
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        LOG.d(TAG, "onSingleTapGesture: ");
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        LOG.d(TAG, "onDoubleTapGesture: ");
    }

    @Override
    public void onDown(MotionEvent e) {
        try {
            //每次按下的时候更新当前亮度和音量，还有进度
            if (mAudioManager == null)return;
            oldProgress = newProgress;
            oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            brightness = mLayoutParams.screenBrightness;
            if (brightness == -1){
                //一开始是默认亮度的时候，获取系统亮度，计算比例值
                brightness = mBrightnessHelper.getBrightness() / 255f;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
