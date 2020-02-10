package com.tencent.tga.liveplugin.live.player.presenter

import android.view.View
import com.loopj.android.tgahttp.Configs.Configs
import com.ryg.utils.LOG
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter
import com.tencent.tga.liveplugin.base.util.DeviceUtils
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils
import com.tencent.tga.liveplugin.base.util.ToastUtil
import com.tencent.tga.liveplugin.live.LiveConfig
import com.tencent.tga.liveplugin.live.LiveInfo
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent
import com.tencent.tga.liveplugin.live.common.broadcast.manager.NetBroadcastMananger
import com.tencent.tga.liveplugin.live.common.proxy.ProxyHolder
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent
import com.tencent.tga.liveplugin.live.player.PlayView
import com.tencent.tga.liveplugin.live.player.PlayView.isFullscreen
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent
import com.tencent.tga.liveplugin.live.player.modle.PlayViewModle
import com.tencent.tga.liveplugin.live.player.ui.video.view.ChatPopwindow
import com.tencent.tga.liveplugin.live.player.ui.video.view.DanmuSettingView
import com.tencent.tga.liveplugin.live.player.ui.video.view.HotWordDialog
import com.tencent.tga.liveplugin.live.right.event.RightViewEvent
import com.tencent.tga.liveplugin.networkutil.NetUtils
import com.tencent.tga.liveplugin.report.ReportManager
import com.tencent.tga.plugin.R

/**
 * created by lionljwang
 */
