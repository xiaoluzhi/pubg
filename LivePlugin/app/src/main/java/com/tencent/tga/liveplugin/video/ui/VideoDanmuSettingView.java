package com.tencent.tga.liveplugin.video.ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

public class VideoDanmuSettingView extends BasePopWindow {
	private RadioButton mDanmuPostion1,mDanmuPostion2,mDanmuPostion3;
	private RadioButton mDanmuSize1,mDanmuSize2,mDanmuSize3;

	public DanmuSettingsChangeListener mListener;

	@SuppressLint("InflateParams")
	public VideoDanmuSettingView(ViewGroup parent) {
		super(parent, true, null);
		setLayout(R.layout.video_danmu_setting);
		initViews();
	}

	private void initViews(){
		mDanmuPostion1 = (RadioButton) root.findViewById(R.id.danmu_setting_position1);
		mDanmuPostion2 = (RadioButton) root.findViewById(R.id.danmu_setting_position2);
		mDanmuPostion3 = (RadioButton) root.findViewById(R.id.danmu_setting_position3);

		mDanmuPostion1.setOnClickListener(mOnClickListener);
		mDanmuPostion2.setOnClickListener(mOnClickListener);
		mDanmuPostion3.setOnClickListener(mOnClickListener);

		mDanmuSize1 = (RadioButton) root.findViewById(R.id.danmu_setting_size1);
		mDanmuSize2 = (RadioButton) root.findViewById(R.id.danmu_setting_size2);
		mDanmuSize3 = (RadioButton) root.findViewById(R.id.danmu_setting_size3);

		mDanmuSize1.setOnClickListener(mOnClickListener);
		mDanmuSize2.setOnClickListener(mOnClickListener);
		mDanmuSize3.setOnClickListener(mOnClickListener);

		int position = LiveShareUitl.getVideoDanmuPosition(mContext);
		switch (position)
		{
			case LiveShareUitl.LIVE_DANMU_POSITION_FULL:
				mDanmuPostion1.setChecked(true);
				break;
			case LiveShareUitl.LIVE_DANMU_POSITION_TOP:
				mDanmuPostion2.setChecked(true);
				break;
			case LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM:
				mDanmuPostion3.setChecked(true);
				break;
			default:break;
		}

		int size = LiveShareUitl.getVideoDanmuSize(mContext);

		switch (size)
		{
			case LiveShareUitl.LIVE_DANMU_SIZE_SMALL:
				mDanmuSize1.setChecked(true);
				break;
			case LiveShareUitl.LIVE_DANMU_SIZE_NORMAL:
				mDanmuSize2.setChecked(true);
				break;
			case LiveShareUitl.LIVE_DANMU_SIZE_BIG:
				mDanmuSize3.setChecked(true);
				break;
			default:break;
		}

		VideoDanmuSeekBar danmuSeekBar = (VideoDanmuSeekBar) root.findViewById(R.id.danmu_setting_bar);
//		if(LiveView.mFont != null){
//			((TextView)(root.findViewById(R.id.danmu_setting_position))).setTypeface(LiveView.mFont);
//			mDanmuSize1.setTypeface(LiveView.mFont);
//			mDanmuSize2.setTypeface(LiveView.mFont);
//			mDanmuSize3.setTypeface(LiveView.mFont);
//
//			((TextView)(root.findViewById(R.id.danmu_setting_size))).setTypeface(LiveView.mFont);
//			mDanmuPostion1.setTypeface(LiveView.mFont);
//			mDanmuPostion1.setTypeface(LiveView.mFont);
//			mDanmuPostion1.setTypeface(LiveView.mFont);
//			danmuSeekBar.setTypeface(LiveView.mFont);
//		}
        danmuSeekBar.mListener = new VideoDanmuSeekBar.OnSeekListener() {
			@Override
			public void onSeek(int showProgress) {
				if (null != mListener) mListener.changeDanmuAlpha(showProgress);
			}
		};
	}

	private View.OnClickListener mOnClickListener =  new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.danmu_setting_position1:
					if (null != mListener) mListener.changeDanmuPosition(LiveShareUitl.LIVE_DANMU_POSITION_FULL);
					LiveShareUitl.saveVideoDanmuPosition(mContext, LiveShareUitl.LIVE_DANMU_POSITION_FULL);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "1", "0");
					break;
				case R.id.danmu_setting_position2:
					if (null != mListener) mListener.changeDanmuPosition(LiveShareUitl.LIVE_DANMU_POSITION_TOP);
					LiveShareUitl.saveVideoDanmuPosition(mContext, LiveShareUitl.LIVE_DANMU_POSITION_TOP);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "1", "1");
					break;
				case R.id.danmu_setting_position3:
					if (null != mListener) mListener.changeDanmuPosition(LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM);
					LiveShareUitl.saveVideoDanmuPosition(mContext, LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "1", "2");
					break;
				case R.id.danmu_setting_size1:
					if (null != mListener) mListener.changeDanmuSize(LiveShareUitl.LIVE_DANMU_SIZE_SMALL);
					LiveShareUitl.saveVidewDanmuSize(mContext, LiveShareUitl.LIVE_DANMU_SIZE_SMALL);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "0", "0");
					break;
				case R.id.danmu_setting_size2:
					if (null != mListener) mListener.changeDanmuSize(LiveShareUitl.LIVE_DANMU_SIZE_NORMAL);
					LiveShareUitl.saveVidewDanmuSize(mContext, LiveShareUitl.LIVE_DANMU_SIZE_NORMAL);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "0", "1");
					break;
				case R.id.danmu_setting_size3:
					if (null != mListener) mListener.changeDanmuSize(LiveShareUitl.LIVE_DANMU_SIZE_BIG);
					LiveShareUitl.saveVidewDanmuSize(mContext, LiveShareUitl.LIVE_DANMU_SIZE_BIG);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", true, LiveInfo.mRoomId, "0", "2");
					break;
				default:
					break;
			}
		}
	};

	public interface DanmuSettingsChangeListener{
		void changeDanmuPosition(int position);
		void changeDanmuSize(int size);
		void changeDanmuAlpha(int progres);
	}

}
