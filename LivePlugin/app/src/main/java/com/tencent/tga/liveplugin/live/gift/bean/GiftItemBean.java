package com.tencent.tga.liveplugin.live.gift.bean;
public class GiftItemBean {
    public String boxid;// 宝箱ID
    public String pic_url;// url地址
    public String thumb;// hover缩略图
    public String tip;// 缩略图提示
    public int recv_state;// 领取状态, 0 待领取, 1 已领取
    public int recv_time;// 领取所需时间
    public int level;// 宝箱等级
    public String name;// 宝箱名称

    public GiftItemBean() {
    }

    public GiftItemBean(String boxid, String pic_url, String thumb, String tip, int recv_state, int recv_time, int level, String name) {
        this.boxid = boxid;
        this.pic_url = pic_url;
        this.thumb = thumb;
        this.tip = tip;
        this.recv_state = recv_state;
        this.recv_time = recv_time / 1000;
        this.level = level;
        this.name = name;
    }
}