class PlayViewPresenter : BaseFrameLayoutPresenter<PlayView, PlayViewModle>(), View.OnClickListener {
    override fun getModel(): PlayViewModle {
        if (modle == null)
            modle = PlayViewModle(this)
        return modle
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.live_pause ->livePause()

            R.id.edit_hot ->hotClick()

            R.id.danmu_oper ->danmuOperClick()

            R.id.danmu_setting ->danmuSettingClick()

            R.id.switch_mode->switchModeClick()

            R.id.edit_text->editTextClick()
        }
    }

    fun editTextClick(){
        if (NoDoubleClickUtils.isDoubleClick()) return
        val string = view.mPlayerController.mEditText.getText().toString()

        view.mChatPopwindow = ChatPopwindow(view.getContext(), 20)

        if (view.mChatPopwindow.popWindow.isShowing())
            return

        if ( "" != string) {
            view.mChatPopwindow.show(view.mPlayerController, string)
        } else {
            view.mChatPopwindow.show(view.mPlayerController, "")
        }

        view.mPlayerController.setmEditText(null)
    }

    fun switchModeClick(){
        if (NoDoubleClickUtils.isDoubleClick()) return
        LiveViewEvent.switchMode(true)
    }

    fun danmuSettingClick(){
        if (NoDoubleClickUtils.isDoubleClick()) return
        view.mDanmuSettingView = DanmuSettingView(view)

        view.mDanmuSettingView.stateView = view.mPlayerStateView

        if (!view.mDanmuSettingView.isShowing()) {
            ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "3", "")
            view.mDanmuSettingView.showInPlayViewRightAndBottom(view, DeviceUtils.dip2px(view.getContext(), 180f))
        }
        view.dismissCtl()

    }

    fun danmuOperClick(){
        val isShow = LiveShareUitl.isShowDanmu(view.getContext())
        if (isFullscreen()) {
            view.delayDismissCtl(5)
        }

        view.mPlayerController.setDanumIcon(!isShow)
        if (!isShow) {
            ReportManager.getInstance().report_WordFlowSwitchInfo(LiveInfo.mRoomId, ReportManager.WORD_FLOW_OPEN)
        } else {
            ReportManager.getInstance().report_WordFlowSwitchInfo(LiveInfo.mRoomId, ReportManager.WORD_FLOW_CLOSE)
        }
        if (isShow) {
            LiveShareUitl.saveDanmuState(view.getContext(), false)
            view.mPlayerStateView.setDanmuVisible(View.GONE)
            ToastUtil.show(view.getContext(), "弹幕已关闭")
        } else {
            LiveShareUitl.saveDanmuState(view.getContext(), true)
            view.mPlayerStateView.setDanmuVisible(View.VISIBLE)
            ToastUtil.show(view.getContext(), "弹幕已开启")
        }

    }

    fun hotClick(){
        if (NoDoubleClickUtils.isDoubleClick()) return
        PlayViewEvent.dismissDialogs()
        if (null == view.hotWordDialog) {
            view.hotWordDialog = HotWordDialog(view)
        }
        if (!view.hotWordDialog.isShowing()) {
            if (NetUtils.isNetworkAvailable(view.getContext())) {
                view.hotWordDialog.showLeft(DeviceUtils.dip2px(view.getContext(), 175f),DeviceUtils.getScreenHeight(view.context), 0, 0)
                ReportManager.getInstance().report_TVWordBarragePanelClick(LiveInfo.mRoomId, if (isFullscreen()) 1 else 0)
            }
        }
    }


    fun livePause() {
        if (NoDoubleClickUtils.isDoubleClick()) return
        if (view.mTVKPlayerManager.mVideoPlayer != null) {
            if (view.mPlayerStateView != null && view.mPlayerStateView.isShowNotWifiPlay())
                return
            if (isFullscreen()) {
                view.delayDismissCtl(4)
            }

            if (view.mTVKPlayerManager.mVideoPlayer != null && view.mTVKPlayerManager.mVideoPlayer.isPlaying()) {
                view.mTVKPlayerManager.mVideoPlayer.pause()
                view.mPlayerController.setOnpauseView()
                view.mTVKPlayerManager.isUserPause = true
                view.showMobileUi(true)
                ReportManager.getInstance().report_PlayPause(isFullscreen(), true)
            } else {
                view.mTVKPlayerManager.isUserPause = false
                view.mTVKPlayerManager.play()
            }
        }
    }

    override fun attach(view: PlayView) {
        super.attach(view)
        try {
            TGARouter.getInstance()!!.registerProvider(this)
            registeEvent(PlayViewEvent::class.java, view)
            LOG.e(LiveConfig.TAG,"PlayViewPresenter attach......")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


     var hasReportDanmuSwitch = false
    //一些字段的初始化上报，等链接建立之后
    fun doPlayViewInitReport() {
        if (hasReportDanmuSwitch) {
            return
        }
        hasReportDanmuSwitch = true
        val isShow = LiveShareUitl.isShowDanmu(getView().context)
        if (isShow) {
            getView().mPlayerStateView.setDanmuVisible(View.VISIBLE)
            ReportManager.getInstance().commonReportFun("UserDanmaSwitch", false, "1")
        } else {
            getView().mPlayerStateView.setDanmuVisible(View.INVISIBLE)
            ReportManager.getInstance().commonReportFun("UserDanmaSwitch", false, "0")
        }
    }


    fun changeLine( event: LiveEvent.LiveLineChange) {
        view.mTVKPlayerManager.isUserPause = false
        NetBroadcastMananger.getInstance().msg.clear()
        ProxyHolder.getInstance().exitRoom(2, false)
        if (event.channelInfo != null) {
            ReportManager.getInstance().commonReportFunAll("MatchChannelReport", true, event.channelInfo.sourceid.toString(), "1")
            RightViewEvent.clearMsg()
            PlayViewEvent.updateRoomInfo(event.channelInfo, true, event.playType)
            view.mLiveLineSelectView.refresh()
        } else {//默认切到kpl直播
            ReportManager.getInstance().commonReportFunAll("MatchChannelReport", true, Configs.SOURCE_ID.toString() + "", "1")
            LiveInfo.mSourceId = Configs.SOURCE_ID
            model.reqCurrentMatch(false)

        }
        view.mPlayerStateView.dismissLiveOffLine()
        view.mPlayerStateView.dissmissNotWifiPlay()

        view.mPlayerStateView.clearDanma()


        if (NetUtils.NetWorkStatus(LiveConfig.mLiveContext) == NetUtils.MOBILE_NET && !LiveConfig.isPlayOnMobileNet) {
            view.showMobileUi(false)
        }
    }
    fun dismissDialogs() {
        if (view?.hotWordDialog != null && view.hotWordDialog.isShowing()) {
            view.hotWordDialog.dismiss()
        }
        if (null != view.mPlayerController) {
            view.mPlayerController.dismissDialogs()
        }
        if (null != view.mDanmuSettingView) {
            view.mDanmuSettingView.dismiss()
        }
        if (null != view.mLiveLineSelectView) {
            view.mLiveLineSelectView.dismiss()
        }
        if (null != view.mPlayerController) {
            view.mPlayerController.setVisibility(View.GONE, isFullscreen())
        }
        if (null != view.mPlayerTitleView && isFullscreen()) {
            view.mPlayerTitleView.setVisibility(View.GONE)
        }

    }

}
