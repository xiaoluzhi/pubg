package com.tencent.tga.liveplugin.report;

/**
 * Created by hyqiao on 2016/9/14.
 * 数据上报结构体
 */
public class ReportBean {
    private String key = "";
    private String value = "";

    public ReportBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key+"--"+value;
    }
}
