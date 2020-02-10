package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.ppkdc_schedule.MatchItem;
import com.tencent.tga.liveplugin.base.mvp.BaseView;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.view.BaseViewInter;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.right.schedule.bean.MatchCategoryBean;
import com.tencent.tga.liveplugin.live.right.schedule.presenter.SchedulePresenter;
import com.tencent.tga.liveplugin.live.right.schedule.ui.DataErrorView;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

/**
 * Created by lionljwang on 2017/3/30.
 */
public class ScheduleView extends BaseView<SchedulePresenter>{

    private final static String TAG = "ScheduleView";
    SchedulePresenter schedulePresenter;
    public PullToRefreshListView mLvMatch;

    public ArrayList<MatchCategoryBean> mList;
    public CommonAdapter mAdapter;

    public Context mContext;
    public DataErrorView errorDataView;//异常UI

    public RelativeLayout mRlyGoToday;//回到今日
    public ImageView mIvRankEntranceIcon;
    public TextView mTvRankEntranceName;
    public RelativeLayout mRlyMatchRank;//积分榜
    public ImageView mIvRankEnteranceIcon;
    public ImageView mIvMatchRankTopBg;

    public LinearLayout mLlyPullDownTip;
    public ImageView mIvPullDownTip;
    public TextView mTvPullDownTip;

    public ScheduleView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    protected SchedulePresenter getPresenter() {
        if (schedulePresenter == null)
            schedulePresenter = new SchedulePresenter();
        return schedulePresenter;
    }


    private void initView() {
        DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.mvp_schedule_view, this);
        mLvMatch = (PullToRefreshListView) findViewById(R.id.mLvMatch);
        mRlyGoToday = (RelativeLayout) findViewById(R.id.mRlyGoToday);
        mRlyMatchRank = (RelativeLayout) findViewById(R.id.mRlyMatchRank);
        mIvRankEnteranceIcon = (ImageView) findViewById(R.id.mIvRankEnteranceIcon);
        mIvRankEntranceIcon = (ImageView) findViewById(R.id.mIvRankEntranceIcon);
        mTvRankEntranceName = (TextView) findViewById(R.id.mTvRankEntranceName);
        mIvMatchRankTopBg = (ImageView) findViewById(R.id.mIvMatchRankTopBg);
        if(LiveConfig.mFont != null){
            mTvRankEntranceName.setTypeface(LiveConfig.mFont);
        }
        mLlyPullDownTip = (LinearLayout) findViewById(R.id.mLlyPullDownTip);
        mIvPullDownTip = (ImageView) findViewById(R.id.mIvPullDownTip);
        mTvPullDownTip = (TextView) findViewById(R.id.mTvPullDownTip);

        //getPresenter().init();
    }

    //初始化数据，第一次进入的时候拉数据
    public boolean isFirst = true;
    public void initData(){
        if(isFirst){
            getPresenter().init();
            isFirst = false;
        }
    }

    public static ScheduleView getInstance(Context context) {
        ScheduleView view = (ScheduleView)  DLPluginLayoutInflater.getInstance(context).inflate(R.layout.mvp_schedule_view, null);
        view.initView();
        return view;
    }

    public void updateData(MatchItem matchItem){
        getPresenter().updateData(matchItem);
    }

    public void updateData(int sourceId){
        getPresenter().updateData(sourceId);
    }

    public void updateData(String matchID,int subscribe_state){
        getPresenter().updateData(matchID,subscribe_state);
    }

    public void onCreate() {
        TLog.d(TAG, "onCreate");
    }

    public void onStart(boolean isReal) {
        TLog.d(TAG,"onStart");
    }

    public void onDestroy() {

        /*if(SubscribeDialog.dialog_builder != null){
            SubscribeDialog.dialog_builder.releaseDialog();
        }*/
    }
}
