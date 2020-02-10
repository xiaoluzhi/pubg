package com.tencent.tga.liveplugin.poptv.manager;

import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.qqlive.multimedia.TVKSDKMgr;
import com.tencent.qqlive.multimedia.tvkcommon.api.ITVKProxyFactory;
import com.tencent.qqlive.multimedia.tvkplayer.api.ITVKMediaPlayer;
import com.tencent.qqlive.multimedia.tvkplayer.api.ITVKVideoViewBase;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKPlayerMsg;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKPlayerVideoInfo;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKUserInfo;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.poptv.bean.PopTvBean;
import com.tencent.tga.liveplugin.poptv.view.PopStateView;
import com.tencent.tga.liveplugin.report.PopTvReport;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by hyqiao on 2018/4/9.
 */

public class PopTvPalyerManager {

    private final static String TAG = "PopTvPalyerManager";

    private Activity mActivity;
    private ViewGroup mVideoLayout;
    private PopStateView mPopStateView;
    private PopTvBean mPopTvBean;

    private String mDefine = "sd";
    //腾讯视频播放器相关
    private ITVKMediaPlayer mVideoPlayer = null;
    private TVKUserInfo mUserinfo = null;
    private TVKPlayerVideoInfo mPlayerinfo = null;
    private ITVKProxyFactory mfactory = null;
    private View mVideoView = null;
    private int mLastWidth = 0;
    private int mLastHeigth = 0;

    public PopTvPalyerManager(Activity mActivity, ViewGroup mVideoLayout, PopStateView mPopStateView, PopTvBean mPopTvBean) {
        this.mActivity = mActivity;
        this.mVideoLayout = mVideoLayout;
        this.mPopStateView = mPopStateView;
        this.mPopTvBean = mPopTvBean;
    }

    public void initTxPlayer(){
        isInitReport = false;

        lastBroadCastNetState = NetUtils.NetWorkStatus(mActivity);

        if (NetUtils.NetWorkStatus(mActivity) == NetUtils.WIFI_NET) {
            initPlayerUiOnUi();
        } else if (NetUtils.NetWorkStatus(mActivity) == NetUtils.NO_NET) {
            Toast.makeText(mActivity, "网络异常，请检测网络", Toast.LENGTH_SHORT).show();
        } else if (NetUtils.NetWorkStatus(mActivity) == NetUtils.MOBILE_NET) {

        }

        registerBroadcast();
    }

    public void releaseTxPlayer(){
        try {
            if(mVideoPlayer != null){
                mVideoPlayer.stop();
                mVideoPlayer.release();
                mVideoPlayer = null;
                mUserinfo = null;
                mPlayerinfo = null;
                mfactory = null;
                mVideoView = null;
            }

            unRegisterBroadcast();
        }catch (Exception e){
            TLog.e(TAG,"releaseTxPlayer error "+e.getMessage());
        }
    }

    public void playUnderMobile(){
        initPlayerUiOnUi();
        //play(mPopTvBean.mVid);
        LiveConfig.isPlayOnMobileNet = true;
    }
    private void initPlayerUiOnUi() {
        initReport();//只有初始化播放时才初始化打点上报，保证移动网络数据统计的准确性

        initPlayerUi();
    }

    /**
     * 只有初始化播放时才初始化打点上报，保证移动网络数据统计的准确性
     *
     * @author hyqiao
     * @time 2017/2/23 10:32
     */
    private boolean isInitReport = false;

    private void initReport() {
        if (!isInitReport) {
            isInitReport = true;
        } else {
            return;
        }
        PopTvReport.getInstance().init(mActivity, mPopTvBean.mOpenID,mPopTvBean.mUid, mPopTvBean.mVid, mPopTvBean.mAreaID);
    }

    private void initPlayerUi() {
        LOG.e(TAG, "play vid initPlayerUi");
        if (mActivity == null)
            return;
        if (mVideoPlayer == null) {
            mfactory = TVKSDKMgr.getProxyFactory();
            //TVK_SDKMgr.setUseP2P(Configs.isP2P);

            if (mfactory == null)
                return;
            ITVKVideoViewBase videoViewBase = mfactory.createVideoView_Scroll(mActivity);

            if (videoViewBase == null)
                return;

            if (videoViewBase instanceof View)
                mVideoView = (View) videoViewBase;
            else
                return;

            mVideoPlayer = mfactory.createMediaPlayer(mActivity, videoViewBase);
            if (mVideoLayout == null)
                return;
            mVideoLayout.setBackgroundColor(Color.BLACK);
            mVideoLayout.addView(mVideoView);
//            mVideoLayout.setVisibility(View.INVISIBLE);
            initListeners();
        }

        play(mPopTvBean.mVid);
    }

