package com.tencent.tga.liveplugin.live.common.bean

/**
 * Created by agneswang on 2020/1/10.
 */
data class RoomInfo(var roomid : String, var vid : String, var sourceid : Int, var online_num : Int, var live_title : String,
                    var room_title : String, var is_default : Int)