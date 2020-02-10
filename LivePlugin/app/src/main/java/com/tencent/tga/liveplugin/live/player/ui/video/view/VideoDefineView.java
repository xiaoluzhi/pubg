package com.tencent.tga.liveplugin.live.player.ui.video.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.qqlive.multimedia.tvkplayer.api.TVKNetVideoInfo;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class VideoDefineView extends FrameLayout {
	private final View root;
	private final LayoutInflater inflater;
	private final Context context;
	protected ViewGroup parent;

	private ArrayList<TVKNetVideoInfo.DefnInfo> definitions;
	private final ListView lvDefinition;
	private TVKNetVideoInfo.DefnInfo playVideoDefinition;
	private LiveDefineView.VideoDefinChangeListener mChangeListener;
	public DismissListener mDismissListener;

	public Typeface font = null;

	@SuppressLint("InflateParams")
	public VideoDefineView(ViewGroup parent) {
		super(parent.getContext());
		this.parent = parent;
		context = parent.getContext();
		inflater = DLPluginLayoutInflater.getInstance(context);
		root = inflater.inflate(R.layout.view_controller_definition, null);
		root.setOnClickListener(null);
		lvDefinition = (ListView) root.findViewById(R.id.lv_definition);

		lvDefinition.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TLog.d(VideoDefineView.class.getSimpleName(),"setOnItemClickListener ");
				if (playVideoDefinition.getDefnId() == definitions.get(definitions.size()-position-1).getDefnId()) {
					return;
				}
				TVKNetVideoInfo.DefnInfo temp = definitions.get(definitions.size()-1-position);
				if (temp != null && mChangeListener!=null) {
					mChangeListener.onChange(temp);
				}
				dismiss();
			}
		});

	}

	public void setVideoDefinChangeListener(LiveDefineView.VideoDefinChangeListener listener){
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
			TVKNetVideoInfo.DefnInfo temp = definitions.get(definitions.size()-1-position);
			TextView tvDefinition = new TextView(context);

			tvDefinition.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			tvDefinition.setPadding(0, DeviceUtils.dip2px(context, 15), 0, DeviceUtils.dip2px(context, 15));
			tvDefinition.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
			tvDefinition.setTextColor(0xFFFFFFFF);
			tvDefinition.setTextSize(12);
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
			if (font!=null)
				tvDefinition.setTypeface(font);
			return tvDefinition;
		}

	}

	public boolean isShowing() {
		if (null != parent && parent.indexOfChild(root) != -1) {
			return true;
		} else {
			return false;
		}
	}

	public void dismiss() {
		try {
			if (null != parent && parent.indexOfChild(root) != -1) {
				parent.removeView(root);
			}
			if (mDismissListener != null) {
				mDismissListener.onDismiss();
			}
			if (mChangeListener != null) {
				mChangeListener.onDismiss();
			}
		} catch (Exception e) {
			TLog.e("VideoDefinView", "dismiss exception");
		}
	}

	public void setOnDismissListener(DismissListener listener) {
		mDismissListener = listener;
	}
	public void show(ArrayList<TVKNetVideoInfo.DefnInfo> definitionsArrayList, final TVKNetVideoInfo.DefnInfo playVideoDefinition) {
		this.definitions = definitionsArrayList;
		LayoutParams layoutParams = new LayoutParams(DeviceUtils.dip2px(getContext(), 85), LayoutParams.MATCH_PARENT);
		layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		if (null != parent && parent.indexOfChild(root) == -1) {
			parent.addView(root, layoutParams);
		}
		if (definitions != null && definitions.size() > 0) {
			this.playVideoDefinition = playVideoDefinition;
			DefinitionItemAdapter mAdapter = new DefinitionItemAdapter();
			lvDefinition.setAdapter(mAdapter);
		}
	}

	public interface DismissListener{
		void onDismiss();
	}

}
