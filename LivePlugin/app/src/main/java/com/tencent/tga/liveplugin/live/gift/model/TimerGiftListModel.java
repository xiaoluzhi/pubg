package com.tencent.tga.liveplugin.live.gift.model;

import android.view.View;

import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutModelInter;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.gift.presenter.TimerGiftListPresenter;
import com.tencent.tga.liveplugin.live.gift.proxy.ReceiveTreasureBoxProxy;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.plugin.R;

public class TimerGiftListModel extends BaseFrameLayoutModelInter {
    private static final String TAG ="TimerGiftListModel";

    public ReceiveTreasureBoxProxy proxy = new ReceiveTreasureBoxProxy();
    public ReceiveTreasureBoxProxy.Param param = new ReceiveTreasureBoxProxy.Param();

    private TimerGiftListPresenter mPresenter;

    public TimerGiftListModel(TimerGiftListPresenter presenter){
        mPresenter = presenter;
    }

    @Override
    public TimerGiftListPresenter getPresenter() {
        return mPresenter;
    }

    public void reveive(final String boxId, final int level, final int serverTime,int type){
        param.boxid = boxId+"";
        param.server_time = serverTime;
        param.user_name = UserInfo.getInstance().mNickName;
        param.level = level;
        proxy.postReq(new NetProxy.Callback() {
            @Override
            public void onSuc(int code) {
                try {
                    if (param.rsp != null && param.rsp.result != null) {
                        TLog.e(TAG, "TimerGiftListModel param.rsp:" + param.rsp.toString());
                        TLog.e(TAG, "TimerGiftListModel reveive result" + param.rsp.result);
                        if (param.rsp.result == 0) {
                            mPresenter.getView().receiveBoxId(PBDataUtils.byteString2String(param.rsp.boxid),param.rsp.recv_box);
                            switch (type){
                                case 1:
                                    mPresenter.getView().mGift1.setBackgroundResource(R.drawable.icon_box_can_receive_bg2);
                                    mPresenter.getView().mGift1.releaseView();
                                    break;
                                case 2:
                                    mPresenter.getView().mGift2.setBackgroundResource(R.drawable.icon_box_can_receive_bg2);
                                    mPresenter.getView().mGift2.releaseView();
                                    break;
                                case 3:
                                    mPresenter.getView().mGift3.setBackgroundResource(R.drawable.icon_box_can_receive_bg);
                                    mPresenter.getView().mGift3.releaseView();
                                    break;
                                case 4:
                                    mPresenter.getView().mGift4.setBackgroundResource(R.drawable.icon_box_can_receive_bg);
                                    mPresenter.getView().mGift4.releaseView();
                                    break;
                            }
                        } else {
                            if (param.rsp.err_info != null)
                                ToastUtil.show(mPresenter.getView().getContext(), PBDataUtils.byteString2String(param.rsp.err_info));
                        }
                    }
                }catch (Exception e){
                    TLog.e(TAG,"TimerGiftListModel reveive error : "+e.getMessage());
                }

            }

            @Override
            public void onFail(int errorCode) {
                if (mPresenter !=null && mPresenter.getView() !=null && mPresenter.getView().getContext() !=null)
                    ToastUtil.show(LiveConfig.mLiveContext, "领取失败");
                LOG.e(TAG, "TimerGiftListModel reveive fail"+boxId);
            }
        }, param);
    }
}
