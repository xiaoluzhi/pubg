package com.tencent.tga.liveplugin.live.right.schedule.proxy;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.matchsubscribe.CMD;
import com.tencent.protocol.tga.matchsubscribe.MatchSubscribeReq;
import com.tencent.protocol.tga.matchsubscribe.MatchSubscribeRsp;
import com.tencent.protocol.tga.matchsubscribe.SUBCMD;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.base.LivePluginConstant;
import com.tencent.tga.liveplugin.base.util.WireHelper;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;


public class MatchSubscribeProxy extends NetProxy<MatchSubscribeProxy.Param> {
    private static final String TAG ="MatchSubscribeProxy";
    public static class Param{
        //in
        public String user_id;

        //in
        public String match_id;

        public int operation_type;

        public String game_id;

        public String openid;
        public int entry_type;

        //out
        public MatchSubscribeRsp matchSubscribeRsp;
    }

    @Override
    protected int getCmd() {
        return CMD.CMD_MATCH.getValue();
    }

    @Override
    protected int getSubcmd() {
        return SUBCMD.SUBCMD_SUBSCRIBE_MATCH_OP.getValue();
    }

    @Override
    protected Request convertParamToPbReqBuf(Param param) {
        MatchSubscribeReq.Builder builder = new MatchSubscribeReq.Builder();
        builder.user_id(PBDataUtils.string2ByteString(param.user_id));
        builder.game_id(PBDataUtils.string2ByteString(param.game_id));
        builder.match_id(PBDataUtils.string2ByteString(param.match_id));
        builder.entry_type(param.entry_type);
        builder.operation_type = param.operation_type;
        builder.user_openid(PBDataUtils.string2ByteString(param.openid));
        builder.jf_gameid = PBDataUtils.string2ByteString(LivePluginConstant.JINGFEN_ID);
        byte[] payload = builder.build().toByteArray();
        // fill signature
        byte[] signature = Sessions.globalSession().access_token;
        Request request = Request.createEncryptRequest(getCmd(),
                getSubcmd(),
                payload,
                null,
                null,
                signature);
        TLog.e(TAG,"convertParamToPbReqBuf  : "+builder.toString());
        return request;
    }

    @Override
    protected int parsePbRspBuf(Message msg, Param param) {
        try {
            param.matchSubscribeRsp = WireHelper.wire().parseFrom(msg.payload, MatchSubscribeRsp.class);
            TLog.e(TAG,"parsePbRspBuf  : "+param.matchSubscribeRsp.toString());
        } catch (Exception e) {
            TLog.e(TAG,"parsePbRspBuf error : "+e.getMessage());
        }
        return 0;
    }
}
