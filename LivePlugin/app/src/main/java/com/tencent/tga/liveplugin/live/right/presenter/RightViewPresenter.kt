package com.tencent.tga.liveplugin.live.right.presenter

import android.view.View
import com.ryg.utils.LOG
import com.tencent.tga.liveplugin.base.mvp.BasePresenter
import com.tencent.tga.liveplugin.base.routerCenter.TGARouter
import com.tencent.tga.liveplugin.live.LiveConfig
import com.tencent.tga.liveplugin.live.right.LiveRightContainer
import com.tencent.tga.liveplugin.live.right.event.RightViewEvent
import com.tencent.tga.liveplugin.live.right.modle.RightViewModle
import com.tencent.tga.plugin.R

/**
 * created by lionljwang
 */
class RightViewPresenter : BasePresenter<LiveRightContainer, RightViewModle>(), View.OnClickListener {
    override fun getModel(): RightViewModle {
        if (modle == null)
            modle = RightViewModle(this)
        return modle
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.chat_control -> view?.clickChat()
            R.id.schedule_control -> view?.clickSchdule()
        }
    }

    override fun attach(view: LiveRightContainer) {
        super.attach(view)
        try {
            TGARouter.getInstance()!!.registerProvider(this)
            registeEvent(RightViewEvent::class.java, view)
            LOG.e(LiveConfig.TAG,"RightViewPresenter attach......")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
