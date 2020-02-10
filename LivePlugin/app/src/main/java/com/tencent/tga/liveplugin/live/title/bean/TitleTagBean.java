package com.tencent.tga.liveplugin.live.title.bean;

/**
 * Created by hyqiao on 2017/5/16.
 */

public class TitleTagBean {
    private String name;
    private int id;
    private String iconUrl;
    private String openUrl;

    private boolean isTagVisible;
    private boolean isRedPotVisible;

    public TitleTagBean(String name, int id, String iconUrl, String openUrl, boolean isTagVisible, boolean isRedPotVisible) {
        this.name = name;
        this.id = id;
        this.iconUrl = iconUrl;
        this.openUrl = openUrl;
        this.isTagVisible = isTagVisible;
        this.isRedPotVisible = isRedPotVisible;
    }


    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getOpenUrl() {
        return openUrl;
    }

    public void setOpenUrl(String openUrl) {
        this.openUrl = openUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTagVisible() {
        return isTagVisible;
    }

    public void setTagVisible(boolean tagVisible) {
        isTagVisible = tagVisible;
    }

    public boolean isRedPotVisible() {
        return isRedPotVisible;
    }

    public void setRedPotVisible(boolean redPotVisible) {
        isRedPotVisible = redPotVisible;
    }
}
