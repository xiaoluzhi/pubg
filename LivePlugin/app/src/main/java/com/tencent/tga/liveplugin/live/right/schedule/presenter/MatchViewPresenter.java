package com.tencent.tga.liveplugin.live.right.schedule.presenter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
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
import com.tencent.tga.liveplugin.live.right.schedule.model.MatchViewModel;
import com.tencent.tga.liveplugin.live.right.schedule.ui.MatchView;
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
                if(getView().mMatchItem.match_state == MATCH_STATE_NOT_START){
                    //订阅部分暂用之前的逻辑，通知到主页面调用请求，之后可以切换到MatchViewModel中
                    if(getView().mMatchItem.subcribe_state == 1){
                        TLog.e(TAG,"取消订阅");
                        //LiveShareUitl.saveUserSubscribtion(getView().getContext(), PBDataUtils.byteString2String(getView().mMatchItem.match_id), false);
                        //NotificationCenter.defaultCenter().publish(new LiveEvent.reqMatchSubscribe(PBDataUtils.byteString2String(getView().mMatchItem.match_id), SubscribeOperationType.SUB_OP_TYPE_CANCEL.getValue(), EnterType.PpkdcNormal.getValue()));
                    }else {
                        //LiveShareUitl.saveUserSubscribtion(getView().getContext(), PBDataUtils.byteString2String(getView().mMatchItem.match_id), true);
                        //NotificationCenter.defaultCenter().publish(new LiveEvent.reqMatchSubscribe(PBDataUtils.byteString2String(getView().mMatchItem.match_id),SubscribeOperationType.SUB_OP_TYPE_SUBSCRIBE.getValue(), EnterType.PpkdcNormal.getValue()));
                        TLog.e(TAG,"订阅");
                    }
                }else if(getView().mMatchItem.match_state == MATCH_STATE_FINISHED){
                    if(getView().mMatchItem.record_vid_list != null && getView().mMatchItem.record_vid_list.size() > 0){
                        TLog.e(TAG,"启动播放器");
                        ArrayList<String> videoList = new ArrayList<String>();
                        for(int i=0;i<getView().mMatchItem.record_vid_list.size();i++){
                            videoList.add(PBDataUtils.byteString2String(getView().mMatchItem.record_vid_list.get(i)));
                        }
                        String match_date = TimeUtils.getMatchDate(Long.valueOf(PBDataUtils.byteString2String(getView().mMatchItem.match_time))*1000L);
                        String title = String.format("%s %s %s %s %s:%s %s",
                                match_date,
                                PBDataUtils.byteString2String(getView().mMatchItem.match_main_title),
                                PBDataUtils.byteString2String(getView().mMatchItem.match_sub_title),
                                PBDataUtils.byteString2String(getView().mMatchItem.host_team_name),
                                getView().mMatchItem.host_team_score,
                                getView().mMatchItem.guest_team_score,
                                PBDataUtils.byteString2String(getView().mMatchItem.guest_team_name));
                        //NotificationCenter.defaultCenter().publish(new LiveEvent.VideoPlay(videoList,title));

                        //直接调用播放，以及上报
                        //VideoPlayActivity.launch(that, false, event.vids, null, event.title, -1);
                        //ReportManager.getInstance().report_TVVideoClick(event.vids.get(0).toString(), ReportManager.BOTTOM_MATCH);
                    }else {
                        TLog.e(TAG,"播放列表为空");
                    }
                }else {
                    TLog.e(TAG," 当前 match_state = "+getView().mMatchItem.match_state);
                }
            }
        });
    }

    // 赛事未开始 : 1  ; 赛事已取消 : 2  ; 赛事进行中 : 3  ; 赛事已结束 : 4
    public void setData(MatchItem mb){
        getView().mMatchItem = mb;
        getView().mTvLeftName.setText(PBDataUtils.byteString2String(mb.host_team_name));
        getView().mTvRightName.setText(PBDataUtils.byteString2String(mb.guest_team_name));
        setPlayState(mb);
        setScore(mb);
        setIcon(mb);
    }

    private void setScore(MatchItem mb){
        SpannableString spannableString = null;
        if(mb.match_state == MATCH_STATE_FINISHED){
            if(mb.host_team_score>mb.guest_team_score){
                spannableString = getSpannableString(mb.host_team_score+":"+mb.guest_team_score,(mb.host_team_score+"").length(),"#FECC21","#FFFFFF");
            }else if(mb.host_team_score<mb.guest_team_score){
                spannableString = getSpannableString(mb.host_team_score+":"+mb.guest_team_score,(mb.host_team_score+":").length(),"#FFFFFF","#FECC21");
            }else {
                spannableString = getSpannableString(mb.host_team_score+":"+mb.guest_team_score,(mb.host_team_score+":").length(),"#FFFFFF","#FFFFFF");
            }
        }else if(mb.match_state == MATCH_STATE_RUNNING){
            spannableString = getSpannableString(mb.host_team_score+":"+mb.guest_team_score,(mb.host_team_score+":").length(),"#FFFFFF","#FFFFFF");
        }else {
            spannableString = getSpannableString("-:-","-".length(),"#FFFFFF","#FFFFFF");
        }

        getView().mTvScore.setText(spannableString);
    }

    private void setIcon(MatchItem mb){
        ImageLoaderUitl.loadimage(PBDataUtils.byteString2String(mb.host_team_logo),getView().mIvLeftIcon);
        ImageLoaderUitl.loadimage(PBDataUtils.byteString2String(mb.guest_team_logo),getView().mIvRightIcon);
    }
    //设置回放的UI
    private void setReplayUI(){
        if(getView().mTvPlayAndSubscription == null)
            return;
        getView().mTvPlayAndSubscription.setText("");
        getView().mTvPlayAndSubscription.setBackground(null);
        //getView().mTvPlayAndSubscription.setBackgroundResource(R.drawable.icon_replay);
    }
    /**
     * 设置订阅的UI
     * isSubscribe true ：已经订阅 ； false：未订阅
     * @author hyqiao
     * @time 2017/4/4 14:51
     */
    private void setSubscribeUI(boolean isSubscribe){
        if(getView().mTvPlayAndSubscription == null)
            return;
        getView().mTvPlayAndSubscription.setVisibility(View.VISIBLE);
        getView().mTvPlayAndSubscription.setText("");
        //getView().mTvPlayAndSubscription.setBackgroundResource(R.drawable.subscribe_selector);
        if(isSubscribe){//已经订阅
            getView().mTvPlayAndSubscription.setSelected(false);
        }else {//未订阅
            getView().mTvPlayAndSubscription.setSelected(true);
        }
    }

    /**
    * 直播中状态
    * @author hyqiao
    * @time 2018/4/30 10:30
    */
    private void setLivingState(){
        if(getView().mTvPlayAndSubscription == null)
            return;
        getView().mTvPlayAndSubscription.setVisibility(View.VISIBLE);
        getView().mTvPlayAndSubscription.setBackground(null);
        getView().mTvPlayAndSubscription.setTextColor(0xFFFECC21);
        getView().mTvPlayAndSubscription.setText("直播中");
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
    }


    private void setPlayState(MatchItem mb){
        String time = PBDataUtils.byteString2String(mb.match_time);
        int type = mb.match_state;
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
            if(mb.subcribe_state == 1){
                setSubscribeUI(true);
            }else {
                setSubscribeUI(false);
            }
        }else if(type == MATCH_STATE_FINISHED){
            if(mb.record_vid_list != null && mb.record_vid_list.size() != 0){
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
