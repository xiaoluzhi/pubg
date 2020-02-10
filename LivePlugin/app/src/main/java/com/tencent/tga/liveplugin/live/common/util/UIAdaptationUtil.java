package com.tencent.tga.liveplugin.live.common.util;

import com.tencent.common.log.tga.TLog;

import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;

public class UIAdaptationUtil {

    //private static String[] cameraHoleArray = {"VCE-AL00","VCE-TL00","VCE-L22","PCT-AL10","PCT-TL10","PCT-L29","SM-G8870"};
    private static ArrayList<String> cameraHoleList = new ArrayList<>();
    public static void initCameraHoleList(String str){
        try {
            if(!TextUtils.isEmpty(str)){
                String[] modelArr = str.split("\\|");
                for(int i= 0;modelArr != null && i<modelArr.length;i++){
                    cameraHoleList.add(modelArr[i]);
                }
            }else {
                addDefaultModel();
            }
        }catch (Exception e){
            addDefaultModel();
        }
    }

    private static void addDefaultModel(){
        cameraHoleList.add("VCE-AL00");
        cameraHoleList.add("VCE-TL00");
        cameraHoleList.add("VCE-L22");
        cameraHoleList.add("PCT-AL10");
        cameraHoleList.add("PCT-TL10");
        cameraHoleList.add("PCT-L29");
        cameraHoleList.add("SM-G8870");
    }
    public static boolean hasCameraHole(){
        try {
            String phone_model = Build.MODEL;
            TLog.e("UIAdaptationUtil", "phone_model is " + phone_model);
            for(int i = 0;cameraHoleList != null && i<cameraHoleList.size();i++){
                if (!TextUtils.isEmpty(phone_model) && phone_model.equals(cameraHoleList.get(i))){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
