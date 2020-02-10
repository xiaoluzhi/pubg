package com.tencent.tga.liveplugin.video;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.DLCallBackManager;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.vod_op.DianZanOp;
import com.tencent.qqlive.multimedia.TVKSDKMgr;
import com.tencent.qqlive.multimedia.tvkcommon.api.ITVKProxyFactory;
import com.tencent.qqlive.multimedia.tvkplayer.api.ITVKMediaPlayer;
import com.tencent.qqlive.multimedia.tvkplayer.api.ITVKVideoViewBase;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKNetVideoInfo;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKPlayerMsg;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKPlayerVideoInfo;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKUserInfo;
import com.tencent.tga.apngplayer.ApngImageLoader;
import com.tencent.tga.liveplugin.base.notification.NotificationCenter;
import com.tencent.tga.liveplugin.base.notification.Subscriber;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.util.AnimationUtil;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.common.util.TextUitl;
import com.tencent.tga.liveplugin.live.liveView.LiveView;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.BackgroundCacheStuffer;
import com.tencent.tga.liveplugin.live.player.ui.video.view.ChatPopwindow;
import com.tencent.tga.liveplugin.live.player.ui.video.view.LiveDefineView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.VideoDefineView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.VideoSelectView;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.liveplugin.report.VideoMonitorReport;
import com.tencent.tga.liveplugin.video.proxy.AddZanProxy;
import com.tencent.tga.liveplugin.video.proxy.GetVodDanmuHttpProxy;
import com.tencent.tga.liveplugin.video.proxy.GetVodInfoHttpProxy;
import com.tencent.tga.liveplugin.video.proxy.SendDanmuProxy;
import com.tencent.tga.liveplugin.video.ui.VideoDanmuSettingView;
import com.tencent.tga.liveplugin.video.ui.VideoPlayerController;
import com.tencent.tga.plugin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.tga.controller.DrawHandler;
import master.flame.danmaku.tga.danmaku.model.BaseDanmaku;
import master.flame.danmaku.tga.danmaku.model.DanmakuTimer;
import master.flame.danmaku.tga.danmaku.model.Duration;
import master.flame.danmaku.tga.danmaku.model.IDisplayer;
import master.flame.danmaku.tga.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.tga.danmaku.model.android.Danmakus;
import master.flame.danmaku.tga.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.tga.ui.widget.DanmakuSurfaceView;


/**
 * 点播视频相关界面
 * Created by agneswang on 2017/4/16.
 */
public class VideoPlayView extends FrameLayout {

    private static final String TAG = "VideoPlayView";

    private Context mContext;

    private LiveView mParent;
    //腾讯视频相关ui
    private FrameLayout mVideoLayout;
    private ITVKMediaPlayer mVideoPlayer = null;
    private TVKUserInfo mUserinfo = null;
    private TVKPlayerVideoInfo mPlayerinfo = null;
    private ITVKProxyFactory mfactory = null;
    private View mVideoView = null;
    private boolean mIstop = false;
    private boolean mIsUsertop = false;

    private ArrayList<String> mVids; //视频列表，用于点播视频

    private String mReportVid = "";//上报所需的vid，视频中心报传过来的vid，赛事回放报列表第一个vid

    /*标识当前Activity启动来源，true表示来自视频列表，false表示来自点播视频*/
    private boolean isFromSchedule;

    private boolean isPlayBack;//是否是回放类型
    /**
     * 视频标题
     */
    private ArrayList<String> mVidTitles;
    /**
     * 当前清晰度id
     */
    private String mDefine = "shd";
    private TVKNetVideoInfo.DefnInfo mcurDefnInfo;
    /**
     * 当前当前播放位置
     */
    private long mPosition;
    /**
     * 评论数量
     */
    private ArrayList<Integer> mCommentNum;

    /***
     * 游戏字体
     */
    private Typeface mFont = null;

    //数据操作相关ui
    private ImageView mClose;
    private View mTopView;//标题
    private TextView mTitleView;//标题
    private VideoPlayerController mBottomView;//底部导航栏
    public int mOldprogress;//进度条进度
    private boolean mSeekBarUpdage = true;
    private boolean isSeeking;

    private int mPlayIndex = 0;//当前播放第几场

    private VideoDefineView mVideoDefineView;//清晰度弹出框
    private VideoSelectView mSelectView;//选集弹出框

    private static final int CONTROL_SHOW_TIME = 5000;//上下导航栏保持时间，超过此时间自动隐藏，对导航栏操作，比如暂定，时间重新计算，清晰度，选集，不显示
    private static final String TIME_FORMAT_WITHOUT_HOUR = "%02d:%02d";//时间
    private static final int MSG_HIDE_CONTROL_VIEW = 0;//自动隐藏
    private static final int MSG_UPDATE_PLAY = 1;
    private static final int MSG_SHOW_PLAYUI = 2;
    private static final int MSG_PLAY_NEXT = 3;
    private static final int MSG_NOTIFY_WEBVIEW = 4;

    /***
     * 进度条相关
     ********/
    private int mMin;//当前min
    private int mSec;//当前sec
    private int mTotal;//总时间
    private Timer timer;//计时器刷新进度条
    private TimerTask mTimerTask;

    private Handler mHandler = new MsgHandler();

    /**
     * 视频缓冲动画
     */
    private ImageView mAnimView;
    private AnimationDrawable mAnim;

    /**
     * 移动网络提示相关
     ***/
    private View mLlyMobileNet;
    private ImageView mMobilePlay;
    private TextView mMobileText;

    private long mBeginTime = 0;

    public DanmakuSurfaceView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;
    private static float dens = 1.4f;

    private TextView mPlayNum;
    private FrameLayout mAddZanContainer;
    private ImageView mAddZanIcon;
    private TextView mAddZanNum;
    /****
     * @param context
     * @param vids
     * @param vidTitles
     * @param commentNums
     * @param isPlayBack 是否是回放
     */
    public static void launch(LiveView view, Context context, boolean isFromSchedule, ArrayList vids, ArrayList vidTitles, int index, ArrayList commentNums, boolean isPlayBack) {
        try {
            VideoPlayView videoPlayView = (VideoPlayView) DLPluginLayoutInflater.getInstance(context).inflate(R.layout.activity_video_play, null);
            videoPlayView.mParent = view;
            videoPlayView.isFromSchedule = isFromSchedule;
            videoPlayView.mVids = vids;
            videoPlayView.mVidTitles = vidTitles;
            videoPlayView.mCommentNum = commentNums;
            videoPlayView.isPlayBack = isPlayBack;
            videoPlayView.mContext = DLPluginLayoutInflater.getInstance(context).getContext();
            if (null != vids && index >=0 && index < vids.size()) videoPlayView.mPlayIndex = index;
            videoPlayView.onCreate();
        } catch (Exception e) {
            TLog.e(TAG, "create video view exception " + e.getMessage());
        }
    }

    public VideoPlayView(Context context, AttributeSet att) {
        super(context, att);
    }

