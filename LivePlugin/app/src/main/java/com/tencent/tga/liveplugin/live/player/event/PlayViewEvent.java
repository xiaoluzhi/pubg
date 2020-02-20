package com.tencent.tga.liveplugin.live.player.event;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.routerCenter.BaseEvent;
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo;
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.broadcast.manager.EventBroadcastMananger;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.presenter.PlayViewPresenter;
import com.tencent.tga.liveplugin.live.right.event.RightViewEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.view.View;

import java.util.List;

/**
 * created by lionljwang
 */
public class PlayViewEvent extends BaseEvent {
    private static final int DO_PLAY_VIEW_INIT_REPORT = 1;

    private static final int STOP_PALY = 2;

    private static final int RE_PLAY = 3;

    private static final int MOBILEPLAY = 4;

    private static final int LIVELINE_CLICK = 5;

    private static final int VIDEOFINE_CLICK = 6;

    private static final int UPDATE_ROOM_INFO = 7;

    private static final int OFF_LINE = 8;

    private static final int SET_ONLINE_NUM = 9;

    private static final int SET_SIZE_AND_VISIBILITY = 10;

    private static final int SHOW_LIVE_LINE_VIEW = 11;

    private static final int LINE_CHANGE = 12;

    private static final int LOCK_SCREEN = 13;

    private static final int INIT = 14;

    private static final int REQ_CUR_MATCH = 15;

    private static final int DISMISS_DIALOG = 16;

    private static final int NET_WORK_CHANGE = 17;

    private static final int UPDATE_PLAYVIEW = 18;

    private static final int SHOW_LIVE_LINE_TIPS = 19;

    private static final int HIDE_LIVE_LINE_VIEW = 20;

    private static final String TAG = "PlayViewEvent";

    public PlayViewEvent(View view) {
        super(view);
    }

    public static void doPlayViewInitReport() {
        doExc( DO_PLAY_VIEW_INIT_REPORT);
    }

    public static void stopPlay() {
        doExc( STOP_PALY);
    }

    public static void rePlay() {
        doExc( RE_PLAY);
    }

    public static void mobileplay() {
        doExc( MOBILEPLAY);
    }

    public static void videfineClick() {
        doExc( VIDEOFINE_CLICK);
    }

    public static void liveLineClick() {
        doExc(LIVELINE_CLICK);
    }


    public static void updateRoomInfo(RoomInfo roomInfo, boolean isUpdate, int playType) {
        doExc( UPDATE_ROOM_INFO,roomInfo,isUpdate, playType);
    }

    public static void offLine(int sourceId) {
        doExc( OFF_LINE,sourceId);
    }

    public static void setOnlineNum(int onlineNum) {
        doExc( SET_ONLINE_NUM,onlineNum);
    }

    public static void setSizeAndVivibility(List<ChannelInfo> list) {
        doExc( SET_SIZE_AND_VISIBILITY,list);
    }

    public static void updatePlayViewInfo(RoomInfo info, int playType) {
        doExc(UPDATE_PLAYVIEW, info, playType);
    }

    public static void showLiveLineView(List<ChannelInfo> list) {
        doExc( SHOW_LIVE_LINE_VIEW,list);
    }

    public static void lineChange(LiveEvent.LiveLineChange liveLineChange) {
        doExc( LINE_CHANGE,liveLineChange);
    }

    public static void hideLiveLineView() {
        doExc(HIDE_LIVE_LINE_VIEW);
    }

    public static void lockScreen(LiveEvent.LockSreen lockSreen) {
        doExc( LOCK_SCREEN,lockSreen);
    }

    public static void init() {
        doExc( INIT);
    }
    public static void reqCurMatch(boolean isLiveLine) {
        doExc( REQ_CUR_MATCH,isLiveLine);
    }
    public static void dismissDialogs() {
        doExc( DISMISS_DIALOG);
    }

    public static void netWorkChange(int  state) {
        doExc(NET_WORK_CHANGE,state);
    }
    public static void showLiveLineTips() {
        doExc(SHOW_LIVE_LINE_TIPS);
    }

    private static void doExc(int tyep,Object... params){
        TGARouter.Companion.getInstance().execute(PlayViewPresenter.class.getName(), PlayViewEvent.class.getName(), tyep,params);
    }
    
