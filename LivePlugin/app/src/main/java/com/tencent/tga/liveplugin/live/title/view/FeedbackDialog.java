package com.tencent.tga.liveplugin.live.title.view;

import com.loopj.android.tgahttp.Configs.Configs;
import com.loopj.android.tgahttp.httputil.HttpBaseUrlWithParameterProxy;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder;
import com.tencent.tga.liveplugin.base.view.BaseDialog;
import com.tencent.tga.liveplugin.live.common.proxy.UpdateProxy;
import com.tencent.tga.liveplugin.live.title.bean.QuestionBean;
import com.tencent.tga.liveplugin.live.title.proxy.FeedbackQuestionHttpProxy;
import com.tencent.tga.liveplugin.networkutil.NetUtils;
import com.tencent.tga.liveplugin.networkutil.UserInfo;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hyqiao on 2016/9/23.
 */
public class FeedbackDialog extends BaseDialog {

    private static final String TAG ="FeedbackDialog";

    public static final String TV_FEEDBACK ="电视台意见反馈";

    public FeedbackDialog(Context context, int theme) {
        super(context, theme);
    }

    public static FeedbackDialog.Builder dialog_builder;
    public static void launchDialog(Activity activity, Typeface font, String title){
        if (null == activity) return;
        if(dialog_builder == null){
            try {
                TLog.e(TAG,"launchDialog Builder");
                dialog_builder = new Builder(activity);
                dialog_builder.create(font,title);
            } catch (Exception e) {
                TLog.e(TAG,"launchDialog error : "+e.getMessage());
            }
        }
        dialog_builder.reqQuestionlist();
    }

    public static class Builder {
        private WeakReference<Activity> mWeakReferenceActivity;
        private Handler mHandler;
        private GridView mGvQuestion;
        private EditText mEtDescription,mEtContacts;
        private ArrayList<QuestionBean> mQuestionlist = new ArrayList<>();
        private CommonAdapter mQuestionlistAdapter;
        private FeedbackDialog dialog;
        private TextView mTvQuestion,mTvDescription,mTvContacts;
        private ImageView mTvFeedBackTitle;
        private Button mBtnEnsure;
        private ImageView mIvClose;
        private TextView mTvVersion;
        private int mCurrentPosition = -1;
        private String mFeedBackTitle = "";
        private Typeface font;

        public Builder(Activity activity) throws UnsupportedEncodingException {
            this.mWeakReferenceActivity = new WeakReference<Activity>(activity);
            dialog = new FeedbackDialog(mWeakReferenceActivity.get(), R.style.Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xcc000000));

        }

        public void releaseDialog(){
            TLog.e(TAG,"releaseDialog succ");
            try {
                if(dialog!=null){
                    dialog.cancel();
                    dialog = null;
                }
                dialog_builder = null;
            }catch (Exception e){
                TLog.e(TAG,"释放视频中心失败");
            }
        }

        public FeedbackDialog create(Typeface font,String title) {
            this.mFeedBackTitle = title;
            this.font = font;
            View layout = dialog.setLayout(R.layout.dialog_feedback, null);
            ReportManager.getInstance().report_TVUserFeedback(ReportManager.TV_TO_FEEDBACK);
            mHandler = new Handler();

            initUI(layout);

            initAdapter();

            initListener();

            return dialog;
        }

