package com.tencent.tga.liveplugin.live.gift;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.hpjy_treasure.ReceiveItem;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GiftReceiveItemView extends RelativeLayout {

    private static String TAG = "GiftReceiveItemView";

    private Context mContext;
    private ImageView mIvGiftReceiveIcon;
    private TextView mTvIvGiftReceiveName;

    public GiftReceiveItemView(Context context) {
        super(context);

        mContext = context;
        init();
    }

    private void init() {
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.gift_receive_item_view, this);
        mIvGiftReceiveIcon = findViewById(R.id.mIvGiftReceiveIcon);
        mTvIvGiftReceiveName = findViewById(R.id.mTvIvGiftReceiveName);
        if (null != LiveConfig.mFont) mTvIvGiftReceiveName.setTypeface(LiveConfig.mFont);
    }

    public void setData(ReceiveItem receiveItem) {
        try {
            ImageLoaderUitl.loadimage(PBDataUtils.byteString2String(receiveItem.pic),mIvGiftReceiveIcon);
            mTvIvGiftReceiveName.setText(PBDataUtils.byteString2String(receiveItem.tip));
        } catch (Exception e) {
            TLog.e(TAG, "setData error : " + e.getMessage());
        }
    }
}
