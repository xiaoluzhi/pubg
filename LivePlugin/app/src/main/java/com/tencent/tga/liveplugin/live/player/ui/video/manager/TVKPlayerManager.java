package com.tencent.tga.liveplugin.live.player.ui.video.manager;

import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.qqlive.multimedia.TVKSDKMgr;
import com.tencent.qqlive.multimedia.tvkcommon.api.ITVKProxyFactory;
import com.tencent.qqlive.multimedia.tvkplayer.api.ITVKMediaPlayer;
import com.tencent.qqlive.multimedia.tvkplayer.api.ITVKVideoViewBase;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKNetVideoInfo;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKPlayerMsg;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKPlayerVideoInfo;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKUserInfo;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.LiveDefineView;
import com.tencent.tga.liveplugin.live.title.TitleView;
import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.liveplugin.report.VideoMonitorReport;

import android.text.TextUtils;
import android.view.View;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import static com.tencent.tga.liveplugin.live.player.PlayView.MSG_HIDE_CONTROL_VIEW;


/**
 * Created by agneswang on 2017/3/15.
 * 腾讯视频播放器处理类，负责腾讯视频播放器UI的初始化，回调注册，以及提供播放、暂停等功能
 */

public class TVKPlayerManager {

    private static final String TAG = "TVKPlayerManager";

    private SoftReference<PlayView> mPlayView;

    public ITVKMediaPlayer mVideoPlayer = null;
    private TVKUserInfo mUserinfo = null;
    private TVKPlayerVideoInfo mPlayerinfo = null;
    private ITVKProxyFactory mfactory = null;
    private ITVKVideoViewBase mVideoView = null;
    public View mVideoViewCasted;

    public String mdefine = "";
    private boolean isUserChangeDefine = false;

    private VideoMonitorReport monitorReport;

    /**
     * 是否已经主动暂停,手动暂停，去html5
     * <p/>
     * 防止网络广播在后台或者去h5还会播放
     */
    public boolean isUserPause = false;


    public TVKPlayerManager(PlayView view) {
        mPlayView = new SoftReference<>(view);
    }

    public boolean isRecycle() {
        return mPlayView == null || mPlayView.get() == null;
    }

    public void init() {
        if (isRecycle()) return;
        mfactory = TVKSDKMgr.getProxyFactory();
        if (null != mfactory && mPlayView.get().mPlayerLayout != null) {
            mVideoView = mfactory.createVideoView(mPlayView.get().getContext());
            mVideoPlayer = mfactory.createMediaPlayer(mPlayView.get().getContext(), mVideoView);
            mVideoViewCasted = (View) mVideoView;
            mVideoViewCasted.setVisibility(View.VISIBLE);
            monitorReport = VideoMonitorReport.getInstance(mPlayView.get().getContext());
            mdefine = LiveShareUitl.getLiveDefine(mPlayView.get().getContext());
            initListeners();
            if (!isAttached()) mPlayView.get().mPlayerLayout.addView(mVideoViewCasted);
            mPlayView.get().initAttachedViews();
        }
    }


