package com.tencent.tga.liveplugin.live.common.bean;

import com.tencent.common.log.tga.TLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by agneswang on 2017/2/8.
 */

public class ConfigInfo {
    private static final String TAG = "ConfigInfo";
    private static final String CONFIG_OPEN = "1";

    public static final String DANMA_SWITCH = "danmu_switch";

    public static final String SCHEDULE_H5_URL = "schedule_h5_url";
    public static final String BOX_SWITCH = "box_switch";
    public static final String BOX_ICON = "box_icon";
    public static final String BOX_ANIM_SWITCH = "box_anim_switch";

    public static final String SCORE_RANK_SWITCH = "score_rank_switch";
    public static final String SCORE_RANK_URL = "score_rank_url";

    private static List<String> mFunctionReqList = new ArrayList<>();
    private HashMap<String, String> mConfigMap = new HashMap();

    private static  volatile  ConfigInfo mInstance;

    public static ConfigInfo getmInstance(){
        if (mInstance == null) mInstance = new ConfigInfo();
        return mInstance;
    }

    public void  clear(){
        mConfigMap.clear();
        mInstance= null;
    }

    public static class FuntionItemStruct{
        public String key;
        public String value;

        public FuntionItemStruct(String key, String value) {
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
    }
    public void intData(List<FuntionItemStruct> functionItems) {
        try {
            for (FuntionItemStruct item : functionItems) {
                mConfigMap.put(item.key,
                        item.value);
            }
        } catch (Exception e) {
            TLog.e(TAG, "parse config exception");
        }
    }

    public void intData(JSONObject jsonObject) {
        try {
            for(int i= 0;getFunctionReqList() != null && i<getFunctionReqList().size();i++){
                String key = getFunctionReqList().get(i);
                mConfigMap.put(key, jsonObject.optString(key));
            }
        } catch (Exception e) {
            TLog.e(TAG, "parse config exception");
        }
    }


    public boolean getConfig(String key) {
        if (null != mConfigMap) {
            return CONFIG_OPEN.equals(mConfigMap.get(key));
        }
        return false;
    }

    public String getStringConfig(String key) {
        if (null != mConfigMap) {
            return mConfigMap.get(key);
        }
        return "";
    }

    /**
     请求开关项列表
     后续添加开关项请求在此添加
     */
    public static List<String> getFunctionReqList() {
        mFunctionReqList.clear();
        mFunctionReqList.add(DANMA_SWITCH);
        mFunctionReqList.add(SCHEDULE_H5_URL);
        mFunctionReqList.add(BOX_SWITCH);
        mFunctionReqList.add(BOX_ICON);
        mFunctionReqList.add(BOX_ANIM_SWITCH);
        mFunctionReqList.add(SCORE_RANK_SWITCH);
        mFunctionReqList.add(SCORE_RANK_URL);
        return mFunctionReqList;
    }

    @Override
    public String toString() {
        return "ConfigInfo{" +
                "mConfigMap=" + mConfigMap +
                '}';
    }
}
