package com.tencent.tga.liveplugin.live.right.schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.plugin.R;

import java.util.ArrayList;

public class IntegralDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TeamBankBean> arrayList;
    private int type;
    public IntegralDetailAdapter(Context context){
        this.context=context;

    }
    public void setData(ArrayList<TeamBankBean> arrayList,int type){
        this.arrayList=arrayList;
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
        ViewTotalHolder holderTotal=null;
        ViewGamesHolder holderGames=null;
       if (view==null){
           if (type==0){
               view= DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_total_integral_details,null);
               holderTotal=new ViewTotalHolder();
               holderTotal.rankImg=view.findViewById(R.id.item_total_integral_rankImg);
               holderTotal.rankText=view.findViewById(R.id.item_total_integral_rankText);
               holderTotal.teamLogo=view.findViewById(R.id.item_total_integral_teamImg);
               holderTotal.teamName=view.findViewById(R.id.item_total_integral_teamName);
               holderTotal.linearLayout=view.findViewById(R.id.item_games_integral_linear);
               holderTotal.gradeTotal=view.findViewById(R.id.item_total_integral_gradeTotal);
               view.setTag(holderTotal);
           }else{
               view=DLPluginLayoutInflater.getInstance(context).inflate(R.layout.item_games_integral_details,null);
               holderGames=new ViewGamesHolder();
               holderGames.rankImg=view.findViewById(R.id.item_games_integral_rankImg);
               holderGames.rankText=view.findViewById(R.id.item_games_integral_rankText);
               holderGames.teamLogo=view.findViewById(R.id.item_games_integral_teamImg);
               holderGames.teamName=view.findViewById(R.id.item_games_integral_teamName);
               holderGames.grade1=view.findViewById(R.id.item_games_integral_grade1);
               holderGames.grade2=view.findViewById(R.id.item_games_integral_grade2);
               holderGames.gradeTotal=view.findViewById(R.id.item_games_integral_gradeTotal);
               view.setTag(holderGames);
           }
    }else{
           if (type==0){
               holderTotal= (ViewTotalHolder) view.getTag();
           }else{
               holderGames= (ViewGamesHolder) view.getTag();
           }
       }
       if (type==0){
           if (holderTotal!=null){
               switch (i) {
                   case 0:
                       holderTotal.rankImg.setImageResource(R.drawable.first_place);
                       break;
                   case 1:
                       holderTotal.rankImg.setImageResource(R.drawable.second_place);
                       break;
                   case 2:
                       holderTotal.rankImg.setImageResource(R.drawable.third_place);
                       break;
                   default:
                       holderTotal.rankText.setText(i+"");
                       break;
               }
               ImageLoaderUitl.loadimage(arrayList.get(i).getTeam_logo(),holderTotal.teamLogo);
               holderTotal.teamName.setText(arrayList.get(i).getTeam_name());
               for (int j=0;j<arrayList.get(i).getList().size();j++){
                   TextView textView=new TextView(context);
                   textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1));
                   textView.setText(arrayList.get(i).getList().get(j).getBo_score()+"");
                   textView.setTextColor(Color.parseColor("#858585"));
                   textView.setTextSize(8);
                   textView.setGravity(Gravity.CENTER);
                   holderTotal.linearLayout.addView(textView);
               }
               holderTotal.gradeTotal.setText(arrayList.get(i).getTotal_score()+"");

           }
       }else{
           if (holderGames!=null){
               switch (i) {
                   case 0:
                       holderGames.rankImg.setImageResource(R.drawable.first_place);
                       break;
                   case 1:
                       holderGames.rankImg.setImageResource(R.drawable.second_place);
                       break;
                   case 2:
                       holderGames.rankImg.setImageResource(R.drawable.third_place);
                       break;
                   default:
                       holderGames.rankText.setText(i+"");
                       break;
               }
               ImageLoaderUitl.loadimage(arrayList.get(i).getTeam_logo(),holderGames.teamLogo);
               holderGames.teamName.setText(arrayList.get(i).getTeam_name());
               holderGames.grade1.setText(arrayList.get(i).getList().get(0).getRank_score()+"");
               holderGames.grade2.setText(arrayList.get(i).getList().get(0).getEliminlate_score()+"");
               holderGames.gradeTotal.setText(arrayList.get(i).getList().get(0).getBo_score()+"");
           }

       }
       return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private class ViewTotalHolder{
        private ImageView rankImg;
        private TextView rankText;
        private ImageView teamLogo;
        private TextView teamName;
        private TextView gradeTotal;
        private LinearLayout linearLayout;
    }
    private class ViewGamesHolder{
        private ImageView rankImg;
        private TextView rankText;
        private ImageView teamLogo;
        private TextView teamName;
        private TextView grade1,grade2,gradeTotal;
    }
}
