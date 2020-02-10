package com.tencent.tga.liveplugin.live.common.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;


public class RedPotRadioView extends RadioButton {

    private Paint paint = new Paint();

    private boolean isShowTip = false;

    private Dot mDot;

    private class Dot{
        int color;

        int radius;

        int marginTop;

        int marginRight;

        Dot() {
            float density = getContext().getResources().getDisplayMetrics().density;
            radius = (int) (3 * density);
            marginTop = (int) (0 * density);
            marginRight = (int) (0 * density);
            color = 0xFFFF0000;
        }
    }

    public RedPotRadioView(Context context) {
        super(context);
        init();
    }

    public RedPotRadioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPotRadioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDot = new Dot();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isShowTip) {
            float cx = getWidth() - mDot.marginRight - mDot.radius;
            float cy = mDot.marginTop + mDot.radius;

            int tempColor = paint.getColor();
            paint.setColor(mDot.color);
            paint.setAntiAlias(true);                       //设置画笔为无锯齿
            canvas.drawCircle(cx, cy, mDot.radius, paint);
            paint.setColor(tempColor);
        }
    }

    public void setTipOn(boolean show) {
        if (isShowTip == show)return;
        isShowTip = show;
        invalidate();
    }
}
