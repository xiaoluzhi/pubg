package com.tencent.tga.liveplugin.live.right.schedule.proxy;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.ppkdc_schedule.CMD;
import com.tencent.protocol.tga.ppkdc_schedule.SUBCMD;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;

/**
 * Created by hyqiao on 2017/4/5.
 */

public class MultiMatchScheduleListProxy extends NetProxy<MultiMatchScheduleListProxy.Param> {

    private static final String TAG ="MultiMatchScheduleListProxy";
    public static class Param{
        //in
        public String game_id;
        public String user_id;
        public int direction;
        public int page_match_day;
        public int account_type;
        //out
        //public GetMultiMatchScheduleListRsp getMultiMatchScheduleListRsp;
    }

    @Override
    protected int getCmd() {
        return CMD.CMD_PPKDC_SCHEDULE.getValue();
    }

    @Override
    protected int getSubcmd() {
        return SUBCMD.SUBCMD_GET_CHANNEL_LIST.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {

        /*GetMultiMatchScheduleListReq.Builder builder = new GetMultiMatchScheduleListReq.Builder();
        builder.user_id(PBDataUtils.string2ByteString(param.user_id));
        builder.game_id(PBDataUtils.string2ByteString(param.game_id));
        builder.direction(param.direction);
        builder.page_match_day(param.page_match_day);
        builder.account_type(param.account_type);*/

        TLog.e(TAG,"user_id : "+param.user_id);
        TLog.e(TAG,"game_id : "+param.game_id);
        TLog.e(TAG,"direction : "+param.direction);
        TLog.e(TAG,"page_match_day : "+param.page_match_day);

        byte[] payload = null;
        //byte[] payload = builder.build().toByteArray();
        // fill signature
        byte[] signature = Sessions.globalSession().access_token;
        Request request = Request.createEncryptRequest(getCmd(),
                getSubcmd(),
                payload,
                null,
                null,
                signature);
        return request;
    }

    @Override
    protected int parsePbRspBuf(Message msg, Param param) {
        try {
            //param.getMultiMatchScheduleListRsp = WireHelper.wire().parseFrom(msg.payload, GetMultiMatchScheduleListRsp.class);
        } catch (Exception e) {
            //e.printStackTrace();
            TLog.e(TAG,"getMultiMatchScheduleListRsp解析失败");
            //param.getMultiMatchScheduleListRsp = null;
        }
        return 0;
    }

}
