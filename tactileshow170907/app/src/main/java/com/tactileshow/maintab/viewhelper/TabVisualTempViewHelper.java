package com.tactileshow.maintab.viewhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.tactileshow.main.R;
import com.tactileshow.maintab.view.DefinedScrollView;
import com.tactileshow.maintab.view.DefinedViewPager;
import com.tactileshow.manager.SQLiteManager;
import com.tactileshow.manager.TactileModel;
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

    private LineChartHelper lineChartHelper;

    public TabVisualTempViewHelper(Context context, DefinedViewPager viewPager) {
        initView(context, viewPager);
    }

    private void initView(Context context, final DefinedViewPager viewPager) {
        parentView = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_temp, null);
        definedScrollView = parentView.findViewById(R.id.visual_temp_scroll);
        queryView = parentView.findViewById(R.id.visual_temp_query);

        LineChart lineChart = parentView.findViewById(R.id.tab_visual_temp_line_chart);
        lineChartHelper = new LineChartHelper(lineChart);

        initViewClick(viewPager);
    }

    private void initViewClick(final DefinedViewPager viewPager) {
        lineChartHelper.setOnTouchChartCallback(new LineChartHelper.OnTouchChartCallback() {
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
                // defineChartView.changeMode(isNow);
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

                        boolean result = lineChartHelper.setDataList(modelList, LineChartHelper.TypeOfTemp);
                        SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
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

                        boolean result = lineChartHelper.setDataList(modelList, LineChartHelper.TypeOfTemp);
                        SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
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

                        boolean result = lineChartHelper.setDataList(modelList, LineChartHelper.TypeOfTemp);
                        SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
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

                            boolean result = lineChartHelper.setDataList(modelList, LineChartHelper.TypeOfTemp);
                            SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
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

                            boolean result = lineChartHelper.setDataList(modelList, LineChartHelper.TypeOfTemp);
                            SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
                        }
                    });
                }
            }
        });
    }

    public void addData(long stamp, float tempNum) {
        lineChartHelper.addData(stamp, tempNum);
    }

    public View getView() {
        return parentView;
    }
}
