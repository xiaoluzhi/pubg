package com.tencent.tga.liveplugin.live.player.ui.video.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
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
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

/**
 * 选集
 */
public class VideoSelectView extends FrameLayout {
    private final View root;
    private final LayoutInflater inflater;
    private final Context context;
    protected ViewGroup parent;
    //	private PopupWindow pop;
    public DismissListener mDismissListener;
    private String[] mSlectNames = {"第一场", "第二场", "第三场", "第四场", "第五场", "第六场", "第七场", "第八场", "第九场", "第十场"};

    private ArrayList<String> mSelects = new ArrayList<>();
    private final ListView lvDefinition;
    private int mPosition;
    private VideoSelectChangeListener mChangeListener;

    public Typeface font = null;

    public interface VideoSelectChangeListener {
        void onChange(int position);

        void onClick();

        void onDismiss();
    }

    @SuppressLint("InflateParams")
    public VideoSelectView(ViewGroup parent) {
        super(parent.getContext());
        this.parent = parent;
        inflater = DLPluginLayoutInflater.getInstance(parent.getContext());
        context = inflater.getContext();
        root = inflater.inflate(R.layout.view_controller_definition, null);
        root.setOnClickListener(null);
        lvDefinition = (ListView) root.findViewById(R.id.lv_definition);

        lvDefinition.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TLog.d(VideoSelectView.class.getSimpleName(), "setOnItemClickListener ");
                if (mPosition == position) {
                    return;
                }
                String temp = mSelects.get(position);
                if (temp != null && mChangeListener != null) {
                    mChangeListener.onChange(position);
                }
                dismiss();
            }
        });
    }

    public void setVideoSelectChangeListener(VideoSelectChangeListener listener) {
        mChangeListener = listener;
    }

    public class DefinitionItemAdapter extends BaseAdapter {

        public DefinitionItemAdapter() {
        }

        @Override
        public int getCount() {
            if (mSelects != null)
                return mSelects.size();
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
            String temp = mSelects.get(position);
            TextView tvDefinition = new TextView(context);
            tvDefinition.setSingleLine(true);
            tvDefinition.setLayoutParams(new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tvDefinition.setPadding(0, DeviceUtils.dip2px(context, 15), 0,
                    DeviceUtils.dip2px(context, 15));
            tvDefinition.setGravity(Gravity.CENTER);
            tvDefinition.setTextColor(0xffc6d2f1);
            tvDefinition.setTextSize(14);
            tvDefinition.setSelected(false);
            if (temp != null) {
                if (mPosition == position) {
                    tvDefinition.setTextColor(0xffFFC951);
                }
                if (temp != null && !"".equals(temp)) {
                    tvDefinition.setText(temp);
                }
            }
            if (font != null)
                tvDefinition.setTypeface(font);
            return tvDefinition;
        }

    }

    public void dismiss() {
        try {
            if (null != parent && parent.indexOfChild(root) != -1) {
                parent.removeView(root);
            }
            if (mChangeListener != null) {
                mChangeListener.onDismiss();
            }
        } catch (Exception e) {

        }
    }

    public void setOnDismissListener(DismissListener listener) {
        mDismissListener = listener;
    }

    public boolean isShowing() {
        if (null != parent && parent.indexOfChild(root) != -1) {
            return true;
        } else {
            return false;
        }
    }

    public void show(ArrayList<String> vids, boolean isFromVidList, boolean isPlayback, int position) {
        if (vids == null || vids.size() <= 0)
            return;
        if (null != mSelects) {
            mSelects.clear();
            if (isFromVidList && !isPlayback) {
                for (int i = vids.size(); i > 0; i--) {
                    mSelects.add("第" + i + "期");
                }
            } else {
                for (int i = 0; i < vids.size() && vids.size() < 10; i++)
                    mSelects.add(mSlectNames[i]);
            }
            mPosition = position;

            if (mSelects.size() > 0) {
                DefinitionItemAdapter mAdapter = new DefinitionItemAdapter();
                lvDefinition.setAdapter(mAdapter);
            }
        }
        LayoutParams layoutParams = new LayoutParams(DeviceUtils.dip2px(getContext(), 85), LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        if (null != parent && parent.indexOfChild(root) == -1) {
            parent.addView(root, layoutParams);
        }
    }

    public interface DismissListener{
        void onDismiss();
    }

}
