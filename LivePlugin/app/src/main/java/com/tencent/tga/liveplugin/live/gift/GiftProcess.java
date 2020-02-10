package com.tencent.tga.liveplugin.live.gift;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GiftProcess extends View {

    private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gift_process);
    private Paint paintBar =new Paint();

    public int per1,per2,per3,per4;
    private int left;

    public GiftProcess(Context context) {
        super(context);
    }

    public GiftProcess(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(0, 0, left, getHeight());
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, paintBar);
    }

    public void setProcess(int process){
        try {
            if (process <= per1)
                left = DeviceUtils.dip2px(getContext(), (50 * process / per1));//头部2dip，
            else if (process <= per2)
                left = DeviceUtils.dip2px(getContext(), 50 + (85 * (process - per1) / (per2 - per1)));//头部2dip+第一段32dip
            else if (process <= per3)
                left = DeviceUtils.dip2px(getContext(), 50 + 85 + (85 * (process - per2) / (per3 - per2)));
            else if (process < per4)
                left = DeviceUtils.dip2px(getContext(), 50 + 85 + 85 + (85 * (process - per3) / (per4 - per3)));
            else {
                left = DeviceUtils.dip2px(getContext(), 343);
            }
            invalidate();
        } catch (Exception e) {
            TLog.e("GiftProcess", e.getMessage());
        }
    }

}
