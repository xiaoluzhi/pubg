package com.tencent.tga.liveplugin.live.player.ui.video.view;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.common.log.tga.TLog;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKNetVideoInfo;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.util.LiveShareUitl;
import com.tencent.tga.liveplugin.live.player.PlayView;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class LiveDefineView extends BasePopWindow {

	private ArrayList<TVKNetVideoInfo.DefnInfo> definitions;
	private ListView lvDefinition;
	private TVKNetVideoInfo.DefnInfo playVideoDefinition;
	private VideoDefinChangeListener mChangeListener;


	public interface VideoDefinChangeListener {
		void onChange(TVKNetVideoInfo.DefnInfo defnInfo);

		void onClick();

		void onDismiss();
	}

	@SuppressLint("InflateParams")
	public LiveDefineView(PlayView parent, LiveDefineView.VideoDefinChangeListener listener) {
		super(parent, true, () -> listener.onDismiss());
		setLayout(R.layout.view_controller_definition);
		lvDefinition =  root.findViewById(R.id.lv_definition);

		lvDefinition.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TLog.d(LiveDefineView.class.getSimpleName(),"setOnItemClickListener ");
				if (playVideoDefinition.getDefnId() == definitions.get(definitions.size()-position-1).getDefnId())
				{
					return;
				}
				TVKNetVideoInfo.DefnInfo temp = definitions.get(definitions.size()-position-1);
				if (temp != null && mChangeListener!=null) {
					mChangeListener.onChange(temp);
					LiveShareUitl.saveLiveTips(parent.getContext(), temp.getDefn());
				}
				dismiss();
			}
		});
	}

	public void setVideoDefinChangeListener(VideoDefinChangeListener listener){
		mChangeListener = listener;
	}

	public class DefinitionItemAdapter extends BaseAdapter {

		public DefinitionItemAdapter() {
		}

		@Override
		public int getCount() {
			if (definitions != null)
				return definitions.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TVKNetVideoInfo.DefnInfo temp = definitions.get(definitions.size()-position-1);
			TextView tvDefinition = new TextView(mContext);
			tvDefinition.setLayoutParams(new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			tvDefinition.setPadding(0, DeviceUtils.dip2px(mContext, 15), 0,
					DeviceUtils.dip2px(mContext, 15));
			tvDefinition.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
			tvDefinition.setTextColor(0xFFFFFFFF);
			tvDefinition.setTextSize(11);
			tvDefinition.setSelected(false);
			if (temp != null) {
				if (playVideoDefinition != null
					&& !TextUtils.isEmpty(temp.getDefnName()) && temp.getDefnName().equals(playVideoDefinition.getDefnName())) {
					tvDefinition.setTextColor(0xffFFC951);
				}
				if(temp.getDefnName()!=null && !"".equals(temp.getDefnName())){
					String string = temp.getDefnName().trim();
					if (!TextUtils.isEmpty(string)&& string.length()>2)
						tvDefinition.setText(string.substring(0,2)+" "+string.substring(2,string.length()));
				}
			}
			tvDefinition.setTypeface(LiveConfig.mFont);
			return tvDefinition;
		}

	}

	public void dismiss() {
		super.dismiss();
		if (mChangeListener !=null)
		{
			mChangeListener.onDismiss();
		}
	}


	public void show(PlayView playView ,ArrayList<TVKNetVideoInfo.DefnInfo> definitionsArrayList,final TVKNetVideoInfo.DefnInfo playVideoDefinition,int h) {
		if (definitionsArrayList == null || definitionsArrayList.size() == 0) {
			return;
		}
		showInPlayViewRightAndBottom(playView, DeviceUtils.dip2px(mContext, 100));
		this.definitions = definitionsArrayList;
		this.playVideoDefinition = playVideoDefinition;

		DefinitionItemAdapter mAdapter = new DefinitionItemAdapter();
		lvDefinition.setAdapter(mAdapter);
	}

}
