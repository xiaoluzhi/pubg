package com.tencent.tga.liveplugin.report;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.common.log.tga.TLogForReport;
import com.tencent.tga.liveplugin.base.util.TimeUtils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by hyqiao on 2016/10/25.
 */
public class PopTvReport {
    private static String TAG = "PopTvReport";
    private static PopTvReport popTvReport;
    public static PopTvReport getInstance() {
        if (popTvReport == null){
            popTvReport = new PopTvReport();
        }
        return popTvReport;
    }

    private Vector<ReportBean> mList;
    private WeakReference<Activity> mWeakReferenceActivity;

    //source(1：直播；2：视频；3：弹窗)
    private Timer timer;
    private TimerTask task;
    private final static int mReportPeriod = 10*1000;//10S上传一次
    private final static int mMaxReportTime = 12*60*2;//最多上报12小时
    private String timestamp;//每次上报都是初始化的时间戳
    private int count = 0;//统计打点的次数
    private String mOpenID,mVid;
    private String mGameUid = "";
    private int mAreaID;

    public int isFirst = 1;
    public boolean isReportLoadFail = false;//视频加载失败上报一次
    private long bufferStartTime = 0;
    private long bufferEndTime = 0;

    public static String mBannerInfo = "0";

    public void init(Activity activity, String mOpenID,String gameuid, String mVid, int mAreaID){
        mWeakReferenceActivity = new WeakReference<>(activity);
        this.mOpenID = mOpenID;
        this.mGameUid = gameuid;
        this.mVid = mVid;
        this.mAreaID = mAreaID;

        isFirst = 1;
        isReportLoadFail = false;//视频加载失败上报一次
        bufferStartTime = System.currentTimeMillis();//初始化一次开始时间，为第一次缓冲时常上报做准备
        bufferEndTime = 0;
        mList = new Vector<>();
        count = 0;


        timestamp = TimeUtils.getCurrentTime();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                doTask();
            }
        };
        timer.schedule(task, 0, mReportPeriod);
    }

    private void doTask(){
        try{
            count++;
            if(count>=mMaxReportTime){
                release();
                return;
            }
            if(mList!= null){
                mList.add(new ReportBean("TGAPoptvPeroid_hpjy",VideoMonitorReport.getTGAPoptvPeriodForm(mWeakReferenceActivity.get(), mOpenID, timestamp,count,mVid,mAreaID,mBannerInfo)));

                reportPopTv(mList);
                if(mList!=null){
                    mList.clear();
                }
            }
        }catch (Throwable a)
        {
            TLog.e(TAG,"list 同步异常 : "+a.getMessage());
        }
    }
    public void release(){
        TLog.e(TAG,"PopTvReport release");
        try{
            if (timer != null) {
                timer.purge();
                timer.cancel();
                timer = null;
            }
            if (task !=null)
            {
                task.cancel();
            }
            if(mList!=null){
                mList.clear();
                mList = null;
            }
            popTvReport = null;
        }catch (Exception e){
            TLog.e(TAG,"弹窗定时器释放异常 ："+e.getMessage());
        }

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
    public void setReportVideoQualityEndTime(String mOpenid,String vid,int isFirst,String mDefine,int streamtype){
        bufferEndTime = System.currentTimeMillis();
        long period = bufferEndTime-bufferStartTime;

        if(period>5*60*1000){
            return;
        }
        reportVideoQuality(mOpenid,vid,isFirst,period,mDefine,streamtype);
    }
    //isFirst（1：首次；2：非首次）
    public void reportVideoQuality(String mOpenid,String vid,int isFirst,long period,String mDefine,int streamtype){
        if(null != mWeakReferenceActivity && null != mWeakReferenceActivity.get()){
            String reportContent = VideoMonitorReport.getVideoQualityForm(mWeakReferenceActivity.get(), mOpenid ,3, vid, isFirst, period, mDefine,streamtype);
            if(mList!= null) {
                mList.add(new ReportBean("TGAVideoPlayMonitor_hpjy", reportContent));
            }
        }
    }

    public void report_TGAVideoLoadMonitor(String mOpenid,int errorModel,int errorWhat,String vid,String mdefine){
        if(null != mWeakReferenceActivity && null != mWeakReferenceActivity.get()){
            String reportContent = VideoMonitorReport.getVideoLoadFailForm(mWeakReferenceActivity.get(),mOpenid,3,errorModel,errorWhat,vid,mdefine);
            if(mList!= null) {
                mList.add(new ReportBean("TGAVideoLoadMonitor_hpjy", reportContent));
            }
        }
    }

    ReportHttpProxy reportHttpProxy = new ReportHttpProxy();
    ReportHttpProxy.Param reportHttpProxyParam = new ReportHttpProxy.Param();
    public void reportPopTv(Vector<ReportBean> list){
        try {
            if(reportHttpProxyParam.list!=null){
                reportHttpProxyParam.list.clear();
            }else {
                reportHttpProxyParam.list = new Vector<>();
            }
            reportHttpProxyParam.uid = mGameUid;
            reportHttpProxyParam.areaId = mAreaID+"";
            reportHttpProxyParam.list.addAll(list);

            for(int i = 0;list != null && i<list.size();i++){
                TLogForReport.e(TAG,String.format("%s %s",list.get(i).getKey(),list.get(i).getValue()));
            }

            reportHttpProxy.postReq(mWeakReferenceActivity.get(), new HttpBaseUrlWithParameterProxy.Callback() {
                @Override
                public void onSuc(int i) {

                    if(reportHttpProxyParam.result==0){
                        TLog.e(TAG,"ReportHttpProxy success");
                    }else {
                        TLog.e(TAG,"ReportHttpProxy callback with exception code : "+reportHttpProxyParam.result);
                    }
                }

                @Override
                public void onFail(int i) {
                    TLog.e(TAG,"ReportHttpProxy onFail : "+i);
                }
            },reportHttpProxyParam);
        } catch (Exception e) {
            TLog.e(TAG,"数据处理异常 : "+e.getMessage());
        }
    }
}
