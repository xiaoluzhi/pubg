package com.tencent.tga.liveplugin.live.player.ui.video.view;

import com.loopj.android.tgahttp.Configs.Configs;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.utils.LOG;
import com.tencent.common.log.tga.TLog;
import com.tencent.protocol.tga.chatMsg.BalconyTipsType;
import com.tencent.protocol.tga.expressmsg.BusinessType;
import com.tencent.tga.imageloader.core.assist.FailReason;
import com.tencent.tga.imageloader.core.display.CircleBitmapDisplayer;
import com.tencent.tga.imageloader.core.listener.ImageLoadingListener;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.util.BitMapUitl;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.common.views.CenterImageSpan;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.tga.controller.DrawHandler;
import master.flame.danmaku.tga.danmaku.loader.ILoader;
import master.flame.danmaku.tga.danmaku.loader.IllegalDataException;
import master.flame.danmaku.tga.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.tga.danmaku.model.BaseDanmaku;
import master.flame.danmaku.tga.danmaku.model.DanmakuTimer;
import master.flame.danmaku.tga.danmaku.model.Duration;
import master.flame.danmaku.tga.danmaku.model.IDisplayer;
import master.flame.danmaku.tga.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.tga.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.tga.danmaku.model.android.Danmakus;
import master.flame.danmaku.tga.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.tga.danmaku.parser.IDataSource;
import master.flame.danmaku.tga.ui.widget.DanmakuSurfaceView;


/**
 * Created by lionljwang on 2016/4/13.
 *
 * 最要是固定提示view
 *
 * 弹幕，加载动画，wifi提示暂停提示，多赛程下线提示
 */
public class PlayerStateView extends RelativeLayout {
    private static final String TAG = "PlayerStateView";
    private boolean isAttached =false;
    private Context mContext;

    public DanmakuSurfaceView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;

    private static float dens = 1.4f;

    private ImageView mAnimView;
    private AnimationDrawable mAnim;
    private View mAnimViewCon;

    public View mNotWifiContainer;
    public ImageView mNotWifiImgaeView;
    public TextView mNotWifiTextView;

    public TextView mDefineChangeTips;

    public ToggleButton mLockScreen;

    public static int mDanmuSize ;
    public static int mSwitSize ;
    public static int mDanmuAlpha ;

    public View mLiveOffLine;
    public View mLiveOffLineView;
    /**
     *
     * @param context
     */
    public PlayerStateView(Context context) {
        super(context);

        mDanmuAlpha = (LiveShareUitl.getLiveDanmuAlpha(context)+109)*255/364;
        setBackgroundColor(Color.TRANSPARENT);
        mContext = DLPluginLayoutInflater.getInstance(mContext).getContext();

        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.view_player_state_view, this);

        initUI();

        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        dens = dm.densityDpi / 160.0f - 0.6f;
        mSwitSize = (int) (2*dens);
        mDanmuSize = (int)(LiveShareUitl.getLiveDanmuSize(context)*dens);
        setmDanmuPosition(LiveShareUitl.getLiveDanmuPosition(context));

