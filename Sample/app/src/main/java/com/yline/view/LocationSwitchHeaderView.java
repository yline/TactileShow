package com.yline.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yline.R;
import com.yline.model.LocationSwitchModel;
import com.yline.utils.UIScreenUtil;
import com.yline.view.recycler.adapter.CommonRecyclerAdapter;
import com.yline.view.recycler.holder.Callback;
import com.yline.view.recycler.holder.RecyclerViewHolder;

import java.util.List;

public class LocationSwitchHeaderView extends RelativeLayout {
    private static final int HotCityMax = 6;

    private ChoiceCityAdapter choiceCityAdapter;

    private Callback.OnRecyclerItemClickListener<LocationSwitchModel.HotCityItemModel> onRecyclerItemClickListener;

    public LocationSwitchHeaderView(Context context) {
        this(context, null);
    }

    public LocationSwitchHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_location_switch_header, this, true);

        initView(context);
    }

    private void initView(Context context) {
        // 定位显示大小
        RelativeLayout locationResultView = findViewById(R.id.location_switch_header_rl);
        ViewGroup.LayoutParams resultLayoutParams = locationResultView.getLayoutParams();
        resultLayoutParams.width = (UIScreenUtil.getScreenWidth(context) - UIScreenUtil.dp2px(context, 70)) / 3;
        locationResultView.setLayoutParams(resultLayoutParams);

        // 重新定位大小
        RelativeLayout relocationView = findViewById(R.id.location_switch_header_relocation);
        ViewGroup.LayoutParams relocationLayoutParams = relocationView.getLayoutParams();
        relocationLayoutParams.width = (UIScreenUtil.getScreenWidth(context) - UIScreenUtil.dp2px(context, 70)) / 3;
        relocationView.setLayoutParams(relocationLayoutParams);

        // 列表
        RecyclerView recyclerView = findViewById(R.id.location_switch_header_hot_recycler);
        choiceCityAdapter = new ChoiceCityAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setAdapter(choiceCityAdapter);
    }

    public void setData(List<LocationSwitchModel.HotCityItemModel> hotCityModelList) {
        if (null == hotCityModelList || hotCityModelList.size() == 0) {
            findViewById(R.id.location_switch_header_hot).setVisibility(View.GONE);
        } else {
            findViewById(R.id.location_switch_header_hot).setVisibility(View.VISIBLE);

            if (hotCityModelList.size() > HotCityMax) {
                hotCityModelList = hotCityModelList.subList(0, HotCityMax);
            }
            choiceCityAdapter.setDataList(hotCityModelList, true);
        }
    }

    public void setOnRecyclerItemClickListener(Callback.OnRecyclerItemClickListener<LocationSwitchModel.HotCityItemModel> onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public void setOnSearchClickListener(View.OnClickListener listener) {
        findViewById(R.id.location_switch_header_search).setOnClickListener(listener);
    }

    /**
     * 精选城市
     */
    private class ChoiceCityAdapter extends CommonRecyclerAdapter<LocationSwitchModel.HotCityItemModel> {

        @Override
        public int getItemRes() {
            return R.layout.item_location_switch_header;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            holder.setText(R.id.item_location_switch_header, sList.get(position).getName());

            holder.setOnClickListener(R.id.item_location_switch_header, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onRecyclerItemClickListener) {
                        onRecyclerItemClickListener.onItemClick(holder, sList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                    }
                }
            });
        }
    }
}
