package com.tencent.tga.liveplugin.live.player.ui.video.bean;

/**
 * Created by hyqiao on 2018/4/20.
 */

public class HotWordBean {
    public int id;
    public String content;

    public HotWordBean(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
