package com.tencent.tga.liveplugin.live.right.schedule.bean;

/**
 * Created by hyqiao on 2017/3/31.
 */

public class MatchViewBean {
    private String left_name;
    private String right_name;
    private int type = -1;//0 直播 ；1 直播结束无回放 ；2 回放 ；3 未订阅 ；4 已经订阅
    private String time = "10:30";

    public MatchViewBean(String left_name, String right_name, int type) {
        this.left_name = left_name;
        this.right_name = right_name;
        this.type = type;
    }

    public String getLeft_name() {
        return left_name;
    }

    public void setLeft_name(String left_name) {
        this.left_name = left_name;
    }

    public String getRight_name() {
        return right_name;
    }

    public void setRight_name(String right_name) {
        this.right_name = right_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
