package com.tencent.tga.liveplugin.live.right.schedule.presenter;

import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBean;
import com.tencent.tga.liveplugin.live.right.schedule.model.ScheduleTeamModel;
import com.tencent.tga.liveplugin.live.right.schedule.ui.ScheduleTeamView;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class ScheduleTeamPresenter extends BaseFrameLayoutPresenter <ScheduleTeamView, ScheduleTeamModel>{
    private static String TAG = "ScheduleTeamPresenter";
    private ScheduleTeamModel scheduleTeamModel;
    @Override
    public ScheduleTeamModel getModel() {
        if (scheduleTeamModel==null){
            scheduleTeamModel=new ScheduleTeamModel(this);
        }
        return scheduleTeamModel;
    }
    public void getData(String matchId){
        getModel().requestList(matchId);
    }
    public void setData(ArrayList<TeamBean> list){
        TLog.e(TAG,"setData");
        getView().mGridView.setAdapter(new CommonAdapter<TeamBean>(getView().getContext(),list, R.layout.schedule_team_view_item) {
            @Override
            public void convert(ViewHolder holder, TeamBean teamBean) {
                ImageView imageView=holder.getView(R.id.team_item_img);
                ImageLoaderUitl.loadimage(teamBean.getTeam_logo(),imageView);
                TextView textView=holder.getView(R.id.team_item_name);
                textView.setText(teamBean.getTeam_name());
            }
        });
        getView().show();
    }
}
