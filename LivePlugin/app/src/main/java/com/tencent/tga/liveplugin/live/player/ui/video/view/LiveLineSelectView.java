package com.tencent.tga.liveplugin.live.player.ui.video.view;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.NoDoubleClickUtils;
import com.tencent.tga.liveplugin.base.util.commonadapter.CommonAdapter;
import com.tencent.tga.liveplugin.base.util.commonadapter.ViewHolder;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.LiveInfo;
import com.tencent.tga.liveplugin.live.common.bean.ChannelInfo;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.liveplugin.live.player.event.PlayViewEvent;
import com.tencent.tga.plugin.R;

import android.annotation.SuppressLint;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class LiveLineSelectView extends BasePopWindow{
	private static final String TAG = "LiveLineSelectView";

	private PullToRefreshListView mListView;
	private CommonAdapter mListAda;
	public List<ChannelInfo> channelInfos = new ArrayList<>();

	@SuppressLint("InflateParams")
	public LiveLineSelectView(PlayView parent, PopupWindow.OnDismissListener listener) {
		super(parent, true, listener);
		setLayout(R.layout.view_live_line_select);
		initViews();
	}

	private void initViews(){

		mListView =  root.findViewById(R.id.live_line_listView);

		mListAda = new CommonAdapter<ChannelInfo>(mContext, channelInfos, R.layout.item_line_select_list) {
			@Override
			public void convert(ViewHolder holder, ChannelInfo channelInfo) {
				try {
					LiveLineSelectItem lineSelectItem = holder.getView(R.id.mLineSelectItem);
					lineSelectItem.setData(channelInfo);
					lineSelectItem.setOnClickListener(view -> {
						try {
							if (NoDoubleClickUtils.isDoubleClick()) return;
							if (LiveInfo.mSourceId == channelInfo.getRoom_list().get(0).getSourceid()) 	return;
							PlayViewEvent.lineChange(new LiveEvent.LiveLineChange(channelInfo.getRoom_list().get(0), channelInfo.getPlay_type()));
							LiveLineSelectView.this.dismiss();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					});
				} catch (Exception e) {
					TLog.e(TAG, "parse line select view exception " + e.getMessage());
				}
			}
		};

		mListView.setAdapter(mListAda);
		mListView.invalidate();
		mListView.setMode(PullToRefreshBase.Mode.DISABLED);
	}


	public void dismiss() {
		super.dismiss();
	}

	public void show(PlayView view ,String ids) {
		LiveShareUitl.saveLiveLineRed(mContext,ids);
        mListAda.notifyDataSetChanged();
		super.showInPlayViewRightAndBottom(view, DeviceUtils.dip2px(mContext, 220));
	}

	public void refresh() {
	    mListAda.notifyDataSetChanged();
    }


}
