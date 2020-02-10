package com.tencent.tga.liveplugin.live.player.ui.video.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;

import com.tencent.tga.liveplugin.base.util.HexStringUtil;

import master.flame.danmaku.tga.danmaku.model.BaseDanmaku;
import master.flame.danmaku.tga.danmaku.model.android.SpannedCacheStuffer;


/**
 * Created by lionljwang on 2017/5/23.
 */
public class BackgroundCacheStuffer extends SpannedCacheStuffer {


    final Paint paint = new Paint();

    public RectF mRectF;

    public Shader mShader1 = new LinearGradient(0, 0, 1600, 0, 0xffFA9B0A, Color.TRANSPARENT, Shader.TileMode.CLAMP);

    public Shader mShader2 = new LinearGradient(0, 0, 1600, 0, 0xFF0ad3f1, Color.TRANSPARENT, Shader.TileMode.CLAMP);

    public Shader mShader5 = new LinearGradient(0, 0, 1600, 0, 0xFFf23d5b, Color.TRANSPARENT, Shader.TileMode.CLAMP);

    public Shader mShader4 = new LinearGradient(0, 0, 1600, 0, 0xFF000000, Color.TRANSPARENT, Shader.TileMode.CLAMP);

    public Shader mShader3 = new LinearGradient(0, 0, 1600, 0, 0xFF0ad3f1, Color.TRANSPARENT, Shader.TileMode.CLAMP);

    public Shader mShader6 = new LinearGradient(0, 0, 1600, 0, 0xFFf23d5b, Color.TRANSPARENT, Shader.TileMode.CLAMP);

    public int padding = 10;
    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
        danmaku.padding = 5;
        super.measure(danmaku, paint, fromWorkerThread);
    }

    @Override
    public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        try {
            paint.setAlpha(255);
            switch (danmaku.backgroudType)
            {
                case DanmaBackType.MANAGER:
                    paint.setShader(mShader1);
                    mRectF = new RectF(left, top + 2, left + danmaku.paintWidth - 2, top + danmaku.paintHeight - 2);
                    canvas.drawRoundRect(mRectF, 45, 45, paint);
                    break;
                case DanmaBackType.MANAGER_SMALL_M_GLOZE:
                case DanmaBackType.MANAGER_SMALL_W_GLOZE:
                    if (danmaku.backgroudType == DanmaBackType.MANAGER_SMALL_M_GLOZE)
                        paint.setShader(mShader2);
                    else
                        paint.setShader(mShader5);
                    mRectF = new RectF(left, top + 2, left + danmaku.paintWidth - 2, top + danmaku.paintHeight - 2);
                    canvas.drawRoundRect(mRectF, 45, 45, paint);
                    break;

                case DanmaBackType.MANAGER_BIG_M_GLOZE:
                case DanmaBackType.MANAGER_BIG_W_GLOZE:
                    paint.setShader(getShaderByColor(danmaku.color_bg,(int)(danmaku.paintWidth+2)));
                    paint.setAntiAlias(true);
                    mRectF = new RectF(left, top + 2, left + danmaku.paintWidth+2, top + danmaku.paintHeight - 2);
                    canvas.drawRoundRect(mRectF, 45, 45, paint);
                    break;
                case DanmaBackType.MANAGER_CHAT_BOX:
                    //paint.setShader(mShader4);
                    paint.setShader(getShader(0xFF000000,(int)(danmaku.paintWidth+2)));
                    mRectF = new RectF(left, top + 2, left + danmaku.paintWidth - 2, top + danmaku.paintHeight - 2);
                    canvas.drawRoundRect(mRectF, 45, 45, paint);
                    break;
                case DanmaBackType.KOL_GLOZE:
                    paint.setShader(getShaderByColor(danmaku.color_bg,(int)(danmaku.paintWidth+2)));
                    paint.setAntiAlias(true);
                    mRectF = new RectF(left, top + 2, left + danmaku.paintWidth+2, top + danmaku.paintHeight - 2);
                    canvas.drawRoundRect(mRectF, 45, 45, paint);
                    break;
                default:
                    paint.setColor(Color.TRANSPARENT);
                    break;
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
    }

    private Shader getShaderByColor(String str_color,int width){
        try {
            return new LinearGradient(0, 0, width, 0, HexStringUtil.hexString2colorInt(str_color), Color.TRANSPARENT, Shader.TileMode.CLAMP);
        }catch (Exception e){
            return new LinearGradient(0, 0, width, 0, 0xFF000000, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        }
    }

    private Shader getShader(int color,int width){
        try {
            return new LinearGradient(0, 0, width, 0, color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        }catch (Exception e){
            return new LinearGradient(0, 0, width, 0, 0xFF000000, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        }
    }
}
