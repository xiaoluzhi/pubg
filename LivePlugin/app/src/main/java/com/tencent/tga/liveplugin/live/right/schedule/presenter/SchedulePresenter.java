package com.tencent.tga.liveplugin.live.right.schedule.presenter;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.ppkdc_schedule.MatchItem;
import com.tencent.tga.gson.Gson;
import com.tencent.tga.liveplugin.base.mvp.BasePresenter;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.ThreadPoolManager;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.right.schedule.ScheduleView;
import com.tencent.tga.liveplugin.live.right.schedule.ScoreRankWebView;
import com.tencent.tga.liveplugin.live.right.schedule.bean.MatchCategoryBean;
import com.tencent.tga.liveplugin.live.right.schedule.bean.MatchDayInfoBean;
import com.tencent.tga.liveplugin.live.right.schedule.model.ScheduleModel;
import com.tencent.tga.liveplugin.live.right.schedule.ui.DataErrorView;
import com.tencent.tga.liveplugin.live.right.schedule.ui.MatchDateView;
import com.tencent.tga.liveplugin.live.right.schedule.ui.MatchView;
import com.tencent.tga.liveplugin.networkutil.NetProxy;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lionljwang on 2017/3/30.
 */
public class SchedulePresenter extends BasePresenter<ScheduleView,ScheduleModel> {

    private final static String TAG = "SchedulePresenter";
    ScheduleModel mChatModel;

    private boolean isFinishBottomLoadMore = false;
    private boolean isFinishTopLoadMore = false;

    private int mCurrentDate = 0;//当前天获取到的赛程日期，返回今日赛程使用
    private int current_position = -1;

    private ScheduleView scheduleView;

    @Override
    public ScheduleModel getModel() {
        if (mChatModel == null)
            mChatModel = new ScheduleModel();
        return mChatModel;
    }

    public void init(){

        initAdapter();

        //initData();

        getMatchList(0,ScheduleModel.DEFAULT_VALUE);

        initPullDownTips();

        initRankEntrance();

        scheduleView = getView();

    }


    public synchronized void getMatchList(final int date, final int direction){
        getModel().reqMatchList(getView().mContext,new HttpBaseUrlWithParameterProxy.Callback() {
            @Override
            public void onSuc(int code) {
                TLog.e(TAG, "getMatchList onSuc : " + code);
                try {
                    getView().mLvMatch.onRefreshComplete();
                    if (getModel().mProxyHolder.getHpjyScheduleListHttpProxyParam.data != null) {
                        MatchDayInfoBean matchDayInfoBean = new Gson().fromJson(getModel().mProxyHolder.getHpjyScheduleListHttpProxyParam.data,MatchDayInfoBean.class);
                        if (matchDayInfoBean!=null){
                            if (matchDayInfoBean.getIs_finish().equals("1")) {
                                if (direction == ScheduleModel.FORWARD) {
                                    isFinishTopLoadMore = true;
                                } else if (direction == ScheduleModel.BACKWARD) {
                                    isFinishBottomLoadMore = true;
                                }
                            }
                            if (matchDayInfoBean.getMatch_day_list().size()!= 0) {

                                if (date == 0) {
                                    mCurrentDate = matchDayInfoBean.getMatch_day_list().get(0).getMatch_day_time();
                                }

                                AddDataToList(matchDayInfoBean.getMatch_day_list(), getModel().mProxyHolder.getHpjyScheduleListHttpProxyParam.direction);

                                TLog.e(TAG, "getMatchList : " + getModel().mProxyHolder.getHpjyScheduleListHttpProxyParam.data);
                            }
                        } else {
                            setEmptyDataView(true, DataErrorView.ERROR_MSG_EMPTY);
                        }
                    } else {
                        setEmptyDataView(true, DataErrorView.ERROR_MSG_EMPTY);
                    }
                    getView().mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    TLog.e(TAG, "getMatchList error : " + e.getMessage());
                }
            }

            @Override
            public void onFail(int errorCode) {
                //getView().setText("ddd");
                TLog.e(TAG,"getMatchList onFail : "+errorCode);
                try {
                    if(errorCode == NetProxy.Callback.ERROR_CODE_UNREACHABLENET){
                        ToastUtil.show(getView().mContext,"网络异常，请检测网络");
                    }
                    getView().mLvMatch.onRefreshComplete();
                    setEmptyDataView(false,DataErrorView.ERROR_MSG_LOADFAIL);
                }catch (Exception e){
                    TLog.e(TAG,"getMatchList onFail error : "+e.getMessage());
                }
            }
        },date,direction);
    }

