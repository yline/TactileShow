package com.tactileshow.maintab.viewhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tactileshow.main.R;

import java.util.Locale;

/**
 * 原始信息界面
 *
 * @author yline 2017/9/24 -- 19:35
 * @version 1.0.0
 */
public class OriginViewHelper {
    private View view;

    private TextView tvTemp, tvHum, tvHeader;

    public OriginViewHelper(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_tab_origin, null);

        tvTemp = view.findViewById(R.id.label_detail_temp);
        tvHum = view.findViewById(R.id.label_detail_hum);
        tvHeader = view.findViewById(R.id.label_detail_header);
    }

    public void setTemp(double number) {
        String str = String.format(Locale.CHINA, "%.2f", number);
        tvTemp.setText(str);
    }

    public void setHum(double number) {
        String str = String.format(Locale.CHINA, "%.2f", number);
        tvHum.setText(str);
    }

    public void setHeader(double number) {
        String str = String.format(Locale.CHINA, "%.2f", number);
        tvHeader.setText(str);
    }

    public View getView() {
        return view;
    }
}
