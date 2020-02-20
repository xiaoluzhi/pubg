package com.tencent.tga.liveplugin.live.right.schedule.bean;

import java.util.ArrayList;

public class TeamBankBean {
    private String teamid;
    private String team_name;
    private String team_short_name;
    private String team_logo;
    private String total_score;
    private int bo_count;
    private ArrayList<TeamScoreBean> list;

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_short_name() {
        return team_short_name;
    }

    public void setTeam_short_name(String team_short_name) {
        this.team_short_name = team_short_name;
    }

    public String getTeam_logo() {
        return team_logo;
    }

    public void setTeam_logo(String team_logo) {
        this.team_logo = team_logo;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public int getBo_count() {
        return bo_count;
    }

    public void setBo_count(int bo_count) {
        this.bo_count = bo_count;
    }

    public ArrayList<TeamScoreBean> getList() {
        return list;
    }

    public void setList(ArrayList<TeamScoreBean> list) {
        this.list = list;
    }
}
