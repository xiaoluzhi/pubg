package com.tencent.tga.liveplugin.live.gift.model;


import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutModelInter;
import com.tencent.tga.liveplugin.live.gift.presenter.TimerGiftPresenter;

public class TimerGiftModel extends BaseFrameLayoutModelInter {

    public TimerGiftPresenter presenter;

    public TimerGiftModel(TimerGiftPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected TimerGiftPresenter getPresenter() {
        return null;
    }
}
