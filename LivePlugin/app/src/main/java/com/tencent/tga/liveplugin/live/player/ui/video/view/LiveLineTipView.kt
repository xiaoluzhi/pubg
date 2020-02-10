package com.tencent.tga.liveplugin.live.player.ui.video.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ryg.dynamicload.internal.DLPluginLayoutInflater
import com.tencent.tga.liveplugin.base.util.DeviceUtils
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent
import com.tencent.tga.plugin.R

/**
 * Created by agneswang on 2020/1/13.
 */
class LiveLineTipView : LinearLayout {
    var mSwtichBtn : Button? = null
    constructor(context: Context):super(context){
        DLPluginLayoutInflater.getInstance(context).inflate(R.layout.view_line_select_tips,this)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        DLPluginLayoutInflater.getInstance(context).inflate(R.layout.view_line_select_tips,this)
        initView()
    }

    fun initView() {
        mSwtichBtn = findViewById(R.id.line_tips_switch)
    }

    fun show(parent : ViewGroup) {
        var params : FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.RIGHT or Gravity.TOP
        params.topMargin = DeviceUtils.dip2px(parent.context, 20f)
        params.rightMargin = DeviceUtils.dip2px(parent.context, 12f)
        if (parent.indexOfChild(this) == -1) parent.addView(this, params) else visibility = View.VISIBLE
        mSwtichBtn?.setOnClickListener{
            PlayViewEvent.liveLineClick()
        }
    }
}