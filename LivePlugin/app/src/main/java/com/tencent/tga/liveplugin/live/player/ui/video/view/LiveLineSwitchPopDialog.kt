package com.tencent.tga.liveplugin.live.player.ui.video.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import com.tencent.tga.liveplugin.base.view.BaseDialog
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent
import com.tencent.tga.plugin.R

/**
 * Created by agneswang on 2020/1/13.
 */
class LiveLineSwitchPopDialog(context: Context?) : BaseDialog(context, R.style.Dialog) {
    var mCancelBtn : Button? = null
    var mSwitchBtn : Button? = null
    var mDefaultRoom : RoomInfo? = null
    var mDefaultPlayType : Int = 1

    init {
        initView()
    }

    fun initView() {
        setLayout(R.layout.popwindow_offline, null)
        window.setBackgroundDrawable(ColorDrawable(0xd900000))
        mCancelBtn = findViewById(R.id.cancel_switch_btn)
        mSwitchBtn = findViewById(R.id.switch_btn)
        mCancelBtn?.setOnClickListener{
            dismiss()
        }
        mSwitchBtn?.setOnClickListener{
            PlayViewEvent.lineChange(LiveEvent.LiveLineChange(mDefaultRoom, mDefaultPlayType))
            dismiss()
        }
    }

    fun setData(channelInfo: ChannelInfo) {
        mDefaultRoom = channelInfo.room_list.get(0)
        mDefaultPlayType = channelInfo.play_type
        show()
    }
}