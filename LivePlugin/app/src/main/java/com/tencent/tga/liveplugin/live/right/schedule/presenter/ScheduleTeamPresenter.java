package com.tencent.tga.liveplugin.live.right.schedule.presenter;


import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.right.schedule.ScheduleTeamAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBean;
import com.tencent.tga.liveplugin.live.right.schedule.model.ScheduleTeamModel;
import com.tencent.tga.liveplugin.live.right.schedule.ui.ScheduleTeamView;

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
        try {
            TLog.e(TAG, "setData");
            getView().mGridView.setAdapter(new ScheduleTeamAdapter(list, getView().getContext()));
            getView().mGridView.setHorizontalSpacing(DeviceUtils.dip2px(getView().getContext(), 7));
            getView().mGridView.setVerticalSpacing(DeviceUtils.dip2px(getView().getContext(), 13));
            getView().show();
        } catch (Exception e) {
            TLog.e(TAG, "setData error : " + e.getMessage());
        }
    }
}
