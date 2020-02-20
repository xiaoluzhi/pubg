package com.tencent.tga.liveplugin.live.right.schedule.presenter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.ppkdc_schedule.MatchItem;
import com.tencent.tga.liveplugin.base.mvp.BasePresenter;
import com.tencent.tga.liveplugin.base.notification.NotificationCenter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.TimeUtils;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.right.schedule.bean.MatchDayInfoBean;
import com.tencent.tga.liveplugin.live.right.schedule.model.MatchViewModel;
import com.tencent.tga.liveplugin.live.right.schedule.ui.IntegralDetailsView;
import com.tencent.tga.liveplugin.live.right.schedule.ui.MatchView;
import com.tencent.tga.liveplugin.live.right.schedule.ui.ScheduleTeamView;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by hyqiao on 2017/4/4.
 */

public class MatchViewPresenter extends BasePresenter<MatchView,MatchViewModel> {

    private final static String TAG = "MatchViewPresenter";

    MatchViewModel matchViewModel;

    public final static int MATCH_STATE_NOT_START = 1;
    public final static int MATCH_STATE_CANCELED = 2;
    public final static int MATCH_STATE_RUNNING = 3;
    public final static int MATCH_STATE_FINISHED = 4;

    public boolean isNowTime;//是否现在及以后的时间 UI调整

    @Override
    public MatchViewModel getModel() {
        if (matchViewModel == null)
            matchViewModel = new MatchViewModel();
        return matchViewModel;
    }

    public void init(){
        initListener();
    }

