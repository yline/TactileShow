package com.tactileshow.maintab.viewhelper.visual;

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
 * 第三渠道
 *
 * @author yline 2017/9/28 -- 16:46
 * @version 1.0.0
 */
public class VisualHeaderViewHelper {
    private View parentView;
    private DefinedScrollView definedScrollView;
    private VisualQueryView queryView;

    private LineChartHelper lineChartHelper;

    public VisualHeaderViewHelper(Context context, DefinedViewPager viewPager) {
        initView(context, viewPager);
    }

    private void initView(Context context, final DefinedViewPager viewPager) {
        parentView = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_header, null);
        definedScrollView = parentView.findViewById(R.id.visual_header_scroll);
        queryView = parentView.findViewById(R.id.visual_header_query);

        LineChart lineChart = parentView.findViewById(R.id.tab_visual_header_line_chart);
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

        queryView.setOnVisualQueryCallback(new VisualQueryView.OnVisualQueryCallback() {
            @Override
            public void onModeChange(boolean isNow) {
                lineChartHelper.changeMode(isNow);
                SDKManager.toast(isNow ? "开始展示实时数据" : "实时数据停止监听，准备展示历史数据");
            }

            @Override
            public void onQueryHour(final View view, long currentStamp) {
                view.setClickable(false);
                SQLiteManager.getInstance().loadAsync(currentStamp - 3_600_000, currentStamp, new SQLiteManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<TactileModel> modelList) {
                        view.setClickable(true);

                        boolean result = lineChartHelper.setHistoryDataList(modelList, LineChartHelper.TypeOfHeader);
                        SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
                    }
                });
            }

            @Override
            public void onQueryDay(final View view, long currentStamp) {
                view.setClickable(false);
                SQLiteManager.getInstance().loadAsync(currentStamp - 86_400_000, currentStamp, new SQLiteManager.OnReadCallback() {
                    @Override
                    public void onFailure(String errorMsg) {
                        view.setClickable(true);
                        SDKManager.toast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<TactileModel> modelList) {
                        view.setClickable(true);

                        boolean result = lineChartHelper.setHistoryDataList(modelList, LineChartHelper.TypeOfHeader);
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

                        boolean result = lineChartHelper.setHistoryDataList(modelList, LineChartHelper.TypeOfHeader);
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

                            boolean result = lineChartHelper.setHistoryDataList(modelList, LineChartHelper.TypeOfHeader);
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

                            boolean result = lineChartHelper.setHistoryDataList(modelList, LineChartHelper.TypeOfHeader);
                            SDKManager.toast(result ? "加载成功" : "该时间段内没有数据");
                        }
                    });
                }
            }
        });
    }

    public void addData(long stamp, float headerNum) {
        lineChartHelper.addNowData(stamp, headerNum);
    }

    public View getView() {
        return parentView;
    }
}
