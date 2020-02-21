package com.tencent.tga.liveplugin.live.right.schedule.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutView;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.view.BasePopWindow;
import com.tencent.tga.liveplugin.live.right.schedule.presenter.IntegralDetailsViewPresenter;
import com.tencent.tga.plugin.R;

public class IntegralDetailsView extends BaseFrameLayoutView<IntegralDetailsViewPresenter> {
    private static final String TAG = "IntegralDetailsView";
    private IntegralDetailsViewPresenter mPresenter;
    private String matchId, roomId;
    private View mRootView;
    private BasePopWindow mPopWindow;
    private ViewGroup mParent;
    public ListView totalList, detailList;
    private TextView mTitle;
    private ImageView mClose;
    private String title;

    public IntegralDetailsView(Context context, String matchId, String roomId, String title, ViewGroup parent) {
        super(context);
        this.matchId = matchId;
        this.roomId = roomId;
        this.title = title;
        mParent = parent;
    }

    @Override
    protected IntegralDetailsViewPresenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new IntegralDetailsViewPresenter();
        }
        return mPresenter;
    }

    public void initView() {
        mRootView = DLPluginLayoutInflater.getInstance(getContext()).inflate(R.layout.integral_details_view, null);
        totalList = mRootView.findViewById(R.id.integral_details_listView1);
        detailList = mRootView.findViewById(R.id.integral_details_listView2);
        mTitle = mRootView.findViewById(R.id.integral_details_view_title);
        mClose = mRootView.findViewById(R.id.integral_details_view_close);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = DeviceUtils.dip2px(getContext(), 476);
        params.height = DeviceUtils.dip2px(getContext(), 275);
        params.gravity = Gravity.CENTER;
        addView(mRootView, params);
        setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        getPresenter().getData(matchId, roomId);
        mTitle.setText(title);
        mRootView.findViewById(R.id.integral_details_listViewBg).getBackground().setAlpha(53);

        mClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    public void show() {
        try {
            if (mPopWindow == null) {
                mPopWindow = new BasePopWindow(this, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, false);
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

            if (!mPopWindow.isShowing()) {
                mPopWindow.showAtLocation(mParent, Gravity.CENTER, 0, 0);
            }
        } catch (Exception e) {
            TLog.e(TAG, "IntegralDetailsView show error : " + e.getMessage());
        }
    }

    public boolean isShowing() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            return true;
        }
        return false;
    }

    public void close() {
        try {
            mPopWindow.dismiss();
        } catch (Exception e) {
            TLog.e(TAG, "IntegralDetailsView close error : " + e.getMessage());
        }
    }
}