    private void initListener(){
        getView().mTvPlayAndSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NoDoubleClickUtils.isDoubleClick()){
                    return;
                }
                if(getView().matchListBean.getMatch_state() == MATCH_STATE_NOT_START){
                    //订阅部分暂用之前的逻辑，通知到主页面调用请求，之后可以切换到MatchViewModel中
                    if(getView().matchListBean.getSubscribe_state() == 1){
                        TLog.e(TAG,"取消订阅");
                        //LiveShareUitl.saveUserSubscribtion(getView().getContext(), PBDataUtils.byteString2String(getView().mMatchItem.match_id), false);
                        //NotificationCenter.defaultCenter().publish(new LiveEvent.reqMatchSubscribe(PBDataUtils.byteString2String(getView().mMatchItem.match_id), SubscribeOperationType.SUB_OP_TYPE_CANCEL.getValue(), EnterType.PpkdcNormal.getValue()));
                    }else {
                        //LiveShareUitl.saveUserSubscribtion(getView().getContext(), PBDataUtils.byteString2String(getView().mMatchItem.match_id), true);
                        //NotificationCenter.defaultCenter().publish(new LiveEvent.reqMatchSubscribe(PBDataUtils.byteString2String(getView().mMatchItem.match_id),SubscribeOperationType.SUB_OP_TYPE_SUBSCRIBE.getValue(), EnterType.PpkdcNormal.getValue()));
                        TLog.e(TAG,"订阅");
                    }
                }else if(getView().matchListBean.getMatch_state() == MATCH_STATE_FINISHED){
                    if(getView().matchListBean.getRecord_vid_list() != null && getView().matchListBean.getRecord_vid_list().size() > 0){
                        TLog.e(TAG,"启动播放器");
                        ArrayList<String> videoList = new ArrayList<String>();
                        for(int i=0;i<getView().matchListBean.getRecord_vid_list().size();i++){
                            videoList.add(getView().matchListBean.getRecord_vid_list().get(i));
                        }
                        String match_date = TimeUtils.getMatchDate(Long.valueOf(getView().matchListBean.getMatch_time())*1000L);
                        /*String title = String.format("%s %s %s %s %s:%s %s",
                                match_date,
                                PBDataUtils.byteString2String(getView().mMatchItem.match_main_title),
                                PBDataUtils.byteString2String(getView().mMatchItem.match_sub_title),
                                PBDataUtils.byteString2String(getView().mMatchItem.host_team_name),
                                getView().mMatchItem.host_team_score,
                                getView().mMatchItem.guest_team_score,
                                PBDataUtils.byteString2String(getView().mMatchItem.guest_team_name));
                        NotificationCenter.defaultCenter().publish(new LiveEvent.VideoPlay(videoList,title));*/

                        //直接调用播放，以及上报
                        //VideoPlayActivity.launch(that, false, event.vids, null, event.title, -1);
                        //ReportManager.getInstance().report_TVVideoClick(event.vids.get(0).toString(), ReportManager.BOTTOM_MATCH);
                    }else {
                        TLog.e(TAG,"播放列表为空");
                    }
                }else {
                    TLog.e(TAG," 当前 match_state = "+getView().matchListBean.getMatch_state());
                }
            }
        });
        getView().mTvTeamOrRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里点击事件
                if (getView().mTvTeamOrRank.getText().equals("参赛队伍")){
                    //这是 getView().matchListBean.getMatch_id()
                    ScheduleTeamView scheduleTeamView=new ScheduleTeamView(getView().getContext()
                            ,getView().matchListBean.getMatch_id(),getView());
                    scheduleTeamView.initView();
                }else if (getView().mTvTeamOrRank.getText().equals("积分详情")){
                    String title=TimeUtils.getMatchDate(Long.valueOf(getView().matchListBean.getMatch_time())*1000L)+getView().matchListBean.getMatch_main_title();
                    IntegralDetailsView integralDetailsView=new IntegralDetailsView(getView().getContext(),getView().matchListBean.getMatch_id()
                            ,getView().matchListBean.getRoomid(),title,getView());
                    integralDetailsView.initView();
                }
            }
        });
    }

    // 赛事未开始 : 1  ; 赛事已取消 : 2  ; 赛事进行中 : 3  ; 赛事已结束 : 4
    /*public void setData(MatchItem mb){
        getView().mMatchItem = mb;
        getView().mTvLeftName.setText(PBDataUtils.byteString2String(mb.host_team_name));
        getView().mTvRightName.setText(PBDataUtils.byteString2String(mb.guest_team_name));
        setPlayState(mb);
        setScore(mb);
        setIcon(mb);
    }*/

    public void setData(MatchDayInfoBean.MatchDayListBean.MatchListBean mb){
        getView().matchListBean = mb;
        getView().mTvLeftName.setText(mb.getHost_team_name());
        getView().mTvRightName.setText(mb.getGuest_team_name());
        getView().mTvMatchtitle.setText(mb.getMatch_main_title());
        getView().mTvMatchSubTitle.setText(mb.getMatch_sub_title());
        setPlayState(mb);
        setScore(mb);
        setIcon(mb);

        getView().post(new Runnable() {
            @Override
            public void run() {
                try{
                    ViewGroup.LayoutParams layoutParams = getView().mIvLeftLine.getLayoutParams();
                    layoutParams.height = getView().getHeight();
                    getView().mIvLeftLine.setLayoutParams(layoutParams);
                }catch (Exception e){

                }
            }
        });
    }

    private void setScore(MatchDayInfoBean.MatchDayListBean.MatchListBean mb){
        SpannableString spannableString = null;
        if(mb.getMatch_state() == MATCH_STATE_FINISHED){
            if(mb.getHost_team_score()>mb.getGuest_team_score()){
                spannableString = getSpannableString(mb.getHost_team_score()+":"+mb.getGuest_team_score(),(mb.getHost_team_score()+"").length(),"#FECC21","#FFFFFF");
            }else if(mb.getHost_team_score()<mb.getGuest_team_score()){
                spannableString = getSpannableString(mb.getHost_team_score()+":"+mb.getGuest_team_score(),(mb.getHost_team_score()+":").length(),"#FFFFFF","#FECC21");
            }else {
                spannableString = getSpannableString(mb.getHost_team_score()+":"+mb.getGuest_team_score(),(mb.getHost_team_score()+":").length(),"#FFFFFF","#FFFFFF");
            }
        }else if(mb.getMatch_state() == MATCH_STATE_RUNNING){
            spannableString = getSpannableString(mb.getHost_team_score()+":"+mb.getGuest_team_score(),(mb.getHost_team_score()+":").length(),"#FFFFFF","#FFFFFF");
        }else {
            spannableString = getSpannableString("-:-","-".length(),"#FFFFFF","#FFFFFF");
        }

        getView().mTvScore.setText(spannableString);
    }

    private void setIcon(MatchDayInfoBean.MatchDayListBean.MatchListBean mb){
        ImageLoaderUitl.loadimage(mb.getHost_team_logo(),getView().mIvLeftIcon);
        ImageLoaderUitl.loadimage(mb.getGuest_team_logo(),getView().mIvRightIcon);
    }
    //设置回放的UI
    private void setReplayUI(){
        if(getView().mTvStatus == null)
            return;
        getView().mTvStatus.setVisibility(View.VISIBLE);
        getView().mTvStatus.setText("回放");
        getView().mTvStatus.setTextColor(Color.parseColor("#59B56C"));
        getView().mTvStatus.setBackgroundResource(R.drawable.schedule_item_replay_bg);
    }
    /**
     * 设置订阅的UI
     * isSubscribe true ：已经订阅 ； false：未订阅
     * @author hyqiao
     * @time 2017/4/4 14:51
     */
    private void setSubscribeUI(boolean isSubscribe){
        if(getView().mTvStatus == null)
            return;
        getView().mTvStatus.setVisibility(View.VISIBLE);
        getView().mTvStatus.setBackgroundResource(R.drawable.schedule_item_subscribe_selector);
        if(isSubscribe){//已经订阅
            getView().mTvStatus.setSelected(true);
            getView().mTvStatus.setText("已订");
            getView().mTvStatus.setTextColor(Color.parseColor("#A8A8A8"));
        }else {//未订阅
            getView().mTvStatus.setSelected(false);
            getView().mTvStatus.setText("订阅");
            getView().mTvStatus.setTextColor(Color.parseColor("#FF9E44"));
        }
    }

    /**
     * 未开始状态
     */
    private void setUnStarState(){
        if(getView().mTvPlayAndSubscription == null || getView().mTvStatus == null)
            return;
        getView().mTvPlayAndSubscription.setVisibility(View.VISIBLE);
        getView().mTvPlayAndSubscription.setBackground(null);
        getView().mTvPlayAndSubscription.setTextColor(0xFFA8A8A8);
        getView().mTvPlayAndSubscription.setText("未开始");

        getView().mTvTeamOrRank.setText("参赛队伍");
        if (isNowTime){
            getView().mTvTeamOrRank.setBackgroundResource(R.drawable.schedule_item_rank_detail_bg_now);
        }else {
            getView().mTvTeamOrRank.setBackgroundResource(R.drawable.schedule_item_rank_detail_bg);
        }
    }

    /**
    * 直播中状态
    * @author hyqiao
    * @time 2018/4/30 10:30
    */
    private void setLivingState(){
        if(getView().mTvPlayAndSubscription == null || getView().mTvStatus == null)
            return;
        getView().mTvPlayAndSubscription.setVisibility(View.VISIBLE);
        getView().mTvPlayAndSubscription.setBackground(null);
        getView().mTvPlayAndSubscription.setTextColor(0xFFFFC951);
        getView().mTvPlayAndSubscription.setText("直播中");

        getView().mTvStatus.setVisibility(GONE);

        getView().mTvTeamOrRank.setText("参赛队伍");
        if (isNowTime){
            getView().mTvTeamOrRank.setBackgroundResource(R.drawable.schedule_item_rank_detail_bg_now);
        }else {
            getView().mTvTeamOrRank.setBackgroundResource(R.drawable.schedule_item_rank_detail_bg);
        }
    }

    /**
    * 直播结束状态
    * @author hyqiao
    * @time 2018/5/4 10:48
    */
    private void setFinishedState(){
        if(getView().mTvPlayAndSubscription == null)
            return;
        getView().mTvPlayAndSubscription.setVisibility(View.VISIBLE);
        getView().mTvPlayAndSubscription.setBackground(null);
        getView().mTvPlayAndSubscription.setTextColor(0xFF7092b3);
        getView().mTvPlayAndSubscription.setText("已结束");

        getView().mTvTeamOrRank.setText("积分详情");
        if (isNowTime){
            getView().mTvTeamOrRank.setBackgroundResource(R.drawable.schedule_item_rank_detail_bg_now);
        }else {
            getView().mTvTeamOrRank.setBackgroundResource(R.drawable.schedule_item_rank_detail_bg);
        }
    }


    private void setPlayState(MatchDayInfoBean.MatchDayListBean.MatchListBean mb){
        String time = mb.getMatch_time();
        int type = mb.getMatch_state();
        if(getView().mTvMatchTime == null){
            return;
        }
        String match_time = TimeUtils.getMatchTime(Long.valueOf(time)*1000L);
        getView().mTvMatchTime.setText(match_time);
        if(type == MATCH_STATE_RUNNING){
            setTvPlayAndSubscriptionSize(true);
            setLivingState();
        }else if(type == MATCH_STATE_NOT_START){
            setTvPlayAndSubscriptionSize(false);
            if(mb.getSubscribe_state() == 1){
                setSubscribeUI(true);
            }else {
                setSubscribeUI(false);
            }
        }else if(type == MATCH_STATE_FINISHED){
            if(mb.getRecord_vid_list() != null && mb.getRecord_vid_list().size() != 0){
                setTvPlayAndSubscriptionSize(false);
                setReplayUI();
            }else {
                setTvPlayAndSubscriptionSize(true);
                setFinishedState();
            }
        }else {
            getView().mTvPlayAndSubscription.setVisibility(GONE);
        }
    }

    private void setTvPlayAndSubscriptionSize(boolean isBig){
        try {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getView().mTvPlayAndSubscription.getLayoutParams();
            if(isBig){
                lp.width = DeviceUtils.dip2px(getView().mContext,32);
            }else {
                lp.width = DeviceUtils.dip2px(getView().mContext,29);
            }
            getView().mTvPlayAndSubscription.setLayoutParams(lp);
        }catch (Exception e){
            TLog.e(TAG,"setTvPlayAndSubscriptionSize error : "+e.getMessage());
        }
    }

    private SpannableString getSpannableString(String str,int splitPosition,String c1,String c2){
        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor(c1));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor(c2));

        spannableString.setSpan(colorSpan1, 0, splitPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(colorSpan2, splitPosition, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
