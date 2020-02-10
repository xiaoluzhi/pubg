package com.tencent.tga.liveplugin.report;

import android.content.Context;
import android.os.Build;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.tga.liveplugin.base.util.TimeUtils;
import com.tencent.tga.liveplugin.networkutil.NetUtils;

/**
 * Created by hyqiao on 2016/11/8.
 */

public class VideoMonitorReport {
    private Context mContext;
    private int position = 1;//(1：直播；2：视频；3：弹窗)
    private long bufferStartTime = 0;
    private long bufferEndTime = 0;

    private static VideoMonitorReport monitorReport;

    private VideoMonitorReport(Context mContext) {
        this.mContext = mContext;
    }

    public static VideoMonitorReport getInstance(Context context) {
        if (monitorReport == null){
            monitorReport = new VideoMonitorReport(context);
        }
        return monitorReport;
    }

    public void unInit(){
        monitorReport = null;
        mContext = null;

    }

    /**
     * 进入视频播放页面的时候调用，进入设置为2，退出设置为1；
     * @author hyqiao
     * @time 2016/10/19 11:01
     */
    public void setVideoQualityPosition(int position){
        this.position = position;
        bufferStartTime = System.currentTimeMillis();
    }
    /**
     * 开始缓冲调用
     * @author hyqiao
     * @time 2016/10/19 11:02
     */
    public void setReportVideoQualityStartTime(){
        bufferStartTime = System.currentTimeMillis();
    }
    /**
     * 缓冲完成调用
     * @author hyqiao
     * @time 2016/10/19 11:02
     */
    public void setReportVideoQualityEndTime(String mOpenid, String vid,int isFirst,String mdefine,int streamtype){
        bufferEndTime = System.currentTimeMillis();
        long period = bufferEndTime-bufferStartTime;
        if(period>5*60*1000){
            return;
        }
        reportVideoQuality(mOpenid,vid,isFirst,period,mdefine,streamtype);
    }
    //isFirst（1：首次；2：非首次）
    public void reportVideoQuality(String mOpenid,String vid,int isFirst,long period,String mdefine,int streamtype){
        ReportManager.getInstance().report_VideoQuality(new ReportBean("TGAVideoPlayMonitor", getVideoQualityForm(mContext,mOpenid,position,vid,isFirst,period,mdefine,streamtype)));
    }
    public static String getVideoQualityForm(Context context,String mOpenid, int source, String vid, int isFirst, long period, String mdefine,int streamtype){
        String reportContent = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                mOpenid, Configs.CLIENT_TYPE,
                Build.MODEL, Configs.plugin_version+"",TimeUtils.getCurrentTime(),source, NetUtils.getReportNetStatus(context),vid,isFirst,
                period,mdefine,TimeUtils.getCurrentTime(),streamtype);
        return reportContent;
    }


    public void report_TGAVideoLoadMonitor(String mOpenid,int source,int errorModel,int errorWhat,String vid,String mdefine){
        ReportManager.getInstance().report_TGAVideoLoadMonitor(new ReportBean("TGAVideoLoadMonitor", getVideoLoadFailForm(mContext,mOpenid,source,errorModel,errorWhat,vid,mdefine)));
    }

    public static String getVideoLoadFailForm(Context context,String mOpenid,int source,int errorModel,int errorWhat,String vid,String mdefine){
        String reportContent = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                mOpenid,Configs.CLIENT_TYPE,
                Build.MODEL,Configs.plugin_version+"",source, NetUtils.getReportNetStatus(context),errorModel,errorWhat,vid,
                mdefine,TimeUtils.getCurrentTime());
        return reportContent;
    }

    public static String getTGAPoptvPeriodForm(Context context,String openID,String timestamp,int count,String vid,int areaid,String banner_type){
        String reportContent = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                openID,timestamp,Build.MODEL,Configs.plugin_version+"",TimeUtils.getCurrentTime(),
                count,vid, NetUtils.getReportNetStatus(context),Configs.CLIENT_TYPE,
                Configs.GAME_ID,areaid,banner_type );
        return reportContent;
    }

    /**
     * 清晰度选择操作
     * sd 标清 ：1
     * hd 高清 ：2
     * shd 超清 : 3
     * fhd 蓝光 ：4
     * @author hyqiao
     * @time 2016/11/4 14:48
     */
    public static int getDefinitionIndex(String Definition){
        if(Definition.equals("sd")){
            return 1;
        }else if(Definition.equals("hd")){
            return 2;
        }else if(Definition.equals("shd")){
            return 3;
        }else if(Definition.equals("fhd")){
            return 4;
        }else {
            return 0;//其他清晰度
        }
    }
}
