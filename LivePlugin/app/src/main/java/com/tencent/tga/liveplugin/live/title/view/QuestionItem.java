package com.tencent.tga.liveplugin.live.title.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.live.title.bean.QuestionBean;
import com.tencent.tga.plugin.R;

/**
 * Created by hyqiao on 2016/9/27.
 */
public class QuestionItem extends RelativeLayout{
    private Context mContext;
//    private View mContainer;
    private TextView mQuestionName;
    private ImageView /*mIvUnSelect,*/mIvSelected;
    public QuestionItem(Context context) {
        super(context);
    }

    public QuestionItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    private void init(){
        /*mContainer = */ DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.question_item_view,this);
        mQuestionName = (TextView) findViewById(R.id.mQuestionName);
        mQuestionName.setTextSize(13);
        mIvSelected = (ImageView) findViewById(R.id.mIvSelected);
//        mIvUnSelect = (ImageView) findViewById(R.id.mIvUnSelect);
    }

    public void setData(QuestionBean qb, Typeface font){
        if(font != null){
            mQuestionName.setTypeface(font);
        }
        mQuestionName.setText(qb.getName());
        if(qb.isSelect()){
            mIvSelected.setVisibility(View.VISIBLE);
        }else {
            mIvSelected.setVisibility(View.GONE);
        }
    }
}
