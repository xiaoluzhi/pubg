package com.tencent.tga.liveplugin.live.liveView.event

import android.view.View
import com.tencent.tga.liveplugin.base.routerCenter.BaseEvent
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent
import com.tencent.tga.liveplugin.live.liveView.LiveView
import com.tencent.tga.liveplugin.live.liveView.presenter.LiveViewPresenter
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgBean
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity
import java.util.*

class LiveViewEvent(view: View) : BaseEvent(view) {

    override fun execute(type: Int?, vararg params: Any): Any? {
        when (type) {
            SWITCH_MODE -> (mView as LiveView).switchMode(params[0] as Boolean)
            IMAGELOADER_INIT -> (mView as LiveView).mTitleView.refreshTitleIcon()
            ADD_CHAT_MSG -> (mView as LiveView).chatView.addChatMsg(params[0] as ChatMsgBean)
            SEND_MSG -> (mView as LiveView).handleSendMsg(params[0] as LiveEvent.SendMsg)
            UPDATE_PLAYVIEW_VISIVILITY -> (mView as LiveView).updatePlayViewVisibility(params[0] as Boolean)
            ONSTART -> (mView as LiveView).onStart(params[0] as Boolean)
            ON_VIEW_STOP-> (mView as LiveView).onViewStop()
            LUANCH_CONFIG_TAB-> (mView as LiveView).launchConfigTAB(params[0] as String)
            DISMISS_CONFIG_TAG-> (mView as LiveView).disMissConfigTAB()
            SHOW_TEXT_MSG-> (mView as LiveView).presenter.addMsg(params[0] as ChatMsgEntity)
            LAUCNH_VIDEO_VIEW->(mView as LiveView).launchVideoView(params[0] as Boolean, params[1] as ArrayList<String>
                    , params[2] as ArrayList<String>, params[3] as Int, params[4] as ArrayList<String>, params[5] as Boolean)
            VIEDO_SEND_DANMU->(mView as LiveView).mVideoPlayView?.sendDanmu(params[0] as String)
            VIDEO_FINISH->{
                (mView as LiveView).mVideoPlayView?.finish()
            }
            SHOW_SCHEDULE->(mView as LiveView).showSchedule()
            REFRESH_WEBVIEW_TITLE->(mView as LiveView).refreshWebViewTitle(params[0] as String)

        }
        return null
    }

    companion object {
        private val SWITCH_MODE = 1

        private val IMAGELOADER_INIT = 2

        private val ADD_CHAT_MSG = 3

        private val SEND_MSG = 4

        private val UPDATE_PLAYVIEW_VISIVILITY = 5

        private val ONSTART = 6

        private val ON_VIEW_STOP = 7

        private val LUANCH_CONFIG_TAB = 8

        private val DISMISS_CONFIG_TAG = 9

        private val SHOW_TEXT_MSG = 10

        private val LAUCNH_VIDEO_VIEW = 11

        private val VIEDO_SEND_DANMU=12

        private val VIDEO_FINISH = 13

        private val SHOW_SCHEDULE  = 16

        private val REFRESH_WEBVIEW_TITLE = 17


        fun showTextMsg(entity: ChatMsgEntity) {
            doExc(SHOW_TEXT_MSG, entity)
        }

        fun sendMsg( msgEvent:LiveEvent.SendMsg) {
            doExc(SEND_MSG, msgEvent)
        }

        fun switchMode(flag: Boolean) {
            doExc(SWITCH_MODE, flag)
        }

        fun imageLoaderInit() {
            doExc(IMAGELOADER_INIT)
        }

        fun addChatMsg(bean: ChatMsgBean) {
            doExc(ADD_CHAT_MSG, bean)
        }

        fun updatePlayViewVisibility(isShow: Boolean) {
            doExc(UPDATE_PLAYVIEW_VISIVILITY, isShow)
        }

        fun onViewStop() {
            doExc(ON_VIEW_STOP)
        }

        fun onstart(isShow: Boolean) {
            doExc(ONSTART, isShow)
        }

        fun launchConfigTAB(url: String) {
            doExc(LUANCH_CONFIG_TAB, url)
        }
        fun disMissConfigTAB() {
            doExc(DISMISS_CONFIG_TAG)
        }

        fun refreshWebViewTitle(title : String) {
            doExc(REFRESH_WEBVIEW_TITLE)
        }

        fun launchVideoView(isFromSchedule:Boolean, vids:ArrayList<String>, vidTitles:ArrayList<String> ,index:Int,commentNums:ArrayList<Int>,isPlayBack:Boolean) {
            doExc(LAUCNH_VIDEO_VIEW, isFromSchedule,vids, vidTitles,index,commentNums,isPlayBack)
        }
        fun sendVideoDanmu(msg:String){
            doExc(VIEDO_SEND_DANMU,msg)
        }

        fun videoFinish(){
            doExc(VIDEO_FINISH)
        }


        fun showSchedule() {
            doExc(SHOW_SCHEDULE)
        }



        private fun doExc(tyep: Int, vararg params: Any) {
            TGARouter.getInstance()!!.execute(LiveViewPresenter::class.java.name, LiveViewEvent::class.java.name, tyep, *params)
        }

    }
}