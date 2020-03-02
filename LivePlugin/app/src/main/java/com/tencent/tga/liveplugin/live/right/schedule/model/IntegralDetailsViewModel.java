package com.tencent.tga.liveplugin.live.right.schedule.model;

import android.text.TextUtils;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutModelInter;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamScoreBean;
import com.tencent.tga.liveplugin.live.right.schedule.presenter.IntegralDetailsViewPresenter;
import com.tencent.tga.liveplugin.live.right.schedule.proxy.IntegralDetailsProxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IntegralDetailsViewModel extends BaseFrameLayoutModelInter {
    private static final String TAG ="IntegralDetailsViewModel";
    private IntegralDetailsViewPresenter presenter;
    private ArrayList<String> list=new ArrayList<>();
    public IntegralDetailsViewModel(IntegralDetailsViewPresenter presenter){
        this.presenter=presenter;
    }
    @Override
    protected BaseFrameLayoutPresenter getPresenter() {
        return presenter;
    }
    private IntegralDetailsProxy proxy=new IntegralDetailsProxy();
    private IntegralDetailsProxy.Param param=new IntegralDetailsProxy.Param();
    public void requestList(String matchId,String roomId,int type){
        //type--0代表总计，1代表第一局
        param.roomId=roomId;
        param.matchId=matchId;
        TLog.e(TAG, "reqGiftList 成功 :" +  param.matchId);
        param.bo_num=type;
        proxy.postReq(getPresenter().getView().getContext(), new HttpBaseUrlWithParameterProxy.Callback() {
            @Override
            public void onSuc(int i) {
                try {
                    TLog.e(TAG, "reqGiftList 成功 :" + param.response);
                    if (!TextUtils.isEmpty(param.response)) {
                        try {
                            JSONObject jsonObject = new JSONObject(param.response);
                            if (jsonObject.optInt("result")==0){
                                JSONArray array=jsonObject.optJSONArray("rank_list");
                                int count=array.getJSONObject(0).optInt("bo_count")+1;
                                ArrayList<String> titleList=changeCount(count);
                                presenter.setTotalList(titleList);
                                ArrayList<TeamBankBean> list=new ArrayList<>();
                                for (int j=0;j<array.length();j++){
                                    TeamBankBean teamBankBean=new TeamBankBean();
                                    teamBankBean.setTeamid(array.getJSONObject(j).optString("teamid"));
                                    teamBankBean.setTeam_name(array.getJSONObject(j).optString("team_name"));
                                    teamBankBean.setTeam_short_name(array.getJSONObject(j).optString("team_short_name"));
                                    teamBankBean.setTeam_logo(array.getJSONObject(j).optString("team_logo"));
                                    teamBankBean.setTotal_score(array.getJSONObject(j).optString("total_score"));
                                    JSONArray array2=array.getJSONObject(j).optJSONArray("bo_score_list");
                                    ArrayList<TeamScoreBean> list2=new ArrayList<>();
                                    for (int k=0;k<array2.length();k++){
                                        TeamScoreBean teamScoreBean=new TeamScoreBean();
                                        teamScoreBean.setEliminlate_score(array2.getJSONObject(k).optInt("eliminlate_score"));
                                        teamScoreBean.setBo_score(array2.getJSONObject(k).optInt("bo_score"));
                                        teamScoreBean.setRank_score(array2.getJSONObject(k).optInt("rank_score"));
                                        list2.add(teamScoreBean);
                                    }
                                    teamBankBean.setList(list2);
                                    list.add(teamBankBean);
                                }
                                if (list.size() > 0) {
                                    presenter.setDetailList(list, type, titleList);
                                }else{
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
                    TLog.e(TAG, "IntegralDetailsProxy error : " + e.getMessage());
                }
            }

            @Override
            public void onFail(int i) {
                TLog.e(TAG, "IntegralDetailsProxy onFail" +i);
                ToastUtil.show(getPresenter().getView().getContext(),"网络异常");
            }
        },param);
    }
    private ArrayList<String> changeCount(int count){
        ArrayList<String> dataList=new ArrayList<>();
        list.add("总积分");
        list.add("第一局");
        list.add("第二局");
        list.add("第三局");
        list.add("第四局");
        list.add("第五局");
        list.add("第六局");
        list.add("第七局");
        list.add("第八局");
        list.add("第九局");
        for(int i=0;i<count;i++){
            dataList.add(list.get(i));
        }
        return dataList;
    }
}
