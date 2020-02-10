package com.tencent.tga.liveplugin.live.right.schedule.proxy

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy
import com.tencent.common.log.tga.TLog
import com.tencent.tga.liveplugin.base.util.HttpUtil
import java.io.UnsupportedEncodingException

/**
 * Created by agneswang on 2020/1/10.
 */
class GetRoomListProxy : HttpBaseUrlWithParameterProxy<GetRoomListProxy.Param>() {

    override fun getUrl(param: Param): String {
        val url = HttpUtil.getHttpUrl("getHpjyChannelList") + getParameter(param)

        TLog.e(TAG, "getUrl : $url")
        return url
    }

    override fun convertParamToPbReqBuf(param: Param): ByteArray? {
        val data: String = ""

        TLog.e(TAG, data)

        try {
            return data.toByteArray(charset("utf-8"))
        } catch (e: UnsupportedEncodingException) {
            return null
        }

    }

    override fun parsePbRspBuf(bytes: ByteArray, param: Param): Int {
        //解析对象
        try {
            param.response = String(bytes)
            TLog.e(TAG, "json: " + param.response!!)
        } catch (e: Throwable) {
            TLog.e(TAG, "json :" + e.message)
        }
        return 0
    }

    class Param {
        var response: String? = null
    }

    companion object {
        private val TAG = "GetRoomListProxy"
    }
}