    private void play(String vid) {
        try {
            TLog.e(TAG, String.format("play vid %s", vid));
            if (null != mVideoPlayer && mVideoPlayer.isPlaying()) mVideoPlayer.stop();
            mUserinfo = new TVKUserInfo("", "");
            if ("1".equals(UnityBean.getmInstance().accountType))
                mUserinfo.setLoginCookie(mPopTvBean.mUid);
            else
                mUserinfo.setLoginCookie(mPopTvBean.mOpenID);

            mUserinfo.setWx_openID(mPopTvBean.mOpenID);

            mVideoPlayer.setXYaxis(TVKPlayerMsg.PLAYER_SCALE_BOTH_FULLSCREEN);

            mPlayerinfo = new TVKPlayerVideoInfo(TVKPlayerMsg.PLAYER_TYPE_ONLINE_LIVE, vid, "");
            PopTvReport.getInstance().setReportVideoQualityStartTime();
            if (Build.VERSION.SDK_INT < 21) {
                mPlayerinfo.addConfigMap("player_forcetype", String.valueOf(TVKPlayerMsg.PLAYER_FORCE_TYPE_SELF_SOFT));
                mVideoPlayer.openMediaPlayer(mActivity, mUserinfo, mPlayerinfo, "sd", 0, 0);
            } else {
                mVideoPlayer.openMediaPlayer(mActivity, mUserinfo, mPlayerinfo, mDefine, 0, 0);
            }
        }catch (Exception e){
            TLog.e(TAG,"play error : "+e.getMessage());
        }
    }


