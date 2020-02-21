package com.tencent.tga.liveplugin.live.right.schedule.model;

import android.text.TextUtils;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutModelInter;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBean;
import com.tencent.tga.liveplugin.live.right.schedule.presenter.ScheduleTeamPresenter;
import com.tencent.tga.liveplugin.live.right.schedule.proxy.ScheduleTeamProxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScheduleTeamModel extends BaseFrameLayoutModelInter {
    private static final String TAG ="ScheduleTeamModel";
    private ScheduleTeamPresenter mPresenter;

    public ScheduleTeamModel(ScheduleTeamPresenter presenter){
        mPresenter=presenter;
    }
    @Override
    protected BaseFrameLayoutPresenter getPresenter() {
        return mPresenter;
    }
    private ScheduleTeamProxy proxy=new ScheduleTeamProxy();
    private ScheduleTeamProxy.Param param=new ScheduleTeamProxy.Param();
    public void requestList(String matchId){
        TLog.e(TAG,"requestList");
        param.matchid=matchId;
        proxy.postReq(getPresenter().getView().getContext(), new HttpBaseUrlWithParameterProxy.Callback() {
            @Override
            public void onSuc(int i) {
                try {
                if (!TextUtils.isEmpty(param.response)) {
                    try {
                        TLog.e(TAG,"param.response is"+param.response);
                        JSONObject jsonObject = new JSONObject(param.response);
                        if (jsonObject.optInt("result")==0){
                            JSONArray array=jsonObject.optJSONArray("team_list");
                            ArrayList<TeamBean> list=new ArrayList<>();
                            for (int a=0;a<array.length();a++){
                                TeamBean bean=new TeamBean();
                                bean.setTeam_logo(array.optJSONObject(a).optString("team_logo"));
                                bean.setTeamid(array.optJSONObject(a).optString("team_id"));
                                bean.setTeam_name(array.optJSONObject(a).optString("team_name"));
                                bean.setTeam_short_name(array.optJSONObject(a).optString("team_short_name"));
                                list.add(bean);
                            }
                            if (list.size() >0) {
                                mPresenter.setData(list);
                            } else {
                                ToastUtil.show(getPresenter().getView().getContext(),"数据异常");
                            }
                        }else{
                            TLog.e(TAG, "ScheduleTeamProxy result !=0" );
                            ToastUtil.show(getPresenter().getView().getContext(),"网络异常");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                    TLog.e(TAG, "ScheduleTeamProxy error : " + e.getMessage());
                }
            }

            @Override
            public void onFail(int i) {
                TLog.e(TAG, "ScheduleTeamProxy onFail is: " +i);
                ToastUtil.show(getPresenter().getView().getContext(),"网络异常");
            }
        },param);
    }
}
