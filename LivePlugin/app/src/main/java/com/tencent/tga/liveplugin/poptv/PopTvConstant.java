package com.tencent.tga.liveplugin.poptv;

import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by hyqiao on 2017/11/6.
 */

public class PopTvConstant {
    private static ArrayList<String> popExitBlackList = new ArrayList<>();
    public static void initPopExitBlackList(String str){
        try {
            if(!TextUtils.isEmpty(str)){
                String[] modelArr = str.split("\\|");
                for(int i= 0;modelArr != null && i<modelArr.length;i++){
                    popExitBlackList.add(modelArr[i]);
                }
            }else {
                addDefaultModel();
            }
        }catch (Exception e){
            addDefaultModel();
        }
    }

    private static void addDefaultModel(){
        popExitBlackList.add("PRO 7 Plus");
        popExitBlackList.add("PRO 7");
        popExitBlackList.add("ONEPLUS A5000");
    }

    public static boolean isExitAnimReady(){
        for(int i = 0;popExitBlackList != null && i<popExitBlackList.size();i++){
            if(Build.MODEL.equals(popExitBlackList.get(i))){
                return false;
            }
        }
        return true;
    }
}
