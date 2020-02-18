package com.tencent.tga.liveplugin.report;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.common.log.tga.TLogForReport;
import com.tencent.tga.liveplugin.base.util.TimeUtils;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.Sessions;
import com.tencent.tga.liveplugin.networkutil.UserInfo;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * Created by hyqiao on 2016/8/10.
 * 数据上报
 */
public class ReportManager {

    private static String TAG = "ReportManager";
    private static ReportManager reportManager;
    private Vector<ReportBean> mList = new Vector<>();
    public long mBeginTime = 0;
    private int MAX_REPORT_LIMIT = 3;//达到最大条数立即上传
    private int MIN_REPORT_TIME = 1000*60;//最短时间上报
    String channelId = ""+0;//预留
    private boolean hasInit = false;

    private WeakReference<Context> mWeakReferenceContext;
    public static ReportManager getInstance() {
        if (reportManager == null){
            reportManager = new ReportManager();
        }
        return reportManager;
    }
    /**
    * 上报初始化，初始化进入电视台时间，定时器初始化
    * @author hyqiao
    * @time 2016/9/14 15:10
    */
    public void init(Context mContext){
        hasInit = true;
        mWeakReferenceContext = new WeakReference<>(mContext);
        mBeginTime = System.currentTimeMillis();
        TLog.e(TAG, "ReportManager Init finish");
    }

    public boolean hasInit(){
        return hasInit;
    }
    /**
    * 释放资源
    * @author hyqiao
    * @time 2016/9/14 15:10
    */
    public void unInit(){
        hasInit = false;
        if(mList!=null){
            mList.clear();
            mList = null;
        }
        reportManager = null;
        TLog.e(TAG, "ReportManager unInit finish");
    }

    /**
    * 通用的上报方法,通用字段已经封装好(前五个:uuid,clientType,gameId,areaId,dtEventTime)
     * isReportWithLimit : true 非立即上报；false 立即上报
    * @author hyqiao
    * @time 2017/2/13 14:40
    */
    public void commonReportFun(String report_name,boolean isReportWithLimit,String...args){
        if(args == null){
            TLog.e(TAG,"上报失败，参数为NULL");
            return;
        } else {
            if (isReportWithLimit) {
                addDataToLimitList(new ReportBean(report_name, getReportContent(args)));
            } else {
                addDataToUnLimitList(new ReportBean(report_name, getReportContent(args)));
            }
        }
    }

    /**
    * 通用的上报方法,全部字段都需要封装
     * isReportWithLimit : true 非立即上报；false 立即上报
    * @author hyqiao
    * @time 2017/2/13 14:42
    */
    public void commonReportFunAll(String report_name,boolean isReportWithLimit,String...args){
        if(args == null){
            TLog.e(TAG,"上报失败，参数为NULL");
            return;
        } else {
            if (isReportWithLimit) {
                if (Configs.Debug)
                    TLog.e(TAG, report_name + " added to LimitList ; args sizes : " +  args.length);
                addDataToLimitList(new ReportBean(report_name, getReportContentWithoutFormat(args)));
            } else {
                if (Configs.Debug)
                    TLog.e(TAG, report_name + " added to UnLimitList ; args sizes : " + args.length);
                addDataToUnLimitList(new ReportBean(report_name, getReportContentWithoutFormat(args)));
            }
        }

    }

    /**
    * 立即上报所有的数据
    * @author hyqiao
    * @time 2017/5/4 16:06
    */
    public void reportAllWithoutLimit(){
        if(mList == null)
            return;
        reportListWithOutLimit();
    }

    /**
    * 把数据加到上传列表中，满足限制条件就上传
    * @author hyqiao
    * @time 2016/11/8 17:16
    */
    private void addDataToLimitList(ReportBean rb){
        ReportBean reportBean = getMSpeedReportBean(rb);
        if(mList == null)
            return;
        mList.add(reportBean);
        reportListWithLimit();
    }

    /**
     * 把数据加到上传列表中，无限制条件上传
     * @author hyqiao
     * @time 2016/11/8 17:16
     */
    private void addDataToUnLimitList(ReportBean rb){
        ReportBean reportBean = getMSpeedReportBean(rb);
        if(mList == null)
            return;
        mList.add(reportBean);
        reportListWithOutLimit();
    }

    private ReportBean getMSpeedReportBean(ReportBean rb){
        return new ReportBean(rb.getKey()+"_hpjy",rb.getValue());
    }
    /**
    * 满足限制上传 时间和条数
    * @author hyqiao
    * @time 2016/11/8 11:15
    */
    private long last_time = 0;
    private void reportListWithLimit(){
        if(mList != null && mList.size()>=MAX_REPORT_LIMIT){
            if((System.currentTimeMillis()-last_time)<MIN_REPORT_TIME){
                TLog.e(TAG, "ReportManager Time less MIN_REPORT_TIME,not report");
                return;
            }
            reportListWithOutLimit();
            last_time = System.currentTimeMillis();
        }else {
            TLog.e(TAG, "ReportManager Count less MAX_REPORT_LIMIT,not report");
        }
    }

