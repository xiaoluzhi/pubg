package com.tencent.tga.liveplugin.networkutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by hyqiao on 2016/4/1.
 */
public class VersionUtil {

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            //versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            TLog.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 返回当前插件版本号，自定义的
     * @return
     */
    public static int getPluginVersionCode() {
        int versionCode = 0;
        try {
            versionCode = Configs.plugin_version;
            //versioncode = pi.versionCode;
        } catch (Exception e) {
            TLog.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * 返回当前程序版本号
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            //versioncode = pi.versionCode;
        } catch (Exception e) {
            TLog.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    public static String machineCode = "";

//机器码
    @SuppressLint("MissingPermission")
    public static synchronized String getMachineCode(Context context)
    {
        if(!TextUtils.isEmpty(machineCode)) return machineCode;
        try{
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String tmDevice = "";
            String tmSerial = "";

            if(tm != null){
                tmDevice = "getDeviceId";
                tmSerial = "getSimSerialNumber";
            }
            String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String uniqueId = deviceUuid.toString();
            if(!TextUtils.isEmpty(uniqueId)){
                machineCode = md5(uniqueId);
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

        if(TextUtils.isEmpty(machineCode)){
            machineCode = getAndroidUUID(context);
        }

        if(!TextUtils.isEmpty(machineCode)){
            return machineCode;
        }
        return "MachineCodeIsNull";
    }
    private static String getAndroidUUID(Context context) {
        try {
            String uuid_sp = getSpUUID(context);
            if(!TextUtils.isEmpty(uuid_sp)){
                return uuid_sp;
            }
            String uuid = md5(UUID.randomUUID().toString());
            setSpUUID(context,uuid);
            return uuid;
        }catch (Throwable e){
            e.printStackTrace();
        }
        return "";
    }

    private static String UUID_TAG = "UUID_TAG";
    private static String getSpUUID(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_TAG, Context.MODE_PRIVATE);
            return sharedPreferences.getString("uuid", "");
        }catch (Throwable t){
            t.printStackTrace();
        }
        return "";
    }

    private static void setSpUUID(Context context,String value) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(UUID_TAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uuid", value);
            editor.commit();
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String getModelWithURLEncode(){
        //return URLEncoder.encode(Build.MODEL);
        return Uri.encode(Build.MODEL);
        //return Build.MODEL.replace(" ", "");
    }

}
