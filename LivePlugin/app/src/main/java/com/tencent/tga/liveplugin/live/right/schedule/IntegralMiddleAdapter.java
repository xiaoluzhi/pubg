package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class IntegralMiddleAdapter extends BaseAdapter {
    private static final String TAG = "IntegralDetailAdapter";
    private Context context;
    private ArrayList<TeamBankBean> arrayList;
    private int type;

    public IntegralMiddleAdapter(Context context) {
        this.context = context;

    }

    public void setData(ArrayList<TeamBankBean> arrayList, int type) {
        this.arrayList = arrayList;
        this.type = type;
    }

    public void refreshData(ArrayList<TeamBankBean> arrayList, int type) {
        this.arrayList = arrayList;
        this.type = type;
        notifyDataSetChanged();
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            TLog.e(TAG,"arrayList.get(0).getBo_count()=="+arrayList.get(0).getBo_count());
            view = DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_middle_integral_details, null);
            LinearLayout linearLayout1 = view.findViewById(R.id.item_middle_integral_linear1);
            LinearLayout linearLayout2 = view.findViewById(R.id.item_middle_integral_linear2);
            if (type == 0) {
                for (int j = 0; j < arrayList.get(i).getList().size(); j++) {
                    TextView textView = new TextView(context);
                    if (arrayList.get(i).getList().size() < 5) {
                        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    } else {
                        textView.setLayoutParams(new LinearLayout.LayoutParams(DeviceUtils.dip2px(context, 48), DeviceUtils.dip2px(context, 21)));
                    }
                    textView.setText(arrayList.get(i).getList().get(j).getBo_score() + "");
                    textView.setTextColor(Color.parseColor("#858585"));
                    textView.setTextSize(8);
                    textView.setGravity(Gravity.CENTER);
                    if (arrayList.get(i).getList().size() < 5) {
                        linearLayout2.setVisibility(View.VISIBLE);
                        linearLayout1.setVisibility(View.GONE);
                        linearLayout2.addView(textView);
                    } else {
                        linearLayout1.addView(textView);
                    }
                }
            } else {
                TextView rank = new TextView(context);
                rank.setLayoutParams(new LinearLayout.LayoutParams(DeviceUtils.dip2px(context, 120), DeviceUtils.dip2px(context, 21)));
                rank.setText(arrayList.get(i).getList().get(type - 1).getRank_score() + "");
                rank.setTextColor(Color.parseColor("#858585"));
                rank.setTextSize(8);
                rank.setGravity(Gravity.CENTER);
                linearLayout1.addView(rank);
                TextView eliminlate = new TextView(context);
                eliminlate.setLayoutParams(new LinearLayout.LayoutParams(DeviceUtils.dip2px(context, 120), DeviceUtils.dip2px(context, 21)));
                eliminlate.setText(arrayList.get(i).getList().get(type - 1).getEliminlate_score() + "");
                eliminlate.setTextColor(Color.parseColor("#858585"));
                eliminlate.setTextSize(8);
                eliminlate.setGravity(Gravity.CENTER);
                linearLayout1.addView(eliminlate);
            }
        } catch (Exception e) {
            TLog.e(TAG, "IntegralDetailAdapter getView error is" + e.getMessage());
        }
        return view;

    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
