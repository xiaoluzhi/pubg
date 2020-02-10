package com.tencent.tga.liveplugin.live.gift;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.gift.bean.GiftItemBean;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GiftFloatTipPopWindowView extends RelativeLayout {

    private static String TAG = "GiftFloatTipPopWindowView";

    private Context mContext;
    private ImageView mTvGiftFloatIcon;
    private TextView mTvGiftFloatName;
    private TextView mTvGiftFloatTips;

    public GiftFloatTipPopWindowView(Context context) {
        super(context);

        mContext = context;
        init();
    }

    private void init() {
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.gift_prize_info_view, this);
        mTvGiftFloatIcon = findViewById(R.id.mTvGiftFloatIcon);
        mTvGiftFloatName = findViewById(R.id.mTvGiftFloatName);
        mTvGiftFloatTips = findViewById(R.id.mTvGiftFloatTips);
        if (null != LiveConfig.mFont) {
            mTvGiftFloatName.setTypeface(LiveConfig.mFont);
            mTvGiftFloatTips.setTypeface(LiveConfig.mFont);
        }
    }

    public void setData(GiftItemBean giftItemBean){
        try {
            ImageLoaderUitl.loadimage(giftItemBean.thumb, mTvGiftFloatIcon);
            mTvGiftFloatName.setText(giftItemBean.name);
            mTvGiftFloatTips.setText(giftItemBean.tip);
        }catch (Exception e){
            TLog.e(TAG,"setData error : "+e.getMessage());
        }
    }
}