        initSetting();
    }

    public static void setmDanmuSize(int size){
        mDanmuSize = (int)(size*dens);
        if (PlayView.isFullscreen())
            mDanmuSize+=mSwitSize;
    }

    public static void setmDanmuAlpha(int alpha){
        mDanmuAlpha = (alpha+109)*255/364;
        int value=alpha*100/255;
        ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "2", value + "");
    }

    public void setmDanmuPosition(final int position){
        mDanmakuView.post(() -> {
            try {
                HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
                switch (position) {
                    case LiveShareUitl.LIVE_DANMU_POSITION_FULL:

                        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 20); // 滚动弹幕最大显示8行
                        mDanmakuContext.setMaximumLines(maxLinesPair);
                        mDanmakuContext.alignBottom(false);
                        break;
                    case LiveShareUitl.LIVE_DANMU_POSITION_TOP:
                        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4); // 滚动弹幕最大显示4行
                        mDanmakuContext.setMaximumLines(maxLinesPair);
                        mDanmakuContext.alignBottom(false);
                        break;
                    case LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM:
                        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4); // 滚动弹幕最大显示4行
                        mDanmakuContext.setMaximumLines(maxLinesPair);
                        mDanmakuContext.alignBottom(true);

                        break;
                    default:
                        break;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

    }
    public void attached(ViewGroup view) {
        if (!isAttached && view != null) {
            isAttached = true;
            view.addView(this);
        }
    }

    private boolean mIsFullscreen = false;

    /**
     * 全屏半屏切换
     * @param isFullScreen
     */
    public void switchScreen(boolean isFullScreen){
        mIsFullscreen = isFullScreen;
        if (!isFullScreen) {
            mLockScreen.setVisibility(View.GONE);
        }
        if (mDanmakuView!=null && mDanmakuView.isShown()){
            if (isFullScreen){
                mDanmakuContext.setScrollSpeedFactor(1.2f);
                mDanmuSize += mSwitSize;

                RelativeLayout.LayoutParams params = (LayoutParams) mDanmakuView.getLayoutParams();
                params.topMargin = (int) mContext.getResources().getDimension(R.dimen.video_top_height);


            }else {
                mDanmakuContext.setScrollSpeedFactor(1f);
                mDanmuSize -= mSwitSize;

                RelativeLayout.LayoutParams params = (LayoutParams) mDanmakuView.getLayoutParams();
                params.topMargin =0 ;
            }
            clearDanma();
        }

    }


    private void initUI(){
        mDanmakuView =  findViewById(R.id.sv_danmaku);
        initDamu();
        mAnimView =  findViewById(R.id.anim_loading);
        mAnim = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.video_loading);
        mAnimView.setImageDrawable(mAnim);
        mAnimViewCon = findViewById(R.id.anim_loading_container);


        mNotWifiContainer = findViewById(R.id.not_wifi_container);
        mNotWifiContainer.setOnClickListener(view -> PlayViewEvent.dismissDialogs());
        mNotWifiImgaeView =  findViewById(R.id.not_wifi_play);
        mNotWifiImgaeView.setOnClickListener(view -> PlayViewEvent.mobileplay());
        mNotWifiTextView =  findViewById(R.id.not_wifi_play_tv);
        mNotWifiTextView.setTypeface(LiveConfig.mFont);


        mDefineChangeTips =  findViewById(R.id.define_change_tips);
        mDefineChangeTips.setTypeface(LiveConfig.mFont);

        mLiveOffLine = findViewById(R.id.live_offline_container);
        mLiveOffLineView = findViewById(R.id.online_tips_oper);

        mLiveOffLine.setOnClickListener(null);
        mLiveOffLineView.setOnClickListener(v -> {
            dismissLiveOffLine();
            PlayViewEvent.lineChange(new LiveEvent.LiveLineChange(null, 0));
        });

        mLockScreen = findViewById(R.id.lock_screen);
        mLockScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NoDoubleClickUtils.isDoubleClick())return;

                LiveConfig.mLockSwitch = !LiveConfig.mLockSwitch;

                ReportManager.getInstance().commonReportFun("TVPlayerLock", true,  LiveConfig.mLockSwitch ?"1":"2");
                PlayViewEvent.lockScreen(new LiveEvent.LockSreen(LiveConfig.mLockSwitch));
                mLockScreen.setChecked(LiveConfig.mLockSwitch);
                showLockScreenDelayDismiss1(View.VISIBLE);
            }
        });
    }

    public void showLockScreenDelayDismiss1(int visiable){
        if (View.VISIBLE == visiable)
            setVisibility(visiable);
        mLockScreen.setVisibility(visiable);
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1,5000);
    }
    public void showLockScreen1(int visiable){
        if (View.VISIBLE ==  visiable)
            setVisibility(VISIBLE);
        mLockScreen.setVisibility(visiable);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mLockScreen.setVisibility(GONE);
        }
    };


    public void showLiveOffLine(){
        setVisibility(VISIBLE);
        mLiveOffLine.setVisibility(VISIBLE);
    }
    public void dismissLiveOffLine(){
        mLiveOffLine.setVisibility(GONE);
    }
    public boolean isOnlive(){
        if (mLiveOffLine!=null && mLiveOffLine.getVisibility() == VISIBLE)
            return true;
        return false;
    }

    public void showDefineChangeTips()
    {
        setVisibility(VISIBLE);
        if (mDefineChangeTips !=null)
        {
            mDefineChangeTips.setVisibility(VISIBLE);

        }
    }
    public void unShowDefineChangeTips()
    {
        if (mDefineChangeTips !=null)
            mDefineChangeTips.setVisibility(GONE);
    }

    public boolean isShowDefineChangeTips()
    {
        if (mDefineChangeTips !=null)
          return   mDefineChangeTips.getVisibility() == VISIBLE;
        return false;
    }

    public void showLoading(){
        TLog.e(TAG,"showLoading begin");
        setVisibility(VISIBLE);
        if (mAnim!=null && mNotWifiContainer!=null && mNotWifiContainer.getVisibility() !=VISIBLE){
            mAnimViewCon.setVisibility(View.VISIBLE);
            mAnimViewCon.setTag(1);
            mAnimView.setVisibility(View.VISIBLE);
            mAnim.start();
            TLog.e(TAG,"showLoading finish");
        }
    }
    public void dismissLoading(){
        TLog.e(TAG,"dismissLoading begin");
        if (mAnim!=null){
            mAnimViewCon.setVisibility(View.GONE);
            mAnimViewCon.setTag(null);
            mAnimView.setVisibility(View.GONE);
            mAnim.stop();
            TLog.e(TAG,"dismissLoading finish");
        }
    }

    public void showNotPlay(boolean isVideoPause)
    {
        dismissLoading();
        setVisibility(VISIBLE);
        mNotWifiContainer.setVisibility(VISIBLE);
        if (mNotWifiTextView!=null){
            if (isVideoPause)
            {
                mNotWifiTextView.setText("直播已停止，点击开始");
            }else {
                mNotWifiTextView.setText("当前非WIFI网络环境，继续观看需要消耗流量");
            }
        }

    }

    public boolean isShowNotWifiPlay(){
        if (mNotWifiContainer !=null)
            return mNotWifiContainer.getVisibility() == VISIBLE;
        return false;
    }

    public void dissmissNotWifiPlay()
    {
        mNotWifiContainer.setVisibility(GONE);
    }


    public void initDamu() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 20); // 滚动弹幕最大显示8行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.alignBottom(true);
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 5).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1f).setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmakuView != null) {
            mParser = createParser(null);
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {
                }

                @Override
                public void recycle() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });

            mDanmakuView.prepare(mParser, mDanmakuContext);
            //mDanmakuView.showFPS(true);
            mDanmakuView.showFPS(Configs.Debug);
            mDanmakuView.enableDanmakuDrawingCache(true);

            int pos =  LiveShareUitl.getLiveDanmuPosition(getContext());
            setmDanmuPosition(pos);
        }
    }



    public void danmaPause()
    {

        LOG.e(TAG,"danmaPause,,,,");
        if (mDanmakuView !=null && mDanmakuView.isPrepared())mDanmakuView.pause();
    }

    public void danmaResume()
    {
        LOG.e(TAG,"danmaResume,,,,");
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused())mDanmakuView.resume();
    }

    public void danmaDestroy()
    {
        LOG.e(TAG,"danmaDestroy,,,,");
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public void clearDanma(){
        mDanmakuView.removeAllDanmakus(true);
    }

    public void addMsg(final ChatMsgEntity entity){
      try {
          if (mDanmakuView!=null && mDanmakuView.isShown())
              addDanmaku(entity);
      }catch (Throwable throwable){
          LOG.e(TAG,"danmaku exc"+throwable.getMessage());
      }

    }

    public void setDanmuVisible(int visible){
        if (null == mDanmakuView) return;
        if (visible == VISIBLE) {
            mDanmakuView.show();
        } else{
            mDanmakuView.hide();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {
        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
              /*  // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {

                        if (mDanmakuView != null) {
                            mDanmakuView.invalidateDanmaku(danmaku, false);
                        }
                    }
                }.start();*/
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
        }
    };

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            if (loader !=null)
                loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        if (null != loader) {
            IDataSource<?> dataSource = loader.getDataSource();
            parser.load(dataSource);
        }
        return parser;

    }

    private String content;

    private void addDanmaku(final ChatMsgEntity entity) {

       final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);

        danmaku.isLive = true;
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        if (mIsFullscreen)
        {
            danmaku.duration = new Duration(9500);
        }else {
            danmaku.duration = new Duration(7000);
        }

        content =entity.text.trim();
        danmaku.textColor = Color.WHITE;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.alpha = mDanmuAlpha;

        if (entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.getValue()){
            danmaku.text = content.trim();

            if (entity.isSel) {
                danmaku.priority = 1;
                danmaku.text = content.trim();
                danmaku.borderColor = 0xfff7e35d;
            }


            danmaku.isBitmap = false;//不用bitmap 绘制节约内存

        }else if(entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.getValue()){//运营消息
            int time;

            if (mIsFullscreen)
            {
                time = 12000+(content.trim().length()*mDanmuSize/400);
            }else {
                time = 8500+(content.trim().length()*mDanmuSize/600);
            }

            danmaku.duration = new Duration(time);
            danmaku.alpha = 255;
            if (entity.subType == 4)//系统消息
            {
                content = String.format("   %s",entity.text);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                int size = mDanmuSize+4;
                Drawable drawable = BitMapUitl.zoomDrawable(mContext.getResources().getDrawable(R.drawable.danma_manger_icon), size, size);
                drawable.setBounds(0, 0, size, size);
                CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                danmaku.text = spannableStringBuilder;
                danmaku.textColor = 0xfffea800;
                danmaku.priority = 1;
                danmaku.backgroudType = DanmaBackType.MANAGER_CHAT_BOX;
            }else if(entity.subType == 1)//房管消息
            {

                content =String.format("   房管：%s",entity.text);
                //content =String.format("   %s：%s",entity.name,entity.text);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                int size = mDanmuSize+4;
                Drawable drawable = BitMapUitl.zoomDrawable(mContext.getResources().getDrawable(R.drawable.danma_room_manger_icon), size, size);
                drawable.setBounds(0, 0, size, size);
                CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                danmaku.text = spannableStringBuilder;
                danmaku.textColor = 0xffffffff;
                danmaku.priority = 1;
                danmaku.backgroudType = DanmaBackType.MANAGER;
            }else if(entity.subType == 2||entity.subType == 5)//小解说
            {
                content = String.format("   %s：%s",entity.name,entity.text);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                int size = mDanmuSize+4;
                Drawable drawable = BitMapUitl.zoomDrawable(mContext.getResources().getDrawable(R.drawable.danma_small_gloze_icon), size, size);
                drawable.setBounds(0, 0, size, size);
                CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                danmaku.text = spannableStringBuilder;
                danmaku.textColor = 0xffffffff;
                danmaku.priority = 1;
                danmaku.backgroudType = entity.subType;

            }else if(entity.subType == 3||entity.subType == 6)//嘉宾
            {
                danmaku.backgroudType = entity.subType;
                danmaku.text = String.format("         %s：%s",entity.name,entity.text);
                danmaku.priority = 1;
                if (!TextUtils.isEmpty(entity.nickUrl)){
                    ImageLoaderUitl.loadimage(entity.nickUrl, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {}
                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                        }
                        @Override
                        public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                            content = String.format("   %s：%s", entity.name, entity.text);
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                            int size = DeviceUtils.dip2px(getContext(), 18);
                            CircleBitmapDisplayer.CircleDrawable drawable = new CircleBitmapDisplayer.CircleDrawable(bitmap, 0x000000, 0);
                            drawable.setBounds(0, 0, size, size);
                            CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                            spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            danmaku.alpha = 255;
                            danmaku.time = mDanmakuView.getCurrentTime() + 100;
                            danmaku.textSize = mDanmuSize;
                            danmaku.textColor = 0xffffffff;
                            danmaku.backgroudType =entity.subType;
                            danmaku.text = spannableStringBuilder;
                            danmaku.priority = 1;
                            if(entity.subType == 3){
                                danmaku.color_bg ="1ce2ff";
                            }else{
                                danmaku.color_bg ="ff496b";
                            }
//                            danmaku.color_bg = entity.color;


                            mDanmakuView.addDanmaku(danmaku);
                        }
                        @Override
                        public void onLoadingCancelled(String s, View view) {
                        }
                    });
                }
            }else if(entity.subType == 7){
                danmaku.backgroudType = entity.subType;
                danmaku.text = String.format("         %s：%s",entity.name,entity.text);
                danmaku.priority = 1;
                if (!TextUtils.isEmpty(entity.nickUrl)){
                    ImageLoaderUitl.loadimage(entity.nickUrl, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {}
                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                        }
                        @Override
                        public void onLoadingComplete(String s, View view,final Bitmap bitmap) {
                            content =String.format("   %s：%s",entity.name,entity.text);
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                            int size = DeviceUtils.dip2px(getContext(),18);
                            CircleBitmapDisplayer.CircleDrawable drawable = new CircleBitmapDisplayer.CircleDrawable(bitmap,0x000000,0);
                            //Drawable drawable = BitMapUitl.zoomDrawable(mContext.getResources().getDrawable(R.drawable.danma_room_manger_icon), size, size);
                            drawable.setBounds(0, 0, size, size);
                            CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                            spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                            danmaku.alpha = 255;
                            danmaku.time = mDanmakuView.getCurrentTime() + 100;
                            danmaku.textSize = mDanmuSize;
                            danmaku.textColor = 0xffffffff;
                            danmaku.backgroudType = entity.subType;
                            danmaku.text = spannableStringBuilder;
                            danmaku.priority = 1;
                            danmaku.color_bg = entity.color;

                            mDanmakuView.addDanmaku(danmaku);
                        }
                        @Override
                        public void onLoadingCancelled(String s, View view) {
                        }
                    });
                }
            }

        }else if(entity.msgType ==  BusinessType.BUSINESS_TYPE_BOX_CRITICAL_MSG.getValue()){//
            danmaku.alpha = 255;
            content = String.format("   %s",entity.text);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
            int size = mDanmuSize+8;
            Drawable drawable = BitMapUitl.zoomDrawable(mContext.getResources().getDrawable(R.drawable.gift_icon_danmu), size, size);
            drawable.setBounds(0, 0, size, size);
            CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
            spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            danmaku.text = spannableStringBuilder;
            danmaku.textColor = 0xfffea800;
            danmaku.priority = 1;

        }else if (entity.msgType == BalconyTipsType.E_TIPS_TYPE_INVITE_TIPS.getValue()){
            danmaku.alpha = 255;
            content = String.format("   %s",entity.text);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
            int size = mDanmuSize+8;
            Drawable drawable = BitMapUitl.zoomDrawable(mContext.getResources().getDrawable(R.drawable.danma_manger_icon), size, size);
            drawable.setBounds(0, 0, size, size);
            CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
            spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            danmaku.text = spannableStringBuilder;
            danmaku.textColor = 0xfffea800;
            danmaku.priority = 1;
            danmaku.backgroudType = DanmaBackType.MANAGER_CHAT_BOX;
        }
        else {
            return;
        }

        if (entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.getValue() && (entity.subType == 3||entity.subType == 6||entity.subType == 7)){

            return;
        }
        danmaku.time = mDanmakuView.getCurrentTime() + 100;
        danmaku.textSize = mDanmuSize ;
        mDanmakuView.addDanmaku(danmaku);
    }
    public long mLoadingTime1 = 0l,mLoadingTime2 = 0l;
    private ImageView iv_center;
    private ProgressBar pb;
    private HideRunnable mHideRunnable;
    private int duration = 1000;
    private View mSettingRoot;
    private TextView mTitle;
    private TextView mTips;

    private void initSetting(){
        mSettingRoot = findViewById(R.id.setting_root);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        pb = (ProgressBar) findViewById(R.id.pb);
        mTitle = findViewById(R.id.title);
        mTips = findViewById(R.id.tips);
        mHideRunnable = new HideRunnable();
    }

    //显示
    public void show(){
        mSettingRoot.setVisibility(VISIBLE);
        removeCallbacks(mHideRunnable);
        postDelayed(mHideRunnable,duration);
    }

    //设置进度
    public void setProgress(int progress){
        pb.setProgress(progress);
    }

    public float getProgress(){
        if (pb!= null)return pb.getProgress()/100f;
        return 0f;
    }

    //设置持续时间
    public void setDuration(int duration) {
        this.duration = duration;
    }

    //设置显示图片
    public void voiceIsOpen(boolean flag){
        setVisibility(VISIBLE);
        mTitle.setText("音量");
        if (flag)
        {
            iv_center.setImageResource(R.drawable.volume_on_w);
            mTips.setVisibility(GONE);
            pb.setVisibility(VISIBLE);
        }
        else
        {
            iv_center.setImageResource(R.drawable.volume_off_w);
            mTips.setVisibility(VISIBLE);
            pb.setVisibility(GONE);
            mTips.setText("静音");
        }
    }

    public void updateBrightUi(){
        setVisibility(VISIBLE);
        mTitle.setText("亮度");
        mTips.setVisibility(GONE);
        pb.setVisibility(VISIBLE);
        iv_center.setImageResource(R.drawable.brightness_w);
    }

    //隐藏自己的Runnable
    private class HideRunnable implements Runnable{
        @Override
        public void run() {
            mSettingRoot.setVisibility(GONE);
            ReportManager.getInstance().commonReportFun("TVPlayerAdjustment", true, TextUtils.equals("亮度",mTitle.getText())?"2":"1",String.valueOf(1.0*pb.getProgress()/pb.getMax()));
            // reportGestureAction = "1";
        }
    }

}