        private void initUI(View layout){
            mIvClose = layout.findViewById(R.id.mIvClose);

            mTvQuestion = layout.findViewById(R.id.mTvQuestion);
            mTvDescription = layout.findViewById(R.id.mTvDescription);
            mTvContacts = layout.findViewById(R.id.mTvContacts);
            mTvFeedBackTitle = layout.findViewById(R.id.mTvFeedBackTitle);
            mBtnEnsure = (Button) layout.findViewById(R.id.mBtnEnsure);
            mEtDescription = (EditText) layout.findViewById(R.id.mEtDescription);
            mEtContacts = (EditText) layout.findViewById(R.id.mEtContacts);
            if(font != null){
                //mTvQuestion.setTypeface(font);
                //mTvDescription.setTypeface(font);
                //mTvContacts.setTypeface(font);
                //mBtnEnsure.setTypeface(font);
//                mEtDescription.setTypeface(font);
//                mEtContacts.setTypeface(font);
            }

            mGvQuestion = (GridView) layout.findViewById(R.id.mGvQuestion);

            mTvVersion = (TextView) layout.findViewById(R.id.mTvVersion);
            mTvVersion.setText("v"+ Configs.plugin_version);
        }
        private void initAdapter(){
            mQuestionlistAdapter = new CommonAdapter<QuestionBean>(mWeakReferenceActivity.get(),mQuestionlist,R.layout.item_question_list) {
                @Override
                public void convert(ViewHolder holder, QuestionBean questionBean) {
                    QuestionItem mQuestionItem = holder.getView(R.id.mQuestionItem);
                    mQuestionItem.setData(questionBean,font);
                }
            };
            mGvQuestion.setAdapter(mQuestionlistAdapter);
        }

        private void setBtnSureClickAble(boolean isClickAble){
            try{
                mBtnEnsure.setClickable(isClickAble);
                mBtnEnsure.setAlpha(isClickAble?1.0f:0.5f);
            }catch (Exception e){
                TLog.e(TAG,"setBtnSureClickAble exception : "+e.getMessage());
            }
        }