    private void initListeners() {
        mVideoPlayer.setOnVideoPreparedListener(new ITVKMediaPlayer.OnVideoPreparedListener() {
                                                    @Override
                                                    public void onVideoPrepared(ITVKMediaPlayer mpImpl) {
                                                        TLog.d(TAG, "setOnVideoPreparedListener onVideoPrepared...");

                                                        if (mPopStateView != null) {
                                                            mPopStateView.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (mPopStateView != null) {
                                                                        mPopStateView.hideLoading();
                                                                    }
                                                                    PopTvReport.getInstance().setReportVideoQualityEndTime(mPopTvBean.mOpenID, mPopTvBean.mVid, PopTvReport.getInstance().isFirst, mDefine, 1);
                                                                    if (PopTvReport.getInstance().isFirst == 1) {
                                                                        PopTvReport.getInstance().isFirst = 2;
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        mVideoPlayer.start();
//                                                        mVideoLayout.post(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                mVideoLayout.setVisibility(View.VISIBLE);
//                                                            }
//                                                        });
                                                    }
                                                }

        );

        mVideoPlayer.setOnCompletionListener(new ITVKMediaPlayer.OnCompletionListener() {
                                                 @Override
                                                 public void onCompletion(ITVKMediaPlayer mpImpl) {
                                                     TLog.d(TAG, "setOnCompletionListener onCompletion...");
                                                 }
                                             }
        );


        mVideoPlayer.setOnErrorListener(new ITVKMediaPlayer.OnErrorListener() {
                                            @Override
                                            public boolean onError(ITVKMediaPlayer mpImpl, int model, int what,
                                                                   int extra, String detailInfo, Object Info) {
                                                TLog.e(TAG, "setOnErrorListener onError..." + model + " what " + what);
                                                if (!PopTvReport.getInstance().isReportLoadFail) {
                                                    PopTvReport.getInstance().isReportLoadFail = true;
                                                    PopTvReport.getInstance().report_TGAVideoLoadMonitor(mPopTvBean.mOpenID, model, what, mPopTvBean.mVid, mDefine);
                                                }

                                                TLog.e(TAG, "视频播放失败(" + model + ", " + what + ")" + extra);
                                                if (NetUtils.NetWorkStatus(mActivity) == NetUtils.NO_NET) {
                                                    mActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                Toast.makeText(mActivity, "网络异常，请检测网络", Toast.LENGTH_SHORT).show();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
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
                                               TLog.e(TAG, "setOnInfoListener onInfo..." + what);
                                               switch (what) {
                                                   case TVKPlayerMsg.PLAYER_INFO_START_BUFFERING:
                                                       PopTvReport.getInstance().setReportVideoQualityStartTime();

                                                       if (mPopStateView != null) {
                                                           mPopStateView.post(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   mPopStateView.showLoading();
                                                               }
                                                           });
                                                       }
                                                       break;
                                                   case TVKPlayerMsg.PLAYER_INFO_SUCC_SET_DECODER_MODE:
                                                   case TVKPlayerMsg.PLAYER_INFO_ENDOF_BUFFERING:
                                                       try {
                                                           PopTvReport.getInstance().setReportVideoQualityEndTime(mPopTvBean.mOpenID, mPopTvBean.mVid, PopTvReport.getInstance().isFirst, mDefine, 1);
                                                           if (PopTvReport.getInstance().isFirst == 1) {
                                                               PopTvReport.getInstance().isFirst = 2;
                                                           }

                                                           if (mPopStateView != null) {
                                                               mPopStateView.post(new Runnable() {
                                                                   @Override
                                                                   public void run() {
                                                                       if (mPopStateView != null) {
                                                                           mPopStateView.hideLoading();
                                                                       }
                                                                   }
                                                               });
                                                           }
                                                       } catch (Exception e) {
                                                           TLog.e(TAG, "PLAYER_INFO_ENDOF_BUFFERING error : " + e.getMessage());
                                                       }

                                                       break;
                                                   default:
                                                       break;
                                               }
                                               return false;
                                           }
                                       }
        );

    }


    private void registerBroadcast() {
        try {
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mActivity.registerReceiver(mNetStateChangeReceiver, mFilter);
        } catch (Exception e) {
            TLog.e(TAG, "register receiver failed");
        }
    }

    private void unRegisterBroadcast() {
        if (mNetStateChangeReceiver != null) {
            try {
                mActivity.unregisterReceiver(mNetStateChangeReceiver);
                mNetStateChangeReceiver = null;
            } catch (Exception e) {
            }
        }
    }

    private long lastBroadCastTime = System.currentTimeMillis();
    private int lastBroadCastNetState = -1;
    private final static int BROADCAST_INTERVAL = 2000;
    private BroadcastReceiver mNetStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                /**返回当前网络状况：1表示移动网络，2表示wifi,3表示无网络连接*/
                int state = NetUtils.NetWorkStatus(context);
                TLog.e("state  == " + state);

                long lastTime = lastBroadCastTime;
                int lastState = lastBroadCastNetState;
                lastBroadCastTime = System.currentTimeMillis();
                lastBroadCastNetState = state;
                if (lastState == state && (System.currentTimeMillis() - lastTime) < BROADCAST_INTERVAL) {
                    TLog.e(TAG, "重复的广播  state = " + state);
                } else {
                    TLog.e(TAG, "执行的广播  state = " + state);
                    if (state == NetUtils.WIFI_NET) {
                        playOnNetBroast();
                    } else if (state == NetUtils.MOBILE_NET) {
                        if (LiveConfig.isPlayOnMobileNet) {
                            playOnNetBroast();
                        } else {
                            if (mVideoPlayer != null) {
                                mVideoPlayer.stop();
                            }
                            if (mPopStateView != null) {
                                mPopStateView.showNetTips();
                            }

                            if (mPopStateView != null) {
                                mPopStateView.hideLoading();
                            }
                        }
                    }
                }
            } else {
                TLog.e(TAG, "other system broadcast");
            }
        }
    };


    private void playOnNetBroast() {
        if (mPopStateView != null) {
            mPopStateView.post(new Runnable() {
                @Override
                public void run() {
                    mPopStateView.hideNetTips();
                }
            });
        }
        initPlayerUiOnUi();
    }

    public void stopPlay() {
        if (null != mVideoPlayer) mVideoPlayer.pause();
    }

    public void startPlay() {
        if (null != mVideoPlayer) mVideoPlayer.start();
    }

}
