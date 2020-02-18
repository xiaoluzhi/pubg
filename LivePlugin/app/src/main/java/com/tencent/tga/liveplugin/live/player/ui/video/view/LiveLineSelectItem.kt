package com.tencent.tga.liveplugin.live.player.ui.video.view

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.ryg.dynamicload.internal.DLPluginLayoutInflater
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder
import com.tencent.tga.liveplugin.base.view.AutoAdaptGridView
import com.tencent.tga.liveplugin.live.LiveInfo
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent
import com.tencent.tga.liveplugin.report.ReportManager
import com.tencent.tga.plugin.R

/**
 * Created by agneswang on 2020/1/10.
 */
class LiveLineSelectItem : RelativeLayout {
    lateinit var mChannelImage: ImageView
    lateinit var mChannelTitle: TextView
    lateinit var mChannelTips: TextView
    lateinit var mRoomGridView : AutoAdaptGridView
    lateinit var mAdapter : CommonAdapter<RoomInfo>
    var mRoomList : List<RoomInfo> = ArrayList<RoomInfo>()
    var mPlayType : Int = 1
    constructor(context: Context):super(context){
        DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_live_channelinfo,this)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_live_channelinfo,this)
        initView()
    }


    fun initView(){
        mChannelImage = findViewById<ImageView>(R.id.live_line_icon)
        mChannelTitle = findViewById(R.id.live_line_title)
        mChannelTips = findViewById(R.id.live_line_tips)
        mRoomGridView = findViewById<AutoAdaptGridView>(R.id.live_line_roomlist)
        mRoomGridView.setOnItemClickListener { parent, view, position, id ->
            var roomInfo = mRoomList?.get(position)
            PlayViewEvent.lineChange(LiveEvent.LiveLineChange(roomInfo, mPlayType))
            ReportManager.getInstance().report_RoomClick()
        }
    }

    fun setData(channelInfo : ChannelInfo) {
        if (channelInfo == null)
            return
        mRoomList = channelInfo.room_list
        mPlayType = channelInfo.play_type
        mAdapter = object : CommonAdapter<RoomInfo>(context, mRoomList, R.layout.item_line_select_room) {
            override fun convert(holder: ViewHolder, roomInfo: RoomInfo) {
                var currentIcon = holder.getView<ImageView>(R.id.current_icon)
                var roomTitle = holder.getView<TextView>(R.id.room_title)
                holder.getView<LinearLayout>(R.id.live_line_room).setBackgroundResource(if(roomInfo.sourceid == LiveInfo.mSourceId) R.drawable.live_line_room_sel else R.drawable.live_line_room_unsel)
                currentIcon.visibility = if (roomInfo.sourceid == LiveInfo.mSourceId) View.VISIBLE else View.GONE
                roomTitle.setTextColor(if (roomInfo.sourceid == LiveInfo.mSourceId) Color.parseColor("#FFC951") else Color.WHITE)
                roomTitle.setText(roomInfo.room_title)
            }
        }

        mRoomGridView.setAdapter(mAdapter)
        mRoomGridView.invalidate()
        mAdapter.notifyDataSetChanged()

        if (!TextUtils.isEmpty(channelInfo.title)) {
            mChannelTitle?.setText(channelInfo.title)
            mChannelTitle?.setTextColor(Color.WHITE)
            for(roomInfo in channelInfo.room_list) {
                if (roomInfo.sourceid == LiveInfo.mSourceId) {
                    mChannelTitle?.setTextColor(Color.parseColor("#FFC951"))
                }
            }
        }

        if (channelInfo?.play_type == 2) {
            mChannelTips?.setText("重播")
            mChannelTips?.setBackgroundColor(Color.parseColor("#5375A5"))
        } else {
            mChannelTips?.setText("LIVE")
            mChannelTips?.setBackgroundColor(Color.parseColor("#EDB32F"))
        }

        if (!TextUtils.isEmpty(channelInfo.img)) {
            ImageLoaderUitl.loadimage(channelInfo.img, mChannelImage)
            mChannelImage?.setBackgroundResource(R.color.transparent)
            for(roomInfo in channelInfo.room_list) {
                if (roomInfo.sourceid == LiveInfo.mSourceId) {
                    mChannelImage?.setBackgroundResource(R.drawable.live_line_icon_sel)
                }
            }
        }
    }

}
