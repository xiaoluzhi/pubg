package com.tencent.tga.liveplugin.live.player;

import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKNetVideoInfo;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutView;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo;
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.liveView.gesture.VideoPlayerOnGestureListener;
import com.tencent.tga.liveplugin.live.liveView.gesture.VolumeAndBrightListener;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.live.player.presenter.PlayViewPresenter;
import com.tencent.tga.liveplugin.live.player.ui.video.manager.TVKPlayerManager;
import com.tencent.tga.liveplugin.live.player.ui.video.view.ChatPopwindow;
import com.tencent.tga.liveplugin.live.player.ui.video.view.DanmuSettingView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.HotWordDialog;
import com.tencent.tga.liveplugin.live.player.ui.video.view.LiveDefineView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.LiveLineSelectView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.LiveLineSwitchPopDialog;
import com.tencent.tga.liveplugin.live.player.ui.video.view.LiveLineTipView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.PlayerController;
import com.tencent.tga.liveplugin.live.player.ui.video.view.PlayerStateView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.PlayerTitleView;
import com.tencent.tga.liveplugin.live.title.TitleView;
import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by agneswang on 2017/3/13.
 */
public class PlayView extends BaseFrameLayoutView<BaseFrameLayoutPresenter> {

    private static final String TAG = "PlayView";

    public PlayerController mPlayerController;
    public PlayerStateView mPlayerStateView;
    public PlayerTitleView mPlayerTitleView;
    public FrameLayout mPlayerLayout;
    public DanmuSettingView mDanmuSettingView;
    public LiveLineSelectView mLiveLineSelectView;
    public LiveLineTipView mLiveLineTipsView;
    public HotWordDialog hotWordDialog;
    public TVKPlayerManager mTVKPlayerManager;
    public ChatPopwindow mChatPopwindow;

    public static final int VIDEO_MODE_PREVIEW = 1;//预览模式
    public static final int VIDEO_MODE_FULLSCREEN = 2;//全屏模式
    public volatile static int mCurrentVideoMode = VIDEO_MODE_PREVIEW;//当前播放器模式
    public static final int MSG_HIDE_CONTROL_VIEW = 3;
    public static final int MSG_LIVEING_BUFFER = 4;//视频缓冲
    public static final int MSG_LIVEING_BUFFER_TIPS_UNSHOW = 5;//视频清晰度切换提示消失
    public static final int PLAYER_CONTROL_VIEW_SHOWTIME = 5000; // 控制栏在6秒钟之后消失

    public Handler mHandler;
    public Context mContext;

