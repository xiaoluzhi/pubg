package com.tencent.tga.liveplugin.live.right.schedule.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutView;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.right.schedule.presenter.ScheduleTeamPresenter;
import com.tencent.tga.plugin.R;

public class ScheduleTeamView extends BaseFrameLayoutView<ScheduleTeamPresenter>{
    private ScheduleTeamPresenter scheduleTeamPresenter;
    private View mRootView;
    public  GridView mGridView;
    private BasePopWindow mPopWindow;
    private ImageView mClose;
    private String matchId;
    private ViewGroup mParent;
    public ScheduleTeamView(Context context,String matchId,ViewGroup parent) {
        super(context);
        this.matchId=matchId;
        mParent=parent;
    }

    @Override
    protected ScheduleTeamPresenter getPresenter() {
        if (scheduleTeamPresenter==null){
            scheduleTeamPresenter=new ScheduleTeamPresenter();
        }
        return scheduleTeamPresenter;
    }
    public void initView(){
        mRootView = DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.schedule_team_view,null);
        mGridView=mRootView.findViewById(R.id.schedule_team_view_grid);

        mClose=mRootView.findViewById(R.id.schedule_team_view_close);
        mClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = DeviceUtils.dip2px(getContext(),476);
        params.height = DeviceUtils.dip2px(getContext(),275);
        params.gravity = Gravity.CENTER;
        addView(mRootView, params);
        setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        getPresenter().getData(matchId);
    }


    public void show()
    {
        if (mPopWindow == null)
        {
            mPopWindow = new BasePopWindow(this, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,false);
            mPopWindow.setFocusable(true);
            mPopWindow.setOutsideTouchable(false);
            mPopWindow.setBackgroundDrawable(new ColorDrawable(0xB2000000));
            setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    mPopWindow.setHideBottomBar();
                }
            });
        }

        if (!mPopWindow.isShowing())
        {
            mPopWindow.showAtLocation(mParent, Gravity.CENTER, 0, 0);
        }
    }
    public boolean isShowing(){
        if(mPopWindow != null && mPopWindow.isShowing()){
            return true;
        }
        return false;
    }
    public void close(){
        try {
            mPopWindow.dismiss();
        }catch (Exception e){
            TLog.e("ScheduleTeamView","ScheduleTeamView close error : "+e.getMessage());
        }
    }
}
