package com.tencent.tga.liveplugin.live.player.ui.video.view;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.base.view.DanmuSeekBar;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.liveView.LiveView;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.report.ReportManager;
import com.tencent.tga.plugin.R;

public class DanmuSettingView extends BasePopWindow{
	private RadioButton mDanmuPostion1,mDanmuPostion2,mDanmuPostion3;
	private RadioButton mDanmuSize1,mDanmuSize2,mDanmuSize3;


	@SuppressLint("InflateParams")
	public DanmuSettingView(ViewGroup parent) {
		super(parent, true, null);
		setLayout(R.layout.view_danmu_setting);
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

		int position = LiveShareUitl.getLiveDanmuPosition(mContext);
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

		int size = LiveShareUitl.getLiveDanmuSize(mContext);

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

		DanmuSeekBar danmuSeekBar = (DanmuSeekBar) root.findViewById(R.id.danmu_setting_bar);
		((TextView)(root.findViewById(R.id.danmu_setting_position))).setTypeface(LiveConfig.mFont);
		mDanmuSize1.setTypeface(LiveConfig.mFont);
		mDanmuSize2.setTypeface(LiveConfig.mFont);
		mDanmuSize3.setTypeface(LiveConfig.mFont);

		((TextView)(root.findViewById(R.id.danmu_setting_size))).setTypeface(LiveConfig.mFont);
		mDanmuPostion1.setTypeface(LiveConfig.mFont);
		mDanmuPostion1.setTypeface(LiveConfig.mFont);
		mDanmuPostion1.setTypeface(LiveConfig.mFont);

		danmuSeekBar.setTypeface(LiveConfig.mFont);
	}

	public PlayerStateView stateView;

	private View.OnClickListener mOnClickListener =  new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.danmu_setting_position1:
					stateView.setmDanmuPosition(LiveShareUitl.LIVE_DANMU_POSITION_FULL);
					LiveShareUitl.saveLiveDanmuPosition(mContext, LiveShareUitl.LIVE_DANMU_POSITION_FULL);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "1", "2");
					break;
				case R.id.danmu_setting_position2:
					stateView.setmDanmuPosition(LiveShareUitl.LIVE_DANMU_POSITION_TOP);
					LiveShareUitl.saveLiveDanmuPosition(mContext, LiveShareUitl.LIVE_DANMU_POSITION_TOP);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "1", "0");
					break;
				case R.id.danmu_setting_position3:
					stateView.setmDanmuPosition(LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM);
					LiveShareUitl.saveLiveDanmuPosition(mContext, LiveShareUitl.LIVE_DANMU_POSITION_BOTTOM);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "1", "1");
					break;
				case R.id.danmu_setting_size1:
					PlayerStateView.setmDanmuSize(LiveShareUitl.LIVE_DANMU_SIZE_SMALL);
					LiveShareUitl.saveLiveDanmuSize(mContext, LiveShareUitl.LIVE_DANMU_SIZE_SMALL);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "0", "0");
					break;
				case R.id.danmu_setting_size2:
					PlayerStateView.setmDanmuSize(LiveShareUitl.LIVE_DANMU_SIZE_NORMAL);
					LiveShareUitl.saveLiveDanmuSize(mContext, LiveShareUitl.LIVE_DANMU_SIZE_NORMAL);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "0", "1");
					break;
				case R.id.danmu_setting_size3:
					PlayerStateView.setmDanmuSize(LiveShareUitl.LIVE_DANMU_SIZE_BIG);
					LiveShareUitl.saveLiveDanmuSize(mContext, LiveShareUitl.LIVE_DANMU_SIZE_BIG);
					ReportManager.getInstance().commonReportFun("TVWordBarrageSetting", false, LiveInfo.mRoomId, "0", "2");
					break;
				default:
					break;
			}
		}
	};

}