    private synchronized void AddDataToList(List<MatchDayInfoBean.MatchDayListBean> list, int direction){
        try {
            List<MatchCategoryBean> list_temp = new ArrayList<>();
            for(int i = 0;i<list.size();i++){
                if(isExist(list.get(i).getMatch_day_time())){
                    continue;
                }
                list_temp.add(new MatchCategoryBean(list.get(i).getMatch_day_time(),true));
                for(int j = 0;list.get(i).getMatch_list() != null && j<list.get(i).getMatch_list().size();j++){
                    list_temp.add(new MatchCategoryBean(list.get(i).getMatch_list().get(j)));
                }
            }
            if(direction == ScheduleModel.BACKWARD){
                getView().mList.addAll(list_temp);
            }else {
                getView().mList.addAll(0,list_temp);
            }
            getView().mAdapter.notifyDataSetChanged();

            isShowGoToday();//获取数据后判断一次是否需要回到今日赛程

        }catch (Exception e){
            TLog.e(TAG,"AddDataToList error : "+e.getMessage());
        }
    }

    private boolean isExist(int date){
        if(getView().mList != null){
            for(int i = 0;i<getView().mList.size();i++){
                if(getView().mList.get(i).getDate() == date){
                    return true;
                }
            }
        }
        return false;
    }


