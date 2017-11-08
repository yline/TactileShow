package com.tactileshow.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.tactileshow.main.R;
import com.tactileshow.maintab.viewhelper.visual.LineChartHelper;
import com.yline.application.SDKManager;

import java.util.ArrayList;

public class CharActivity extends AppCompatActivity {
    private LineChartHelper lineChartHelper;

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char);

        mChart = (LineChart) findViewById(R.id.char_line_chart);
        lineChartHelper = new LineChartHelper(mChart);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            lineChartHelper.addData(System.currentTimeMillis(), (float) (Math.random() * 10));
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    public void onAddData(View view) {
        SDKManager.toast("onAddData");

        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    public void onSetData(View view) {
        SDKManager.toast("onSetData");

        ArrayList<Entry> yVals3 = new ArrayList<>();

        long time = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            float mult = 1;
            float val = (float) (Math.random() * mult) + 500;

            int value = LineChartHelper.stampToValue(time + i * 1000);
            yVals3.add(new Entry(value, val));
        }

        lineChartHelper.setDataList(yVals3);
    }
}
