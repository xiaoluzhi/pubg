package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBean;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class ScheduleTeamAdapter extends BaseAdapter {
    private ArrayList<TeamBean> arrayList;
    private Context context;
    public ScheduleTeamAdapter(ArrayList<TeamBean> arrayList, Context context){
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            view= DLPluginLayoutInflater.getInstance(context).inflate(R.layout.schedule_team_view_item,null);
            holder=new ViewHolder();
            holder.textView=view.findViewById(R.id.team_item_name);
            holder.imageView=view.findViewById(R.id.team_item_img);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        holder.textView.setText(arrayList.get(i).getTeam_name());
        ImageLoaderUitl.loadimage(arrayList.get(i).getTeam_logo(),holder.imageView);
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
