package com.tencent.tga.liveplugin.networkutil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tencent.common.log.tga.TLog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by hyqiao on 2016/2/26.
 */
public class NetUtils {

    private static String TAG = "NetUtils";
    //获取本机IP地址
//    public static String GetIP(Context context){
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
//        }
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        String ip = intToIp(ipAddress);
//        return ip;
//    }
    public static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    //获取本机IP地址
    public static String getLocalIPAddress(){
        try {
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en!=null&&en.hasMoreElements();){
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() &&(inetAddress instanceof Inet4Address)){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG,"getLocalIPAddress 失败");
            //e.printStackTrace();
        }
        return "IpIsNull";
    }


    public final static int MOBILE_NET = 1;
    public final static int WIFI_NET = 2;
    public final static int NO_NET = 3;
    /**返回当前网络状况：1表示移动网络，2表示wifi,3表示无网络连接
     * @param context
     * @return
     */
    public static int NetWorkStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);//移动网络
        //NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//wifi
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();//获取当前有效网络名称
        if(activeInfo != null){
            if(activeInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                return MOBILE_NET;
            }else if(activeInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return WIFI_NET;
            }else{
                return NO_NET;
            }
        }else{
            return NO_NET;
        }
    }

    /**当前网络是否有效
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
            } else {
                //如果仅仅是用来判断网络连接
                //则可以使用 cm.getActiveNetworkInfo().isAvailable();
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
    * 上报用到的网络状态
    * @author hyqiao
    * @time 2016/11/8 11:18
    */
    public static int getReportNetStatus(Context context){
        int net_status;
        if(context == null)
            return -1;
        if(NetUtils.NetWorkStatus(context) == NetUtils.MOBILE_NET){
            net_status = 1;
        }else if(NetUtils.NetWorkStatus(context) == NetUtils.WIFI_NET){
            net_status = 0;
        }else{
            net_status = 2;
        }
        return net_status;
    }

}