    private LiveDefineView.VideoDefinChangeListener mListener;
    private TVKNetVideoInfo.DefnInfo mCurDefine;
    private ArrayList<TVKNetVideoInfo.DefnInfo> mDefnInfos;
    public static volatile boolean isFirstShowControl = true;//第一次显示30s

    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initViews(context);
    }

    @Override
    public PlayViewPresenter getPresenter() {
        if (presenter == null) {
            presenter = new PlayViewPresenter();
        }
        return (PlayViewPresenter) presenter;
    }

    public void initViews(Context context) {
        mCurrentVideoMode = VIDEO_MODE_PREVIEW;
        mPlayerLayout = new FrameLayout(context);
        mPlayerLayout.setBackground(getResources().getDrawable(R.drawable.player_view_black_background));
        addView(mPlayerLayout);
        mHandler = new MsgHandler(this);

        LiveConfig.isShowDanmuSet = LiveShareUitl.getLiveDanmuSettingHis(getContext()) || (LiveShareUitl.getLiveDanmuSettingTime(getContext()) > 2);
    }

    public void init() {
        mTVKPlayerManager = new TVKPlayerManager(this);
        initPlayerController();
        mTVKPlayerManager.init();
        init(getContext());
    }

    /**
     * 隐藏控制栏
     */
    public void delayDismissCtl(int i) {

        LOG.e(TAG, "delayDismissCtl int " + i);

        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
        if (isFirstShowControl) {
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 30000);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, PLAYER_CONTROL_VIEW_SHOWTIME);
        }
        isFirstShowControl = false;
    }

    /**
     * 隐藏控制
     */
    public void dismissCtl() {
        if (isFullscreen())
            mPlayerTitleView.setVisibility(View.GONE, false);// 消失控制栏
        mPlayerController.setVisibility(View.GONE, isFullscreen());
        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
    }


    public void onDestroy() {
        TLog.d(TAG, "onDestroy");
        if (mPlayerStateView != null) {
            mPlayerStateView.danmaDestroy();
        }
        mTVKPlayerManager.release();
    }

    /**
     * isReal true表示由UnityActivity onStart回调
     * false表示电视台初始化调用
     */
    public void onStart(boolean isReal) {
        TLog.d(TAG, "onStart isReal is " + isReal);
        if (!isReal) {
        } else {
            if (mPlayerStateView != null)
                mPlayerStateView.danmaResume();
            if (NetUtils.NetWorkStatus(getContext()) == NetUtils.MOBILE_NET && !LiveConfig.isPlayOnMobileNet) {
                showMobileUi(false);
            }
        }
        if (TitleView.mCurrentSelection == DefaultTagID.LIVE && null != mPlayerController) {
            if (PlayView.isFullscreen() && LiveConfig.mLockSwitch) {
                return;
            }
            mPlayerController.setVisibility(View.VISIBLE, isFullscreen());
            delayDismissCtl(1);
        }
        LiveInfo.isStopPLay = false;
        mTVKPlayerManager.play();
    }

    public void onStop() {
        TLog.d(TAG, "palyview onStop isReal is ");
        LiveInfo.isStopPLay = true;
        if (mHandler != null) {
            mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
            mHandler.removeCallbacksAndMessages(null);
        }

        if (mPlayerController != null) {
            if (isFullscreen()) {
                mPlayerTitleView.setVisibility(View.GONE, false);// 消失控制栏
            }
            mPlayerController.setVisibility(View.GONE, isFullscreen());

            if (mDanmuSettingView != null && mDanmuSettingView.isShowing())
                mDanmuSettingView.dismiss();

            if (mPlayerController.mDefineView != null && mPlayerController.mDefineView.isShowing())
                mPlayerController.mDefineView.dismiss();
        }
        if (hotWordDialog != null && hotWordDialog.isShowing()) {
            hotWordDialog.dismiss();
        }
        if (mLiveLineSelectView != null && mLiveLineSelectView.isShowing()) {
            mLiveLineSelectView.dismiss();
        }
        if (mPlayerStateView != null)
            mPlayerStateView.danmaPause();
        mTVKPlayerManager.stopPlay();
    }

    public void initAttachedViews() {
        initPlayStateView();
        initPlayerTitle();
    }

    private void initPlayerController() {
        if (mPlayerController == null) {
            mPlayerController = PlayerController.newInstance(this);
            mPlayerController.setVisibility(GONE);
            mPlayerController.attatch(this);
            mPlayerController.mPause.setOnClickListener(getPresenter());
            mPlayerController.mHot.setOnClickListener(getPresenter());
            mPlayerController.mDanmuOper.setOnClickListener(getPresenter());
            mPlayerController.mDanmuSetting.setOnClickListener(getPresenter());
            mPlayerController.mSwitch.setOnClickListener(getPresenter());
            mPlayerController.mEditText.setOnClickListener(getPresenter());
        }
    }

    private void initPlayerTitle() {
        if (mPlayerTitleView == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dip2px(getContext(), 30));
            params.gravity = Gravity.TOP;
            mPlayerTitleView = PlayerTitleView.newInstance(getContext());

            addView(mPlayerTitleView, getChildCount(), params);
        }
    }

    public void liveLineClick() {
        if (NoDoubleClickUtils.isDoubleClick())
            return;
        if (mLiveLineSelectView != null && mLiveLineSelectView.isShowing()) {
            mLiveLineSelectView.dismiss();
            return;
        }
        getPresenter().modle.reqCurrentMatch(true);
        ReportManager.getInstance().report_ChannelEntryClick();
    }

    public void showLiveLineView(List<ChannelInfo> mListInfo) {
        getPresenter().dismissDialogs();
        mPlayerTitleView.isClickRedPot = true;
        mLiveLineSelectView = new LiveLineSelectView(PlayView.this, () -> mPlayerTitleView.mLiveLineSelect.setImageResource(R.drawable.tile_live_line_select_unshow));
        mPlayerTitleView.mLiveLineSelect.setTipOn(false);
        if (mListInfo != null && mListInfo.size() > 0) {
            mLiveLineSelectView.channelInfos.clear();
            mLiveLineSelectView.channelInfos.addAll(mListInfo);
        }
        String ids = "";
        for (ChannelInfo channelInfo : mListInfo) {
            for (RoomInfo info : channelInfo.getRoom_list()) {
                ids += info.getSourceid();
            }
        }
        mLiveLineSelectView.show(this, ids);
        mPlayerTitleView.mLiveLineSelect.setImageResource(R.drawable.tile_live_line_select_show);
    }

    public void showLiveLineTipsView() {
        if (null == mLiveLineTipsView) mLiveLineTipsView = new LiveLineTipView(getContext());
        mLiveLineTipsView.show(this);
        if (null != mHandler) mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mLiveLineTipsView) mLiveLineTipsView.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void initPlayStateView() {
        mPlayerStateView = new PlayerStateView(getContext());
        mPlayerStateView.attached(this);
        mPlayerStateView.setLayoutParams(mPlayerLayout.getLayoutParams());

        if (mPlayerStateView != null) {
            mPlayerStateView.showLoading();
        }
    }


    public void mobilePlayClick() {
        if (NetUtils.NetWorkStatus(getContext()) == NetUtils.MOBILE_NET) {
            LiveConfig.isPlayOnMobileNet = true;
        }

        mTVKPlayerManager.isUserPause = false;
        if (mTVKPlayerManager.mVideoPlayer != null) {
            mTVKPlayerManager.play();
            ReportManager.getInstance().report_PlayPause(isFullscreen(), true);
        }
        if (null != mPlayerStateView)
            mPlayerStateView.showLoading();
    }


    /**
     * 判断当前播放器全屏还是半屏
     *
     * @return
     */
    public static boolean isFullscreen() {
        return mCurrentVideoMode == VIDEO_MODE_FULLSCREEN;
    }

    /**
     * 提供给其他fragment调用，用于停止播放
     */
    public void stopPlay() {
        if (null != mTVKPlayerManager) mTVKPlayerManager.stopPlay();
    }


    /***
     * 当前sourceid 下线
     * @param sourceId
     */
    public void offLine(int sourceId) {
        try {
            if (sourceId == LiveInfo.mSourceId && mTVKPlayerManager.mVideoPlayer != null) {
                mTVKPlayerManager.mVideoPlayer.stop();
                LiveLineSwitchPopDialog dialog = new LiveLineSwitchPopDialog(LiveConfig.mLiveContext);
                ChannelInfo info = null;
                for (ChannelInfo channelInfo : LiveInfo.mChannelInfos) {
                    for (RoomInfo room : channelInfo.getRoom_list()) {
                        if (sourceId == room.getSourceid()) {
                            info = channelInfo;
                        }
                    }
                }
                if (null != info && null != info.getRoom_list() && info.getRoom_list().size() > 0
                        && sourceId != info.getRoom_list().get(0).getSourceid()) { //只有当前不是主直播间的时候才会出现弹窗
                    dialog.setData(info);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void updateRoomInfo(RoomInfo roomInfo, int playType) {
        LOG.e(TAG, "updateRoomInfo............." + roomInfo);
        if (!TextUtils.isEmpty(roomInfo.getLive_title())) {
            if (mPlayerTitleView != null) {
                if (roomInfo.getOnline_num() > 0) mPlayerTitleView.setmOnlineNum(roomInfo.getOnline_num());
                mPlayerTitleView.setTitle(roomInfo.getLive_title(), playType);
            }
        }

        if (TextUtils.isEmpty(roomInfo.getVid())) return;
        String vid = roomInfo.getVid();
        if (!TextUtils.isEmpty(vid) && !TextUtils.equals(LiveInfo.mLiveid, vid) && (LiveInfo.mSourceId == roomInfo.getSourceid())) {
            LiveInfo.mLiveid = vid;
            if (!mTVKPlayerManager.isUserPause && mTVKPlayerManager.mVideoPlayer != null) {
                mTVKPlayerManager.mVideoPlayer.stop();
                mTVKPlayerManager.play();
                if (mPlayerTitleView != null) {
                    mPlayerTitleView.setVisibility(View.VISIBLE, false);// 消失控制栏
                }
            }
        }
        ReportManager.getInstance().report_ScreenNum(LiveInfo.mRoomId,(!PlayView.isFullscreen())?1:2);
    }


    /**
     * 提供给其他fragment调用，用于控制点播弹窗，视频中心返回继续视频播放
     */
    public void play() {
        if (null != mTVKPlayerManager && !mTVKPlayerManager.isUserPause) {
            mTVKPlayerManager.play();
        }
    }

    /***
     * 根据vid，播放腾讯视频
     */
    public void showMobileUi(final boolean isVideoPause) {
        if (mPlayerController != null) {
            if (mPlayerStateView != null) {
                mPlayerStateView.showNotPlay(isVideoPause);
            }
            mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
            mPlayerController.setOnpauseView();
            if (isFullscreen())
                mPlayerTitleView.setVisibility(View.GONE);// 消失控制栏
            mPlayerController.setVisibility(View.GONE);
        }

    }

    public static class MsgHandler extends Handler {
        private WeakReference<PlayView> mPlayView;

        MsgHandler(PlayView playView) {
            if (null != playView) {
                mPlayView = new WeakReference<>(playView);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != mPlayView && null != mPlayView.get()) {
                switch (msg.what) {
                    case MSG_HIDE_CONTROL_VIEW:
                        mPlayView.get().dismissCtl();
                        break;
                    case MSG_LIVEING_BUFFER:
                        mPlayView.get().showDefinChangeTips();
                        break;
                    case MSG_LIVEING_BUFFER_TIPS_UNSHOW:
                        mPlayView.get().unShowDefinChangeTips();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private VideoGestureListener mVideoGestureListener;
    public void init(Context context) {
        VideoPlayerOnGestureListener mOnGestureListener = new VideoPlayerOnGestureListener(this, new VolumeAndBrightListener(this));
        GestureDetector mGestureDetector = new GestureDetector(context, mOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));
    }


    public void videDefineClick() {
        if (NoDoubleClickUtils.isDoubleClick())
            return;
        PlayViewEvent.dismissDialogs();
        if (mPlayerController.mDefineView == null)
            mPlayerController.mDefineView = new LiveDefineView(this, mListener);


        mPlayerController.mDefineView.setVideoDefinChangeListener(mListener);

        if (!mPlayerController.mDefineView.isShowing()) {
            int h = ((View) getParent()).getHeight();
            int sh = DeviceUtils.getScreenHeight(getContext());
            if (h == sh)//全屏
                mPlayerController.mDefineView.show(this, mDefnInfos, mCurDefine, ((View) getParent()).getHeight());
            else {
                mPlayerController.mDefineView.show(this, mDefnInfos, mCurDefine, ((View) getParent()).getHeight() - DeviceUtils.dip2px(getContext(), 30));
            }

        }
        if (mListener != null) {
            mListener.onClick();
        }
    }

    public void setDefine(final TVKNetVideoInfo.DefnInfo curDefine, final ArrayList<TVKNetVideoInfo.DefnInfo> defnInfos, final LiveDefineView.VideoDefinChangeListener listener) {
        if (curDefine != null && defnInfos != null && defnInfos.size() > 0) {
            mListener = listener;
            mCurDefine = curDefine;
            mDefnInfos = defnInfos;
            mPlayerController.setCurDefine(curDefine);
        }
    }


    /***
     * 加载，清晰度切换提示判断
     */
    public void onLiveLoading() {
        if (mPlayerStateView.mLoadingTime1 > mPlayerStateView.mLoadingTime2) {
            if (mPlayerStateView.mLoadingTime2 > System.currentTimeMillis() - 60 * 1000) {
                showDefinChangeTips();
            } else {
                mPlayerStateView.mLoadingTime2 = System.currentTimeMillis();
                mHandler.removeMessages(PlayView.MSG_LIVEING_BUFFER);
                mHandler.sendEmptyMessageDelayed(PlayView.MSG_LIVEING_BUFFER, 15 * 1000);
            }

        } else {
            if (mPlayerStateView.mLoadingTime1 > System.currentTimeMillis() - 60 * 1000) {
                showDefinChangeTips();
            } else {
                mPlayerStateView.mLoadingTime1 = System.currentTimeMillis();
                mHandler.removeMessages(PlayView.MSG_LIVEING_BUFFER);
                mHandler.sendEmptyMessageDelayed(PlayView.MSG_LIVEING_BUFFER, 15 * 1000);
            }

        }
    }

    /***
     * 缓冲结束，取消计时提示
     */
    public void onLivePlaying() {
        mHandler.removeMessages(PlayView.MSG_LIVEING_BUFFER);
        unShowDefinChangeTips();
    }

    /***
     * 显示清晰度切换提示
     */
    public void showDefinChangeTips() {
        if (null == mPlayerStateView)
            return;

        if (mPlayerStateView.isShowNotWifiPlay()) return;

        mPlayerStateView.mLoadingTime1 = 0l;
        mPlayerStateView.mLoadingTime2 = 0l;

        if (PlayView.isFullscreen() && LiveConfig.mLockSwitch) return;
        mPlayerStateView.showDefineChangeTips();
        mPlayerController.setVisibility(View.VISIBLE, PlayView.isFullscreen());

        mHandler.removeMessages(PlayView.MSG_LIVEING_BUFFER_TIPS_UNSHOW);
        mHandler.sendEmptyMessageDelayed(PlayView.MSG_LIVEING_BUFFER_TIPS_UNSHOW, 5 * 1000);
    }

    /***
     * 隐藏清晰度切换提示
     */
    public void unShowDefinChangeTips() {
        if (mPlayerStateView != null && mPlayerStateView.isShowDefineChangeTips()) {
            mPlayerStateView.unShowDefineChangeTips();
            delayDismissCtl(6);
        }
    }


    public void setVideoGestureListener(VideoGestureListener videoGestureListener) {
        mVideoGestureListener = videoGestureListener;
    }

    /**
     * 用于提供给外部实现的视频手势处理接口
     */

    public interface VideoGestureListener {
        public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);


        public void onSingleTapGesture(MotionEvent e);

        public void onDoubleTapGesture(MotionEvent e);

        public void onDown(MotionEvent e);
    }

}