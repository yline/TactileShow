package com.tactileshow.maintab.viewhelper;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.OnDrawLineChartTouchListener;
import com.tactileshow.manager.TactileModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 管理 LineChart
 *
 * @author yline 2017/11/4 -- 8:30
 * @version 1.0.0
 */
public class LineChartHelper {
    public static final int TypeOfTemp = 0;
    public static final int TypeOfHum = 1;

    private static final int MaxCount = 60; // 显示的最大数据

    private static final int DIVISOR_HOUR = 3_600_000; // 1个小时
    private static final int DIVISOR_MINUTE = 60_000; // 1个分钟
    private static final int DIVISOR_SECOND = 1_000; // 1个秒

    private LineChart mLineChart;
    private OnTouchChartCallback onTouchChartCallback;

    public LineChartHelper(LineChart lineChart) {
        mLineChart = lineChart;

        lineChart.setOnTouchListener(new OnDrawLineChartTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (null != onTouchChartCallback) {
                            onTouchChartCallback.onActionDown();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (null != onTouchChartCallback) {
                            onTouchChartCallback.onActionUp();
                        }
                        break;
                    default:
                        break;
                }
                return super.onTouch(v, event);
            }
        });

        // 右侧坐标轴，取消
        YAxis yRightAxis = lineChart.getAxisRight();
        yRightAxis.setDrawAxisLine(false);
        yRightAxis.setDrawLabels(false);
        yRightAxis.setEnabled(false);

        // X轴，底部，并Label = 5
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5); // lable个数
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long calculateValue = (long) value;
                String result = String.format(Locale.CHINA, "%02d:%02d:%02d", calculateValue / DIVISOR_HOUR, (calculateValue / DIVISOR_MINUTE) % 60, (calculateValue / DIVISOR_SECOND) % 60);
                return result;
            }
        });
    }

    public void addData(long stamp, float y) {
        if (null != mLineChart) {
            initLineDataSet();

            if (mLineChart.getData().getDataSetCount() > 0) {
                LineDataSet dataSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);

                int value = stampToValue(stamp);
                dataSet.addEntry(new Entry(value, y));

                mLineChart.getData().notifyDataChanged();
                mLineChart.notifyDataSetChanged();

                if (dataSet.getEntryCount() >= MaxCount) {
                    int count = dataSet.getEntryCount();

                    int maxValue = (int) dataSet.getValues().get(count - 1).getX();
                    int minValue = (int) dataSet.getValues().get(count - MaxCount).getX();

                    mLineChart.setVisibleXRangeMaximum(maxValue - minValue);
                    mLineChart.moveViewToX(minValue);
                } else {
                    mLineChart.invalidate();
                }
            }
        }
    }

    /**
     * 设置 从数据库中，读取的数据
     *
     * @param modelList   数据内容
     * @param tactileType 展示的内容 {LineChartHelper.TypeOfTemp},{LineChartHelper.TypeOfHum}
     * @return true 设置成功
     */
    public boolean setDataList(List<TactileModel> modelList, int tactileType) {
        if (null != modelList && modelList.size() > 0) {
            List<Entry> entryList = new ArrayList<>();

            int xValue;
            float yValue;
            for (int i = 0; i < modelList.size(); i++) {
                xValue = stampToValue(modelList.get(i).getTime());
                if (TypeOfTemp == tactileType) {
                    yValue = modelList.get(i).getTemp();
                } else if (TypeOfHum == tactileType) {
                    yValue = modelList.get(i).getHum();
                } else {
                    return false;
                }

                entryList.add(new Entry(xValue, yValue));
            }

            setDataList(entryList);
            return true;
        }

        return false;
    }

    /**
     * 设置直接显示的数据
     *
     * @param dataList 直接展示的数据
     */
    public void setDataList(List<Entry> dataList) {
        if (null != dataList && dataList.size() > 0) {
            initLineDataSet();

            if (mLineChart.getData().getDataSetCount() > 0) {
                LineDataSet dataSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);

                dataSet.setValues(dataList);

                mLineChart.getData().notifyDataChanged();
                mLineChart.notifyDataSetChanged();

                if (dataSet.getEntryCount() > MaxCount) {
                    int count = dataSet.getEntryCount();

                    int maxValue = (int) dataSet.getValues().get(count - 1).getX();
                    int minValue = (int) dataSet.getValues().get(count - 1 - MaxCount).getX();

                    mLineChart.setVisibleXRangeMaximum(maxValue - minValue);
                    mLineChart.moveViewToX(minValue);
                } else {
                    mLineChart.invalidate();
                }
            }
        }
    }

    private void initLineDataSet() {
        if (null == mLineChart.getData()) {
            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), "DataSet A");

            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setColor(Color.RED);
            dataSet.setLineWidth(1);
            dataSet.setCircleColor(Color.RED);
            dataSet.setCircleRadius(2);
            dataSet.setDrawCircleHole(false);

            LineData data = new LineData(dataSet);
            mLineChart.setData(data);
        }
    }

    public static int stampToValue(long stamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stamp);
        return calendar.get(Calendar.HOUR_OF_DAY) * DIVISOR_HOUR + calendar.get(Calendar.MINUTE) * DIVISOR_MINUTE + calendar.get(Calendar.SECOND) * DIVISOR_SECOND + calendar.get(Calendar.MILLISECOND);
    }

    public void setOnTouchChartCallback(OnTouchChartCallback onTouchChartCallback) {
        this.onTouchChartCallback = onTouchChartCallback;
    }

    public interface OnTouchChartCallback {
        /**
         * 手指抬上
         */
        void onActionUp();

        /**
         * 手指放下
         */
        void onActionDown();
    }
}
