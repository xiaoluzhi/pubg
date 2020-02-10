package com.tencent.tga.liveplugin.poptv.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.poptv.bean.BannerType;
import com.tencent.tga.liveplugin.networkutil.SPUtils;
import com.tencent.tga.plugin.R;

/**
 * Created by hyqiao on 2017/6/14.
 */

public class BottomOperateView extends RelativeLayout {
    private static String TAG = "BottomOperateView";
    private Context mContext;
    private Resources mResources;

    private Typeface mFont;
    private RelativeLayout mRlyFullScreenContent;
    private ImageView mIvFullScreen,mIvFullScreenRedPot;

    private LinearLayout mLlyOperateContent;
    private RelativeLayout mRlyRoot;


    private RelativeLayout mRlyDefault;
    private ImageView mIvCloseDefault;

    private RelativeLayout mRlyFullScreenDefaultContent;
    private ImageView mIvFullScreenDefault,mIvFullScreenDefaultRedPot;


    private boolean redPotHasShowed = true;

    public BottomOperateView(Context context, Resources resources) {
        super(context);
        mContext = context;
        mResources = resources;
        init();
    }

    private void init(){
        XmlResourceParser xmlTitleview = mResources.getLayout(R.layout.poptv_bottom_operate_layout);
        DLPluginLayoutInflater.getInstance(mContext).inflate(xmlTitleview,this);

        mRlyFullScreenContent = (RelativeLayout) findViewById(R.id.mRlyFullScreenContent);
        mIvFullScreenRedPot = (ImageView) findViewById(R.id.mIvFullScreenRedPot);
        mIvFullScreen = (ImageView) findViewById(R.id.mIvFullScreen);

        mLlyOperateContent = (LinearLayout) findViewById(R.id.mLlyOperateContent);
        mRlyRoot = (RelativeLayout) findViewById(R.id.mRlyRoot);

        mRlyDefault = (RelativeLayout) findViewById(R.id.mRlyDefault);
        mRlyDefault.setVisibility(GONE);
        mIvCloseDefault = (ImageView) findViewById(R.id.mIvCloseDefault);

        mRlyFullScreenDefaultContent = (RelativeLayout) findViewById(R.id.mRlyFullScreenDefaultContent);
        mIvFullScreenDefaultRedPot = (ImageView) findViewById(R.id.mIvFullScreenDefaultRedPot);
        mIvFullScreenDefault = (ImageView) findViewById(R.id.mIvFullScreenDefault);

        redPotHasShowed = SPUtils.SPGetBool(mContext,SPUtils.screen_tv);
        if(!redPotHasShowed){
            mIvFullScreenRedPot.setVisibility(VISIBLE);
        }
    }

    public void setFullScreenClick(OnClickListener listener){
        if(mIvFullScreen != null && listener != null){
            mIvFullScreen.setOnClickListener(listener);
        }
        if(mIvFullScreenDefault != null && listener != null){
            mIvFullScreenDefault.setOnClickListener(listener);
        }

    }

    private ShowDefaultUIListener mShowDefaultUIListener;
    public void setShowDefaultUIListener(ShowDefaultUIListener l){
        this.mShowDefaultUIListener = l;
    }

    public interface ShowDefaultUIListener{
        void showDefaultui();
    }

    public boolean isShowedRedPot() {
        return redPotHasShowed;
    }

    public void setRedPotInVisible() {
        SPUtils.SPSaveBool(mContext,SPUtils.screen_tv,true);
    }

    public void setCloseClick(OnClickListener listener){
        if(mIvCloseDefault != null && listener != null){
            mIvCloseDefault.setOnClickListener(listener);
        }
    }

    public void showDefaultUI(){
        if(mIvFullScreen != null ){
            mRlyFullScreenContent.setVisibility(GONE);
        }
        if(mLlyOperateContent != null ){
            mLlyOperateContent.setVisibility(GONE);
        }

        if(mRlyDefault != null ){
            mRlyDefault.setVisibility(VISIBLE);
            if(!redPotHasShowed){
                mIvFullScreenDefaultRedPot.setVisibility(VISIBLE);
            }
        }
    }

    private boolean isBannerBgSucc = false;
    private boolean isButtonBgSucc = false;

    public void setData(final String operation_image, final String operation_button){
        try{
            if(!TextUtils.isEmpty(operation_image) && !TextUtils.isEmpty(operation_button) ){
                ImageLoaderUitl.loadImageFromNet(operation_image, new ImageLoaderUitl.ImageLoadSuccess() {
                    @Override
                    public void loadSucc() {
                        isBannerBgSucc = true;
                        showNetImage(operation_image,operation_button);
                    }
                });

                ImageLoaderUitl.loadImageFromNet(operation_button, new ImageLoaderUitl.ImageLoadSuccess() {
                    @Override
                    public void loadSucc() {
                        isButtonBgSucc = true;
                        showNetImage(operation_image,operation_button);
                    }
                });
            }else {
                showDefaultUI();
            }

        }catch (Throwable throwable){
            TLog.e(TAG,"ImageLoaderUitl throwable : "+throwable.getMessage());
        }
    }

    private void showNetImage(String operation_image,String operation_button){
        try {
            if(isBannerBgSucc && isButtonBgSucc){
                ImageLoaderUitl.loadImageForViewGroup(operation_image,mRlyRoot);
                mIvFullScreen.setBackground(null);
                ImageLoaderUitl.loadImageForImageView(operation_button,mIvFullScreen);
            }
        }catch (Exception e){
            TLog.e(TAG,"showNetImage error : "+e.getMessage());
        }

    }

    private TextView getTipsTextView(int type,int num){
        String str = null;
        if(type == BannerType.BannerSeeingFriendList.getValue()){
            str = String.format("等%s名好友正在观赛",num);
        }else {
            str = String.format("等%s名好友看过比赛",num);
        }

        TextView textView = new TextView(mContext);
        textView.setPadding(DeviceUtils.dip2px(mContext,2.5f),0,0,0);
        textView.setTextSize(12);
        textView.setSingleLine();
        textView.setTextColor(Color.parseColor("#fe9d20"));
        if(mFont != null){
            textView.setTypeface(mFont);
        }
        if(type == BannerType.BannerSeeingFriendList.getValue() && num<=3){
            textView.setText("正在观赛");
        }else {
            SpannableString spannableString = new SpannableString(str);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#ff5400"));
            spannableString.setSpan(colorSpan, 1, 1+(""+num).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        }


        return textView;
    }

    public void setFont(Typeface font){
        if(font!=null){
            mFont = font;
            //mBtnFullScreen.setTypeface(mFont);
        }
    }

    public void releaseView(){
        setBackground(null);
        mRlyRoot.setBackground(null);
        mIvFullScreen.setBackground(null);
    }
}
