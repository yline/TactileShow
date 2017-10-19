package com.yline.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.yline.R;
import com.yline.application.SDKManager;
import com.yline.base.BaseAppCompatActivity;
import com.yline.log.LogFileUtil;
import com.yline.manager.LocationSwitchManager;
import com.yline.model.LocationSwitchModel;
import com.yline.view.LetterLinearItemDecoration;
import com.yline.view.LocationSwitchHeaderView;
import com.yline.view.SliderBarLayout;
import com.yline.view.SliderBarView;
import com.yline.view.recycler.adapter.HeadFootRecyclerAdapter;
import com.yline.view.recycler.holder.Callback;
import com.yline.view.recycler.holder.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 城市切换
 *
 * @author yline 2017/10/9 -- 21:26
 * @version 1.0.0
 */
public class LocationSwitchActivity extends BaseAppCompatActivity {
    public static void launcher(Context context) {
        if (null != context) {
            Intent intent = new Intent(context, LocationSwitchActivity.class);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    private LocationAdapter adapter;
    private LetterLinearItemDecoration letterLinearItemDecoration;
    private SliderBarLayout sliderBarLayout;
    private LocationSwitchHeaderView headerView;

    private SparseArray<String> groupKeyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_switch);

        initView();
        initData();
    }

    private void initView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.location_switch_recycler);

        adapter = new LocationAdapter();
        letterLinearItemDecoration = new LetterLinearItemDecoration(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(letterLinearItemDecoration);
        recyclerView.setAdapter(adapter);

        headerView = new LocationSwitchHeaderView(this);
        adapter.addHeadView(headerView);

        sliderBarLayout = (SliderBarLayout) findViewById(R.id.location_switch_slider_bar);
        sliderBarLayout.setOnLetterTouchedListener(new SliderBarView.OnLetterTouchedListener() {
            @Override
            public void onTouched(int position, String str) {
                int indexOfArray = groupKeyArray.indexOfValue(str);
                LogFileUtil.v("indexOfArray = " + indexOfArray);

                if (-1 != indexOfArray && indexOfArray < groupKeyArray.size()) {
                    int positionOfAdapter = groupKeyArray.keyAt(indexOfArray);
                    recyclerView.scrollToPosition(positionOfAdapter);
                } else {
                    recyclerView.scrollToPosition(0);
                }
            }
        });

        initViewClick();
    }

    private void initViewClick() {
        // 点击搜索
        headerView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSearchActivity.launcher(LocationSwitchActivity.this, headerView.findViewById(R.id.location_switch_header_search));
            }
        });

        // 点击 精选城市
        headerView.setOnRecyclerItemClickListener(new Callback.OnRecyclerItemClickListener<LocationSwitchModel.HotCityItemModel>() {
            @Override
            public void onItemClick(RecyclerViewHolder viewHolder, LocationSwitchModel.HotCityItemModel hotCityItemModel, int position) {
                SDKManager.toast("letter = " + hotCityItemModel.getName());
                LogFileUtil.v("letter = " + hotCityItemModel.getName());
            }
        });

        // 点击 列表选择
        adapter.setOnRecyclerItemClickListener(new Callback.OnRecyclerItemClickListener<LocationSwitchModel.DownLoadItemModel>() {
            @Override
            public void onItemClick(RecyclerViewHolder viewHolder, LocationSwitchModel.DownLoadItemModel downLoadItemModel, int position) {
                SDKManager.toast("letter = " + downLoadItemModel.getName());
                LogFileUtil.v("letter = " + downLoadItemModel.getName());
            }
        });

        // 点击返回按钮
        findViewById(R.id.location_switch_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        long startTime = System.currentTimeMillis();
        LocationSwitchModel.DownLoadModel downLoadModel = LocationSwitchManager.genDownLoadModel(this);

        if (null != downLoadModel) {
            // 展示头部数据
            if (null == downLoadModel.getHot_city() || downLoadModel.getHot_city().size() == 0) {
                // 模拟数据
                List<LocationSwitchModel.HotCityItemModel> headerList = new ArrayList<>();
                headerList.add(new LocationSwitchModel.HotCityItemModel("北京", "111111"));
                headerList.add(new LocationSwitchModel.HotCityItemModel("杭州", "222222"));
                /*headerList.add(new LocationSwitchModel.HotCityItemModel("南昌", "333333"));
                headerList.add(new LocationSwitchModel.HotCityItemModel("厦门", "444444"));
                headerList.add(new LocationSwitchModel.HotCityItemModel("长沙", "555555"));
                headerList.add(new LocationSwitchModel.HotCityItemModel("深圳", "666666"));
                headerList.add(new LocationSwitchModel.HotCityItemModel("上海", "777777"));*/
                headerView.setData(headerList);
            } else {
                headerView.setData(downLoadModel.getHot_city());
            }

            // headerView.setData(downLoadModel.getHot_city());

            // 英文字母
            Set<String> regionListLetter = downLoadModel.getRegion_list().keySet();
            List<String> letterList = new ArrayList<>(regionListLetter);
            letterList.add(0, "#");
            String[] letters = new String[]{};
            letters = letterList.toArray(letters);

            sliderBarLayout.setLetter(letters);

            // 列表数据
            groupKeyArray = new SparseArray<>();
            List<LocationSwitchModel.DownLoadItemModel> dataList = new ArrayList<>();
            List<LocationSwitchModel.DownLoadItemModel> tempModelList;
            int tempLocationSize = 0;
            for (String str : downLoadModel.getRegion_list().keySet()) {
                groupKeyArray.append(tempLocationSize, str);

                tempModelList = downLoadModel.getRegion_list().get(str);

                tempLocationSize += tempModelList.size();
                dataList.addAll(tempModelList);
            }

            adapter.setDataList(dataList, true);
            letterLinearItemDecoration.setKeys(groupKeyArray);

            LogFileUtil.v("total diffTime = " + (System.currentTimeMillis() - startTime));
        }
    }

    private class LocationAdapter extends HeadFootRecyclerAdapter<LocationSwitchModel.DownLoadItemModel> {
        private Callback.OnRecyclerItemClickListener<LocationSwitchModel.DownLoadItemModel> onRecyclerItemClickListener;

        private void setOnRecyclerItemClickListener(Callback.OnRecyclerItemClickListener<LocationSwitchModel.DownLoadItemModel> onRecyclerItemClickListener) {
            this.onRecyclerItemClickListener = onRecyclerItemClickListener;
        }

        @Override
        public int getItemRes() {
            return R.layout.item_location_switch;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
            holder.setText(R.id.item_location_switch_title, sList.get(position).getName());
            holder.setText(R.id.item_location_switch_sub, sList.get(position).getParent_name());

            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onRecyclerItemClickListener) {
                        onRecyclerItemClickListener.onItemClick(holder, sList.get(position), position);
                    }
                }
            });
        }
    }
}
