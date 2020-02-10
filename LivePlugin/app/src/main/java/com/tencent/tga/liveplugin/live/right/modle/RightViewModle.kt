package com.tencent.tga.liveplugin.live.right.modle

import com.tencent.tga.liveplugin.base.mvp.BaseModelInter
import com.tencent.tga.liveplugin.live.right.presenter.RightViewPresenter

class RightViewModle( presenter: RightViewPresenter) : BaseModelInter<RightViewPresenter>() {
    override fun getPresenter(): RightViewPresenter {
        return presenter
    }
    init {
        this.presenter = presenter
    }


}