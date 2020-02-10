package com.tencent.tga.liveplugin.base.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.live.player.ui.video.view.PlayerStateView;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.plugin.R;

/**
 * Created by Chance on 14-5-15.
 */
public class DanmuSeekBar extends LinearLayout {

    Context mContext;
    SeekBar mSeekBar;
    TextView mPercent;

    private final static String TAG = "DANMU_Seekbar";
    
    private TextView mTitle;

    public DanmuSeekBar(Context context) {
        super(context);
        mContext = context;
        initUI();
    }

    public DanmuSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
    }

    private void initUI() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        DLPluginLayoutInflater.getInstance(mContext).inflate(R.layout.layout_danmu_seekbar, this, true);
        mSeekBar = (SeekBar) findViewById(R.id.video_bar_seek_bar);

        mSeekBar.setMax(LiveShareUitl.LIVE_DANMU_ALPHA_MAX);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);

        mTitle = (TextView) findViewById(R.id.tv_type);
        mPercent = (TextView) findViewById(R.id.percent);

        setDanmuLevel();
    }

    private void setmPercent(int alpha){
        if (mPercent !=null) {
            int pecent = (alpha*100/LiveShareUitl.LIVE_DANMU_ALPHA_MAX);
            mPercent.setText(pecent+"%");
        }


    }

    
    /**
     * 设置弹幕的设置各值
     */
    public void setDanmuLevel() {
        int showProgress = LiveShareUitl.getLiveDanmuAlpha(mContext);
    	mSeekBar.setProgress(showProgress);

        setmPercent(showProgress);
    }

    public void setTypeface(Typeface font )
    {
        mTitle.setTypeface(font);
        mPercent.setTypeface(font);
    }

    

    OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           // Log.d(TAG, "onProgressChanged:" + progress + " user: " + fromUser);
//            if (mTotalTime > 0) {
//                long timeMs = DanmuSeekBar.this.getCurrentTime();
//                mCurrentTimeTextView.setText(timeString(timeMs));
//            }

            setmPercent(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "onStartTrackingTouch");
          
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            
            int showProgress = 0;
            if (seekBar != null) {

            	showProgress = seekBar.getProgress();

            	seekBar.setProgress(showProgress);
            }
            PlayerStateView.setmDanmuAlpha(showProgress);
            LiveShareUitl.saveLiveDanmuAlpha(mContext,showProgress);
        }
    };
}