    public void onCreate() {

        //如果传递过来的视频 & 视频列表参数为空，返回
        if (null == mVids || (null != mVids && mVids.size() == 0)) {
            return;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.addView(this, params);

        mParent.mVideoPlayView = this;
        setVideoGestureListener(mParent);
        mParent.updatePlayViewVisibility(false);

        mVideoLayout = (FrameLayout) findViewById(R.id.layout_videolayout);

        initFont();

        initVideoView();
        initListeners();
        initViews();
        initOnClickListeners();
        regBroadCast();
        mDefine = LiveShareUitl.getVideoDefine(getContext());
        play();
        mBeginTime = System.currentTimeMillis();
        onStart(false);
        initDanmu();
        initVideoData();
        VideoMonitorReport.getInstance(getContext()).setVideoQualityPosition(2);
        ReportManager.getInstance().commonReportFun("VideoPlayer", false);
    }

    public void onStop() {
        mIstop = true;
        if (mVideoPlayer != null) {
            mPosition = mVideoPlayer.getCurrentPosition();
            mVideoPlayer.pause();
            if (mBottomView != null) {
                mBottomView.setPauseIcon(R.drawable.control_icon_playing);
            }
        }
    }

    public void onStart(boolean isReal) {
        if (isReal) {
            mIstop = false;
            if (!mIsUsertop && mVideoPlayer != null)
                play(mPosition);
        }
    }

    public void onDestroy() {
        ReportManager.getInstance().report_TVVideoPlayFinish(mBeginTime, System.currentTimeMillis(),
                mVids.get(mPlayIndex), ReportManager.VIDEO_CENTER, "");//后续去掉视频中心后，mTGLId替换为“”
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (mDanmuTimerTask != null) {
            mDanmuTimerTask.cancel();
            mDanmuTimerTask = null;
        }
        if (mDanmuTimer != null) {
            mDanmuTimer.purge();
            mDanmuTimer.cancel();
            mDanmuTimer = null;
        }

        mDanmuList.clear();

        if (mVideoPlayer != null) {
            mVideoPlayer.stop();
            mVideoPlayer.release();
        }
        if (null != mDanmakuView) mDanmakuView.release();
        NotificationCenter.defaultCenter().publish(new LiveEvent.PlayerResume());
        unRegBroadCast();
        VideoMonitorReport.getInstance(getContext()).setVideoQualityPosition(1);
        setVideoGestureListener(null);
        mParent = null;
    }

    public void finish() {
        mParent.removeView(this);
        mParent.mVideoPlayView = null;
        mParent.updatePlayViewVisibility(true);
        onStop();
        onDestroy();
    }

    /**
     * 注册事件广播
     */
    private void regBroadCast() {
        NotificationCenter.defaultCenter().subscriber(LiveEvent.NetWorkStateChange.class, mNetWorkStateChangeSubscriber);

    }

    /**
     * 取消注册事件广播
     */
    private void unRegBroadCast() {
        NotificationCenter.defaultCenter().unsubscribe(LiveEvent.NetWorkStateChange.class, mNetWorkStateChangeSubscriber);
    }


    private Subscriber<LiveEvent.NetWorkStateChange> mNetWorkStateChangeSubscriber = new Subscriber<LiveEvent.NetWorkStateChange>() {
        @Override
        public void onEvent(LiveEvent.NetWorkStateChange event) {
            TLog.e(TAG, "event.state-->" + event.state);
            /**返回当前网络状况：1表示移动网络，2表示wifi,3表示无网络连接*/

            TLog.e(TAG, String.format("event.state--> event.state = %s mPosition = %s", event.state, mPosition));
            if (mVideoPlayer != null && event.state == 3)
                mPosition = mVideoPlayer.getCurrentPosition();

            if (event.state == 1 && !LiveConfig.isPlayOnMobileNet) {
                if (mVideoPlayer.isPlaying()) {
                    if (mVideoPlayer != null) {
                        mVideoPlayer.pause();
                        if (null != mDanmakuView) mDanmakuView.pause();
                    }
                }

                if (mBottomView != null) {
                    showMobilePlayUI(false);//显示移动网络提示
                    mBottomView.setPauseIcon(R.drawable.control_icon_playing);

                }
            } else if (event.state == 2 || (event.state == 1 && LiveConfig.isPlayOnMobileNet)) {

                if (mIsUsertop) {
                    showMobilePlayUI(true);
                    if (null != mDanmakuView) mDanmakuView.pause();
                    return;
                }

                if (mVideoPlayer != null) {

                    if (event.state == 1)
                        mDefine = "sd";

                    play(mPosition);
                    if (null != mDanmakuView) mDanmakuView.resume();
                }
            }
        }
    };

    /**
     * 获得王者字体
     */
    private void initFont() {
        DLCallBackManager.SDK2Plugin callBack = DLCallBackManager.getCallBack();
        if (callBack != null) {
            TLog.e(TAG, "onDestory, callback");
            mFont = (Typeface) callBack.callback(DLCallBackManager.SDK2Plugin.GET_FONT, null);
        }
    }

    class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE_CONTROL_VIEW:
                    showControl(View.GONE);// 消失控制栏
                    mAddZanContainer.setVisibility(View.GONE);
                    break;
                case MSG_UPDATE_PLAY:
                    updatePlay();
                    updateDanmu();
                    break;
                case MSG_SHOW_PLAYUI:
                    showPlayUi();
                    break;
                case MSG_PLAY_NEXT:
                    clearDanmu();
                    mPlayIndex++;
                    if (mVids.size() == 1) {
                        mPlayIndex = 0;
                    } else {
                        mPlayIndex = mPlayIndex % mVids.size();
                    }
                    initVideoData();
                    playCom();
                    break;
                case MSG_NOTIFY_WEBVIEW:
                    //notifyWebView();
                default:
                    break;
            }
        }
    }

    private void clearDanmu() {
        mDanmuList.clear();
        mSelfDanmuList.clear();
        if (null != mDanmakuView) mDanmakuView.clearDanmakusOnScreen();
    }


    private void initViews() {
        mTitleView = (TextView) findViewById(R.id.window_title);
        mTitleView.setText(mPlayIndex < mVidTitles.size() ? mVidTitles.get(mPlayIndex) : "");

        mBottomView = findViewById(R.id.bottom_container);
        mTopView = findViewById(R.id.top_container);
        mTopView.setVisibility(View.GONE);

        mBottomView.updateViews(mVids.size() > 1);

        if (mVids.size() > 1) {
            setSelect(mVids, new VideoSelectView.VideoSelectChangeListener() {
                @Override
                public void onChange(int positon) {
                    if (mVideoPlayer != null) {
                        mPosition = 0;
                        try {
                            mVideoPlayer.stop();
                            mPlayIndex = positon;
                            clearDanmu();
                            play();
                            initVideoData();
                            if (mIsUsertop) {
                                mBottomView.setPauseIcon(R.drawable.control_icon_playing);
                            } else {
                                mBottomView.setPauseIcon(R.drawable.control_icon_pause);
                            }
                            ReportManager.getInstance().report_TVVideoPlayFinish(mBeginTime, System.currentTimeMillis(),
                                    mVids.get(mPlayIndex), ReportManager.VIDEO_CENTER, "");//后续去掉视频中心后，mTGLId替换为“”
                            mBeginTime = System.currentTimeMillis();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onClick() {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_HIDE_CONTROL_VIEW);
                    }
                }

                @Override
                public void onDismiss() {
                    if (mHandler != null) {
                        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                    }

                }
            });
        }


        mBottomView.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isShowMobilePlayUI())
                    return;
                mVideoPlayer.seekTo(getVideoSeek(seekBar));
                if (null != mDanmakuView) mDanmakuView.clearDanmakusOnScreen();
                mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, CONTROL_SHOW_TIME);
                isSeeking = true;
                mSeekBarUpdage = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isShowMobilePlayUI())
                    return;
                mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                mSeekBarUpdage = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (isShowMobilePlayUI()) {
                    seekBar.setProgress(mOldprogress);
                    return;
                }
                mOldprogress = progress;
                double pos = (double) progress / 1000 * mTotal / 1000;
                int minute = (int) pos / 60;
                int second = (int) pos % 60;
                setCurrentTime(minute, second);
                refreshTimeView();
            }
        });

        TextView videoSelect = (TextView) findViewById(R.id.video_select);
        TextView defineSelect = (TextView) findViewById(R.id.define_select);

        mBottomView.mVideoPause.setOnClickListener(onClickListener);

        mClose = (ImageView) findViewById(R.id.video_close);
        mClose.setOnClickListener(onClickListener);

        mAnimView = (ImageView) findViewById(R.id.anim_loading);
        mAnim = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.video_loading);
        mAnimView.setImageDrawable(mAnim);

        showLoading();
        mBottomView.setPauseIcon(R.drawable.control_icon_playing);

        mLlyMobileNet = findViewById(R.id.mLlyMobileNet);
        mMobilePlay = (ImageView) findViewById(R.id.mIvMobileNet);
        mMobilePlay.setOnClickListener(onClickListener);
        mMobileText = (TextView) findViewById(R.id.mIvMobileNetText);

        mPlayNum = findViewById(R.id.play_num);
        mAddZanContainer = findViewById(R.id.add_zan_entry);
        mAddZanIcon = findViewById(R.id.zan_icon);
        mAddZanNum = findViewById(R.id.zan_num);
        if (mFont != null) {
            mTitleView.setTypeface(mFont);
            videoSelect.setTypeface(mFont);
            defineSelect.setTypeface(mFont);
            mPlayNum.setTypeface(mFont);
            mAddZanNum.setTypeface(mFont);
        }

        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, CONTROL_SHOW_TIME);
    }

    private void showBubble() {
        ApngImageLoader.getInstance().displayApng("assets://add_zan.png", mAddZanIcon,
                new ApngImageLoader.ApngConfig(Integer.MAX_VALUE, true, true));
    }

    private void initVideoView() {
        mfactory = TVKSDKMgr.getProxyFactory();
        if (mfactory == null)
            return;

        ITVKVideoViewBase videoViewBase = mfactory.createVideoView(getContext());

        mVideoView = (View) videoViewBase;
        mVideoPlayer = mfactory.createMediaPlayer(getContext(), videoViewBase);
        mVideoLayout.addView(mVideoView, 0);
        mOnGestureListener = new VideoPlayerOnGestureListener(mVideoLayout);
        mGestureDetector = new GestureDetector(getContext(), mOnGestureListener);
        //取消长按，不然会影响滑动
        mGestureDetector.setIsLongpressEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if(mGestureDetector == null){
                        return false;
                    }
                    //监听触摸事件
                    return mGestureDetector.onTouchEvent(event);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
        initSetting();
    }

    /***
     * 设置当前播放器选集
     *
     * @param strings
     * @param listener
     */
    public void setSelect(final ArrayList<String> strings, final VideoSelectView.VideoSelectChangeListener listener) {
        mBottomView.mSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NoDoubleClickUtils.isDoubleClick())
                    return;
                if (isShowMobilePlayUI())
                    return;
                if (isSeeking) {
                    ToastUtil.show(getContext(), "正在快进中，请稍后切换选集");
                    return;
                }
                dismissDialogs();
                if (mSelectView == null)
                    mSelectView = new VideoSelectView(mVideoLayout);

                mSelectView.font = mFont;

                mSelectView.setOnDismissListener(new VideoSelectView.DismissListener() {
                    @Override
                    public void onDismiss() {
                        listener.onDismiss();
                    }
                });
                mSelectView.setVideoSelectChangeListener(listener);

                if (!mSelectView.isShowing()) {
                    mSelectView.show(strings, true, isPlayBack,mPlayIndex);
                }

                if (listener != null) {
                    listener.onClick();
                }
            }
        });

    }


    private void initOnClickListeners() {
        mBottomView.initOnClickListener(onClickListener);
        mAddZanContainer.setOnClickListener(onClickListener);
    }

    /**
     * 显示上下导航栏
     *
     * @param visibility
     */
    public void showControl(int visibility) {
        if (mBottomView.getVisibility() != visibility) {
            if (visibility == View.VISIBLE) {
                mTopView.setVisibility(visibility);
                mTopView.startAnimation(AnimationUtil.topIn(mContext));
                mBottomView.setVisibility(visibility);
                mBottomView.startAnimation(AnimationUtil.bottomIn(mContext));
            } else {
                mTopView.startAnimation(AnimationUtil.topOut(mContext, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mTopView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                }));
                mBottomView.startAnimation(AnimationUtil.bottomOut(mContext, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mBottomView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                }));
            }
        }
    }

    private void updatePlay() {
        setTotalTime((int) mVideoPlayer.getDuration());
        timer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!mSeekBarUpdage)
                    return;
                if (!mVideoPlayer.isPlaying())
                    return;
                double pos = mVideoPlayer.getCurrentPosition();
                float seek = mVideoPlayer.getDuration();
                if (seek == 0) {
                    if (pos == 0)
                        setVideoProgress(0);
                } else {
                    setVideoProgress((int) (1000 * pos / seek));
                }
                pos /= 1000;
                int minute = (int) pos / 60;
                int second = (int) pos % 60;
                setCurrentTime(minute, second);
            }
        };
        timer.schedule(mTimerTask, 0, 1000);
    }

    private void dismissDialogs() {
        if (null != mSelectView && mSelectView.isShowing()) {
            mSelectView.dismiss();
        }
        if (null != mVideoDefineView && mVideoDefineView.isShowing()) {
            mVideoDefineView.dismiss();
        }
    }

    /*******************移动网络提示，移动网络会暂停显示提示用户是否继续观看***************************************************************************************/

    /**
     * 显示移动网络播放提示
     */
    public void showMobilePlayUI(final boolean isVideoPause) {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mLlyMobileNet != null)
                    mLlyMobileNet.setVisibility(View.VISIBLE);
                if (null != mMobileText) {
                    if (isVideoPause) {
                        mMobileText.setText("视频已暂停，点击开始");
                    } else {
                        mMobileText.setText("当前非WIFI网络环境，继续观看需要消耗流量");
                    }
                }
                dismissLoading();
                dismissDialogs();
            }
        });
    }

    /**
     * 隐藏移动网络播放提示
     */
    public void dismissMobilePlayUI() {
        if (mLlyMobileNet != null)
            mLlyMobileNet.setVisibility(View.GONE);
    }

    public boolean isShowMobilePlayUI() {
        if (mLlyMobileNet != null)
            return mLlyMobileNet.getVisibility() == View.VISIBLE;
        return false;
    }

    /***
     * 视频缓冲动画显示
     */
    private void showLoading() {
        if (mAnim != null && !isShowMobilePlayUI()) {
            mAnimView.setVisibility(View.VISIBLE);
            mAnim.start();
        }

    }

    /***
     * 视频缓冲动画隐藏
     */
    private void dismissLoading() {
        if (mAnimView != null) {
            mAnimView.setVisibility(View.GONE);
            mAnim.stop();
        }

    }

    /***
     * 是否显示动画
     *
     * @return
     */
    public boolean isShowLoading() {
        if (mAnimView != null)
            return mAnimView.getVisibility() == View.VISIBLE;
        return false;
    }

    /**
     * 设置当前视频播放时间
     *
     * @param min
     * @param sec
     */
    public void setCurrentTime(int min, int sec) {
        mMin = min;
        mSec = sec;
    }

    /**
     * 设置视频总时长
     *
     * @param totalTime
     */
    public void setTotalTime(int totalTime) {
        mTotal = totalTime;
        mBottomView.setAllTime(String.format(TIME_FORMAT_WITHOUT_HOUR, (mTotal / 1000) / 60, (mTotal / 1000) % 60));
    }

    /**
     * 更新播放时间
     */
    public void refreshTimeView() {
        mBottomView.setPlayTime(String.format(TIME_FORMAT_WITHOUT_HOUR, mMin, mSec));
    }

    /**
     * 更新进度条
     *
     * @param pos
     */
    public void setVideoProgress(int pos) {
        mBottomView.mSeekBar.setProgress(pos);
    }

    /**
     * get 进度条
     *
     * @param seekBar
     * @return
     */
    private int getVideoSeek(SeekBar seekBar) {
        return (int) ((seekBar.getProgress() / 10) * (mVideoPlayer.getDuration())) / 100;
    }

    /**
     * 播放视频，默认时间从0开始播放
     */
    private void play() {
        play(mPlayIndex, 0);
    }

    private void play(long position) {
        play(mPlayIndex, position);
    }

    /**
     * 播放视频
     *
     * @param position 视频开始播放位置
     */
    private void play(int playIndex, long position) {
        if (mVideoPlayer == null)
            return;
        if (NetUtils.NetWorkStatus(getContext()) == NetUtils.MOBILE_NET) {
            if (!LiveConfig.isPlayOnMobileNet) {
                showMobilePlayUI(false);
                return;
            }
            mDefine = "sd";
        }

        if (mVideoPlayer.isPausing() && !mIsUsertop && !mIstop) {
            mVideoPlayer.start();
            if (mVideoPlayer.isPlaying())
                mVideoPlayer.switchDefinition(mDefine);
        } else if (!mVideoPlayer.isPausing() && !mVideoPlayer.isPlaying()) {
            mVideoPlayer.stop();
            mUserinfo = new TVKUserInfo();
            mUserinfo.setWx_openID(UnityBean.getmInstance().openid);
            if (mVids.size() == 1) {
                mVideoPlayer.setLoopback(true);
                mPlayIndex = 0;
            } else {
                mPlayIndex = playIndex % mVids.size();
            }
            mPlayerinfo = new TVKPlayerVideoInfo(TVKPlayerMsg.PLAYER_TYPE_ONLINE_VOD, mVids.get(mPlayIndex), "");
            mVideoPlayer.openMediaPlayer(getContext(), mUserinfo, mPlayerinfo, mDefine, position, 0);
        }
        if (!mIsUsertop)
            showPlayUi();
        updateTitle();
    }

    /***
     * 播放完成当前视频，继续播放下个视频，视频轮流播放
     */
    private void playCom() {
        mVideoPlayer.stop();
        play();
    }

    public void updateTitle() {
        if (mTitleView == null)
            return;
//        if (!mFromVideoList) {
//            mTitleView.setText(mVidTitles.get(0) + String.format("  第%s场", mPlayIndex + 1));
//        } else {
            mTitleView.setText(mPlayIndex < mVidTitles.size() ? mVidTitles.get(mPlayIndex) : "");
//        }
    }


    public VideoDanmuSettingView danmuSettingView;
    private ChatPopwindow chatPopwindow;
    /**
     * view 点击事件
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (NoDoubleClickUtils.isDoubleClick())//防止短时间内连续点击
                return;

            switch (v.getId()) {
                case R.id.video_close://退出
                    finish();
                    break;
                case R.id.video_pause://视频暂停或者播放
                    if (mVideoPlayer == null) {
                        return;
                    }

                    if (isShowMobilePlayUI() || isShowLoading())
                        return;

                    if (mVideoPlayer.isPlaying()) {
                        mVideoPlayer.pause();
                        mBottomView.setPauseIcon(R.drawable.control_icon_playing);
                        showMobilePlayUI(true);
                        mIsUsertop = true;
                        mHandler.sendEmptyMessage(MSG_HIDE_CONTROL_VIEW);
                        ReportManager.getInstance().report_PlayPause(true, false);
                        if (null != mDanmakuView) mDanmakuView.pause();
                    } else if (mVideoPlayer.isPausing()) {
                        mIsUsertop = false;
                        showPlayUi();
                        mVideoPlayer.start();
                        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, CONTROL_SHOW_TIME);
                        if (null != mDanmakuView) mDanmakuView.resume();
                    } else if (!mVideoPlayer.isPlaying() && !mVideoPlayer.isPausing()) {
                        mIsUsertop = false;
                        play(mPosition);
                        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, CONTROL_SHOW_TIME);
                        if (null != mDanmakuView) mDanmakuView.resume();
                    }
                    break;
                case R.id.mIvMobileNet://移动网络播放
                    mIsUsertop = false;
                    if (NetUtils.NetWorkStatus(getContext()) == NetUtils.MOBILE_NET) {
                        LiveConfig.isPlayOnMobileNet = true;
                        play(mPosition);
                    } else {
                        if (mVideoPlayer.isPausing()) {
                            showPlayUi();
                            mVideoPlayer.start();
                        }
                        if (!mVideoPlayer.isPlaying() && !mVideoPlayer.isPausing()) {
                            play(mPosition);
                        }
                        ReportManager.getInstance().report_PlayPause(true, false);
                    }
                    if (null != mDanmakuView) mDanmakuView.resume();
                    dismissMobilePlayUI();
                    break;
                case R.id.danmu_oper:
                    boolean isShow = LiveShareUitl.isShowVideoDanmu(getContext());
                    mBottomView.setVisibility(View.GONE, true);
                    mTopView.setVisibility(View.GONE);
                    mBottomView.setDanumIcon(!isShow);
                    if (!isShow) {
                        ReportManager.getInstance().commonReportFun("VideoPlayer_WordFlowSwitch", false,"1");
                    } else {
                        ReportManager.getInstance().commonReportFun("VideoPlayer_WordFlowSwitch", false,"0");
                    }
                    if (isShow) {
                        LiveShareUitl.saveVideoDanmuState(getContext(), false);
                        if (null != mDanmakuView) mDanmakuView.setVisibility(View.GONE);
                        ToastUtil.show(getContext(), "弹幕已关闭");
                    } else {
                        LiveShareUitl.saveVideoDanmuState(getContext(), true);
                        if (null != mDanmakuView) mDanmakuView.setVisibility(View.VISIBLE);
                        ToastUtil.show(getContext(), "弹幕已开启");
                    }

                    break;
                case R.id.danmu_setting:
                    danmuSettingView = new VideoDanmuSettingView(VideoPlayView.this);

                    if (!danmuSettingView.isShowing()) {
                        ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "3", "");
                        danmuSettingView.show(DeviceUtils.dip2px(getContext(), 216), getHeight(), 0, 0);
                    }
                    danmuSettingView.mListener = new VideoDanmuSettingView.DanmuSettingsChangeListener() {
                        @Override
                        public void changeDanmuPosition(int position) {
                            setmDanmuPosition(position);
                        }

                        @Override
                        public void changeDanmuSize(int size) {
                            setmDanmuSize(size);
                        }

                        @Override
                        public void changeDanmuAlpha(int progres) {
                            setmDanmuAlpha(progres);
                        }
                    };
                    break;
                case R.id.edit_text:
                    String string = mBottomView.mEditText.getText().toString().trim();

                    chatPopwindow = new ChatPopwindow(mContext,200);
                    chatPopwindow.type = ChatPopwindow.INPUT_TYPE_VIDEO_COMMENT;

                    if (chatPopwindow.popWindow.isShowing())
                        return;

                    if (string != null && !"".equals(string)) {
                        chatPopwindow.show(mBottomView.mEditText, string);
                    } else {
                        chatPopwindow.show(mBottomView.mEditText, "");
                    }
                    ReportManager.getInstance().commonReportFun("VideoPlayer_ChatInfo", false);
                    mBottomView.setmEditText(null);
                    break;
                case R.id.add_zan_entry:
                    updateZanStatus();
                    ReportManager.getInstance().commonReportFun("VideoPlayer_GoodClick", false);
                    break;
                default:
                    break;
            }

        }
    };


    /**
     * 显示播放状态ui，播放按钮和去掉移动网络提示
     */
    private void showPlayUi() {
        mBottomView.setPauseIcon(R.drawable.control_icon_pause);
        dismissMobilePlayUI();
    }


    /***
     * 拿到腾讯视频播放清晰度相关信息，设置清晰度相关信息
     *
     * @param curDefine 当前播放清晰度
     * @param defnInfos 当前播放视频清晰度列表，播放器回调
     * @param listener  切换清晰度回调，播放当前清晰度，
     */
    public void setDefine(final TVKNetVideoInfo.DefnInfo curDefine, final ArrayList<TVKNetVideoInfo.DefnInfo> defnInfos, final LiveDefineView.VideoDefinChangeListener listener) {

        if (curDefine != null && defnInfos != null && defnInfos.size() > 0) {

            mBottomView.mVideoDefin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NoDoubleClickUtils.isDoubleClick())
                        return;

                    if (isShowMobilePlayUI())
                        return;

                    dismissDialogs();

                    if (mVideoDefineView == null)
                        mVideoDefineView = new VideoDefineView(mVideoLayout);

                    mVideoDefineView.font = mFont;

                    mVideoDefineView.setOnDismissListener(new VideoDefineView.DismissListener() {
                        @Override
                        public void onDismiss() {
                            listener.onDismiss();
                        }
                    });
                    mVideoDefineView.setVideoDefinChangeListener(listener);

                    if (!mVideoDefineView.isShowing()) {
                        if (mcurDefnInfo == null)
                            mVideoDefineView.show(defnInfos, curDefine);
                        else
                            mVideoDefineView.show(defnInfos, mcurDefnInfo);
                    }

                    if (listener != null) {
                        listener.onClick();
                    }
                }
            });

            setCurDefine(curDefine);
        }
    }

    /**
     * 设置导航栏当前清晰度
     *
     * @param curDefine
     */
    public void setCurDefine(final TVKNetVideoInfo.DefnInfo curDefine) {
        mcurDefnInfo = curDefine;
        if (curDefine == null || TextUtils.isEmpty(curDefine.getDefn()))
            return;
        if (mTitleView == null)
            return;
        mBottomView.setDefine(curDefine.getDefnName().trim().substring(0, 2));
        mDefine = curDefine.getDefn();
    }

    private int isFirst = 1;
    private boolean isBuffering = false;
    /***
     * 腾讯视频相关回调
     */
    private void initListeners() {
        mVideoPlayer.setOnNetVideoInfoListener(new ITVKMediaPlayer.OnNetVideoInfoListener() {
                                                   @Override
                                                   public void onNetVideoInfo(ITVKMediaPlayer tvk_iMediaPlayer, final TVKNetVideoInfo tvk_netVideoInfo) {
                                                       final ArrayList<TVKNetVideoInfo.DefnInfo> defnInfos = tvk_netVideoInfo.getDefinitionList();
                                                       post(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               setDefine(tvk_netVideoInfo.getCurDefinition(), defnInfos,
                                                                       new LiveDefineView.VideoDefinChangeListener() {
                                                                           @Override
                                                                           public void onChange(TVKNetVideoInfo.DefnInfo defnInfo) {
                                                                               if (mVideoPlayer != null) {
                                                                                   if (mIstop)
                                                                                       return;
                                                                                   try {
                                                                                       mIsUsertop = false;
                                                                                       if (!mVideoPlayer.isPlaying()) {
                                                                                           play();
                                                                                       }
                                                                                       if (mVideoPlayer.isPlaying()) {
                                                                                           mVideoPlayer.switchDefinition(defnInfo.getDefn());
                                                                                       }
                                                                                       String preDefine = mDefine;
                                                                                       mDefine = defnInfo.getDefn();
                                                                                       LiveShareUitl.saveVideoTips(getContext(), mDefine);

                                                                                       mHandler.sendEmptyMessage(MSG_SHOW_PLAYUI);
                                                                                       setCurDefine(defnInfo);

                                                                                       ReportManager.getInstance().report_DefinitionSelectInfo("", preDefine, defnInfo.getDefn(), ReportManager.SOURCE_VIDEO, mReportVid);

                                                                                   } catch (Exception e) {
                                                                                       e.printStackTrace();
                                                                                   }
                                                                               }
                                                                           }

                                                                           @Override
                                                                           public void onClick() {
                                                                               if (mHandler != null) {
                                                                                   mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);//不隐藏导航栏
                                                                                   mHandler.sendEmptyMessage(MSG_HIDE_CONTROL_VIEW);
                                                                               }
                                                                           }

                                                                           @Override
                                                                           public void onDismiss() {//定时隐藏导航栏
                                                                               mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                                                                           }
                                                                       }
                                                               );
                                                           }
                                                       });
                                                   }
                                               }
        );


        mVideoPlayer.setOnVideoPreparedListener(new ITVKMediaPlayer.OnVideoPreparedListener() {
                                                    @Override
                                                    public void onVideoPrepared(ITVKMediaPlayer mpImpl) {
                                                        if (NetUtils.NetWorkStatus(getContext()) == NetUtils.MOBILE_NET && !LiveConfig.isPlayOnMobileNet) {
                                                            showMobilePlayUI(false);
                                                            return;
                                                        }

                                                        if (!mIsUsertop && !mIstop) {
                                                            mVideoPlayer.start();
                                                            isSeeking = false;
                                                            mHandler.sendEmptyMessage(MSG_UPDATE_PLAY);
                                                            mHandler.sendEmptyMessage(MSG_SHOW_PLAYUI);
                                                            mHandler.sendEmptyMessage(MSG_NOTIFY_WEBVIEW);
                                                            mHandler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    dismissLoading();
                                                                }
                                                            });

                                                        } else {
                                                            mVideoPlayer.pause();
                                                        }
                                                        mHandler.sendEmptyMessage(MSG_HIDE_CONTROL_VIEW);
                                                    }
                                                }
        );

        mVideoPlayer.setOnCompletionListener(new ITVKMediaPlayer.OnCompletionListener() {
                                                 @Override
                                                 public void onCompletion(ITVKMediaPlayer mpImpl) {
                                                     mHandler.sendEmptyMessage(MSG_PLAY_NEXT);
                                                 }
                                             }
        );


        mVideoPlayer.setOnErrorListener(new ITVKMediaPlayer.OnErrorListener() {
                                            @Override
                                            public boolean onError(ITVKMediaPlayer mpImpl, int model, int what, int extra, String detailInfo, Object Info) {
                                                TLog.e(TAG, String.format("onError %s %s", model, what));
                                                if (model == 122 && 204 == what) {
                                                    post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mBottomView.setPauseIcon(R.drawable.control_icon_playing);
                                                        }
                                                    });
                                                }
                                                return false;
                                            }
                                        }
        );

        mVideoPlayer.setOnInfoListener(new ITVKMediaPlayer.OnInfoListener() {
                                           @Override
                                           public boolean onInfo(ITVKMediaPlayer mpImpl, int what, Object extra) {
                                               TLog.e(TAG, String.format("onInfo %s",  what));
                                               switch (what) {
                                                   case TVKPlayerMsg.PLAYER_INFO_START_BUFFERING:
                                                       if (mAnimView != null) {
                                                           mAnimView.postDelayed(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   showLoading();
                                                               }
                                                           }, 500);
                                                       }
                                                       isBuffering = true;
                                                       break;
                                                   case TVKPlayerMsg.PLAYER_INFO_SUCC_SET_DECODER_MODE:
                                                   case TVKPlayerMsg.PLAYER_INFO_ENDOF_BUFFERING:
                                                       if (mAnimView != null) {
                                                           mAnimView.postDelayed(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   dismissLoading();
                                                               }
                                                           }, 500);
                                                       }
                                                       //VideoMonitorReport.getInstance(getContext()).setReportVideoQualityEndTime(UnityBean.getmInstance().openid, mParent.mPlayView.mParent.mLiveid, isFirst, mDefine, Configs.isP2P?1:0);
                                                       if (isFirst == 1) {
                                                           isFirst = 2;
                                                       }
                                                       isBuffering = false;
                                                       break;
                                                   default:
                                                       break;
                                               }
                                               return false;
                                           }
                                       }
        );
        mVideoPlayer.setOnSeekCompleteListener(new ITVKMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(ITVKMediaPlayer tvk_iMediaPlayer) {
                TLog.d(TAG, "seek complete");
                isSeeking = false;
            }
        });
    }


    /************************************弹幕相关*****************************************************************/
    public void initDanmu() {
        try {

            mDanmakuView = findViewById(R.id.video_danmaku);
            HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
            maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 20); // 滚动弹幕最大显示8行
            // 设置是否禁止重叠
            HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
            overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
            mDanmakuContext = DanmakuContext.create();
            mDanmakuContext.alignBottom(true);
            mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 5).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1f).setScaleTextSize(1.2f)
                    .setCacheStuffer(new BackgroundCacheStuffer(), null) // 图文混排使用SpannedCacheStuffer
                    .setMaximumLines(maxLinesPair)
                    .preventOverlapping(overlappingEnablePair);
            mParser = createParser();
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void recycle() {

                }
            });
            mDanmakuView.prepare(mParser, mDanmakuContext);
            mDanmakuView.showFPS(Configs.Debug);
            mDanmakuView.enableDanmakuDrawingCache(true);
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            dens = dm.densityDpi / 160.0f - 0.6f;
            mSwitSize = (int) (2*dens);
            mDanmuSize = (int)(LiveShareUitl.getVideoDanmuSize(getContext())*dens);
            setmDanmuAlpha(LiveShareUitl.getVideoDanmuAlpha(getContext()));
            setmDanmuPosition(LiveShareUitl.getVideoDanmuPosition(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BaseDanmakuParser createParser() {
        return new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        };
    }

    private Timer mDanmuTimer;
    private TimerTask mDanmuTimerTask;
    private void updateDanmu() {
        if (mDanmuTimerTask != null) {
            mDanmuTimerTask.cancel();
            mDanmuTimerTask = null;
        }
        if (mDanmuTimer != null) {
            mDanmuTimer.purge();
            mDanmuTimer.cancel();
            mDanmuTimer = null;
        }
        mDanmuTimer = new Timer();
        mDanmuTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (null == mDanmakuView || mDanmakuView.getVisibility() != View.VISIBLE) return;
                if (!mSeekBarUpdage)
                    return;
                if (!mVideoPlayer.isPlaying() || isBuffering)
                    return;
                long time = mVideoPlayer.getCurrentPosition() / 500;
                if (null != mDanmuList.get(time)) {
                    for(String item : mDanmuList.get(time)) {
                        addDanmaku(item, false);
                    }
                }
                if (null != mSelfDanmuList.get(time)) {
                    addDanmaku(mSelfDanmuList.get(time), true);
                }
            }
        };
        mDanmuTimer.schedule(mDanmuTimerTask, 0, 500);
    }

    public static int mDanmuSize ;
    public static int mSwitSize ;
    public static int mDanmuAlpha ;
    public static void setmDanmuSize(int size){
        mDanmuSize = (int)(size*dens);
        if (PlayView.isFullscreen())
            mDanmuSize += mSwitSize;
    }

    public void setmDanmuAlpha(int alpha){
        mDanmuAlpha = (alpha+109)*255/364;
        ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "2", alpha + "");
    }

    public void setmDanmuPosition(final int position){
        mDanmakuView.post(new Runnable() {
            @Override
            public void run() {
                HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
                switch (position) {
                    case LiveShareUitl.LIVE_DANMU_POSITION_FULL:
                        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 20); // 滚动弹幕最大显示8行
                        mDanmakuContext.setMaximumLines(maxLinesPair);
                        mDanmakuContext.alignBottom(false);
                        break;
                    case LiveShareUitl.LIVE_DANMU_POSITION_TOP:
                        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4); // 滚动弹幕最大显示4行
                        mDanmakuContext.setMaximumLines(maxLinesPair);
                        mDanmakuContext.alignBottom(false);
                        break;
                    case LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM:
                        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4); // 滚动弹幕最大显示4行
                        mDanmakuContext.setMaximumLines(maxLinesPair);
                        mDanmakuContext.alignBottom(true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    GetVodDanmuHttpProxy.Param param = new GetVodDanmuHttpProxy.Param();
    GetVodDanmuHttpProxy proxy = new GetVodDanmuHttpProxy();
    private HashMap<Long, ArrayList<String>> mDanmuList = new HashMap<>();
    private HashMap<Long, String> mSelfDanmuList = new HashMap<>();
    public void getDanmuData(){
        try {
            param.vid = mVids.get(mPlayIndex);
            proxy.postReq(mContext, new HttpBaseUrlWithParameterProxy.Callback() {
                @Override
                public void onSuc(int i) {
                    TLog.e(TAG, "GetVodDanmuHttpProxy succ");
                    try {
                        if (param.response != null) {
                            mDanmuList.clear();
                            mSelfDanmuList.clear();
                            JSONObject rsp = new JSONObject(param.response);
                            if (rsp.optInt("result") == 0) {
                                JSONArray array = rsp.getJSONArray("danmu_list");
                                for (int index = 0; index < array.length(); index++) {
                                    long time = array.getJSONObject(index).optLong("offset_ms") / 500;
                                    if (time == 0) time = 1;
                                    String msg = array.getJSONObject(index).optString("msg");
                                    TLog.e(TAG, "time is" + time + " msg is " + msg);
                                    if (null != mDanmuList.get(time)) {
                                        mDanmuList.get(time).add(msg);
                                    } else {
                                        ArrayList<String> list = new ArrayList<>();
                                        list.add(msg);
                                        mDanmuList.put(time, list);
                                    }
                                }
                                TLog.e(TAG, "parse done");
                            }
                        }
                    } catch (Exception e) {
                        TLog.e(TAG, "GetVodDanmuHttpProxy error : " + e.getMessage());
                    }
                }

                @Override
                public void onFail(final int i) {
                    TLog.e(TAG, "GetVodDanmuHttpProxy fail : " + i);

                }
            }, param);
        }catch(Exception e) {
            TLog.e(TAG, "GetVodDanmuHttpProxy error : " + e.getMessage());
        }
    }

    private void addDanmaku(String msg, boolean isSelf) {
        try {
            if (!ConfigInfo.getmInstance().getConfig(ConfigInfo.DANMA_SWITCH)) return;
            if (!LiveShareUitl.isShowVideoDanmu(getContext()))return;
            TLog.e(TAG, "addDanmaku msg is " + msg);
            final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            if (danmaku == null || mDanmakuView == null) {
                return;
            }
            danmaku.isLive = true;
            danmaku.duration = new Duration(9500);
            danmaku.alpha = mDanmuAlpha;
            danmaku.text = msg;
            danmaku.textColor = Color.WHITE;
            danmaku.time = mDanmakuView.getCurrentTime() + 100;
            danmaku.textSize = mDanmuSize;
            danmaku.priority = 1;
            if (isSelf) danmaku.borderColor = 0xfffab121;
            mDanmakuView.addDanmaku(danmaku);
        } catch (Throwable e) {
            TLog.e(TAG, e.getMessage());
        }
    }

    private SendDanmuProxy mSendDanmuProxy = new SendDanmuProxy();
    private SendDanmuProxy.Param mSendDanmuProxyParam = new SendDanmuProxy.Param();

    private static long MSG_TIME = 3000;
    /**
     * 上次聊天时间，客户端根据 MSG_TIME 设置 发送消息间隔
     *
     * @see #MSG_TIME
     */
    private long mLastSendMsgTime = 0l;
    public void sendDanmu(String msg) {
        if (System.currentTimeMillis() - mLastSendMsgTime > MSG_TIME) {
            mLastSendMsgTime = System.currentTimeMillis();
            mSendDanmuProxyParam.msg = msg;
            mSendDanmuProxyParam.offset_ms = (int)mVideoPlayer.getCurrentPosition();
            mSendDanmuProxyParam.vid = mVids.get(mPlayIndex);
            mSendDanmuProxy.postReqWithOutRepeat(new NetProxy.Callback() {
                @Override
                public void onSuc(int code) {
                    try {
                        if (null != mSendDanmuProxyParam.mRsp && mSendDanmuProxyParam.mRsp.result == 0) {
                            TLog.e(TAG, "sendDanmu sucess ");
                            addDanmaku(msg, true);
                            final long time = mVideoPlayer.getCurrentPosition() == 0 ? 500 : mVideoPlayer.getCurrentPosition() / 500;
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mSelfDanmuList.put(time, msg);
                                }
                            }, 1000);//延迟添加，避免自己发送的出现两条弹幕
                        } else if (mSendDanmuProxyParam.mRsp.result == 4){
                            ToastUtil.show(LiveConfig.mLiveContext, PBDataUtils.byteString2String(mSendDanmuProxyParam.mRsp.err_msg));
                        } else {
                            TLog.e(TAG, "sendDanmu result " + mSendDanmuProxyParam.mRsp.result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(int errorCode) {
                    if (Configs.Debug)
                        TLog.e(TAG, "sendDanmu fail " + errorCode);
                }
            }, mSendDanmuProxyParam);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    long time = Math.abs((System.currentTimeMillis() - mLastSendMsgTime) / 1000);
                    if (time > 100 || time < 1) {
                        time = 1;
                    }
                    ToastUtil.show(getContext(), String.format("发言过于频繁，请%s秒后再试", time));
                }
            }, 500);
        }
    }

    /************************************弹幕相关*****************************************************************/

    /*******************************点赞相关**********************************************************************/
    GetVodInfoHttpProxy.Param videoParam = new GetVodInfoHttpProxy.Param();
    GetVodInfoHttpProxy videoProxy = new GetVodInfoHttpProxy();

    private boolean isZan;
    private void initVideoData() {
        try {
            showBubble();
            videoParam.vid = mVids.get(mPlayIndex);
            videoProxy.postReq(mContext, new HttpBaseUrlWithParameterProxy.Callback() {
                @Override
                public void onSuc(int i) {
                    try {
                        if (videoParam.response != null) {
                            JSONObject rsp = new JSONObject(videoParam.response);
                            if (rsp.optInt("result") == 0) {
                                isZan = rsp.optInt("is_zan") == 1;
                                int zanNum = rsp.optInt("zan_cnt");
                                int playNum = rsp.optInt("view_cnt");
                                if (isZan) {
                                    mAddZanIcon.setImageResource( R.drawable.zan);
                                } else {
                                    showBubble();
                                }
                                mAddZanNum.setText(zanNum + "");
                                if (null != mCommentNum && mPlayIndex < mCommentNum.size() && mCommentNum.get(mPlayIndex) > 0) {
                                    mPlayNum.setText(TextUitl.formatBigNum(mCommentNum.get(mPlayIndex), true));
                                } else {
                                    mPlayNum.setText(playNum > 0 ? TextUitl.formatBigNum(playNum, true) : "0");
                                }
                            }
                        }
                    } catch (Exception e) {
                        TLog.e(TAG, "GetVodInfoHttpProxy error : " + e.getMessage());
                    }
                }

                @Override
                public void onFail(final int i) {
                    TLog.e(TAG, "GetVodInfoHttpProxy fail : " + i);

                }
            }, videoParam);
            mDanmakuView.clearDanmakusOnScreen();
            getDanmuData();
        }catch(Exception e) {
            TLog.e(TAG, "GetVodInfoHttpProxy error : " + e.getMessage());
        }
    }

    private AddZanProxy mAddZanProxy = new AddZanProxy();
    private AddZanProxy.Param mAddZanParam = new AddZanProxy.Param();

    private void updateZanStatus() {
        try {
            mAddZanParam.vid = mVids.get(mPlayIndex);
            mAddZanParam.op = isZan ? DianZanOp.kUnZan.getValue() : DianZanOp.kZan.getValue();
            mAddZanProxy.postReq(new NetProxy.Callback() {
                @Override
                public void onSuc(int code) {
                    if (null != mAddZanParam.mZanRsp && mAddZanParam.mZanRsp.result == 0) {
                        isZan = !isZan;
                        if (isZan) {
                            mAddZanNum.setText(String.valueOf(Integer.valueOf(mAddZanNum.getText().toString()) + 1)); //点赞成功之后点赞数加1
                            mAddZanIcon.setImageResource( R.drawable.zan);
                        } else {
                            mAddZanNum.setText(String.valueOf(Integer.valueOf(mAddZanNum.getText().toString()) - 1));
                            showBubble();
                        }
                    } else {
                        TLog.e(TAG, "add zan result " + mAddZanParam.mZanRsp.result);
                    }

                }

                @Override
                public void onFail(int errorCode) {
                    if (Configs.Debug)
                        TLog.e(TAG, "add zan fail " + errorCode);
                    ToastUtil.show(getContext(),"点赞失败");
                }
            }, mAddZanParam);
        }catch(Exception e) {
            TLog.e(TAG, "GetAddZanProxy error : " + e.getMessage());
        }
    }
    /*******************************点赞相关*************************************************************************/


    /************************************手势相关*****************************************************************/
    public static final int NONE = 0, VOLUME = 1, BRIGHTNESS = 2;
    public int mScrollMode = NONE;


    private VideoPlayerOnGestureListener mOnGestureListener;
    private GestureDetector mGestureDetector;
    private PlayView.VideoGestureListener mVideoGestureListener;

    public class VideoPlayerOnGestureListener extends GestureDetector.SimpleOnGestureListener {


        public VideoPlayerOnGestureListener(View mPlayerLayout) {
        }

        @Override
        public boolean onDown(MotionEvent e) {
            LOG.d(TAG, "onDown: ");
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
                switch (mScrollMode) {
                    case NONE:
                        LOG.d(TAG, "NONE: ");
                        if (e1.getX() < getWidth() / 2) {
                            mScrollMode = BRIGHTNESS;
                        } else {
                            mScrollMode = VOLUME;
                        }
                        break;
                    case VOLUME:
                        if (mVideoGestureListener != null && Math.abs(distanceY) > 20) {
                            mVideoGestureListener.onVolumeGesture(e1, e2, distanceX, distanceY);
                        }
                        LOG.d(TAG, "VOLUME: ");
                        break;
                    case BRIGHTNESS:
                        if (mVideoGestureListener != null && Math.abs(distanceY) > 20) {
                            mVideoGestureListener.onBrightnessGesture(e1, e2, distanceX, distanceY);
                        }
                        LOG.d(TAG, "BRIGHTNESS: ");
                        break;
                }
                return true;
            } catch (Exception e) {
                LOG.d(TAG, "onScroll Exception: ");
                return false;
            }
        }


        @Override
        public boolean onContextClick(MotionEvent e) {
            LOG.d(TAG, "onContextClick: ");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            LOG.d(TAG, "onDoubleTap: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            LOG.d(TAG, "onLongPress: ");
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            LOG.d(TAG, "onDoubleTapEvent: ");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            LOG.d(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LOG.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public void onShowPress(MotionEvent e) {
            LOG.d(TAG, "onShowPress: ");
            super.onShowPress(e);
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            try {
                if (mBottomView != null) {
                    if (NoDoubleClickUtils.isDoubleClick())
                        return false;
                    dismissDialogs();
                    if (isShowMobilePlayUI())
                        return false;
                    if (mBottomView.getVisibility() == View.VISIBLE) {
                        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                        showControl(View.GONE);
                        mAddZanContainer.setVisibility(View.GONE);
                    } else {
                        showControl(View.VISIBLE);
                        mAddZanContainer.setVisibility(View.VISIBLE);
                        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, CONTROL_SHOW_TIME);
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    /************************************手势相关*****************************************************************/

    /************************************音量亮度相关*****************************************************************/

    private ImageView iv_center;
    private ProgressBar pb;
    private HideRunnable mHideRunnable;
    private int duration = 1000;
    private View mSettingRoot;
    private TextView mTitle;
    private TextView mTips;

    private void initSetting(){
        mSettingRoot = findViewById(R.id.setting_root);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        pb = (ProgressBar) findViewById(R.id.pb);
        mTitle = findViewById(R.id.title);
        mTips = findViewById(R.id.tips);
        mHideRunnable = new HideRunnable();
    }

    //显示
    public void show(){
        mSettingRoot.setVisibility(VISIBLE);
        removeCallbacks(mHideRunnable);
        postDelayed(mHideRunnable,duration);
    }

    //设置进度
    public void setProgress(int progress){
        pb.setProgress(progress);
    }

    public float getProgress(){
        if (pb!= null)return pb.getProgress()/100f;
        return 0f;
    }

    //设置持续时间
    public void setDuration(int duration) {
        this.duration = duration;
    }

    //设置显示图片
    public void voiceIsOpen(boolean flag){
        setVisibility(VISIBLE);
        mTitle.setText("音量");
        if (flag)
        {
            iv_center.setImageResource(R.drawable.volume_on_w);
            mTips.setVisibility(GONE);
            pb.setVisibility(VISIBLE);
        }
        else
        {
            iv_center.setImageResource(R.drawable.volume_off_w);
            mTips.setVisibility(VISIBLE);
            pb.setVisibility(GONE);
            mTips.setText("静音");
        }
    }

    public void updateBrightUi(){
        setVisibility(VISIBLE);
        mTitle.setText("亮度");
        mTips.setVisibility(GONE);
        pb.setVisibility(VISIBLE);
        iv_center.setImageResource(R.drawable.brightness_w);
    }

    //隐藏自己的Runnable
    private class HideRunnable implements Runnable{
        @Override
        public void run() {
            try {
                mSettingRoot.setVisibility(GONE);
                ReportManager.getInstance().commonReportFun("TVPlayerAdjustment", true, TextUtils.equals("亮度",mTitle.getText())?"2":"1",String.valueOf(1.0*pb.getProgress()/pb.getMax()));
                // reportGestureAction = "1";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setVideoGestureListener(PlayView.VideoGestureListener videoGestureListener) {
        mVideoGestureListener = videoGestureListener;
    }
    /************************************音量亮度相关*****************************************************************/
}
