package com.tencent.tga.liveplugin.base.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.tencent.common.log.tga.TLog;

import java.lang.reflect.Method;

public class DeviceUtils {

	/**
	 * IMEI
	 */
	public static String deviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		return deviceId == null ? "" : deviceId;
	}

	/**
	 * 判断CPU是否是X86
	 */
	public static boolean isX86() {
		String cup = Build.CPU_ABI;
		if (cup != null && cup.toLowerCase().contains("arm")) {
			return false;
		}
		if (cup != null && cup.toLowerCase().contains("x86")) {
			return true;
		}
		return true;
	}

	public static String getCPUABI() {
		String CPU_ABI = android.os.Build.CPU_ABI;
		String CPU_ABI2 = "none";
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) { // CPU_ABI2 since 2.2
			try {
				CPU_ABI2 = (String)android.os.Build.class.getDeclaredField("CPU_ABI2").get(null);
			} catch (Exception e) { }
		}
		return new StringBuilder(CPU_ABI).append(CPU_ABI2).toString();
	}

	public static boolean isVM() {
		boolean isVM = false;
		try {
			if ("google_sdk".equals(Build.PRODUCT) ||
					"sdk_google_phone_x86".equals(Build.PRODUCT) ||
					"sdk".equals(Build.PRODUCT) ||
					"sdk_x86".equals(Build.PRODUCT) ||
					"vbox86p".equals(Build.PRODUCT) ||
					Build.FINGERPRINT.contains("generic") ||
					Build.MANUFACTURER.contains("Genymotion") ||
					Build.MODEL.contains("Emulator") ||
					Build.MODEL.contains("Android SDK built for x86")) {
				isVM = true;
			}

			if (Build.BRAND.contains("generic") && Build.DEVICE.contains("generic")) {
				isVM = true;
			}

		} catch (Exception e) {
		}
		return isVM;
	}

	public static RunningAppProcessInfo currentProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (manager !=null)
		{
			for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
				if (processInfo.pid == pid) {
					return processInfo;
				}
			}
		}
		return new RunningAppProcessInfo("Empty", -1, null);
	}

	/**
	 * 得到设备屏幕的宽度
	 */
	public static int[] getScreenSize(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		if (context instanceof Activity) {
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		} else {
         	displayMetrics = context.getResources().getDisplayMetrics();
		}
		return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
	}

	/**
	 * 得到设备屏幕的宽度
	 * 横屏时屏幕的宽度大于高度，宽度取两者大的
	 */
	public static int getScreenWidth(Context context) {
		//return getScreenSize(context)[0];
		return getScreenSize(context)[0]>getScreenSize(context)[1]?getScreenSize(context)[0]:getScreenSize(context)[1];
	}

	/**
	 * 得到设备屏幕的高度
	 * 横屏时屏幕的宽度大于高度，高度取两者小的
	 */
	public static int getScreenHeight(Context context) {
		//return getScreenSize(context)[1];
		return getScreenSize(context)[0]>getScreenSize(context)[1]?getScreenSize(context)[1]:getScreenSize(context)[0];
	}

	/**
	 * 得到设备的密度
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static boolean isSmallScreen(Context context) {
		return getScreenHeight(context) < 720 && getScreenWidth(context) < 1280;
	}

	/**
	 * 根据手机分辨率从dp转成px
	 *
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = getScreenDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = getScreenDensity(context);
		return (int) (pxValue / scale + 0.5f) - 15;
	}

	/**
	 * 根据手机分辨率从dp转成px
	 *
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static float dip2pxF(Context context, float dpValue) {
		final float scale = getScreenDensity(context);
		return (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机分辨率从sp转成px
	 *
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static float sp2pxF(Context context, float spValue) {
		final float scale = getScreenDensity(context);
		return (spValue * scale + 0.5f);
	}

	public static float getById(Context context, int id) {
		return context.getResources().getDimension(id);
	}

	public static boolean isOppo() {
		if ("OPPO".equals(Build.MANUFACTURER)) {
			return true;
		}
		return false;
	}

	//有没有虚拟按键的判断
	public static boolean hasVirtualBar(Context context) {
		int vh = 0;
		int vw = 0;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
			vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
			vw = dm.widthPixels - windowManager.getDefaultDisplay().getWidth();
			TLog.e("hasVirtualBar","vh : "+vh);
			TLog.e("hasVirtualBar","vw : "+vw);
			if(vh != 0 || vw != 0){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}
