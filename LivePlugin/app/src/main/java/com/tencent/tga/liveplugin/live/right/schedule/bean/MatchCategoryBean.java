package com.tencent.tga.liveplugin.live.right.schedule.bean;


/**
 * Created by hyqiao on 2017/3/30.
 */

public class MatchCategoryBean {
    public int date;
    public boolean isDate = false;
    public RaceInfoBean mRaceInfoBean;

    public MatchDayInfoBean.MatchDayListBean.MatchListBean matchListBean;

    public MatchDayInfoBean.MatchDayListBean.MatchListBean getMatchListBean() {
        return matchListBean;
    }

    public void setMatchListBean(MatchDayInfoBean.MatchDayListBean.MatchListBean matchListBean) {
        this.matchListBean = matchListBean;
    }

    public MatchCategoryBean(int date, boolean isDate) {
        this.date = date;
        this.isDate = isDate;
    }

    /*public MatchCategoryBean(RaceInfo raceInfo) {
        this.mRaceInfoBean = new RaceInfoBean(raceInfo);
    }*/

    public MatchCategoryBean(MatchDayInfoBean.MatchDayListBean.MatchListBean matchListBean){
        this.matchListBean = matchListBean;
    }


    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }

    public RaceInfoBean getmRaceInfoBean() {
        return mRaceInfoBean;
    }

    public void setmRaceInfoBean(RaceInfoBean mRaceInfoBean) {
        this.mRaceInfoBean = mRaceInfoBean;
    }
}
