package com.tactileshow.view.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.tactileshow.helper.BroadcastModel;
import com.tactileshow.helper.DataManager;
import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.custom.DefineChartView;
import com.tactileshow.view.custom.DefinedScrollView;
import com.tactileshow.view.custom.DefinedViewPager;
import com.yline.application.SDKManager;

import java.util.List;

/**
 * 温度
 *
 * @author yline 2017/9/28 -- 16:46
 * @version 1.0.0
 */
public class TabVisualTempViewHelper {

    private View parentView;
    private DefinedScrollView definedScrollView;
    private TabVisualQueryView queryView;
    private DefineChartView defineChartView;

    public TabVisualTempViewHelper(Context context, DefinedViewPager viewPager) {
        initView(context, viewPager);
    }

    private void initView(Context context, final DefinedViewPager viewPager) {
        parentView = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_temp, null);
        definedScrollView = parentView.findViewById(R.id.visual_temp_scroll);
        queryView = parentView.findViewById(R.id.visual_temp_query);

        defineChartView = parentView.findViewById(R.id.visual_temp_chart);
        defineChartView.setYRange(StaticValue.temp_min_axis, StaticValue.temp_max_axis);
        defineChartView.setOnTouchChartCallback(new DefineChartView.OnTouchChartCallback() {
            @Override
            public void onActionUp() {
                viewPager.setTouchIntercept(true);
                definedScrollView.setTouchIntercept(true);
            }

            @Override
            public void onActionDown() {
                viewPager.setTouchIntercept(false);
                definedScrollView.setTouchIntercept(false);
            }
        });

        queryView.setOnVisualQueryCallback(new TabVisualQueryView.OnVisualQueryCallback() {
            @Override
            public void onModeChange(boolean isNow) {
                defineChartView.changeMode(isNow);
            }

            @Override
            public void onQueryHour(final View view, long currentStamp) {
                view.setClickable(false);
                DataManager.getInstance().loadAsync(currentStamp - 360_1000, currentStamp, new DataManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<BroadcastModel> modelList) {
                        view.setClickable(true);
                        if (modelList.size() == 0) {
                            SDKManager.toast("该时间段内没有数据");
                        } else {
                            BroadcastModel model;
                            for (int i = 0; i < modelList.size(); i++) {
                                model = modelList.get(i);
                                defineChartView.addHistoryData(model.getTime(), model.getTemp());
                            }
                            defineChartView.notifyDataChanged();
                            SDKManager.toast("加载成功");
                        }
                    }
                });
            }

            @Override
            public void onQueryDay(final View view, long currentStamp) {
                view.setClickable(false);
                DataManager.getInstance().loadAsync(currentStamp - 8640_1000, currentStamp, new DataManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<BroadcastModel> modelList) {
                        view.setClickable(true);
                        if (modelList.size() == 0) {
                            SDKManager.toast("该时间段内没有数据");
                        } else {
                            BroadcastModel model;
                            for (int i = 0; i < modelList.size(); i++) {
                                model = modelList.get(i);
                                defineChartView.addHistoryData(model.getTime(), model.getTemp());
                            }
                            defineChartView.notifyDataChanged();
                            SDKManager.toast("加载成功");
                        }
                    }
                });
            }

            @Override
            public void onQueryMonth(final View view, long currentStamp) {
                view.setClickable(false);
                DataManager.getInstance().loadAsync((currentStamp - 2_592_000_000L), currentStamp, new DataManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<BroadcastModel> modelList) {
                        view.setClickable(true);
                        if (modelList.size() == 0) {
                            SDKManager.toast("该时间段内没有数据");
                        } else {
                            BroadcastModel model;
                            for (int i = 0; i < modelList.size(); i++) {
                                model = modelList.get(i);
                                defineChartView.addHistoryData(model.getTime(), model.getTemp());
                            }
                            defineChartView.notifyDataChanged();
                            SDKManager.toast("加载成功");
                        }
                    }
                });
            }

            @Override
            public void onQueryTimeSelect(final View view, long fromStamp, long toStamp) {
                if (fromStamp >= toStamp) {
                    SDKManager.toast("查询的时间输入不合法");
                } else {
                    view.setClickable(false);
                    DataManager.getInstance().loadAsync(fromStamp, toStamp, new DataManager.OnReadCallback() {
                        @Override
                        public void onFailure(String errorMsg) {
                            view.setClickable(true);
                            SDKManager.toast(errorMsg);
                        }

                        @Override
                        public void onSuccess(List<BroadcastModel> modelList) {
                            view.setClickable(true);
                            if (modelList.size() == 0) {
                                SDKManager.toast("该时间段内没有数据");
                            } else {
                                BroadcastModel model;
                                for (int i = 0; i < modelList.size(); i++) {
                                    model = modelList.get(i);
                                    defineChartView.addHistoryData(model.getTime(), model.getTemp());
                                }
                                defineChartView.notifyDataChanged();
                                SDKManager.toast("加载成功");
                            }
                        }
                    });
                }
            }

            @Override
            public void onQueryDateSelect(final View view, long fromStamp, long toStamp) {
                if (fromStamp >= toStamp) {
                    SDKManager.toast("查询的时间输入不合法");
                } else {
                    view.setClickable(false);
                    DataManager.getInstance().loadAsync(fromStamp, toStamp, new DataManager.OnReadCallback() {
                        @Override
                        public void onFailure(String errorMsg) {
                            view.setClickable(true);
                            SDKManager.toast(errorMsg);
                        }

                        @Override
                        public void onSuccess(List<BroadcastModel> modelList) {
                            view.setClickable(true);
                            if (modelList.size() == 0) {
                                SDKManager.toast("该时间段内没有数据");
                            } else {
                                BroadcastModel model;
                                for (int i = 0; i < modelList.size(); i++) {
                                    model = modelList.get(i);
                                    defineChartView.addHistoryData(model.getTime(), model.getTemp());
                                }
                                defineChartView.notifyDataChanged();
                                SDKManager.toast("加载成功");
                            }
                        }
                    });
                }
            }
        });
    }

    public void addData(long stamp, double tempNum) {
        defineChartView.addNowData(stamp, tempNum);
        defineChartView.updateXRange(stamp);
        defineChartView.notifyDataChanged();
    }

    public View getView() {
        return parentView;
    }
}
