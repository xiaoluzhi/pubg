package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class IntegralLeftAdapter extends BaseAdapter {
    private static final String TAG = "IntegralLeftAdapter";
    private Context context;
    private ArrayList<TeamBankBean> arrayList;

    public IntegralLeftAdapter(Context context) {
        this.context = context;

    }

    public void setData(ArrayList<TeamBankBean> arrayList) {
        this.arrayList = arrayList;
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
            view = DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_left_integral_details, null);
            ImageView rankImg = view.findViewById(R.id.item_left_integral_rankImg);
            TextView rankText = view.findViewById(R.id.item_left_integral_rankText);
            ImageView teamLogo = view.findViewById(R.id.item_left_integral_teamImg);
            TextView teamName = view.findViewById(R.id.item_left_integral_teamName);
            View line = view.findViewById(R.id.line);
            if (i == 0) {
                rankImg.setImageResource(R.drawable.first_place);
            } else if (i == 1) {
                rankImg.setImageResource(R.drawable.second_place);
            } else if (i == 2) {
                rankImg.setImageResource(R.drawable.third_place);
            } else {
                rankText.setText((i + 1) + "");
            }
            ImageLoaderUitl.loadimage(arrayList.get(i).getTeam_logo(), teamLogo);
            teamName.setText(arrayList.get(i).getTeam_name());
            if (i == arrayList.size() - 1)
                line.setVisibility(View.GONE);
            return view;
        } catch (Exception e) {
            TLog.e(TAG, "IntegralDetailAdapter getView error is" + e.getMessage());
            return view;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
