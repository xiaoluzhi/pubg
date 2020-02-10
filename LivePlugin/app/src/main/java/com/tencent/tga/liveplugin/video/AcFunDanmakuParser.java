package com.tencent.tga.liveplugin.video;

import android.graphics.Color;
import android.util.DisplayMetrics;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;

import org.json.JSONArray;
import org.json.JSONObject;

import master.flame.danmaku.tga.danmaku.model.BaseDanmaku;
import master.flame.danmaku.tga.danmaku.model.android.Danmakus;
import master.flame.danmaku.tga.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.tga.danmaku.parser.android.JSONSource;


/**
 * Created by agneswang on 2019/6/18.
 */
public class AcFunDanmakuParser extends BaseDanmakuParser {

    @Override
    public Danmakus parse() {
        TLog.e("AcFunDanmakuParser", "mDataSource is " + mDataSource);
        TLog.e("AcFunDanmakuParser", "mDataSource instanceof JSONSource is " + (mDataSource instanceof JSONSource));
        if (mDataSource != null && mDataSource instanceof JSONSource) {
            JSONSource jsonSource = (JSONSource) mDataSource;
            TLog.e("AcFunDanmakuParser", "jsonSource is " + jsonSource.data());
            return doParse(jsonSource.data());
        }
        return new Danmakus();
    }

    /**
     * @param danmakuListData 弹幕数据
     *                        传入的数组内包含普通弹幕，会员弹幕，锁定弹幕。
     * @return 转换后的Danmakus
     */
    private Danmakus doParse(JSONArray danmakuListData) {
        Danmakus danmakus = new Danmakus();
        TLog.e("AcFunDanmakuParser", "danmakuListData is " + danmakuListData);
        if (danmakuListData == null || danmakuListData.length() == 0) {
            return danmakus;
        }
        danmakus = _parse(danmakuListData, danmakus);
        return danmakus;
    }

    private Danmakus _parse(JSONArray jsonArray, Danmakus danmakus) {
        TLog.e("AcFunDanmakuParser", "jsonArray is " + jsonArray);
        if (danmakus == null) {
            danmakus = new Danmakus();
        }
        if (jsonArray == null || jsonArray.length() == 0) {
            return danmakus;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                String msg = obj.getString("msg");
                int type = 1; // 弹幕类型
                long time = obj.optLong("offset_ms"); // 出现时间
                int color = 0xFFFFFFFF; // 颜色
                DisplayMetrics dm = LiveConfig.mLiveContext.getResources().getDisplayMetrics();
                float dens = dm.densityDpi / 160.0f - 0.6f;
                int mDanmuSize = (int)(LiveShareUitl.getVideoDanmuSize(LiveConfig.mLiveContext)*dens) +(int) (2*dens);
                BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(type, mContext);
                if (item != null) {
                    item.setTime(time);
                    item.textSize = mDanmuSize;
                    item.textColor = color;
                    item.textShadowColor = color <= Color.BLACK ? Color.WHITE : Color.BLACK;
                    item.index = i;
                    item.flags = mContext.mGlobalFlagValues;
                    item.alpha = (LiveShareUitl.getVideoDanmuAlpha(LiveConfig.mLiveContext)+109)*255/364;
                    item.setTimer(mTimer);
                    item.text = msg;
                    danmakus.addItem(item);
                }
            } catch (Exception e) {
                TLog.e("AcFunDanmakuParser", e.getMessage());
            }
        }
        return danmakus;
    }
}