        private void initListener(){
            mBtnEnsure.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO: 2016/11/14 用户反复点击的优化
                    if (!NoDoubleClickUtils.isDoubleClick()) {
                        submitQuestion();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setBtnSureClickAble(true);
                            }
                        },2000);
                    }
                }
            });


            mIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });


            mGvQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setListSelect(i);
                    mQuestionlistAdapter.notifyDataSetChanged();
                }
            });
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //dialogInterface.dismiss();
                    releaseDialog();
                }
            });
        }

        public void setListSelect(int position){
            for(QuestionBean qb:mQuestionlist){
                qb.setIsSelect(false);
            }
            if(mCurrentPosition != position){
                mQuestionlist.get(position).setIsSelect(true);
                mCurrentPosition = position;
            }else {
                mCurrentPosition = -1;
            }
        }

        private void loadLocalQuestions() {
            mQuestionlist.clear();
            mQuestionlist.add(new QuestionBean("17", "发言/弹幕", false));
            mQuestionlist.add(new QuestionBean("14", "黑屏/卡顿", false));
            mQuestionlist.add(new QuestionBean("11", "竞猜反馈", false));
            mQuestionlist.add(new QuestionBean("11", "弹窗骚扰", false));
            mQuestionlist.add(new QuestionBean("11", "吐槽建议", false));
            mQuestionlist.add(new QuestionBean("11", "直播内容", false));
            mQuestionlistAdapter.notifyDataSetChanged();
            if(!mWeakReferenceActivity.get().isFinishing() && dialog!=null && !dialog.isShowing()){
                dialog.show();
            }
        }


        UpdateProxy feedbackQuestionProxy;
        UpdateProxy.Param param;
        private void reqQuestionlist(){
            feedbackQuestionProxy = new UpdateProxy();
            param = new UpdateProxy.Param();
            param.reqKeyJSONArray.put("user_feedback_cfg");

            feedbackQuestionProxy.postReq(mWeakReferenceActivity.get(), new HttpBaseUrlWithParameterProxy.Callback() {
                @Override
                public void onSuc(int i) {
                    try {
                        if(!TextUtils.isEmpty(param.response)){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mQuestionlist.clear();
                                    mQuestionlist.addAll(handleQuestionList(param.response));
                                    mQuestionlistAdapter.notifyDataSetChanged();

                                    if(mQuestionlist.size() == 0) {
                                        loadLocalQuestions();
                                        return;
                                    }

                                    if(!mWeakReferenceActivity.get().isFinishing() && dialog!=null && !dialog.isShowing()){
                                        dialog.show();
                                    }
                                }
                            });
                        }else {
//                            showToast("意见反馈数据为空");
                            loadLocalQuestions();
                        }
                    }catch (Exception e){
//                        showToast("意见反馈数据异常");
                        loadLocalQuestions();
                        TLog.e(TAG,"reqHotWord onSuc error : "+e.getMessage());
                    }
                }

                @Override
                public void onFail(int i) {
                    showToast("意见反馈请求失败");
                }
            },param);
        }

        private List<QuestionBean> handleQuestionList(String response){
            List<QuestionBean> list = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject config_key_json = jsonObject.optJSONObject("config_key");
                String src = config_key_json.optString("user_feedback_cfg");
                if(TextUtils.isEmpty(src))
                    return list;

                if (!TextUtils.isEmpty(src)) {
                    JSONObject mJSONObject = new JSONObject(src);
                    Iterator iterator = mJSONObject.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = mJSONObject.getString(key);
                        list.add(new QuestionBean(key, value, false));
                        if (list.size() == 6) {
                            break;
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return list;
        }

        private void showToast(final String msg){
            try {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(mWeakReferenceActivity.get(),msg);
                    }
                });
            }catch (Exception e){
                TLog.e(TAG,"showToast eeror : "+e.getMessage());
            }

        }

        private boolean submitQuestion(){
            QuestionBean qb = null;
            for(QuestionBean q:mQuestionlist){
                if(q.isSelect()){
                    qb = q;
                }
            }

            if(qb == null){
                if(TextUtils.isEmpty(mEtDescription.getText().toString().trim())){
                    ToastUtil.show(mWeakReferenceActivity.get(),"请填写反馈内容");
                    return false;
                }
                qb = new QuestionBean("0","",false);//默认的id必须为0
            }

            if(TextUtils.isEmpty(UserInfo.getInstance().mOpenid)){
                dialog.dismiss();
                return false;
            }

            setBtnSureClickAble(false);
            reqReportFeedBackHttp(qb, mEtDescription.getText().toString(), mEtContacts.getText().toString());
            return true;
        }
        private FeedbackQuestionHttpProxy mFeedbackQuestionHttpProxy = new FeedbackQuestionHttpProxy();
        private FeedbackQuestionHttpProxy.Param mFeedbackQuestionHttpProxyParam = new FeedbackQuestionHttpProxy.Param();
        private void reqReportFeedBackHttp(QuestionBean qb,String desc,String contact){
            int feedbackType = 0;
            try {
                feedbackType = Integer.valueOf(qb.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            mFeedbackQuestionHttpProxyParam.FeedbackType = feedbackType;
            mFeedbackQuestionHttpProxyParam.FeedbackContent = qb.getName();
            mFeedbackQuestionHttpProxyParam.FeedbackDetail = desc;
            mFeedbackQuestionHttpProxyParam.ContactAccount = contact;
            mFeedbackQuestionHttpProxy.postReq(mWeakReferenceActivity.get(), new HttpBaseUrlWithParameterProxy.Callback() {
                @Override
                public void onSuc(int i) {
                    try {
                        if(!TextUtils.isEmpty(mFeedbackQuestionHttpProxyParam.response)){
                            JSONObject jsonObject = new JSONObject(mFeedbackQuestionHttpProxyParam.response);
                            int result = jsonObject.optInt("result",-1);
                            if(result == 0){
                                if(dialog!=null){
                                    ToastUtil.show(mWeakReferenceActivity.get(),"提交成功");
                                    dialog.dismiss();
                                }
                            }else {
                                ToastUtil.show(mWeakReferenceActivity.get(),"服务器异常");
                            }
                        }else {
                            ToastUtil.show(mWeakReferenceActivity.get(),"服务器异常");
                        }
                    }catch (Exception e){e.printStackTrace();}
                }

                @Override
                public void onFail(int i) {
                    TLog.e(TAG, "ReportFeedBackProxy上报请求超时--" + i);
                    if(NetUtils.isNetworkAvailable(mWeakReferenceActivity.get())){
                        ToastUtil.show(mWeakReferenceActivity.get(), "网络异常，请重试");
                    }else{
                        ToastUtil.show(mWeakReferenceActivity.get(),"请求超时");
                    }
                    setBtnSureClickAble(true);
                }
            }, mFeedbackQuestionHttpProxyParam);
        }

    }

}