    public void play() {
        try {
            if (isRecycle()) return;

            if (TitleView.mCurrentSelection != DefaultTagID.LIVE)
                return;

            if (mVideoPlayer == null || LiveInfo.isStopPLay)
                return;

            if (NetUtils.NetWorkStatus(mPlayView.get().getContext()) == NetUtils.MOBILE_NET && !isUserChangeDefine && LiveConfig.isPlayOnMobileNet)
                mdefine = LiveShareUitl.getLiveDefine(mPlayView.get().getContext());

            if (TextUtils.isEmpty(LiveInfo.mLiveid) || TextUtils.isEmpty(mdefine))
                return;


            if (mPlayView.get().mPlayerStateView != null && mPlayView.get().mPlayerStateView.isOnlive())
                return;

            LOG.e(TAG, "paly vid == " + UnityBean.getmInstance().gameUid);
            if (NetUtils.NetWorkStatus(mPlayView.get().getContext()) == NetUtils.MOBILE_NET && !LiveConfig.isPlayOnMobileNet) {
                mPlayView.get().showMobileUi(false);
            } else {

                if (!isUserPause) {
                    mVideoPlayer.stop();
                    mUserinfo = new TVKUserInfo();
                    if ("1".equals(UnityBean.getmInstance().accountType))
                        mUserinfo.setLoginCookie(UnityBean.getmInstance().gameUid);
                    else
                        mUserinfo.setLoginCookie(UnityBean.getmInstance().openid);

                    mUserinfo.setWx_openID(UnityBean.getmInstance().openid);

                    monitorReport.setReportVideoQualityStartTime();
                    mPlayerinfo = new TVKPlayerVideoInfo(TVKPlayerMsg.PLAYER_TYPE_ONLINE_LIVE, LiveInfo.mLiveid, "");
                    mVideoPlayer.openMediaPlayer(mPlayView.get().getContext(), mUserinfo, mPlayerinfo, mdefine, 0, 0);

                }
                if (mPlayView.get().mPlayerController != null) {
                    if (!isUserPause)
                        mPlayView.get().mPlayerController.setOnStartView();
                    if (mPlayView.get().mPlayerStateView != null) {
                        if (isUserPause) {
                            mPlayView.get().showMobileUi(true);
                        } else {
                            mPlayView.get().mPlayerStateView.dismissLiveOffLine();
                            mPlayView.get().mPlayerStateView.dissmissNotWifiPlay();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isAttached() {
        if (isRecycle()) return false;
        return mPlayView.get().mPlayerLayout.indexOfChild(mVideoViewCasted) != -1;
    }

    public void stopPlay() {
        if (isRecycle()) return;
        if (mVideoPlayer != null) {
            new Thread(() -> mVideoPlayer.stop()).start();
            if (null != mPlayView.get().mPlayerController) {
                mPlayView.get().mPlayerController.setOnpauseView();
            }
        }
    }


    public void release() {
        if (mVideoPlayer != null) {
            new Thread(() -> {
                mVideoPlayer.stop();
                mVideoPlayer.release();
            }).start();
        }
    }

    /**************************
     * 播放器相关回调
     ****************************************************************/

    private int isFirst = 1;
    private boolean isReportLoadFail = false;//视频加载失败上报一次

    private void initListeners() {
        mVideoPlayer.setOnNetVideoInfoListener((itvkMediaPlayer, tvkNetVideoInfo) -> {
                    ArrayList<TVKNetVideoInfo.DefnInfo> defnInfos = tvkNetVideoInfo.getDefinitionList();
                    if (isRecycle()) return;
                    mPlayView.get().post(() -> mPlayView.get().setDefine(tvkNetVideoInfo.getCurDefinition(), defnInfos,
                            new LiveDefineView.VideoDefinChangeListener() {
                                @Override
                                public void onChange(TVKNetVideoInfo.DefnInfo defnInfo) {

                                    TLog.e(TAG, "setOnNetVideoInfoListener onInfo..." + defnInfo.getDefn());
                                    if (LiveInfo.isStopPLay)
                                        return;
                                    if (isRecycle()) return;
                                    isUserPause = false;
                                    if (null != mPlayView.get().mPlayerStateView && mPlayView.get().mPlayerStateView.isShowNotWifiPlay())
                                        return;
                                    mPlayView.get().post(() -> {
                                        if (mVideoPlayer != null) {
                                            isUserChangeDefine = true;
                                            String preDefine = mdefine;
                                            try {
                                                if (mVideoPlayer.isPausing()) {
                                                    mVideoPlayer.start();
                                                }
                                                if (mVideoPlayer.isPlaying()) {
                                                    mVideoPlayer.switchDefinition(defnInfo.getDefn());
                                                    mPlayView.get().mPlayerController.setOnStartView();
                                                } else {
                                                    play();
                                                }
                                                mdefine = defnInfo.getDefn();
                                                mPlayView.get().unShowDefinChangeTips();

                                                mPlayView.get().mPlayerController.setCurDefine(defnInfo);
                                                ReportManager.getInstance().report_DefinitionSelectInfo(LiveInfo.mRoomId, preDefine, defnInfo.getDefn(), ReportManager.SOURCE_LIVING, LiveInfo.mLiveid);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onClick() {
                                    if (isRecycle()) return;
                                    if (mPlayView.get().mHandler != null && PlayView.isFullscreen()) {
                                        mPlayView.get().mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
                                    }
                                }

                                @Override
                                public void onDismiss() {
                                    if (isRecycle()) return;
                                    if (PlayView.isFullscreen()) {
                                        mPlayView.get().delayDismissCtl(3);
                                    }
                                }
                            }));
                }
        );


        mVideoPlayer.setOnVideoPreparedListener(itvkMediaPlayer -> {
                    TLog.e(TAG, "setOnVideoPreparedListener ...");
                    if (isRecycle()) return;
                    if (isUserPause) {
                        mVideoPlayer.stop();
                    } else {
                        mVideoPlayer.start();
                        mPlayView.get().post(() -> {
                            mPlayView.get().mPlayerStateView.dismissLoading();
                            if (mPlayView.get().mPlayerController != null) {
                                mPlayView.get().mPlayerController.setOnStartView();
                            }
                        });

                        monitorReport.setReportVideoQualityEndTime(UnityBean.getmInstance().openid, LiveInfo.mLiveid, isFirst, mdefine, 1);
                        if (isFirst == 1) {
                            isFirst = 2;
                        }
                    }
                }
        );

        mVideoPlayer.setOnErrorListener((itvkMediaPlayer, i, i1, i2, s, o) -> {
                    if (isRecycle()) return false;
                    int model = i;
                    int what = i1;
                    int extra = i2;
                    TLog.e(TAG, "setOnErrorListener onError..." + model + " what " + what);
                    if (!isReportLoadFail) {
                        isReportLoadFail = true;
                        monitorReport.report_TGAVideoLoadMonitor(UnityBean.getmInstance().openid, 1, model, what, LiveInfo.mLiveid, mdefine);
                    }
                    if (model == 122 && 204 == what) {

                        if (mPlayView.get().mPlayerController != null) {

                            mPlayView.get().post(() -> mPlayView.get().mPlayerController.setOnpauseView());
                        }
                    }
                    TLog.e(TAG, "视频播放失败(" + model + ", " + what + ")" + extra);

                    return false;
                }

        );

        monitorReport.setVideoQualityPosition(1);

        mVideoPlayer.setOnInfoListener((itvkMediaPlayer, i, o) -> {
                    TLog.e(TAG, "setOnInfoListener onInfo..." + i);
                    switch (i) {
                        case TVKPlayerMsg.PLAYER_INFO_START_BUFFERING:
                            if (mPlayView.get().mPlayerStateView != null) {
                                mPlayView.get().post(() -> mPlayView.get().mPlayerStateView.showLoading());
                            }
                            mPlayView.get().post(() -> mPlayView.get().onLiveLoading());
                            monitorReport.setReportVideoQualityStartTime();
                            break;
                        case TVKPlayerMsg.PLAYER_INFO_SUCC_SET_DECODER_MODE:
                        case TVKPlayerMsg.PLAYER_INFO_ENDOF_BUFFERING:
                            if (mPlayView.get().mPlayerStateView != null) {
                                mPlayView.get().mPlayerStateView.post(() -> mPlayView.get().mPlayerStateView.dismissLoading());
                            }
                            monitorReport.setReportVideoQualityEndTime(UnityBean.getmInstance().openid, LiveInfo.mLiveid, isFirst, mdefine, 1);
                            if (isFirst == 1) {
                                isFirst = 2;
                            }
                            mPlayView.get().post(() ->mPlayView.get().onLivePlaying());
                            break;
                        default:
                            break;
                    }
                    return false;
                }
        );

    }

}
