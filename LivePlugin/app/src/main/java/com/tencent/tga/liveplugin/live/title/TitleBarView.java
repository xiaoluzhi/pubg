package com.tencent.tga.liveplugin.live.title;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.title.bean.TitleTagBean;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by hyqiao on 2017/5/15.
 */

public class TitleBarView extends RelativeLayout {
    Context mContext;
    private ImageView mIcon,mIcon_bg;
    private ImageView title;

    public TitleBarView(Context context) {
        super(context);
        initView(context);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.titlebar_item_layout, this);
        mIcon = (ImageView) findViewById(R.id.icon);
        mIcon_bg = (ImageView) findViewById(R.id.icon_bg);
        title = findViewById(R.id.title);

    }

    public void setData(TitleTagBean tb){
        if(tb != null){
            //设置TAG是否隐藏
            setViewVisible(tb.isTagVisible());
            if ("default".equals(tb.getIconUrl())) {
                mIcon.setImageResource(R.drawable.toptag_live_selected);
                title.setImageResource(R.drawable.live);
            } else {
                ImageLoaderUitl.loadimage(tb.getIconUrl(), mIcon);
                ImageLoaderUitl.loadimage(tb.getName(), title);
            }

        }
    }

    private void setViewVisible(boolean isVisible){
        if(isVisible){
            setVisibility(VISIBLE);
        }else {
            setVisibility(GONE);
        }
    }

    public void setIconSelect(boolean isSelect){
        if(mIcon == null){
            TLog.e("TitleBarView","mIcon is null");
            return;
        }

        mIcon.setSelected(isSelect);
        mIcon_bg.setSelected(isSelect);
        title.setSelected(isSelect);
    }
}
