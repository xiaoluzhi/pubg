package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class IntegralTitleAdapter extends BaseAdapter {
    private ArrayList<String> arrayList;
    private Context context;
    private int checked=-1;
    public IntegralTitleAdapter(ArrayList<String> arrayList,Context context){
        this.arrayList=arrayList;
        this.context=context;
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

    public void setChecked(int checked){
        this.checked=checked;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            view= DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_title_integral_details,null);
            holder=new ViewHolder();
            holder.textView=view.findViewById(R.id.item_title);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        if (checked==i){
            holder.textView.setBackgroundResource(R.drawable.integral_click);
            holder.textView.setTextColor(Color.parseColor("#FFFFFF"));

        } else {
            holder.textView.setBackgroundResource(R.drawable.integral_list_item_bg);
            holder.textView.setTextColor(Color.parseColor("#8D9293"));
        }
        if (i == 0 && checked == -1) {
            holder.textView.setBackgroundResource(R.drawable.integral_click);
            holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.textView.setText(arrayList.get(i));
        return view;
    }
    public class ViewHolder{
        TextView textView;
    }
}
