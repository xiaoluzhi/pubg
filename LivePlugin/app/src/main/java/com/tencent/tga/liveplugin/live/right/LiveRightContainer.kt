package com.tencent.tga.liveplugin.live.right

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.ryg.dynamicload.internal.DLPluginLayoutInflater
import com.tencent.tga.liveplugin.base.mvp.BaseView
import com.tencent.tga.liveplugin.base.util.ToastUtil
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent
import com.tencent.tga.liveplugin.live.right.chat.ChatView
import com.tencent.tga.liveplugin.live.right.presenter.RightViewPresenter
import com.tencent.tga.liveplugin.live.right.schedule.SchduleWebView
import com.tencent.tga.liveplugin.live.right.schedule.SchduleWebViewDialog
import com.tencent.tga.liveplugin.live.right.schedule.ScheduleView
import com.tencent.tga.liveplugin.report.ReportManager
import com.tencent.tga.plugin.R

class LiveRightContainer(private val mContext: Context, attrs: AttributeSet):BaseView<RightViewPresenter>(mContext, attrs) {

    var mChatView: ChatView
    var mChatCtrol: ImageView
    var mScheduleCtrol: ImageView
    var mViewPage: FrameLayout

    var scheduleWebView : SchduleWebView? =null
    private var hasInitTimerGiftView = false

    var mScheduleView:ScheduleView? = null

    init {
        val view = DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.live_right_container, this)
        mChatCtrol = view.findViewById(R.id.chat_control)
        mChatCtrol.setOnClickListener(getPresenter())
        mChatCtrol.isSelected = true
        mScheduleCtrol = view.findViewById(R.id.schedule_control)
        mScheduleCtrol.setOnClickListener(getPresenter())
        mChatView = ChatView(mContext)
        mViewPage = findViewById(R.id.left_viewPage)
        mViewPage.addView(mChatView)

        mScheduleView = ScheduleView(mContext)
        mViewPage.addView(mScheduleView)
        mChatView.visibility = View.VISIBLE
        mScheduleView!!.visibility = View.GONE
    }

    override fun getPresenter(): RightViewPresenter {
        if (presenter == null) {
            presenter = RightViewPresenter()
        }
        return presenter
    }

    fun clickChat(){
        try {
            mChatCtrol.isSelected = true
            mScheduleCtrol.isSelected = false

            mChatView.visibility = View.VISIBLE
            mScheduleView!!.visibility = View.GONE
            mChatCtrol.setBackgroundResource(R.drawable.right_tab_background);
            mScheduleCtrol.background = null
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun clickSchdule() {
        try {
            ReportManager.getInstance().commonReportFun("TVUserChangeRightTab", true, "1", "2")
            if (null != mChatView.mHotWordDialog) mChatView.mHotWordDialog.dismiss()

            mChatCtrol.isSelected = false
            mScheduleCtrol.isSelected = true

            mChatView.visibility = View.GONE
            mScheduleView!!.visibility = View.VISIBLE
            mScheduleCtrol.setBackgroundResource(R.drawable.right_tab_background);
            mChatCtrol.background = null

            mScheduleView!!.initData()

            /*if (TextUtils.isEmpty(ConfigInfo.getmInstance().getStringConfig(ConfigInfo.SCHEDULE_H5_URL))) {
                ToastUtil.show(mContext, "暂无更多赛程信息，敬请期待")
                return
            }*/

            //LiveViewEvent.showSchedule()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun initTimerGiftView() {
        if (!hasInitTimerGiftView) {
            mChatView.initTimerGiftView(ConfigInfo.getmInstance())
            hasInitTimerGiftView = true
        }
    }
}