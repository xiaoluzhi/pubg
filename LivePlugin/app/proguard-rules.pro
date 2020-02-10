# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5                                                           # 指定代码的压缩级别
-dontusemixedcaseclassnames                                                     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses                                                # 是否混淆第三方jar
-dontpreverify                                                                  # 混淆时是否做预校验
-verbose                                                                        # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法
-keep public class * extends android.app.Activity                               # 保持哪些类不被混淆
-keep public class * extends android.app.Application                            # 保持哪些类不被混淆
-keep public class * extends android.app.Service                                # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver                  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider                    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper               # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference                      # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService              # 保持哪些类不被混淆
-keep public com.tencent.tga.liveplugin.live.LivePlayerActivity

-keep androidx.viewpager.widget.** {*;}
-keep class com.ryg.** {*;}
-keep class com.loopj.android.tgahttp.** {*;}

-keepclasseswithmembernames class * {                                           # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {                                               # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
   public <init>(android.content.Context, android.util.AttributeSet, int);        # 保持自定义控件类不被混淆
}

-keepclassmembers class * extends android.app.Activity {                        # 保持自定义控件类不被混淆
   public void *(android.view.View);
}

-keepclassmembers enum * {                                                      # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {                                # 保持 Parcelable 不被混淆
  public static final android.os.Parcelable$Creator *;
}

-ignorewarnings

-keepattributes Exceptions,InnerClasses

-keep class pi.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.httpproxy.api.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.nonp2pproxy.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.p2pproxy.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.httpproxy.CKeyFacade {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.api.TVK_IMediaPlayer$* {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.api.** {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.api.TVK_IMediaPlayer$* {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.view.TVK_PlayerVideoView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, boolean, boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.tencent.qqlive.mediaplayer.view.TVK_PlayerVideoView_Scroll {
    public <init>(android.content.Context);
    public <init>(android.content.Context, boolean, boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.tencent.qqlive.mediaplayer.playernative.** {
    <fields>;
    <methods>;
}

-keep class  com.tencent.qqlive.mediaplayer.view.IVideoViewBase{
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.videoad.VideoPreAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoMidAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoPauseAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoIvbAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep  class com.tencent.qqlive.mediaplayer.videoad.VideoPostrollAdImpl{
    public <init>(android.content.Context, com.tencent.qqlive.mediaplayer.view.IVideoViewBase,  java.lang.Object);
}

-keep class  com.tencent.qqlive.mediaplayer.bullet.BulletController{
   <fields>;
   <methods>;
}

-keep class  com.tencent.qqlive.mediaplayer.bullet.api{
   <fields>;
   <methods>;
}
-keep class  com.tencent.qqlive.mediaplayer.playerController.api.**{
   <fields>;
   <methods>;
}

-keep class  com.tencent.qqlive.ona.base.AppLaunchReporter{
   <fields>;
   <methods>;
}

-keep class com.qq.taf.** {*;}
-keep class com.tencent.ads.** {*;}
-keep class com.tencent.omg.stat.** {*;}
-keep class vspi.** {*;}

-keep class com.tencent.qqlive.api.** {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.PlayerController.MediaController {
   <fields>;
   <methods>;
}


-keep class com.tencent.qqlive.mediaplayer.player.PlayerImageCapture$* {
    <fields>;
    <methods>;
}

-keep class com.tencent.updata.jni.** {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.dlna.DlnaManager {
    <methods>;
 }
-keep class org.cybergarage.xml.parser.XmlPullParser {
    <fields>;
    <methods>;
}
-keep class org.cybergarage.xml.parser.JaxpParser {
    <fields>;
    <methods>;
}
-keep class org.cybergarage.xml.parser.kXML2Parser {
    <fields>;
    <methods>;
}
-keep class org.cybergarage.xml.parser.XercesParser {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.ona.base.AppLaunchReporter {
    <fields>;
    <methods>;
}
-keep class com.tencent.ads.utility.AdSetting {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.UIController {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.playerController.Loading {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.playerController.MediaControllerView {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.uicontroller.recommendController.LimitPlayView {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.mediaplayer.recommend.RecommendInfo {
    <fields>;
    <methods>;
}

-keep class com.tencent.qqlive.mediaplayer.uicontroller.UIControllerListener {
    <fields>;
    <methods>;
}
-keep class com.tencent.ads.service.AppAdConfig {
    <fields>;
    <methods>;
}
-keep class com.tencent.qqlive.ck.** {*;}
-keep class com.tencent.qqlive.downloadproxy.** {*;}
-keep class com.tencent.qqlive.moduleupdate.** {*;}
-keep class com.tencent.qqlive.multimedia.** {*;}
-keep class com.tencent.ads.** {*;}
-keep class com.tencent.omg.** {*;}
-keep class cn.com.iresearch.dau {*;}
-keep class pi.** {*;}
-keep class com.qq.taf.** {*;}