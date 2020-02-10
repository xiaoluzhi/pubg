package com.tencent.tga.liveplugin.webview;

import com.tencent.common.log.tga.TLog;
import com.tencent.msdk.api.WGPlatform;
import com.tencent.msdk.api.eQQScene;
import com.tencent.msdk.api.eWechatScene;
import com.tencent.tga.imageloader.core.assist.FailReason;
import com.tencent.tga.imageloader.core.listener.ImageLoadingListener;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.ToastUtil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * Created by agneswang on 2017/10/12.
 */

public class ShareUtils {

    private static final String TAG = "ShareUtils";

    private static final String SHARE_TO_WX = "1";
    private static final String SHARE_TO_FRIEND = "2";
    private static final String SHARE_TO_QQ = "3";
    private static final String SHARE_TO_QZONE = "4";

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        boolean isQQExist = isApkExist(context,"com.tencent.mobileqq");
        boolean isQQLiteExist = isApkExist(context,"com.tencent.qqlite");
        if(isQQExist || isQQLiteExist){
            return true;
        }else {
            ToastUtil.show(context, "QQ未安装");
            return false;
        }
    }

    public static boolean isApkExist(Context context,String packageName){
        if (packageName == null || "".equals(packageName))
        {
            TLog.e(TAG, packageName+" not install");
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            TLog.e(TAG, packageName+" install");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            TLog.e(TAG, packageName+" not install");
            return false;
        }
    }
    /**
     * 分享网页
     *
     * @param title   标题
     * @param summary 摘要
     * @param icon    缩略图
     * @param url     网址
     * @param type   分享类型。1(微信),2(朋友圈),3(QQ) 4(QQ空间)
     **/
    public static void shareGuessWebPage(Context context, final String title, final String summary, String icon, final String url, final String type) {
        try {
            TLog.e(TAG, String.format("title is %s summary is %s icon is %s url is %s type is %s", title, summary, icon
                    , url, type));
            if (SHARE_TO_WX.equals(type) || SHARE_TO_FRIEND.equals(type)) {
                if(isApkExist(context,"com.tencent.mm")){
                    ImageLoaderUitl.loadimage(icon, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            TLog.e(TAG,"onLoadingFailed");
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            if (null != bitmap) {
                                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteStream);
                                if(SHARE_TO_WX.equals(type)){
                                    WGPlatform.WGSendToWeixinWithUrl(eWechatScene.WechatScene_Session,title,summary,url,"MSG_INVITE",byteStream.toByteArray(),byteStream.toByteArray().length,"messageExt");
                                }else {
                                    WGPlatform.WGSendToWeixinWithUrl(eWechatScene.WechatScene_Timeline,title,summary,url,"MSG_INVITE",byteStream.toByteArray(),byteStream.toByteArray().length,"messageExt");
                                }
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                        }
                    });
                }else {
                    ToastUtil.show(context, "未安装微信");
                }
            } else if (SHARE_TO_QQ.equals(type)) {
                if (isQQClientAvailable(context)) {
                    WGPlatform.WGSendToQQ(eQQScene.QQScene_Session, title, summary, url, icon, icon.length());
                }

            } else if (SHARE_TO_QZONE.equals(type)) {
                if (isQQClientAvailable(context)) {
                    WGPlatform.WGSendToQQ(eQQScene.QQScene_QZone, title, summary, url, icon, icon.length());
                }
            }else {
                TLog.e(TAG,"type:"+type);
            }
        } catch (Exception e) {
            TLog.e(TAG, "share to web page exception " + e.getMessage());
            ToastUtil.show(context, "分享失败！");
        }
    }
}