    @Nullable
    @Override
    public Object execute(@Nullable Integer type, @NotNull Object... params) {
        try {
            switch (type) {
                case DO_PLAY_VIEW_INIT_REPORT:
                    ((PlayView) getMView()).getPresenter().doPlayViewInitReport();
                    break;
                case STOP_PALY:
                    ((PlayView) getMView()).stopPlay();
                    break;
                case RE_PLAY:
                    ((PlayView) getMView()).play();
                    break;
                case LIVELINE_CLICK:
                    ((PlayView) getMView()).liveLineClick();
                    break;
                case MOBILEPLAY:
                    ((PlayView) getMView()).mobilePlayClick();
                    break;
                case VIDEOFINE_CLICK:
                    ((PlayView) getMView()).videDefineClick();
                    break;
                case UPDATE_ROOM_INFO:
                    if (params != null && params.length == 3) {
                        EventBroadcastMananger.getInstance().updateSourceIDAndRoomId((RoomInfo) params[0], (boolean) params[1]);
                        if (LiveInfo.mSourceId != ((RoomInfo) params[0]).getSourceid() && (boolean) params[1])
                            break;
                    }
                    ((PlayView) getMView()).updateRoomInfo((RoomInfo) params[0], (int)params[2]);
                    RightViewEvent.Companion.initTimerGiftView((RoomInfo) params[0]);
                    break;
                case UPDATE_PLAYVIEW:
                    ((PlayView) getMView()).updateRoomInfo((RoomInfo) params[0], (int)params[1]);
                case OFF_LINE:
                    ((PlayView) getMView()).offLine((int) params[0]);
                    break;
                case SET_ONLINE_NUM:
                    ((PlayView) getMView()).mPlayerTitleView.setmOnlineNum((int) params[0]);
                    break;
                case SET_SIZE_AND_VISIBILITY:
                    ((PlayView) getMView()).mPlayerTitleView.setSizeAndVivibility((List<ChannelInfo>) params[0]);
                    break;

                case SHOW_LIVE_LINE_VIEW:
                    ((PlayView) getMView()).showLiveLineView((List<ChannelInfo>) params[0]);
                    break;
                case LINE_CHANGE:
                    ((PlayView) getMView()).getPresenter().changeLine((LiveEvent.LiveLineChange) params[0]);
                    break;
                case LOCK_SCREEN:
                    if (((LiveEvent.LockSreen) params[0]).isLock) {
                        ((PlayView) getMView()).dismissCtl();
                    } else {

                        ((PlayView) getMView()).mPlayerController.setVisibility(View.VISIBLE, PlayView.isFullscreen());
                        ((PlayView) getMView()).delayDismissCtl(1);
                    }
                    break;
                case INIT:
                    ((PlayView) getMView()).getPresenter().getModel().init();
                    break;
                case REQ_CUR_MATCH:
                    ((PlayView) getMView()).getPresenter().getModel().reqCurrentMatch((Boolean) params[0]);
                    break;
                case DISMISS_DIALOG:
                    ((PlayView) getMView()).getPresenter().dismissDialogs();
                    break;
                case NET_WORK_CHANGE:
                    donetWorkChange((int) params[0]);
                    break;
                case SHOW_LIVE_LINE_TIPS:
                    ((PlayView) getMView()).showLiveLineTipsView();
                    break;
                case HIDE_LIVE_LINE_VIEW:
                    ((PlayView) getMView()).hideLiveLineView();

            }
        } catch (Exception e) {
            TLog.e(TAG, e.getMessage());
        }
        return null;
    }

    private void donetWorkChange(int state){
        TLog.e(TAG, "event.state-->" + state);

        if (LiveInfo.isStopPLay)
            return;

        if (state == 1 && !LiveConfig.isPlayOnMobileNet) {
            ((PlayView) getMView()).showMobileUi(false);
            ((PlayView) getMView()).getPresenter().dismissDialogs();
            PlayViewEvent.stopPlay();

        } else if (state == 2 || (state == 1 && LiveConfig.isPlayOnMobileNet)) {
            if (state == 1)
                ((PlayView) getMView()).mTVKPlayerManager.mdefine = "sd";

            ((PlayView) getMView()).mTVKPlayerManager.play();
        } else if (state == 3) {
            ToastUtil.show(LiveConfig.mLiveContext, "当前网络不可用，请检查网络！");
        }
    }
}
