package com.tencent.tga.liveplugin.live.right.event

import android.view.View
import com.tencent.tga.liveplugin.base.routerCenter.BaseEvent
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo
import com.tencent.tga.liveplugin.live.right.LiveRightContainer
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity
import com.tencent.tga.liveplugin.live.right.presenter.RightViewPresenter
import com.tencent.tga.plugin.R

class RightViewEvent(view: View) : BaseEvent(view) {

    override fun execute(type: Int?, vararg params: Any): Any? {
        when (type) {
            CLEARN_MSG -> (mView as LiveRightContainer).mChatView.clear()
            ADD_SYS_MSG -> (mView as LiveRightContainer).mChatView.addSysNotice(mView!!.resources.getString(R.string.sys_chat_tips))
            SHOW_CHAT_MSG -> (mView as LiveRightContainer).mChatView.addChatMsg(params[0] as ChatMsgEntity)
            INIT_TIME_GIFT_VIEW -> (mView as LiveRightContainer).initTimerGiftView()
            CHAT_TAB_CLICK -> (mView as LiveRightContainer).mChatView.visibility = View.VISIBLE
        }
        return null
    }

    companion object {
        private val CLEARN_MSG = 1
        private val ADD_SYS_MSG = 2
        private val SHOW_CHAT_MSG = 3
        private val INIT_TIME_GIFT_VIEW = 4
        private val CHAT_TAB_CLICK = 5

        fun clearMsg() {
            doExc(CLEARN_MSG)
        }

        fun addSysMsg() {
            doExc(ADD_SYS_MSG)
        }

        fun showChatMsg(entity: ChatMsgEntity) {
            doExc(SHOW_CHAT_MSG, entity)
        }

        fun initTimerGiftView(roomInfo: RoomInfo) {
            doExc(INIT_TIME_GIFT_VIEW, roomInfo)
        }

        fun chatTabClick() {
            doExc(CHAT_TAB_CLICK)
        }

        private fun doExc(tyep: Int, vararg params: Any) {
            TGARouter.getInstance()!!.execute(RightViewPresenter::class.java.name, RightViewEvent::class.java.name, tyep, *params)
        }
    }
}