    /**
    * 立即上传
    * @author hyqiao
    * @time 2016/11/8 11:15
    */
    private synchronized void reportListWithOutLimit(){
        try {
            final Vector<ReportBean> list = new Vector<>();
            if(mList == null)
                return;
            list.addAll(mList);
            mList.clear();
            if(list.size()>0){
                reportTv(list);
            }
        }catch (Throwable throwable){

        }

    }
    /**
    * 电视台pv & uv（DAU）
     * 0:通过王者游戏"赛事"按钮进入 2:通过弹窗进入
    * @author hyqiao
    * @time 2016/8/right_animation_10 20:50
    */

    public void report_EnterTVInfo(int source){
        try {
            String reportContent;
            reportContent = getReportContent(NetUtils.getLocalIPAddress(), source + "", channelId, TimeUtils.getCurrentTime(), Build.MODEL, UserInfo.getInstance().mOpenid, NetUtils.getReportNetStatus(mWeakReferenceContext.get()) + "",UnityBean.getmInstance().pop_banner_type,UnityBean.getmInstance().open_tab+"");
            addDataToUnLimitList(new ReportBean("EnterTVInfo", reportContent));
        } catch (Exception e) {
            TLog.e(TAG, "report_EnterTVInfo exception");
        }
    }

    /**
    * 半屏/全屏播放人数
     * 1:半屏 2:全屏
    * @author hyqiao
    * @time 2016/8/10 20:51
    */
    public void report_ScreenNum(String roomid,int type){
        String reportContent = getReportContent(channelId,roomid,type+"");
        addDataToUnLimitList(new ReportBean("ScreenNum", reportContent));
    }

    /**
    * 用户进行弹幕开关的操作  0:关 1:开
    * @author hyqiao
    * @time 2016/11/4 14:48
    */
    public static int WORD_FLOW_OPEN = 1;
    public static int WORD_FLOW_CLOSE = 0;
    public void report_WordFlowSwitchInfo(String roomid,int SwitchType){
        String reportContent = getReportContent(channelId,roomid,SwitchType+"");
        addDataToUnLimitList(new ReportBean("WordFlowSwitchInfo", reportContent));
    }

    /**
    * 清晰度上报
    * @author hyqiao
    * @time 2016/11/4 17:35
    */
    public static String SOURCE_LIVING = "1";
    public static String SOURCE_VIDEO = "2";
    public void report_DefinitionSelectInfo(String roomid,String prevDefinition,String Definition,String destinationId,String vid){
        String reportContent = getReportContent(channelId,roomid,Definition,
                NetUtils.getReportNetStatus(mWeakReferenceContext.get())+"",prevDefinition,destinationId,vid);
        addDataToUnLimitList(new ReportBean("DefinitionSelectInfo", reportContent));
    }

    /**
    * 视频点击数 :0,底部赛程;1,视频中心
    * @author hyqiao
    * @time 2016/12/1 10:29
    */
    public static int BOTTOM_MATCH = 0;
    public static int VIDEO_CENTER = 1;
    public void report_TVVideoClick(String vid,int WatchSource){
        try {
            String reportContent = getReportContent(NetUtils.getLocalIPAddress(),vid,WatchSource+"");
            addDataToLimitList(new ReportBean("TVVideoClick", reportContent));
        }catch (Exception e){
            TLog.e(TAG,"report_TVVideoClick 失败");
        }
    }
    /**
     * 用户视频播放结束流水 :0,底部赛程;1,视频中心
     * @author hyqiao
     * @time 2016/12/1 10:29
     */
    public void report_TVVideoPlayFinish(long dtBeginTime,long dtEndTime,String vid,int WatchSource,String tglId){
        try {
            int period = (int) ((dtEndTime - dtBeginTime)/1000);
            if(period > 5*60*60)
                return;
            String reportContent = getReportContent(NetUtils.getLocalIPAddress(),TimeUtils.getCurrentTime(dtBeginTime),
                    period+"", TimeUtils.getCurrentTime(dtEndTime),vid,WatchSource+"",
                    NetUtils.getReportNetStatus(mWeakReferenceContext.get())+"",tglId);
            addDataToLimitList(new ReportBean("TVVideoPlayFinish", reportContent));
        }catch (Exception e){
            TLog.e(TAG,"report_TVVideoPlayFinish 失败");
        }
    }
    /**
     * 热词面板点击数
     * @author agneswang
     * @time 2017/2/6
     */
    public void report_TVWordBarragePanelClick(String roomid,int screenMode){
        String reportContent = getReportContent(roomid, screenMode + "");
        addDataToUnLimitList(new ReportBean("TVWordBarragePanelClick", reportContent));
    }

    /**
     * 热词发送数
     * @author agneswang
     * @time 2017/2/6
     */
    public void report_TVWordBarrageHotWordSend(String roomid, int hotwordId, int screenMode){
        String reportContent = getReportContent(roomid, hotwordId + "", screenMode + "");
        addDataToUnLimitList(new ReportBean("TVWordBarrageHotWordSend", reportContent));
    }


