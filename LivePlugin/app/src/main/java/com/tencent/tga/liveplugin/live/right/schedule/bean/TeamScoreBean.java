package com.tencent.tga.liveplugin.live.right.schedule.bean;

public class TeamScoreBean {
    private int rank_score;
    private int eliminlate_score;
    private int bo_score;

    public int getRank_score() {
        return rank_score;
    }

    public void setRank_score(int rank_score) {
        this.rank_score = rank_score;
    }

    public int getEliminlate_score() {
        return eliminlate_score;
    }

    public void setEliminlate_score(int eliminlate_score) {
        this.eliminlate_score = eliminlate_score;
    }

    public int getBo_score() {
        return bo_score;
    }

    public void setBo_score(int bo_score) {
        this.bo_score = bo_score;
    }

    @Override
    public String toString() {
        return "TeamScoreBean{" +
                "rank_score=" + rank_score +
                ", eliminlate_score=" + eliminlate_score +
                ", bo_score=" + bo_score +
                '}';
    }
}
