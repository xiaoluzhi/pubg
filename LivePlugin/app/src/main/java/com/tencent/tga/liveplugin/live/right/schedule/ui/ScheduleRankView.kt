package com.tencent.tga.liveplugin.live.right.schedule.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import com.ryg.dynamicload.internal.DLPluginLayoutInflater
import com.tencent.tga.liveplugin.base.mvp.BaseView
import com.tencent.tga.liveplugin.live.right.schedule.presenter.ScheduleRankPresenter
import com.tencent.tga.plugin.R


//积分榜
class ScheduleRankView (mContext : Context,attrs : AttributeSet) : BaseView<ScheduleRankPresenter>(mContext,attrs){


    var mListView : ListView

    init {
        val view = DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.schedule_rank_detail_view,this)
        mListView = view.findViewById(R.id.mListView)
    }
    override fun getPresenter(): ScheduleRankPresenter {
        if (presenter == null){
            presenter = ScheduleRankPresenter()
        }
        return presenter
    }

}