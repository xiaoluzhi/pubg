package com.tencent.tga.liveplugin.live.player.modle;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.gson.Gson;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutModelInter;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.common.bean.RoomInfo;
import com.tencent.tga.liveplugin.live.common.proxy.UpdateProxy;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.live.player.presenter.PlayViewPresenter;
import com.tencent.tga.liveplugin.live.title.TitleView;
import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * created by lionljwang
 */
public class PlayViewModle extends BaseFrameLayoutModelInter {
    private static final String TAG = "PlayViewModle";

    public PlayViewModle(PlayViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected PlayViewPresenter getPresenter() {
        return presenter;
    }

    public PlayViewPresenter presenter;

    private com.tencent.tga.liveplugin.live.right.schedule.proxy.ProxyHolder mHolder = new com.tencent.tga.liveplugin.live.right.schedule.proxy.ProxyHolder();
    boolean isNotInitSwitch = true;
    public void reqCurrentMatch(final boolean isLiveLine) {
        TLog.e(TAG,"mCurrentSelection = "+ TitleView.mCurrentSelection);
        if (TitleView.mCurrentSelection != DefaultTagID.LIVE)return;

        mHolder.getRoomListProxy.postReq(LiveConfig.mLiveContext, new HttpBaseUrlWithParameterProxy.Callback() {
            @Override
            public void onSuc(int code) {
                String result = mHolder.getRoomListProxyParam.getResponse();
                if(TextUtils.isEmpty(result)){
                    return;
                }
                TLog.e(TAG,"reqCurrentMatch : "+ mHolder.getRoomListProxyParam.getResponse());

                List<ChannelInfo> mChannelList = new ArrayList<>();
                RoomInfo mDefaultRoomInfo = null;
                try {
                    JSONObject rsp = new JSONObject(result);
                    if (0 == rsp.optInt("result")) {
                        JSONArray channelList = rsp.getJSONArray("channel_list");
                        Gson mGson = new Gson();
                        for (int i = 0; i < channelList.length(); i++) {
                            ChannelInfo mChannelInfo = mGson.fromJson(channelList.getJSONObject(i).toString(), ChannelInfo.class);
                            mChannelList.add(mChannelInfo);
                        }
                        mDefaultRoomInfo = findDefaultRoom(mChannelList);
                    }
                } catch (Exception e) {
                    TLog.e(TAG, "parse room info exception");
                }
                LiveInfo.mChannelInfos = mChannelList;
                if (mChannelList.size() > 0) {
                    if (isLiveLine) {
                        PlayViewEvent.showLiveLineView(mChannelList);
                    } else {
                        for (ChannelInfo channelInfo : mChannelList) {
                            if (LiveInfo.mSourceId == 0 || mChannelList.size() == 1)
                                PlayViewEvent.updateRoomInfo(mDefaultRoomInfo, true, channelInfo.getPlay_type());
                            else {
                                for (RoomInfo roomInfo : channelInfo.getRoom_list()) {
                                    if (roomInfo != null && LiveInfo.mSourceId == roomInfo.getSourceid())//断网重连
                                        PlayViewEvent.updateRoomInfo(roomInfo, true, channelInfo.getPlay_type());
                                }
                            }
                        }
                        PlayViewEvent.setSizeAndVivibility(mChannelList);
                        if (isNotInitSwitch && hasInitConfig) {
                            isNotInitSwitch = false;
                        }
                    }
                }
            }

            @Override
            public void onFail(int errorCode) {
                TLog.d(TAG, "reqCurrentMatch 失败 " + errorCode);
            }
        }, mHolder.getRoomListProxyParam);
    }

    private RoomInfo findDefaultRoom(List<ChannelInfo> channelList) {
        try {
            for (ChannelInfo channelInfo : channelList) {
                List<RoomInfo> roomInfos = channelInfo.getRoom_list();
                for (RoomInfo roomInfo : roomInfos) {
                    if (1 == roomInfo.is_default()) {
                        return roomInfo;
                    }
                }
            }
            if (channelList.size() >= 1 && channelList.get(0).getRoom_list().size() >= 1) {
                return channelList.get(0).getRoom_list().get(0);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasInitConfig = false;
    public void init(){
        LOG.e(LiveConfig.TAG,"init hasInitConfig = "+hasInitConfig);
        if (!hasInitConfig) {
            reqHttpCofig();
        } else {
            reqCurrentMatch(false);
        }
    }

    public void reqHttpCofig() {
        hasInitConfig = true;
        UpdateProxy updateProxy = new UpdateProxy();
        final UpdateProxy.Param updateParam = new UpdateProxy.Param();
        for (int i = 0;ConfigInfo.getFunctionReqList() != null && i<ConfigInfo.getFunctionReqList().size();i++){
            updateParam.reqKeyJSONArray.put(ConfigInfo.getFunctionReqList().get(i));
        }

        updateProxy.postReq(LiveConfig.mLiveContext, new HttpBaseUrlWithParameterProxy.Callback() {
            @Override
            public void onSuc(int code) {
                try {
                    LOG.e(LiveConfig.TAG, "reqUpdate Data  = " + code);

                    JSONObject jsonObject = null;
                    try {
                        if(TextUtils.isEmpty(updateParam.response)){
                            TLog.e(LiveConfig.TAG,"updateParam.response is null");
                            return;
                        }
                        jsonObject = new JSONObject(updateParam.response);
                        LOG.e(LiveConfig.TAG, "res config data: " + jsonObject.toString());

                        JSONObject config_key = jsonObject.optJSONObject("config_key");
                        if (config_key != null) {
                            ConfigInfo.getmInstance().intData(config_key);
                        }

                        reqCurrentMatch(false);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        LOG.e(LiveConfig.TAG, "json :" + e.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFail(int errorCode) {
                LOG.e(LiveConfig.TAG, " reqUpdate " + errorCode);

            }
        }, updateParam);
        LOG.e(LiveConfig.TAG, "end reqUpdate");
    }
}
