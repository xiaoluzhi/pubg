package com.tencent.tga.liveplugin.live.right.schedule.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.plugin.R;


/**
 * Created by hyqiao on 2016/8/30.
 */
public class DataErrorView extends RelativeLayout{

    private Context mContext;
//    private View mContainer;
    private TextView mTvTips;
    private Button mBtnRefresh;
    private LinearLayout mLlyAll;

    public final static String ERROR_MSG_EMPTY = "数据为空";
    public final static String ERROR_MSG_LOADFAIL = "数据加载异常,请重试!";

    public DataErrorView(Context context, String errMsg) {
        super(context);
        this.mContext = context;
        initUi();
        setFont(LiveConfig.mFont);
        setErrorText(errMsg);
    }

    public DataErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initUi();
    }

    private void initUi(){
        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.error_data_view, this);
        mTvTips = (TextView) findViewById(R.id.mTvTips);
        mBtnRefresh = (Button) findViewById(R.id.mBtnRefresh);
        mLlyAll = (LinearLayout) findViewById(R.id.mLlyAll);
        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRefreshListener!=null){
                    if(!NoDoubleClickUtils.isDoubleClick()){
                        mRefreshListener.onClick();
                    }
                }
            }
        });
    }

    public void setVisible(boolean isVisible){
        if(mLlyAll!=null){
            if(isVisible){
                mLlyAll.setVisibility(VISIBLE);
            }else {
                mLlyAll.setVisibility(GONE);
            }
        }
    }
    public void setErrorText(String str){
        if(mTvTips != null){
            mTvTips.setText(str);
        }
    }

    public void setmBtnRefreshVisible(boolean visible){
        if(mBtnRefresh != null){
            if(visible){
                mBtnRefresh.setVisibility(View.VISIBLE);
            }else {
                mBtnRefresh.setVisibility(View.GONE);
            }
        }
    }

    public void setFont(Typeface font){
        if(mTvTips != null && font != null){
            mTvTips.setTypeface(font);
            mBtnRefresh.setTypeface(font);
        }
    }

    private RefreshListener mRefreshListener;
    public interface RefreshListener{
        void onClick();
    }

    public void setRefreshListener(RefreshListener l){
        this.mRefreshListener = l;
        mBtnRefresh.setVisibility(View.VISIBLE);
    }
}
