package com.tactileshow.maintab.viewhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.tactileshow.manager.TactileModel;
import com.tactileshow.manager.SQLiteManager;
import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;
import com.tactileshow.maintab.view.DefineChartView;
import com.tactileshow.maintab.view.DefinedScrollView;
import com.tactileshow.maintab.view.DefinedViewPager;
import com.yline.application.SDKManager;

import java.util.List;

/**
 * 湿度
 *
 * @author yline 2017/9/28 -- 16:45
 * @version 1.0.0
 */
public class TabVisualHumViewHelper {

    private View parentView;
    private DefinedScrollView definedScrollView;
    private TabVisualQueryView queryView;
    private DefineChartView defineChartView;

    public TabVisualHumViewHelper(Context context, DefinedViewPager viewPager) {
        initView(context, viewPager);
    }

    private void initView(Context context, final DefinedViewPager viewPager) {
        parentView = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_hum, null);
        definedScrollView = parentView.findViewById(R.id.visual_hum_scroll);
        queryView = parentView.findViewById(R.id.visual_hum_query);

        defineChartView = parentView.findViewById(R.id.visual_hum_chart);
        defineChartView.setYRange(StaticValue.press_min_axis, StaticValue.press_max_axis);
        defineChartView.setOnTouchChartCallback(new DefineChartView.OnTouchChartCallback() {
            @Override
            public void onActionUp() {
                definedScrollView.setTouchIntercept(true);
                viewPager.setTouchIntercept(true);
            }

            @Override
            public void onActionDown() {
                definedScrollView.setTouchIntercept(false);
                viewPager.setTouchIntercept(false);
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
                SQLiteManager.getInstance().loadAsync(currentStamp - 360_1000, currentStamp, new SQLiteManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<TactileModel> modelList) {
                        view.setClickable(true);
                        if (modelList.size() == 0) {
                            SDKManager.toast("该时间段内没有数据");
                        } else {
                            TactileModel model;
                            for (int i = 0; i < modelList.size(); i++) {
                                model = modelList.get(i);
                                defineChartView.addHistoryData(model.getTime(), model.getHum());
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
                SQLiteManager.getInstance().loadAsync(currentStamp - 8640_1000, currentStamp, new SQLiteManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<TactileModel> modelList) {
                        view.setClickable(true);
                        if (modelList.size() == 0) {
                            SDKManager.toast("该时间段内没有数据");
                        } else {
                            TactileModel model;
                            for (int i = 0; i < modelList.size(); i++) {
                                model = modelList.get(i);
                                defineChartView.addHistoryData(model.getTime(), model.getHum());
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
                SQLiteManager.getInstance().loadAsync((currentStamp - 2_592_000_000L), currentStamp, new SQLiteManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<TactileModel> modelList) {
                        view.setClickable(true);
                        if (modelList.size() == 0) {
                            SDKManager.toast("该时间段内没有数据");
                        } else {
                            TactileModel model;
                            for (int i = 0; i < modelList.size(); i++) {
                                model = modelList.get(i);
                                defineChartView.addHistoryData(model.getTime(), model.getHum());
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
                    SQLiteManager.getInstance().loadAsync(fromStamp, toStamp, new SQLiteManager.OnReadCallback() {
                        @Override
                        public void onFailure(String errorMsg) {
                            view.setClickable(true);
                            SDKManager.toast(errorMsg);
                        }

                        @Override
                        public void onSuccess(List<TactileModel> modelList) {
                            view.setClickable(true);
                            if (modelList.size() == 0) {
                                SDKManager.toast("该时间段内没有数据");
                            } else {
                                TactileModel model;
                                for (int i = 0; i < modelList.size(); i++) {
                                    model = modelList.get(i);
                                    defineChartView.addHistoryData(model.getTime(), model.getHum());
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
                    SQLiteManager.getInstance().loadAsync(fromStamp, toStamp, new SQLiteManager.OnReadCallback() {
                        @Override
                        public void onFailure(String errorMsg) {
                            view.setClickable(true);
                            SDKManager.toast(errorMsg);
                        }

                        @Override
                        public void onSuccess(List<TactileModel> modelList) {
                            view.setClickable(true);
                            if (modelList.size() == 0) {
                                SDKManager.toast("该时间段内没有数据");
                            } else {
                                TactileModel model;
                                for (int i = 0; i < modelList.size(); i++) {
                                    model = modelList.get(i);
                                    defineChartView.addHistoryData(model.getTime(), model.getHum());
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

    public void addData(long stamp, double humData) {
        defineChartView.addNowData(stamp, humData);
        defineChartView.updateXRange(stamp);
        defineChartView.notifyDataChanged();
    }

    public View getView() {
        return parentView;
    }
}
