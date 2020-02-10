package com.tencent.tga.liveplugin.live.title;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.TimeUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.UnityBean;
import com.tencent.tga.liveplugin.live.common.util.UIAdaptationUtil;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.live.right.event.RightViewEvent;
import com.tencent.tga.liveplugin.live.title.bean.DefaultTagID;
import com.tencent.tga.liveplugin.live.title.bean.TitleTagBean;
import com.tencent.tga.liveplugin.live.title.view.FeedbackDialog;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.SPUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by agneswang on 2017/3/13.
 */

public class TitleView extends FrameLayout {

    private static final String TAG = "TitleView";

    public static String CELEBRITY_URL = "http://speedm.qq.com/ingame/cp/match/fame-fleet.html?";

    public static String INFO_URL = "http://speedm.qq.com/ingame/cp/match/info.html?";

    public static String GUESS_URL = "https://tga.qq.com/speedmtv/support/?";
    private ImageView mFeedbackLayout;
    private ArrayList<TitleBarView> mListTitleBarView;
    private LinearLayout mLlyTagContent;
    private ArrayList<TitleTagBean> mListTag;

    public static int mCurrentSelection = DefaultTagID.LIVE;

    public static int mLastSelection = DefaultTagID.LIVE;


    private Context mContext;
    public TitleView(Context context) {
        super(context);
        this.mContext = context;
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void onStart(boolean isReal) {
        initTagBeginTime();
        TLog.e(TAG,TAG+" onStart : "+isReal);
    }

    public void onStop() {
        TLog.e(TAG,TAG+" onStop ");
        exitReportTagStayPeriod();
    }

    public void onCreate() {
        TLog.d(TAG, "onCreate");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DeviceUtils.dip2px(getContext(), 30));
        setLayoutParams(lp);

        initView();

        initData();

        initTitleBarViewByData();

        addTitleBarViewToParent();

        setData();

        setListener();
    }

    public void onDestroy() {
        LiveViewEvent.Companion.disMissConfigTAB();
        mCurrentSelection = DefaultTagID.LIVE;
        mLastSelection = DefaultTagID.LIVE;
    }

