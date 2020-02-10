package com.tencent.tga.liveplugin.live.liveView.presenter

import android.text.TextUtils
import android.view.View
import com.loopj.android.tgahttp.Configs.Configs
import com.ryg.utils.LOG
import com.tencent.protocol.tga.expressmsg.BusinessType
import com.tencent.tga.liveplugin.base.mvp.BasePresenter
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter
import com.tencent.tga.liveplugin.base.util.ToastUtil
import com.tencent.tga.liveplugin.live.LiveConfig
import com.tencent.tga.liveplugin.live.LiveInfo
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo
import com.tencent.tga.liveplugin.live.common.bean.UnityBean
import com.tencent.tga.liveplugin.live.liveView.LiveView
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent
import com.tencent.tga.liveplugin.live.liveView.modle.LiveViewModle
import com.tencent.tga.liveplugin.live.player.PlayView
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity
import com.tencent.tga.liveplugin.live.right.event.RightViewEvent
import com.tencent.tga.liveplugin.report.ReportManager
import com.tencent.tga.plugin.R
import java.util.*

/**
 * created by lionljwang
 */
class LiveViewPresenter : BasePresenter<LiveView, LiveViewModle>(), View.OnClickListener {

    override fun getModel(): LiveViewModle {
        if (modle == null)
            modle = LiveViewModle(this)
        return modle
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_icon -> {
                //按住退出键不松手，点击全屏，然后抬起退出键：这样会导致退出Activity延时10S调用onDestory,
                if (PlayView.isFullscreen()) {
                    return //全屏不退出
                }
                if (LiveConfig.mLiveContext != null)
                    LiveConfig.mLiveContext.finish()
            }
        }
    }

    override fun attach(view: LiveView) {
        super.attach(view)
        LOG.e(LiveConfig.TAG,"LiveViewPresenter attach")
        try {
            TGARouter.getInstance()!!.registerProvider(this)
            registeEvent(LiveViewEvent::class.java, view)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun deAttach() {
        super.deAttach()
        LOG.e(LiveConfig.TAG,"LiveViewPresenter deAttach")
    }


    /**
     * 上次聊天时间，客户端根据 MSG_TIME 设置 发送消息间隔
     *
     * @see
     */
    private var mLastSendMsgTime = 0L
    /***弹幕列表，为了防止弹幕重复，需要保存一个列表，收到弹幕比较当前弹幕id是否在列表，在这个列表不显示 */

    private val mRandom = Random()
    fun send(context: String?, isHotword: Boolean, hotwordId: Int) {
        if (context != null && context.length > 0) {
            if (System.currentTimeMillis() - mLastSendMsgTime > UnityBean.getmInstance().chatCd) {
                mLastSendMsgTime = System.currentTimeMillis()
                val seq = mRandom.nextInt()
                model.sendChatMsg(context, seq, if (isHotword) 1 else 0, hotwordId)
                if (isHotword) ReportManager.getInstance().report_TVWordBarrageHotWordSend(LiveInfo.mRoomId, hotwordId, if (PlayView.isFullscreen()) 1 else 0)
            } else {
                var time = UnityBean.getmInstance().chatCd / 1000 - Math.abs((System.currentTimeMillis() - mLastSendMsgTime) / 1000)
                if (time > 100 || time < 1) {
                    time = 1
                }
                ToastUtil.show(LiveConfig.mLiveContext, "发言过于频繁，请${time}秒后再试")
            }
        }
    }

    /**
     * 增加文本消息，半屏的聊天消息和全屏的弹幕消息
     *
     */
    fun addTextMsg(entity: ChatMsgEntity?) {
        if (entity == null || TextUtils.isEmpty(entity.text.trim { it <= ' ' })) {
            return
        }

        if (TextUtils.isEmpty(entity.name)) {
            entity.name = "和平精英"
        }
        addMsg(entity)
    }

    fun addMsg(entity: ChatMsgEntity) {
        if (ConfigInfo.getmInstance().getConfig(ConfigInfo.DANMA_SWITCH))
            view.mPlayView.mPlayerStateView.addMsg(entity)//danma消息处理

        if (Configs.Debug)
            LOG.e(LiveConfig.TAG, "entity.subType" + entity.subType + " entity.showType " + entity.showType)

        //聊天消息和包厢消息分类处理
        if (entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.value) {
            if (TextUtils.isEmpty(entity.roomId) || TextUtils.equals(LiveInfo.mRoomId, entity.roomId)) {
                RightViewEvent.showChatMsg(entity)
            }
        } else if (entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.value) {

            LOG.e(LiveConfig.TAG, "entity.subType" + entity.subType + " entity.showType " + entity.showType)
            RightViewEvent.showChatMsg(entity)
            if (entity.showType == 2 && entity.subType != 3 && entity.subType != 6) {
                LOG.e(LiveConfig.TAG, "entity.subType" + entity.subType + " entity.showType " + entity.showType)
            }
        }
    }
}
