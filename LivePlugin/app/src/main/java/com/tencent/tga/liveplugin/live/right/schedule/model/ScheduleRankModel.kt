package com.tencent.tga.liveplugin.live.right.schedule.model

import com.tencent.tga.liveplugin.base.mvp.BaseModelInter
import com.tencent.tga.liveplugin.live.right.schedule.presenter.ScheduleRankPresenter

class ScheduleRankModel (presenter : ScheduleRankPresenter): BaseModelInter<ScheduleRankPresenter>() {

    override fun getPresenter(): ScheduleRankPresenter {
       return this.presenter
    }
    init {
        this.presenter = presenter
    }
}