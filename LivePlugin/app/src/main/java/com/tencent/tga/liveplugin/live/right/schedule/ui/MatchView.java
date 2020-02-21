package com.tencent.tga.liveplugin.live.right.schedule.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.protocol.tga.ppkdc_schedule.MatchItem;
import com.tencent.tga.liveplugin.base.mvp.BaseView;
import com.tencent.tga.liveplugin.base.view.BaseViewInter;
import com.tencent.tga.liveplugin.live.right.schedule.ScheduleView;
import com.tencent.tga.liveplugin.live.right.schedule.bean.MatchDayInfoBean;
import com.tencent.tga.liveplugin.live.right.schedule.presenter.MatchViewPresenter;
import com.tencent.tga.plugin.R;

/**
 * Created by hyqiao on 2017/3/31.
 */

public class MatchView extends BaseView<MatchViewPresenter> implements BaseViewInter {

    MatchViewPresenter matchViewPresenter;
    public Context mContext;
    public TextView mTvMatchTime;
    public TextView mTvScore;
    public TextView mTvPlayAndSubscription;

    public Typeface mMatchViewFont;
    //public MatchItem mMatchItem;

    public MatchDayInfoBean.MatchDayListBean.MatchListBean matchListBean;

    public TextView mTvStatus;
    public ImageView mIvLeftLine;
    public TextView mTvMatchtitle;
    public TextView mTvMatchSubTitle;
    public TextView mTvTeamOrRank;//参赛队伍/积分情况
    public ImageView mIvArrow;
    public MatchView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    @Override
    protected MatchViewPresenter getPresenter() {
        if(matchViewPresenter == null)
            matchViewPresenter = new MatchViewPresenter();
        return matchViewPresenter;
    }


    public void setFont(Typeface font){
        mMatchViewFont = font;
        if(mMatchViewFont!=null){
            mTvScore.setTypeface(mMatchViewFont);
            mTvMatchTime.setTypeface(mMatchViewFont);
        }
    }

    private void initView() {
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.match_view, this);
        mTvMatchTime = (TextView) findViewById(R.id.mTvMatchTime);
        mTvScore = (TextView) findViewById(R.id.mTvScore);
        mTvPlayAndSubscription = (TextView) findViewById(R.id.mTvPlayAndSubscription);
        mTvMatchSubTitle = findViewById(R.id.mTvMatchSubTitle);
        mTvStatus = findViewById(R.id.mTvStatus);
        mIvLeftLine = findViewById(R.id.mIvLeftLine);
        mTvMatchtitle = findViewById(R.id.mTvMatchtitle);
        mTvTeamOrRank = findViewById(R.id.mTvTeamOrRank);
        mIvArrow = findViewById(R.id.mIvArrow);
        getPresenter().init();

    }

    /*public void setData(MatchItem mb){
        getPresenter().setData(mb);
    }*/
    public void setData(MatchDayInfoBean.MatchDayListBean.MatchListBean mb){
        getPresenter().setData(mb);
    }

    public ScheduleView mScheduleView;

    public void setScheduleView(ScheduleView scheduleView) {
        this.mScheduleView = scheduleView;
    }
}
