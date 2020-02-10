package com.tencent.tga.liveplugin.live.common.bean

/**
 * Created by agneswang on 2020/1/10.
 */
data class ChannelInfo(var play_type : Int, var title : String, var img : String, var room_list : ArrayList<RoomInfo>)