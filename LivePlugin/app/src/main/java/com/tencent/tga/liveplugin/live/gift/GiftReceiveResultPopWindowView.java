package com.tencent.tga.liveplugin.live.gift;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.hpjy_treasure.ReceiveItem;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

public class GiftReceiveResultPopWindowView extends RelativeLayout {

    private static String TAG = "GiftReceiveResultPopWindowView";

    private Context mContext;
    private LinearLayout mLlyReceiveGiftContent;
    private Button mBtnReceiveGiftSure;
    private OnClickListener onClickListener;

    public GiftReceiveResultPopWindowView(Context context) {
        super(context);

        mContext = context;
        init();
    }

    private void init() {
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.gift_receive_info_view, this);
        mLlyReceiveGiftContent = findViewById(R.id.mLlyReceiveGiftContent);
        mBtnReceiveGiftSure = findViewById(R.id.mBtnReceiveGiftSure);
    }

    public void setData(List<ReceiveItem> recv_box) {
        try {
            for (int i = 0;recv_box != null && i<recv_box.size() && i<3;i++){
                GiftReceiveItemView giftReceiveItemView = new GiftReceiveItemView(mContext);
                giftReceiveItemView.setData(recv_box.get(i));
                mLlyReceiveGiftContent.addView(giftReceiveItemView);
            }
        } catch (Exception e) {
            TLog.e(TAG, "setData error : " + e.getMessage());
        }
    }

    public void setOnCloseListener(OnClickListener listener){
        this.onClickListener = listener;
        if(onClickListener != null) {
            mBtnReceiveGiftSure.setOnClickListener(onClickListener);
//            findViewById(R.id.gift_list_close).setOnClickListener(onClickListener);
        }
    }
}
