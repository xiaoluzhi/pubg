package com.tencent.tga.liveplugin.live.right.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder;
import com.tencent.tga.liveplugin.base.view.BaseViewInter;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ConfigInfo;
import com.tencent.tga.liveplugin.live.common.util.LimitQueue;
import com.tencent.tga.liveplugin.live.gift.view.TimerGiftView;
import com.tencent.tga.liveplugin.live.player.ui.video.view.ChatPopwindow;
import com.tencent.tga.liveplugin.live.player.ui.video.view.HotWordDialog;
import com.tencent.tga.liveplugin.live.right.ChatUitl;
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgBean;
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;


/**
 * Created by lionljwang on 2017/3/30.
 */
public class ChatView extends FrameLayout implements BaseViewInter, View.OnClickListener , AbsListView.OnScrollListener,View.OnTouchListener {

    private View mRootView;
    public TextView mHot;
    public ImageView mHotImg;
    public TextView mEditTextView;
    public PullToRefreshListView mListView;

    public TextView mRlyGoLatest;

    public TimerGiftView mTimerGiftView;

    private CommonAdapter mChatListAda;
    public LimitQueue<ChatMsgBean> mChatList = new LimitQueue<>(200);
    public LimitQueue<ChatMsgBean> mCahcheChatList = new LimitQueue<>(20);
    public HotWordDialog mHotWordDialog;
    private boolean isOnTouch;

    public ChatView(Context context) {
        super(DLPluginLayoutInflater.getInstance(context).getContext());

        initView();
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(DLPluginLayoutInflater.getInstance(context).getContext(), attrs);
        initView();
    }


    private void initView(){
        mRootView =  DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.mvp_chat_view,null);

        mHot =  mRootView.findViewById(R.id.edit_hot);
        mHotImg=mRootView.findViewById(R.id.edit_hot_img);
        mHot.setOnClickListener(this);
        mEditTextView =  mRootView.findViewById(R.id.edit_text);
        mEditTextView.setOnClickListener(this);
        mRlyGoLatest = mRootView.findViewById(R.id.mRlyGoLatest);
        mRlyGoLatest.setOnClickListener(this);

        mListView =  mRootView.findViewById(R.id.pull_refresh_chatlist);
        mListView.getRefreshableView().setOnTouchListener(this);
        mListView.getRefreshableView().setOnScrollListener(this);
        addView(mRootView);

        initAdapter();

