package com.tencent.tga.liveplugin.live.right.schedule.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.base.util.TimeUtils;
import com.tencent.tga.plugin.R;

/**
 * Created by hyqiao on 2017/3/31.
 */

public class MatchDateView extends RelativeLayout {
    private Context mContext;
    private TextView mMatchDate;
    private Typeface mMatchDateViewFont;

    public MatchDateView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MatchDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MatchDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void setFont(Typeface font){
        mMatchDateViewFont = font;
        if(mMatchDate!=null&&mMatchDateViewFont!=null){
            mMatchDate.setTypeface(mMatchDateViewFont);
        }
    }

    private void initView() {
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.match_date_view, this);

        mMatchDate = (TextView) findViewById(R.id.mMatchDate);
    }

    public void setData(int date){
        mMatchDate.setText(TimeUtils.getMatchDateWithWeek(date*1000L));
    }
}