    private void initView(){
        mFeedbackLayout = findViewById(R.id.feedback_layout);
        try {
            if(UIAdaptationUtil.hasCameraHole()){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mFeedbackLayout.getLayoutParams();
                layoutParams.rightMargin = DeviceUtils.dip2px(getContext(), 50);
                mFeedbackLayout.setLayoutParams(layoutParams);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mLlyTagContent = (LinearLayout) findViewById(R.id.mLlyTagContent);
    }
    /**
    * 初始化数据列表
    * @author hyqiao
    * @time 2017/5/18 17:09
    */
    private void initData(){
        mListTag = new ArrayList();
        try {
            JSONArray data = new JSONArray(UnityBean.getmInstance().tab_list);
            for(int i=0; i<data.length(); i++){
                JSONObject item = data.getJSONObject(i);
                TitleTagBean titleTagBean = new TitleTagBean(item.optString("name"), item.optInt("id"),
                        item.optString("icon"), item.optString("url"), true, false);
                if(titleTagBean != null){
                    mListTag.add(titleTagBean);
                }
            }
        }catch (Exception e){
            TLog.e(TAG,TAG+" initData error : "+e.getMessage());
            mListTag.clear();
            mListTag.addAll(DefaultTagID.getDefaultTitleTagBeanList());
        }
    }

    /**
    * 初始化控件列表
    * @author hyqiao
     * @time 2017/5/18 17:09
     */
    private void initTitleBarViewByData(){
        mListTitleBarView = new ArrayList<>();
        for(int i = 0;mListTag != null && i<mListTag.size();i++){
            mListTitleBarView.add(new TitleBarView(mContext));
        }
    }

    /**
    * 把控件列表的控件加到布局中
     * @author hyqiao
    * @time 2017/5/18 17:09
    */
    private void addTitleBarViewToParent(){
        for(int i = 0;mListTitleBarView != null && i<mListTitleBarView.size();i++){
            mLlyTagContent.addView(mListTitleBarView.get(i));
        }
    }

    /**
     * 控件列表和数据列表数据绑定
     * @author hyqiao
     * @time 2017/5/18 17:09
     */
    private void setData(){
        for(int i = 0;mListTitleBarView != null && i<mListTitleBarView.size();i++){
            mListTitleBarView.get(i).setData(mListTag.get(mListTag.size() - 1 - i));
        }
    }

    private void setListener(){
        for(int i = 0;mListTitleBarView != null && i<mListTitleBarView.size();i++){
            mListTitleBarView.get(i).setTag(mListTag.get(mListTag.size() - 1 - i).getId());
            mListTitleBarView.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NoDoubleClickUtils.isDoubleClick()) {
                        return;
                    }

                    doClick((Integer) view.getTag());
                }
            });
        }

        //setSelect(mCurrentSelection);//默认选中直播
        if(UnityBean.getmInstance().open_tab != DefaultTagID.LIVE){
            doClick(UnityBean.getmInstance().open_tab);
        }
        setSelect(UnityBean.getmInstance().open_tab);

        mFeedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NoDoubleClickUtils.isDoubleClick()) {
                    return;
                }
                try {
                    FeedbackDialog.launchDialog(LiveConfig.mLiveContext, LiveConfig.mFont, FeedbackDialog.TV_FEEDBACK);
                }catch (Exception e){
                    TLog.e(TAG,"FeedbackDialog.launchDialog error : "+e.getMessage());
                }
            }
        });
    }
    private void updateData(int tagID,boolean isRedPotVisible){
        // TODO: 2017/5/16 根据获得的数据来修改 mListTag
        boolean isNeedRefresh = false;
        for(int i = 0;mListTag != null && i<mListTag.size();i++){
            if(mListTag.get(i).getId() == tagID && (mListTag.get(i).isRedPotVisible() != isRedPotVisible)){
                mListTag.get(i).setRedPotVisible(isRedPotVisible);
                isNeedRefresh = true;
            }
        }
        if(isNeedRefresh){
            setData();
        }
    }

    private void setSelect(int tagID){
        for(int i = 0;mListTitleBarView != null && i<mListTitleBarView.size();i++){
            mListTitleBarView.get(i).setIconSelect(false);
            if((int)mListTitleBarView.get(i).getTag() == tagID){
                mListTitleBarView.get(i).setIconSelect(true);
            }
        }
    }

    public final static String redPotKey = "TitleId";
    private void saveRedPotState(int tagID){
        for(int i = 0;mListTag != null && i<mListTag.size();i++){
            if(mListTag.get(i).getId() == tagID && mListTag.get(i).isRedPotVisible()){
                String saveKey = redPotKey+tagID;
                SPUtils.SPSaveString(mContext,saveKey, TimeUtils.getCurrentDate());
            }
        }
    }

    /**
    * 初始化成功之后，刷新图标
    * @author hyqiao
    * @time 2019/4/22 19:23
    */
    public void refreshTitleIcon(){
        setData();
    }

    /**
    * 设置TAG点击的响应事件
    * @author hyqiao
    * @time 2017/5/18 17:00
    */
    public void doClick(int tagID){

        if(mCurrentSelection == tagID){
            return;
        }

        TLog.e(TAG,"doClick --> "+tagID);

        saveRedPotState(tagID);//保存红点状态

        updateData(tagID, false);//更新红点变为不可见

        mLastSelection = mCurrentSelection;//保存上次TAG的状态

        mCurrentSelection = tagID;

        doReportChangeTagHandle();//上报更换TAG

        doReportStayPeriodHandle();//上报上个TAG的停留时间

        setSelect(tagID);//设置被选中的效果

        try{
            if (tagID == DefaultTagID.LIVE)
            {
                RightViewEvent.Companion.chatTabClick();
                PlayViewEvent.reqCurMatch(false);
                LiveViewEvent.Companion.onstart(true);
            } else{
                LiveViewEvent.Companion.onViewStop();

            }

        }catch (Throwable throwable){
            LOG.e(TAG,"doClick exc");
        }

        TitleTagBean bean = getBeanForTagId(tagID);
        if (null == bean) return;
        if (bean.getId() == DefaultTagID.LIVE) {
            launchLive();
        } else {
            launchConfigTAB(bean.getOpenUrl(), "");
        }
    }
    private void launchConfigTAB(String url, String defaultUrl) {
        TLog.e(TAG, "顶部TAB点击");
        String matchUrl = TextUtils.isEmpty(url)? defaultUrl:url;
        LiveViewEvent.Companion.launchConfigTAB(matchUrl);
    }
    private TitleTagBean getBeanForTagId(int id) {
        for(TitleTagBean bean : mListTag) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }
    /**
    * 启动直播
    * @author hyqiao
    * @time 2017/5/18 17:01
    */
    private void launchLive(){
        LiveViewEvent.Companion.disMissConfigTAB();
    }


    private long mTagBeginTime = System.currentTimeMillis();
    public void initTagBeginTime(){
        mTagBeginTime = System.currentTimeMillis();
    }

    private synchronized void doReportStayPeriodHandle(){
        if(!LiveInfo.isReadyToReport){
            return;
        }

        long period = (System.currentTimeMillis() - mTagBeginTime)/1000;
        if(period<1){//doclick  执行了一次doReportStayPeriodHandle，onstop也执行一次，会有部分数据重复，过滤掉重复数据
            return;
        }
        ReportManager.getInstance().commonReportFun(
                "TVUserExitTab",
                false,
                NetUtils.getLocalIPAddress(),
                mLastSelection+"",
                TimeUtils.getCurrentTime(),
                TimeUtils.getCurrentTime(mTagBeginTime),
                period+"",
                TimeUtils.getCurrentTime());


        mTagBeginTime = System.currentTimeMillis();
    }

    //退出应用的时候再上报一次
    public void exitReportTagStayPeriod(){
        mLastSelection = mCurrentSelection;
        doReportStayPeriodHandle();
    }

    //更换之后上报
    private void doReportChangeTagHandle(){
        if(!LiveInfo.isReadyToReport){
            return;
        }
        ReportManager.getInstance().commonReportFun(
                "TVUserChangeTab",
                false,
                NetUtils.getLocalIPAddress(),
                mLastSelection+"",//切换前
                mCurrentSelection+"",//切换后
                TimeUtils.getCurrentTime());
    }

}