    /**
     * 播放暂停提醒
     * @author agneswang
     * @time 2017/2/6
     */
    public void report_PlayPause(boolean isFullscreen, boolean isLive) {
        String reportContent = getReportContent(isFullscreen? "2" : "1", isLive ? "1" : "2");
        addDataToLimitList(new ReportBean("PlayPauseWarn", reportContent));
    }


    /**
     * 用户反馈入口点击次数,入口位置: 1 电视台首页, 2 视频中心
     * @author hyqiao
     * @time 2016/12/1 11:23
     */
    public static int TV_TO_FEEDBACK = 1;
    public static int VIDEOCENTER_TO_FEEDBACK = 2;

    public void report_TVUserFeedback(int entrySource){
        String reportContent = getReportContent(entrySource+"", LiveInfo.mRoomId);
        addDataToLimitList(new ReportBean("TVUserFeedback", reportContent));
    }

    public void report_ChannelListEnter(){
        String reportContent = getReportContent(UserInfo.getInstance().mOpenid);
        addDataToLimitList(new ReportBean("Channellist_enter_hpjy", reportContent));
    }

    public void report_ChannelEntryClick(){
        String reportContent = getReportContent(UserInfo.getInstance().mOpenid);
        addDataToUnLimitList(new ReportBean("Channellist_enter_hpjy", reportContent));
    }

    public void report_ChannelClick(){
        String reportContent = getReportContent(UserInfo.getInstance().mOpenid);
        addDataToUnLimitList(new ReportBean("Channellist_detail_hpjy", reportContent));
    }

    public void report_RoomClick(){
        String reportContent = getReportContent(UserInfo.getInstance().mOpenid);
        addDataToUnLimitList(new ReportBean("Channellist_groupview_hpjy", reportContent));
    }

    /**
    * 获取上报的内容,部分通用已经封装好
    * @author hyqiao
    * @time 2016/12/2 10:20
    */
    public String getReportContent(String...args){
        String reportContent = getReportFormatString();
        if(args!= null && args.length == 1 && TextUtils.isEmpty(args[0])){
            return reportContent;
        }
        StringBuilder result_content = new StringBuilder(reportContent);
        for(int i = 0;i<args.length ;i++){
            result_content.append("|");
            result_content.append(args[i]);
        }
        return result_content.toString();
    }

    /**
    * 获取上报的内容,全部需要封装
    * @author hyqiao
    * @time 2016/12/2 10:39
    */
    public String getReportContentWithoutFormat(String...args){
        StringBuilder reportContent = new StringBuilder();
        for(int i = 0;i<args.length-1;i++){
            reportContent.append(args[i]);
            reportContent.append("|");
        }
        reportContent.append(args[args.length-1]);

        return reportContent.toString();
    }

    /**
    * 通用部分的格式化
    * @author hyqiao
    * @time 2016/11/8 15:08
    */
    public String getReportFormatString(){
        String uuid = null;
        try {
            uuid = new String(Sessions.globalSession().getUid(),"utf-8");
        } catch (Exception e) {
            uuid = "";
        }
        String clientType = ""+Configs.CLIENT_TYPE;
        String gameId = Configs.GAME_ID;
        String areaId = UserInfo.getInstance().mAreaid+"";
        String clientVersion = Configs.plugin_version+"";
        String submittime = TimeUtils.getCurrentTime();
        return String.format("%s|%s|%s|%s|%s|%s",uuid,clientType,gameId,areaId,clientVersion,submittime);
    }

    /**
    * 上报视频质量监控
    * @author hyqiao
    * @time 2016/11/8 14:30
    */
    public void report_VideoQuality(ReportBean rb){
        addDataToLimitList(rb);
    }

    /**
    * 视频加载失败监控
    * @author hyqiao
    * @time 2016/11/8 14:31
    */
    public void report_TGAVideoLoadMonitor(ReportBean rb){
        addDataToLimitList(rb);
    }

    ReportHttpProxy reportHttpProxy = new ReportHttpProxy();
    ReportHttpProxy.Param reportHttpProxyParam = new ReportHttpProxy.Param();
    public void reportTv(Vector<ReportBean> list){
        try {
            if(reportHttpProxyParam.list!=null){
                reportHttpProxyParam.list.clear();
            }else {
                reportHttpProxyParam.list = new Vector<>();
            }

            reportHttpProxyParam.uid = UnityBean.getmInstance().gameUid;
            reportHttpProxyParam.areaId = UnityBean.getmInstance().areaid;
            reportHttpProxyParam.list.addAll(list);

            for(int i = 0;list != null && i<list.size();i++){
                TLogForReport.e(TAG,String.format("%s %s",list.get(i).getKey(),list.get(i).getValue()));
            }

            reportHttpProxy.postReq(mWeakReferenceContext.get(), new HttpBaseUrlWithParameterProxy.Callback() {
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
