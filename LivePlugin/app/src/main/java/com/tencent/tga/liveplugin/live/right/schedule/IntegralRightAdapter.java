package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class IntegralRightAdapter extends BaseAdapter {
    private static final String TAG = "IntegralRightAdapter";
    private Context context;
    private ArrayList<TeamBankBean> arrayList;
    private int type;

    public IntegralRightAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void setData(ArrayList<TeamBankBean> arrayList,int type) {
        this.arrayList = arrayList;
        this.type=type;
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
            view = DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_right_integral_details, null);
            TextView grade = view.findViewById(R.id.item_right_integral_gradeTotal);
            if (type == 0) {
                if (arrayList.get(i).getTotal_score() != null)
                    grade.setText(arrayList.get(i).getTotal_score());
            } else {
                grade.setText(arrayList.get(i).getList().get(type - 1).getBo_score() + "");
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