        mRlyGoLatest.setTypeface(LiveConfig.mFont);
    }


    public void clear(){
         mChatList.clear();
        disMissChatTips();
    }

    public void setmEditText(String editText) {
        if (mEditTextView == null) return;

        if (TextUtils.isEmpty(editText)) {
            mEditTextView.setText("");
            mEditTextView.setHint(R.string.hint_live_chat);
        } else
            mEditTextView.setText(editText);
    }

    private void initAdapter(){
        mChatListAda = new CommonAdapter<ChatMsgBean>(getContext(), mChatList.queue, R.layout.item_chat_view) {
            @Override
            public void convert(ViewHolder holder, ChatMsgBean chatMsgBean) {

                 if (chatMsgBean!=null && !TextUtils.isEmpty(chatMsgBean.text))
                 {
                     TextView textView = holder.getView(R.id.chat_context);
                     textView.setText(chatMsgBean.text);
                     textView.setTextColor(chatMsgBean.textColor);
                 }
            }
        };

        mListView.setAdapter(mChatListAda);
        mListView.invalidate();
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);

    }


    public synchronized void addChatMsg(final ChatMsgEntity chatMsgEntity){
        mListView.post(new Runnable() {
            @Override
            public void run() {
                ChatMsgBean chatMsgBean = ChatUitl.mInstance.getMsg(chatMsgEntity);

                if (chatMsgBean != null) {
                    if (!ChatView.this.isOnTouch) {
                        mChatList.offer(chatMsgBean);
                        mChatListAda.notifyDataSetChanged();
                        mListView.getRefreshableView().smoothScrollToPosition(mChatList.size() - 1);
                    } else {
                        synchronized (mCahcheChatList) {
                            mCahcheChatList.offer(chatMsgBean);
                        }
                    }
                }
            }
        });
    }

    public synchronized void addChatMsg(final ChatMsgBean chatMsgBean){
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (!isOnTouch) {
                    if (chatMsgBean != null) {
                        mChatList.offer(chatMsgBean);
                        mChatListAda.notifyDataSetChanged();
                        mListView.getRefreshableView().setSelection(mChatList.size() - 1);
                    }
                } else {
                    synchronized (mCahcheChatList) {
                        mCahcheChatList.offer(chatMsgBean);
                    }
                }
            }
        });
    }

    public synchronized void addCacheChatMsg(){
        mListView.post(new Runnable() {
            @Override
            public void run() {

                synchronized (mCahcheChatList){
                    if (mCahcheChatList !=null && mCahcheChatList.size()>0){
                        for (ChatMsgBean chatMsgEntity: mCahcheChatList.queue){
                            mChatList.offer(chatMsgEntity);
                            mChatListAda.notifyDataSetChanged();
                            mListView.getRefreshableView().setSelection(mChatList.size() - 1);
                        }
                        mCahcheChatList.clear();
                    }
                }

            }
        });
    }

    public synchronized void addSysNotice(final String notice){
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mChatList.clear();
                mChatList.offer(getSysNotice(notice));
                mChatListAda.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick())
            return;
        switch (v.getId()){
            case R.id.edit_hot:
                if (null == mHotWordDialog) {
                    mHotWordDialog = new HotWordDialog(this, new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            setHotUp();
                        }
                    });
                }
                if (!mHotWordDialog.isShowing()) {
                    try {
                        int[] view_pos = new int[2];
                        v.getLocationOnScreen(view_pos);
                        setHotDown();
                    }catch (Exception e){
                        TLog.e(LiveConfig.TAG,"edit_hot setHotDown error : "+e.getMessage());
                    }
                }else {
                    setHotUp();
                }

                break;
            case R.id.edit_text:
                String string = mEditTextView.getText().toString().trim();
                ChatPopwindow chatPopwindow = new ChatPopwindow(getContext(), 20);

                if (chatPopwindow.popWindow.isShowing())
                    return;

                if (null != mHotWordDialog && mHotWordDialog.isShowing())
                {
                    setHotUp();
                }

                if (string != null && !"".equals(string)) {
                    chatPopwindow.show(mEditTextView, string);
                } else {
                    chatPopwindow.show(mEditTextView, "");
                }
                mEditTextView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEditTextView.setText("");
                    }
                }, 50);
                break;
            case R.id.mRlyGoLatest:
                isOnTouch = false;
                mRlyGoLatest.setVisibility(View.GONE);
                mListView.getRefreshableView().setSelection(mChatList.size() - 1);
                ReportManager.getInstance().commonReportFun("CheckLatestChanInfo", false, "");
                break;
            default:
                break;
        }
    }

    public void setHotDown(){
        if (NetUtils.isNetworkAvailable(getContext())) {
            mHotImg.setBackgroundResource(R.drawable.hotworld_notfullscreen_down);
            int temp_w = getWidth();
            int temp_h = getHeight() - DeviceUtils.dip2px(getContext(),24);
            mHotWordDialog.show(temp_w, temp_h,
                    DeviceUtils.dip2px(getContext(),5), DeviceUtils.dip2px(getContext(),29));
            ReportManager.getInstance().report_TVWordBarragePanelClick(LiveInfo.mRoomId, false ? 1 : 0);
        }
    }

    public void setHotUp(){
        mHotImg.setBackgroundResource(R.drawable.hotworld_notfullscreen_up);
        mHotWordDialog.dismiss();
    }

    public void initTimerGiftView(ConfigInfo configInfo){
        if (null != configInfo && configInfo.getConfig(ConfigInfo.BOX_SWITCH)) {
            TLog.e("ChatView","观赛奖励开关打开");
            if (mTimerGiftView == null) {
                mTimerGiftView = TimerGiftView.getInstance(getContext(),this);
                mTimerGiftView.initData();
            }
        }
    }




    public ChatMsgBean getSysNotice(String sysNotice){
        ChatMsgBean chatMsgBean = new ChatMsgBean();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sysNotice);
        ForegroundColorSpan span=new ForegroundColorSpan(0xFFFA9B0A);
        spannableStringBuilder.setSpan(span, 0, sysNotice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        chatMsgBean.text = spannableStringBuilder;
        chatMsgBean.textColor = 0xFFFFD000;
        return chatMsgBean;
    }


    public void disMissChatTips(){
        addCacheChatMsg();
        isOnTouch = false;
        mRlyGoLatest.setVisibility(View.GONE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isUpdate){
            if (firstVisibleItem+visibleItemCount+5<totalItemCount)
            {
                isOnTouch = true;
                if (mRlyGoLatest.getVisibility() != View.VISIBLE)
                    ReportManager.getInstance().commonReportFun("ChatLockScreen", false,"");
                mRlyGoLatest.setVisibility(View.VISIBLE);

            }else {
                disMissChatTips();
            }
        }

    }
    boolean isUpdate =true;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction())
        {
            isUpdate =false;
            isOnTouch = true;
        } else if (MotionEvent.ACTION_UP == event.getAction()){
            isUpdate =true;
            if (mListView.getRefreshableView().getLastVisiblePosition()+4<mChatList.size())
            {
                isOnTouch = true;
                if (mRlyGoLatest.getVisibility() != View.VISIBLE)
                    ReportManager.getInstance().commonReportFun("ChatLockScreen", false,"");
                mRlyGoLatest.setVisibility(View.VISIBLE);

            }else {
                disMissChatTips();
            }
        }
        return false;
    }
}
