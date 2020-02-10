package com.tencent.tga.liveplugin.live.liveView.modle

import android.text.TextUtils
import com.loopj.android.tgahttp.Configs.Configs
import com.ryg.utils.LOG
import com.tencent.common.log.tga.TLog
import com.tencent.protocol.tga.expressmsg.BusinessType
import com.tencent.tga.liveplugin.base.mvp.BaseModelInter
import com.tencent.tga.liveplugin.base.util.ToastUtil
import com.tencent.tga.liveplugin.live.LiveConfig
import com.tencent.tga.liveplugin.live.LiveInfo
import com.tencent.tga.liveplugin.live.common.bean.UnityBean
import com.tencent.tga.liveplugin.live.liveView.presenter.LiveViewPresenter
import com.tencent.tga.liveplugin.live.player.proxy.ChatProxy
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity
import com.tencent.tga.liveplugin.networkutil.NetProxy
import com.tencent.tga.liveplugin.networkutil.PBDataUtils

/**
 * created by lionljwang
 */
class LiveViewModle (presenter: LiveViewPresenter) : BaseModelInter<LiveViewPresenter>() {

    override fun getPresenter(): LiveViewPresenter {
        return presenter
    }
    init {
        this.presenter = presenter
    }

    /**
     * send chat msg
     */
    fun sendChatMsg(text: String, seq: Int, type: Int, hotwordId: Int) {
        if (TextUtils.isEmpty(LiveInfo.mRoomId)) {
            return
        }
        var chatProxyProxy = ChatProxy()
        var chatProxyParam= ChatProxy.Param(LiveInfo.mRoomId, text, seq)
        chatProxyParam.nick = UnityBean.getmInstance().nikeName
        chatProxyParam.msgType = type
        chatProxyParam.hotwordId = hotwordId
        chatProxyProxy.postReqWithOutRepeat(object : NetProxy.Callback {
            override fun onSuc(code: Int) {

                if (chatProxyParam.chatRsp != null && (chatProxyParam.chatRsp.result == 3 ||chatProxyParam.chatRsp.result == 4))
                //±»½ûÑÔ
                {
                    val errMsg = PBDataUtils.byteString2String(chatProxyParam.chatRsp.errmsg)
                    if (!TextUtils.isEmpty(errMsg)) {
                        ToastUtil.show(LiveConfig.mLiveContext, errMsg)
                    }

                } else if (chatProxyParam.chatRsp != null && chatProxyParam.chatRsp.result == 0) {

                    val chatMsgEntity = ChatMsgEntity()

                    chatMsgEntity.roomId = LiveInfo.mRoomId
                    chatMsgEntity.name = chatProxyParam.nick
                    chatMsgEntity.text = chatProxyParam.text
                    chatMsgEntity.isSel = true
                    chatMsgEntity.msgType = BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.value
                    chatMsgEntity.bageId = ""

                    presenter.addTextMsg(chatMsgEntity)
                }
                if (Configs.Debug)
                    LOG.e(LiveConfig.TAG, "sendChatMsg code $code ischatMsg")
            }

            override fun onFail(errorCode: Int) {
                if (Configs.Debug)
                    TLog.d(LiveConfig.TAG, "sendChatMsg Ê§°Ü $errorCode")
            }
        }, chatProxyParam)
    }
}
