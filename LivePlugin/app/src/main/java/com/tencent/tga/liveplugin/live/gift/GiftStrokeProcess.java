package com.tencent.tga.liveplugin.live.gift;

import com.tencent.tga.plugin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GiftStrokeProcess extends View {

    private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gift_process_full);
    private Paint paintBar =new Paint();
    public GiftStrokeProcess(Context context) {
        super(context);
    }

    public GiftStrokeProcess(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap!=null && !mBitmap.isRecycled())
            canvas.drawBitmap(mBitmap, 0.0f, 0.0f, paintBar);
    }

}
