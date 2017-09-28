package com.tactileshow.view.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tactileshow.main.R;

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
