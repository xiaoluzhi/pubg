package com.tencent.tga.liveplugin.live.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by agneswang on 2017/4/5.
 */

@SuppressLint("AppCompatCustomView")
public class RedPotView extends ImageView{

    private Paint paint = new Paint();

    private boolean isShowTip = false;

    private Dot mDot;

    private class Dot{
        int color;

        int radius;

        int marginTop;

        int marginRight;

        float density;
        Dot() {
            density = getContext().getResources().getDisplayMetrics().density;
            radius = (int) (3 * density);
            marginTop = (int) (0 * density);
            marginRight = (int) (0 * density);
            color = 0xFFFF0000;
        }

        public void setMargin(int margintop,int marginright){
            this.marginTop = (int) (margintop * density);
            this.marginRight = (int) (marginright * density);
        }
    }

    public RedPotView(Context context) {
        super(context);
        init();
    }

    public RedPotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPotView(Context context, AttributeSet attrs, int defStyleAttr) {
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

            canvas.drawCircle(cx, cy, mDot.radius, paint);
            paint.setColor(tempColor);
        }
    }

    public void setTipOn(boolean show) {
        isShowTip = show;
        invalidate();
    }

    public void setRedPotMargin(int margintop,int marginright){
        mDot.setMargin(margintop,marginright);
    }

    public void setRadius(int radius) {
        mDot.radius = (int)(radius * mDot.density);
    }
}
