package com.tencent.tga.liveplugin.live.right.schedule.presenter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.mvp.BaseFrameLayoutPresenter;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.live.right.schedule.IntegralMiddleAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.IntegralRightAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.IntegralTitleAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.bean.TeamBankBean;
import com.tencent.tga.liveplugin.live.right.schedule.model.IntegralDetailsViewModel;
import com.tencent.tga.liveplugin.live.right.schedule.IntegralLeftAdapter;
import com.tencent.tga.liveplugin.live.right.schedule.ui.IntegralDetailView;
import com.tencent.tga.plugin.R;


import java.util.ArrayList;

public class IntegralDetailsViewPresenter extends BaseFrameLayoutPresenter<IntegralDetailView, IntegralDetailsViewModel> {
    private static final String TAG = "IntegralDetailsViewPresenter";
    private IntegralDetailsViewModel model;
    private IntegralTitleAdapter mTitleAdapter;
    private IntegralMiddleAdapter mMiddleAdapter;
    private IntegralLeftAdapter mLeftAdapter;
    private IntegralRightAdapter mRightAdapter;
    private View mHeadView1, mHeadView2;
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
                mTitleAdapter = new IntegralTitleAdapter(list, getView().getContext());
                getView().titleListView.setAdapter(mTitleAdapter);
                getView().titleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (position != i) {
                            mTitleAdapter.setChecked(i);
                            mTitleAdapter.notifyDataSetInvalidated();
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
            TLog.e(TAG, "type=======" + type);
            if (mLeftAdapter == null)
                mLeftAdapter = new IntegralLeftAdapter(getView().getContext());
            mLeftAdapter.setData(arrayList);
            getView().leftListView.setAdapter(mLeftAdapter);
            if (mMiddleAdapter == null)
                mMiddleAdapter = new IntegralMiddleAdapter(getView().getContext());
            mMiddleAdapter.setData(arrayList, type);
            getView().middleListView.setAdapter(mMiddleAdapter);
            setListViewWidthBasedOnChildren(getView().middleListView);
            if (mRightAdapter==null)
                mRightAdapter=new IntegralRightAdapter(getView().getContext(),type);
            mRightAdapter.setData(arrayList,type);
            getView().rightListView.setAdapter(mRightAdapter);
            ArrayList<String> titles = new ArrayList<>();
            titles.addAll(list);
            titles.remove(0);
            addHeader(type, titles);
            setListViewOnTouchAndScrollListener(getView().leftListView,getView().middleListView,getView().rightListView);

        } catch (Exception e) {
            TLog.e(TAG, "setDetailList error : " + e.getMessage());
        }
    }

    private void addHeader(int type, ArrayList<String> list) {
        try {
            if (mHeadView1 != null) {
                getView().mHeaderView.removeAllViews();
            }
            if (mHeadView2 != null) {
                getView().mHeaderView.removeAllViews();
            }
            if (type == 0) {
                mHeadView1 = DLPluginLayoutInflater.getInstance(getView().getContext()).inflate(R.layout.integral_details_header_total, null);
                LinearLayout linearLayout,linearLayout2;
                linearLayout = mHeadView1.findViewById(R.id.integral_details_header_total_linear);
                linearLayout2 = mHeadView1.findViewById(R.id.integral_details_header_total_linear2);
                for (int i = 0; i < list.size(); i++) {
                    TextView textView = new TextView(getView().getContext());
                    if (list.size() < 5) {
                        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    } else {
                        textView.setLayoutParams(new LinearLayout.LayoutParams(DeviceUtils.dip2px(getView().getContext(), 48), DeviceUtils.dip2px(getView().getContext(), 21)));
                    }
                    textView.setText(list.get(i));
                    textView.setTextColor(Color.parseColor("#BDBDBD"));
                    textView.setTextSize(8);
                    textView.setGravity(Gravity.CENTER);
                    if (list.size()<5) {
                        linearLayout2.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        linearLayout2.addView(textView);
                    } else {
                        linearLayout.addView(textView);
                    }
                }
                getView().mHeaderView.addView(mHeadView1);
            } else {
                mHeadView2 = DLPluginLayoutInflater.getInstance(getView().getContext()).inflate(R.layout.integral_details_header_games, null);
                getView().mHeaderView.addView(mHeadView2);
            }
        } catch (Exception e) {
            TLog.e(TAG, "addHeader error : " + e.getMessage());
        }
    }

    private void setListViewWidthBasedOnChildren(ListView listView) {
        try {
            // 获取ListView对应的Adapter
            IntegralMiddleAdapter listAdapter = (IntegralMiddleAdapter) listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(0, 0);
            int totalWidth = listItem.getMeasuredWidth();
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.width = totalWidth;
            listView.setLayoutParams(params);
        } catch (Exception e) {
            TLog.e(TAG, "setListViewWidthBasedOnChildren error is" + e.getMessage());
        }
    }

    /**
     * listview同步滑动效果
     * */
    public static void setListViewOnTouchAndScrollListener(final ListView listView1, final ListView listView2,final ListView listView3){


        //设置listview2列表的scroll监听，用于滑动过程中左右不同步时校正
        listView2.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //如果停止滑动
                if(scrollState == SCROLL_STATE_IDLE /*|| scrollState == 1*/){
                    //获得第一个子view
                    View subView = view.getChildAt(0);
                    if(subView !=null){
                        final int top = subView.getTop();
                        final int top1 = listView1.getChildAt(0).getTop();
                        final int top3=listView3.getChildAt(0).getTop();
                        final int position = view.getFirstVisiblePosition();
                        //如果两个首个显示的子view高度不等
                        if(top != top1){
                            listView1.setSelectionFromTop(position, top);
                        }
                        if(top != top3){
                            listView3.setSelectionFromTop(position, top);
                        }
                    }
                }

            }

            public void onScroll(AbsListView view, final int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                View subView = view.getChildAt(0);
                if(subView != null){
                    final int top = subView.getTop();
                    //如果两个首个显示的子view高度不等
                    int top1 = listView1.getChildAt(0).getTop();
                    if(!(top1 - 7 < top &&top < top1 + 7)){
                        listView1.setSelectionFromTop(firstVisibleItem, top);
                        listView2.setSelectionFromTop(firstVisibleItem, top);
                        listView3.setSelectionFromTop(firstVisibleItem, top);
                    }

                }
            }
        });

        //设置listview1列表的scroll监听，用于滑动过程中左右不同步时校正
        listView1.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE /*|| scrollState == 1*/){
                    //获得第一个子view
                    View subView = view.getChildAt(0);

                    if(subView !=null){
                        final int top = subView.getTop();
                        final int top1 = listView2.getChildAt(0).getTop();
                        final int top3 = listView3.getChildAt(0).getTop();
                        final int position = view.getFirstVisiblePosition();

                        //如果两个首个显示的子view高度不等
                        if(top != top1){
                            listView1.setSelectionFromTop(position, top);
                            listView2.setSelectionFromTop(position, top);
                        }
                        if(top != top3){
                            listView1.setSelectionFromTop(position, top);
                            listView3.setSelectionFromTop(position, top);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, final int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                View subView = view.getChildAt(0);
                if(subView != null){
                    final int top = subView.getTop();
                    listView1.setSelectionFromTop(firstVisibleItem, top);
                    listView2.setSelectionFromTop(firstVisibleItem, top);
                    listView3.setSelectionFromTop(firstVisibleItem, top);

                }
            }
        });

        //设置listview3列表的scroll监听，用于滑动过程中左右不同步时校正
        listView3.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE /*|| scrollState == 1*/){
                    //获得第一个子view
                    View subView = view.getChildAt(0);

                    if(subView !=null){
                        final int top = subView.getTop();
                        final int top1 = listView1.getChildAt(0).getTop();
                        final int top2 = listView2.getChildAt(0).getTop();
                        final int position = view.getFirstVisiblePosition();

                        //如果两个首个显示的子view高度不等
                        if(top != top1){
                            listView2.setSelectionFromTop(position, top);
                            listView1.setSelectionFromTop(position, top);
                        }
                        if(top != top2){
                            listView3.setSelectionFromTop(position, top);
                            listView2.setSelectionFromTop(position, top);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, final int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                View subView = view.getChildAt(0);
                if(subView != null){
                    final int top = subView.getTop();
                    listView1.setSelectionFromTop(firstVisibleItem, top);
                    listView2.setSelectionFromTop(firstVisibleItem, top);
                    listView3.setSelectionFromTop(firstVisibleItem, top);

                }
            }
        });
    }
}
