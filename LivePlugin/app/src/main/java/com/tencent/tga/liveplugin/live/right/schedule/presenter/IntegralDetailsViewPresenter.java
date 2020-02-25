package com.tencent.tga.liveplugin.live.right.schedule.presenter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.right.schedule.IntegralDetailAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.IntegralTitleAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.liveplugin.live.right.schedule.model.IntegralDetailsViewModel;
import com.tencent.tga.liveplugin.live.right.schedule.ui.IntegralDetailsView;
import com.tencent.tga.plugin.R;


import java.util.ArrayList;

public class IntegralDetailsViewPresenter extends BaseFrameLayoutPresenter<IntegralDetailsView, IntegralDetailsViewModel> {
    private static final String TAG = "IntegralDetailsViewPresenter";
    private IntegralDetailsViewModel model;
    private IntegralTitleAdapter adapter;
    private IntegralDetailAdapter detailAdapter;
    private View viewTotal, viewGames, viewMore;
    private int position = 0;
    private String matchId, roomId;
    private int count = 1;

    @Override
    public IntegralDetailsViewModel getModel() {
        if (model == null) {
            model = new IntegralDetailsViewModel(this);
        }
        return model;
    }

    public void getData(String matchId, String roomId) {
        this.matchId = matchId;
        this.roomId = roomId;
        getModel().requestList(matchId, roomId, 0);
    }

    public void setTotalList(ArrayList<String> list) {
        try {
            if (count == 1) {
                adapter = new IntegralTitleAdapter(list, getView().getContext());
                getView().totalList.setAdapter(adapter);
                getView().totalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (position != i) {
                            adapter.setChecked(i);
                            adapter.notifyDataSetInvalidated();
                            getModel().requestList(matchId, roomId, i);
                        }
                        position = i;
                    }
                });
                getView().show();
                count++;
            } else {
                return;
            }
        } catch (Exception e) {
            TLog.e(TAG, "setTotalList error : " + e.getMessage());
        }
    }

    public void setDetailList(ArrayList<TeamBankBean> arrayList, int type, ArrayList<String> list) {
        try {
            TLog.e(TAG,"type======="+type);
            if (detailAdapter == null)
                detailAdapter = new IntegralDetailAdapter(getView().getContext());
            detailAdapter.setData(arrayList, type);
            if (type == 1) {
                getView().mScrollView.setVisibility(View.VISIBLE);
                getView().normalLinear.setVisibility(View.GONE);
                getView().detailMoreList.setAdapter(detailAdapter);
                setListViewHeightBasedOnChildren(getView().detailMoreList);
            } else {
                getView().mScrollView.setVisibility(View.GONE);
                getView().normalLinear.setVisibility(View.VISIBLE);
                getView().detailNormalList.setAdapter(detailAdapter);
            }
            ArrayList<String> titles = new ArrayList<>();
            titles.addAll(list);
            titles.remove(0);
            addHeader(type, titles);
        } catch (Exception e) {
            TLog.e(TAG, "setDetailList error : " + e.getMessage());
        }
    }

    private void addHeader(int type, ArrayList<String> list) {
        try {
            if (viewTotal != null) {
                getView().mNormalHeaderView.removeAllViews();
            }
            if (viewGames != null) {
                getView().mNormalHeaderView.removeAllViews();
            }
            if (viewMore != null) {
                getView().mMoreHeaderView.removeAllViews();
            }

            if (type == 0) {
                viewTotal = DLPluginLayoutInflater.getInstance(getView().getContext()).inflate(R.layout.integral_details_header_total, null);
                LinearLayout linearLayout;
                linearLayout = viewTotal.findViewById(R.id.integral_details_header_linear);
                for (int i = 0; i < list.size(); i++) {
                    TextView textView = new TextView(getView().getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    textView.setText(list.get(i));
                    textView.setTextColor(Color.parseColor("#BDBDBD"));
                    textView.setTextSize(8);
                    textView.setGravity(Gravity.CENTER);
                    linearLayout.addView(textView);
                }
                getView().mNormalHeaderView.addView(viewTotal);
            } else if (type == 1) {
                viewMore = DLPluginLayoutInflater.getInstance(getView().getContext()).inflate(R.layout.integral_details_header_more, null);
                LinearLayout linearLayout = viewMore.findViewById(R.id.integral_details_header_more_linear);
                for (int i = 0; i < list.size(); i++) {
                    TextView textView = new TextView(getView().getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(DeviceUtils.dip2px(getView().getContext(), 48), DeviceUtils.dip2px(getView().getContext(), 21)));
                    textView.setText(list.get(i));
                    textView.setTextColor(Color.parseColor("#BDBDBD"));
                    textView.setTextSize(8);
                    textView.setGravity(Gravity.CENTER);
                    linearLayout.addView(textView);
                }
                getView().mMoreHeaderView.addView(viewMore);
            } else if (type == 2) {
                viewGames = DLPluginLayoutInflater.getInstance(getView().getContext()).inflate(R.layout.integral_details_header_games, null);
                getView().mNormalHeaderView.addView(viewGames);
            }
        } catch (Exception e) {
            TLog.e(TAG, "addHeader error : " + e.getMessage());
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        IntegralDetailAdapter listAdapter = (IntegralDetailAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        int totalWidth = listItem.getMeasuredWidth();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.width = totalWidth;
        listView.setLayoutParams(params);
    }
}
