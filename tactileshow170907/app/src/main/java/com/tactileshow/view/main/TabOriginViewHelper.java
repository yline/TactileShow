package com.tactileshow.view.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tactileshow.main.R;
import com.yline.utils.LogUtil;

/**
 * 原始信息界面
 *
 * @author yline 2017/9/24 -- 19:35
 * @version 1.0.0
 */
public class TabOriginViewHelper {
    private View view;

    private TextView temp, hum;

    public TabOriginViewHelper(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_tab_origin, null);

        temp = (TextView) view.findViewById(R.id.label_detail_temp);
        hum = (TextView) view.findViewById(R.id.label_detail_hum);

        TabVisualQueryView queryView = view.findViewById(R.id.query_view);
        queryView.setOnVisualQueryCallback(new TabVisualQueryView.OnVisualQueryCallback() {
            @Override
            public void onModeChange(boolean isNow) {
                LogUtil.i("isNow = " + isNow);
            }

            @Override
            public void onQueryHour(View view, long currentStamp) {
                LogUtil.i("currentStamp = " + currentStamp);
            }

            @Override
            public void onQueryDay(View view, long currentStamp) {
                LogUtil.i("currentStamp = " + currentStamp);
            }

            @Override
            public void onQueryMonth(View view, long currentStamp) {
                LogUtil.i("currentStamp = " + currentStamp);
            }

            @Override
            public void onQueryTimeSelect(View view, long fromStamp, long toStamp) {
                LogUtil.i("fromStamp = " + fromStamp + ", toStamp = " + toStamp);
            }

            @Override
            public void onQueryDateSelect(View view, long fromStamp, long toStamp) {
                LogUtil.i("fromStamp = " + fromStamp + ", toStamp = " + toStamp);
            }
        });
    }

    public void setTemp(double number) {
        String str = String.format("%.2f", number);
        temp.setText(str);
    }

    public void setHum(double number) {
        String str = String.format("%.2f", number);
        hum.setText(str);
    }

    public View getView() {
        return view;
    }
}
