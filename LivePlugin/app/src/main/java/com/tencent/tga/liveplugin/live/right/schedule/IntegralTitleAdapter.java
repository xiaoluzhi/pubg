package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class IntegralTitleAdapter extends BaseAdapter {
    private static final String TAG = "IntegralTitleAdapter";
    private ArrayList<String> arrayList;
    private Context context;
    private int checked = -1;

    public IntegralTitleAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            view = DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_title_integral_details, null);
            TextView textView = view.findViewById(R.id.item_title);
            if (checked == i) {
                textView.setBackgroundResource(R.drawable.integral_click);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setPadding(DeviceUtils.dip2px(context, 20), 0, 0, 0);
            } else {
                textView.setBackgroundResource(R.drawable.integral_list_item_bg);
                textView.setTextColor(Color.parseColor("#8D9293"));
                textView.setPadding(DeviceUtils.dip2px(context, 20), 0, 0, 0);
            }
            if (i == 0 && checked == -1) {
                textView.setBackgroundResource(R.drawable.integral_click);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setPadding(DeviceUtils.dip2px(context, 20), 0, 0, 0);
            }
            textView.setText(arrayList.get(i));
        } catch (Exception e) {
            TLog.e(TAG, "IntegralTitleAdapter getView error is" + e.getMessage());
        }
        return view;
    }
}
