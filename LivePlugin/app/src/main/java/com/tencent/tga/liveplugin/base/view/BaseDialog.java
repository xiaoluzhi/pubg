package com.tencent.tga.liveplugin.base.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.loopj.android.tgahttp.Configs.Configs;
import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;

/**
 * Created by hyqiao on 2016/11/17.
 */

public class BaseDialog extends Dialog {
    protected Context context;
    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    /**
    * 设置Layout
    * @author hyqiao
    * @time 2016/11/17 11:18
    */
    public View setLayout(int resource, ViewGroup root){
        try {
            LayoutInflater inflater = DLPluginLayoutInflater.getInstance(getContext());
            View layout = inflater.inflate(resource, root);
            setCanceledOnTouchOutside(false);//碰触周围不关闭
            setContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return layout;
        } catch (Exception e) {
            return null;
        }
    }
    /**
    * 设置全屏显示
    * @author hyqiao
    * @time 2016/11/17 11:05
    */
    @Override
    public void show() {
        try {
            super.show();
                WindowManager.LayoutParams lp = this.getWindow().getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT; //设置宽度
                lp.height = WindowManager.LayoutParams.MATCH_PARENT; //设置宽度
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                this.getWindow().setAttributes(lp);
        } catch (Exception e) {
            TLog.e("BaseDialog", "show dialog exception " + e.getMessage());
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initHideBottomBar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initHideBottomBar();
    }

    public void initHideBottomBar(){
        try {
            setHideBottomBar();
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    TLog.e("BaseDialog","onSystemUiVisibilityChange visibility = "+visibility);
                    setHideBottomBar();
                }
            });
        }catch (Exception e){
            TLog.e("BaseDialog","onSystemUiVisibilityChange initHideBottomBar error : "+e.getMessage());
        }
    }

    public void setHideBottomBar(){
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }catch (Exception e){
            TLog.e("BaseDialog","onSystemUiVisibilityChange setHideBottomBar error : "+e.getMessage());
        }
    }
}