    private void initAdapter() {
        getView().mList = new ArrayList<>();
        getView().mAdapter = new CommonAdapter<MatchCategoryBean>(getView().mContext,getView().mList, R.layout.list_item) {
            @Override
            public void convert(ViewHolder holder, MatchCategoryBean matchBean) {
                FrameLayout viewgroup =  holder.getView(R.id.root);
                try{
                    viewgroup.removeAllViews();
                    if(matchBean.isDate){
                        MatchDateView mdv = new MatchDateView(mContext);
                        mdv.setFont(LiveConfig.mFont);
                        mdv.setData(matchBean.getDate());
                        //viewgroup.addView(tv,DeviceUtils.dip2px(mContext,90),DeviceUtils.dip2px(mContext,16));
                        viewgroup.addView(mdv);
                    }else {
                        //MatchCategoryView mcv = new MatchCategoryView(mContext);
                        //mcv.setFont(LiveConfig.mFont);
                        //mcv.setPadding(30,0,0,0);
                        //mcv.setData(matchBean);
                        MatchView matchView = new MatchView(mContext);
                        matchView.setScheduleView(scheduleView);
                        matchView.setData(matchBean.getMatchListBean());
                        viewgroup.addView(matchView);
                    }
                }catch (Exception e){
                    TLog.e(TAG,"initAdapter error : "+e.getMessage());
                }
            }
        };

        getView().mLvMatch.setMode(PullToRefreshBase.Mode.BOTH);
        getView().mLvMatch.setAdapter(getView().mAdapter);


        getView().mLvMatch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                TLog.e(TAG,"onPullDownToRefresh......");
                getView().mLvMatch.onRefreshComplete();

                hidePullDownTips();

                if(isFinishTopLoadMore){
                    ToastUtil.show(getView().mContext,"已经到顶部");
                    return;
                }
                if(getView().mList != null && getView().mList.size() == 0){
                    getMatchList(0,ScheduleModel.DEFAULT_VALUE);
                }else {
                    getMatchList(getForwardMatchDate(),ScheduleModel.FORWARD);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                TLog.e(TAG,"onPullUpToRefresh......");
                getView().mLvMatch.onRefreshComplete();

                hidePullDownTips();

                if(isFinishBottomLoadMore){
                    ToastUtil.show(getView().mContext,"已经到底部");
                    return;
                }
                if(getView().mList != null && getView().mList.size() == 0){
                    getMatchList(0,ScheduleModel.DEFAULT_VALUE);
                }else {
                    getMatchList(getBackwardMatchDate(),ScheduleModel.BACKWARD);
                }
            }
        });

        getView().mLvMatch.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int SCROLL_STATE) {
                if( SCROLL_STATE == SCROLL_STATE_IDLE){
                    TLog.e(TAG,"onScrollStateChanged end");
                    isShowGoToday();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {

                }
            }
        });

        getView().mRlyGoToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    getView().mRlyGoToday.setVisibility(View.GONE);
                    //smoothScrollToPosition不用这个滚动方法，否则从下往上滚动时，会定位在可视底部
                    getView().mLvMatch.getRefreshableView().setSelection(current_position);
                    //该方法可以用来显示回放
                    //getView().mLvMatch.getRefreshableView().smoothScrollBy(-400,100);
                    current_position = -1;
                    ReportManager.getInstance().commonReportFun("ScheduleReturnClick",true, ReportManager.getInstance().getReportFormatString());
                }catch (Exception e){
                    getView().mRlyGoToday.setVisibility(View.GONE);
                    current_position = -1;
                    TLog.e(TAG,"mBtnGoToday click error : "+e.getMessage());
                }
            }
        });

        getView().mRlyMatchRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NoDoubleClickUtils.isDoubleClick()){
                    return;
                }
                try {
                    LiveViewEvent.Companion.showRankWebView();
                    //hideRankEntranceRedPot();
                }catch (Exception e){
                    TLog.e(TAG," error : "+e.getMessage());
                }
            }
        });
    }

    /**
    * 是否显示返回今天赛程
    * @author hyqiao
    * @time 2017/4/12 11:52
    */
    private void isShowGoToday(){
        try{
            int first_position = getView().mLvMatch.getRefreshableView().getFirstVisiblePosition();
            int end_position = getView().mLvMatch.getRefreshableView().getLastVisiblePosition();

            for(int i = 0;getView().mList != null && i<getView().mList.size();i++){
                if(getView().mList.get(i).isDate && getView().mList.get(i).getDate() == mCurrentDate){
                    current_position = i;
                    break;
                }
            }

            //首次拉取数据，页面更新时 first_position = -1 、end_position = -1
            if(current_position != -1 && first_position != -1 && end_position != -1){
                if(current_position < first_position || current_position > end_position){
                    getView().mRlyGoToday.setVisibility(View.VISIBLE);
                    if(getView().mRlyMatchRank.getVisibility() == View.GONE){
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getView().mRlyGoToday.getLayoutParams();
                        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        getView().mRlyGoToday.setLayoutParams(lp);
                    }
                }else {
                    getView().mRlyGoToday.setVisibility(View.GONE);
                }
            }
        }catch (Exception e){
            TLog.e(TAG,"isShowGoToday error : "+e.getMessage());
        }

    }

    private AnimationDrawable animationDrawable;
    private int mPullDownTipNum = 0;
    private int mPullDownTipNumLimit = 5;

    public void initPullDownTips(){
        try {
           /* mPullDownTipNum = LiveShareUitl.getPullDownTip(getView().mContext);
            if(mPullDownTipNum < mPullDownTipNumLimit && NetUtils.isNetworkAvailable(getView().mContext)){
                LiveShareUitl.savePullDownTip(getView().mContext,mPullDownTipNum+1);
                getView().mLlyPullDownTip.setVisibility(View.VISIBLE);
                if(LiveConfig.mFont != null){
                    getView().mTvPullDownTip.setTypeface(LiveConfig.mFont);
                }
                animationDrawable = (AnimationDrawable)  getView().mIvPullDownTip.getDrawable();
                if(animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
            }*/
        }catch (Exception e){

        }
    }

    public void hidePullDownTips(){
        try {
            if(getView().mLlyPullDownTip.getVisibility() == View.VISIBLE){
                getView().mLlyPullDownTip.setVisibility(View.GONE);
                if(animationDrawable != null){
                    animationDrawable.stop();
                }
            }
        }catch (Exception e){

        }
    }

    /**
    * 获取向上刷新的日期
    * @author hyqiao
    * @time 2017/4/6 18:52
    */
    private int getForwardMatchDate(){
        try{
            for(int i=0;getView().mList != null && i<getView().mList.size();i++){
                if(getView().mList.get(i).isDate){
                    return getView().mList.get(i).getDate();
                }
            }
        }catch (Exception e){
            TLog.e(TAG,"getForwardMatchDate error : "+e.getMessage());
        }
        return 0;
    }
    /**
     * 获取向下刷新的日期
     * @author hyqiao
     * @time 2017/4/6 18:52
     */
    private int getBackwardMatchDate(){
        try{
            if(getView().mList != null){
                for(int i=getView().mList.size()-1; i>=0;i--){
                    if(getView().mList.get(i).isDate){
                        return getView().mList.get(i).getDate();
                    }
                }
            }
        }catch (Exception e){
            TLog.e(TAG,"getBackwardMatchDate error : "+e.getMessage());
        }
        return 0;
    }

    /**
    * 设置数据异常的UI
    * @author hyqiao
    * @time 2017/4/10 15:27
    */
    private void setEmptyDataView(boolean isNeedRefresh,String errMsg){
        if(getView().mList != null && getView().mList.size() != 0){
            TLog.e(TAG,TAG+" :  mList != null");
        }else {
            if(getView().errorDataView == null){
                getView().errorDataView = new DataErrorView(getView().mContext,errMsg);
                getView().errorDataView.setFont(LiveConfig.mFont);
            }
            if(getView().mLvMatch!=null){
                getView().errorDataView.setVisible(true);
                getView().errorDataView.setErrorText(errMsg);
                getView().errorDataView.setmBtnRefreshVisible(isNeedRefresh);
                getView().errorDataView.setRefreshListener(new DataErrorView.RefreshListener() {
                    @Override
                    public void onClick() {
                        getMatchList(0,ScheduleModel.DEFAULT_VALUE);
                    }
                });
                getView().mLvMatch.setEmptyView(getView().errorDataView);
                getView().mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
    * 更新比赛状态：比分，切换比赛等
    * @author hyqiao
    * @time 2017/4/6 14:32
    */
    public void updateData(final MatchItem matchItem){
        TLog.e(TAG,"updateData(MatchItem matchItem)");
        if(getView().mList == null || getView().mAdapter == null){
            return;
        }
        Runnable mRunnable = new Runnable(){
            @Override
            public void run() {
                try{
                    String matchidTemp = PBDataUtils.byteString2String(matchItem.match_id);
                    for(int i = 0;i<getView().mList.size();i++){
                        for(int j = 0;getView().mList.get(i).mRaceInfoBean != null && getView().mList.get(i).mRaceInfoBean.per_match_list != null && j<getView().mList.get(i).mRaceInfoBean.per_match_list.size();j++){
                            MatchItem matchItemTemp = getView().mList.get(i).mRaceInfoBean.per_match_list.get(j);
                            if(PBDataUtils.byteString2String(matchItemTemp.match_id).equals(matchidTemp)){
                                //更新比分
                                getView().mList.get(i).mRaceInfoBean.per_match_list.set(j,matchItem);
                                break;
                            }
                        }
                    }
                    notifyListChanged();
                }catch (Exception e){
                    TLog.e(TAG,"updateData(MatchItem matchItem) error : "+e.getMessage());
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunnable(mRunnable);
    }

    /**
     * 最后一场比赛的结束通知
     * @author hyqiao
     * @time 2017/4/6 15:02
     */
    public void updateData(final int sourceID){
        TLog.e(TAG,"updateData(int sourceID)");
        if(getView().mList == null || getView().mAdapter == null){
            return;
        }
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i = 0;getView().mList != null && i<getView().mList.size();i++){
                        for(int j = 0;getView().mList.get(i).mRaceInfoBean != null && getView().mList.get(i).mRaceInfoBean.source_id == sourceID && getView().mList.get(i).mRaceInfoBean.per_match_list != null && j<getView().mList.get(i).mRaceInfoBean.per_match_list.size();j++){
                            MatchItem matchItemTemp = getView().mList.get(i).mRaceInfoBean.per_match_list.get(j);
                            /*if(matchItemTemp.match_state == MatchState.E_MATCH_STATE_RUNNING.getValue()){
                                //更新比分
                                MatchItem oldM= getView().mList.get(i).mRaceInfoBean.per_match_list.get(j);
                                MatchItem newM = new MatchItem(oldM.match_id, MatchState.E_MATCH_STATE_FINISHED.getValue(), oldM.match_time, oldM.match_main_title, oldM.match_sub_title, oldM.host_team_name, oldM.host_team_logo, oldM.guest_team_name, oldM.guest_team_logo, oldM.host_team_score, oldM.guest_team_score, oldM.room_id, oldM.vid, oldM.online_num, oldM.subcribe_state, oldM.room_type, oldM.record_vid_list, oldM.host_team_id, oldM.guest_team_id, oldM.host_vote_num, oldM.guest_vote_num, oldM.has_cheer,oldM.host_team_short,oldM.guest_team_short, oldM.seasonid);
                                getView().mList.get(i).mRaceInfoBean.per_match_list.set(j,newM);
                                break;
                            }*/
                        }
                    }
                    notifyListChanged();
                }catch (Exception e){
                    TLog.e(TAG,"updateData(String matchID) error : "+e.getMessage());
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunnable(mRunnable);
    }

    /**
    * 更新订阅状态
    * @author hyqiao
    * @time 2017/4/6 14:32
    */
    public void updateData(final String matchID, final int subscribe_state){
        TLog.e(TAG,"updateData(String matchID,int subscribe_state) - " + matchID +" "+ subscribe_state);
        if(getView().mList == null || getView().mAdapter == null){
            return;
        }
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i = 0;getView().mList != null && i<getView().mList.size();i++){
                        if (getView().mList.get(i).getMatchListBean()!=null){
                            if (getView().mList.get(i).getMatchListBean().getMatch_id()!=null){
                                if(getView().mList.get(i).getMatchListBean().getMatch_id().equals(matchID)){
                                    int state = 0;
                                    if(subscribe_state == 2){
                                        state = 0;
                                        TLog.e("subscribe_state == 2");
                                    }else {
                                        state = 1;
                                        TLog.e("subscribe_state != 2");
                                    }
                                    MatchDayInfoBean.MatchDayListBean.MatchListBean oldM = getView().mList.get(i).getMatchListBean();
                                    MatchDayInfoBean.MatchDayListBean.MatchListBean newM = new MatchDayInfoBean.MatchDayListBean.MatchListBean(state
                                            ,oldM.getMatch_id(),oldM.getStage(),oldM.getMatch_state(),oldM.getMatch_time(),oldM.getRecord_vid_list(),oldM.getHas_cheer(), oldM.getMatch_sub_title()
                                            ,oldM.getMatch_main_title(),oldM.getRoomid());
                                    getView().mList.get(i).setMatchListBean(newM);
                                    break;
                                }
                            }
                        }

                    }
                    notifyListChanged();
                }catch (Exception e){
                    TLog.e(TAG,"updateData(String matchID,int subscribe_state) error : "+e.getMessage());
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunnable(mRunnable);
    }

    public void initRankEntrance(){
        if(ConfigInfo.getmInstance().getConfig(ConfigInfo.SCORE_RANK_SWITCH)){
            initRankEntranceView();
        }else {
            try {
                hideRankEntranceRedPot();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initRankEntranceView(){
        try {
            getView().mRlyMatchRank.setVisibility(View.VISIBLE);
            getView().mIvMatchRankTopBg.setVisibility(View.VISIBLE);
            /*JSONObject rank_info = new JSONObject(ConfigInfo.getmInstance().getStringConfig(ConfigInfo.TEAM_RANK_CFG));
            rank_url = rank_info.optString("rank_url");
            getView().mTvRankEntranceName.setText(rank_info.optString("rank_name"));
            ImageLoaderUitl.loadimage(rank_info.optString("rank_logo"),getView().mIvRankEntranceIcon);
            if(LiveShareUitl.getRankEntrance(getView().mContext)){
                getView().mIvRankEnteranceIcon.setVisibility(View.VISIBLE);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * 通知ListView刷新
    * @author hyqiao
    * @time 2017/4/10 14:56
    */
    private Handler handler;
    private void notifyListChanged(){
        try{
            if(handler == null){
                handler = new Handler(Looper.getMainLooper());
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getView().mAdapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"SchedulePresenter notifyListChanged error : "+e.getMessage());
        }
    }

    private void hideRankEntranceRedPot() {
        try {
            /*if(LiveShareUitl.getRankEntrance(getView().mContext)){
                getView().mIvRankEnteranceIcon.setVisibility(View.GONE);
                ((LiveRightContainer)getView().getParent().getParent()).updateSchduleRankRedpot(false);
                LiveShareUitl.saveRankEntrance(getView().mContext,false);
            }*/
        }catch (Exception e){
            TLog.e(TAG,"hideRankEntranceRedPot error : "+e.getMessage());
        }
    }
}
