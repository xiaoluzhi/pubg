package com.tencent.tga.liveplugin.live.player.ui.video.view;

import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.proxy.UpdateProxy;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.ui.video.bean.HotWordBean;
import com.tencent.tga.liveplugin.live.right.chat.ChatView;
import com.tencent.tga.plugin.R;

import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by agneswang on 2017/1/12.
 */

public class HotWordDialog extends BasePopWindow{
    private static final String TAG = "HotWordDialog";

    /**游戏字体*/
    private LinearLayout mLlyHotDialogRoot;
    private ListView mListView;
    private PopupWindow.OnDismissListener mListener;

    private CommonAdapter mListViewAdapter;
    private List<HotWordBean> mHotWordList = new LinkedList<HotWordBean>();

    public HotWordDialog(PlayView parent) {
        super(parent, true, null);
        setLayout(R.layout.hotword_dialog);
        initView(true);
        initAdapter();
    }

    public HotWordDialog(ChatView view, PopupWindow.OnDismissListener listener) {
        super(view, false, listener);
        setLayout(R.layout.hotword_dialog);
        mListener = listener;
        initView(false);
        initAdapter();
    }

    private void initView(boolean isFullScreen){

        mLlyHotDialogRoot = (LinearLayout)root.findViewById(R.id.mLlyHotDialogRoot);

        mListView = (ListView) root.findViewById(R.id.list_hotword);
        if(isFullScreen){
            mListView.setDividerHeight(2);
        }else {
            mListView.setDividerHeight(0);
        }
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view instanceof TextView) {
                    if (null != view.getTag(R.id.hotword_tag)) {
                        int hotwordId = (Integer) view.getTag(R.id.hotword_tag);
                        sendHotWord(((TextView) view).getText().toString(), hotwordId);
                    }
                }
            }
        });
    }

    private void sendHotWord(String hotword, int hotwordId) {
        LiveViewEvent.Companion.sendMsg(new LiveEvent.SendMsg(hotword,true, true, hotwordId));
        dismiss();
    }

    private void loadHotwordListFromLocal() {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                mHotWordList.add(new HotWordBean(0,"666666"));
                mHotWordList.add(new HotWordBean(1,"红红火火，恍恍惚惚"));
                mHotWordList.add(new HotWordBean(2,"满编不浪，钢枪不怂"));
                mHotWordList.add(new HotWordBean(3,"这是伏地魔的春天"));
                mHotWordList.add(new HotWordBean(4,"苟住，蛋糕是你的"));
                mHotWordList.add(new HotWordBean(5,"陈独秀都没你秀"));
                mHotWordList.add(new HotWordBean(6,"抢空投！抢空投！"));
                mHotWordList.add(new HotWordBean(7,"雷神降临，索尔附体"));
                mHotWordList.add(new HotWordBean(8,"这压枪你学不会的"));
                mHotWordList.add(new HotWordBean(9,"这圈谁顶得住啊"));
                mListViewAdapter.notifyDataSetChanged();
            });
        }catch (Exception e){
            TLog.e(TAG,"loadHotwordListFromLocal error : "+e.getMessage());
        }
    }

    private void initAdapter(){
        mListViewAdapter = new CommonAdapter<HotWordBean>(mContext, mHotWordList, R.layout.hotword_item_view) {
            @Override
            public void convert(ViewHolder holder, HotWordBean hotWordBean) {
                TextView hotWordItemView = holder.getView(R.id.hotword_item);
                hotWordItemView.setText(hotWordBean.getContent());
                hotWordItemView.setTypeface(LiveConfig.mFont);
                hotWordItemView.setTag(R.id.hotword_tag, hotWordBean.getId());
            }
        };

        mListView.setAdapter(mListViewAdapter);
        mListView.invalidate();
    }

    //半屏热词
    @Override
    public void show(int w, int h, int paddingRight, int paddingBottom) {
        reqHotWord();
        super.show(w, h, paddingRight, paddingBottom);
        if(mLlyHotDialogRoot != null){
            mLlyHotDialogRoot.setBackground(new ColorDrawable(0xCC000000));
    }
    }

    //全屏热词
    @Override
    public void showLeft(int w, int h, int paddingLeft, int paddingBottom) {
        reqHotWord();
        super.showLeft(w, h, paddingLeft, paddingBottom);
        if(mLlyHotDialogRoot != null){
            mLlyHotDialogRoot.setBackground(new ColorDrawable(0xCC000000));
        }
    }

    UpdateProxy hotWordProxy;
    UpdateProxy.Param param;
    private void reqHotWord(){
        try {
            mHotWordList.clear();
            mListViewAdapter.notifyDataSetChanged();
            hotWordProxy = new UpdateProxy();
            param = new UpdateProxy.Param();
            param.reqKeyJSONArray.put("hot_word_list");

            hotWordProxy.postReq(mContext, new HttpBaseUrlWithParameterProxy.Callback() {
                @Override
                public void onSuc(int i) {
                    try {
                        if(!TextUtils.isEmpty(param.response) && !TextUtils.isEmpty(param.response.trim())){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mHotWordList.clear();
                                    List<HotWordBean> list = handleHotWordList(param.response);
                                    //可能会遇到没配置的情况 即返回 "config_key":{} 会解析异常
                                    if (list!=null&&list.size()>0){
                                        mHotWordList.addAll(list);
                                        mListViewAdapter.notifyDataSetChanged();
                                    }else {
                                        loadHotwordListFromLocal();
                                    }
                                }
                            });

                        }else {
                            //showToast("热词数据为空");
                            loadHotwordListFromLocal();
                        }
                    }catch (Exception e){
                        showToast("热词数据异常");
                        TLog.e(TAG,"reqHotWord onSuc error : "+e.getMessage());
                    }
                }

                @Override
                public void onFail(int i) {
                    //showToast("热词请求失败");
                    loadHotwordListFromLocal();
                }
            },param);
        }catch (Exception e){
            TLog.e(TAG,"reqHotWord error : "+e.getMessage());
        }
    }

    private List<HotWordBean> handleHotWordList(String response){
        List<HotWordBean> list = new ArrayList<>();
        if(TextUtils.isEmpty(response))
            return list;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject config_key_json = jsonObject.optJSONObject("config_key");

            if (config_key_json == null){
                return list;
            }
            String src = config_key_json.optString("hot_word_list");

            String[] idAndContentArr = src.split(";;");
            for(int i = 0; idAndContentArr != null && i<idAndContentArr.length;i++){
                String[] hotwordbeanArr = idAndContentArr[i].split("::");
                list.add(new HotWordBean(Integer.parseInt(hotwordbeanArr[0]),hotwordbeanArr[1]));
            }
        }catch (Exception e){
            TLog.e(TAG,"handleHotWordList error : "+e.getMessage());
        }

        return list;
    }

    private void showToast(final String msg){
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(mContext,msg);
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"showToast eeror : "+e.getMessage());
        }

    }
}
