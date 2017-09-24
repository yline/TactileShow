package com.tactileshow.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.tactileshow.helper.BroadcastModel;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;
import com.tactileshow.view.BodyMap;
import com.tactileshow.view.DefinedPagerAdapter;
import com.tactileshow.view.DefinedViewPager;
import com.tactileshow.view.DetailInfo;
import com.tactileshow.view.GeneralInfo;
import com.tactileshow.view.VisualTabInfo;
import com.tactileshow.view.main.SettingView;
import com.yline.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends Activity {
    private TabHost tabHost;

    private DefinedViewPager viewPager;

    private DefinedPagerAdapter pagerAdapter;

    private List<View> listViews;

    // 图像信息
    private VisualTabInfo visual;

    // 原始数据；温度、湿度
    private DetailInfo detail;

    // 一般信息
    private GeneralInfo general;

    // 设置
    private SettingView set;

    // 人体图
    private BodyMap bodymap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        getScreenMetrics();

        tabHost = (TabHost) findViewById(R.id.tabhost);
        viewPager = (DefinedViewPager) findViewById(R.id.view_pager);
        listViews = new ArrayList<View>();

        visual = new VisualTabInfo(this, viewPager);
        detail = new DetailInfo(this);
        general = new GeneralInfo(this);
        set = new SettingView(this);
        bodymap = new BodyMap(this, viewPager);

        listViews.add(bodymap.getView());
        listViews.add(general.getView());
        listViews.add(visual.getView());
        listViews.add(detail.getView());
        listViews.add(set.getView());

        pagerAdapter = new DefinedPagerAdapter(listViews);
        viewPager.setAdapter(pagerAdapter);

        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec(StaticValue.bodymap_info_tab_name).setIndicator(StaticValue.bodymap_info_tab_name).setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec(StaticValue.general_info_tab_name).setIndicator(StaticValue.general_info_tab_name).setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec(StaticValue.visual_info_tab_name).setIndicator(StaticValue.visual_info_tab_name).setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec(StaticValue.detail_info_tab_name).setIndicator(StaticValue.detail_info_tab_name).setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec(StaticValue.set_tab_name).setIndicator(StaticValue.set_tab_name).setContent(R.id.view1));

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (StaticValue.general_info_tab_name.equals(tabId)) {
                    viewPager.setCurrentItem(1);
                } else if (StaticValue.visual_info_tab_name.equals(tabId)) {
                    viewPager.setCurrentItem(2);
                } else if (StaticValue.detail_info_tab_name.equals(tabId)) {
                    viewPager.setCurrentItem(3);
                } else if (StaticValue.bodymap_info_tab_name.equals(tabId)) {

                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(4);
                }

            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(macro.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            set.showExitDialog();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        visual.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        visual.onRestoreInstanceState(savedState);
    }

    private void getScreenMetrics() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        StaticValue.width = dm.widthPixels;
        StaticValue.height = dm.heightPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    /**
     * 接收到广播后，改变UI
     *
     * @param t
     * @param data
     */
    public void setTemp(Time t, double data) {
        try {
            general.setTemp(data);
            general.setGerm(data);
            visual.setTemp(t, data);
            detail.setTemp(data);
        } catch (NumberFormatException e) {
            Log.e("wshg", "Received an error format data!");
        }
    }

    /**
     * 接收到广播后，改变UI
     *
     * @param t
     * @param data
     */
    public void setPress(Time t, double data) {
        try {
            general.setPress(data);
            visual.setPress(t, data);
            detail.setHum(data);
        } catch (NumberFormatException e) {
            Log.e("wshg", "Received an error format data!");
        }
    }

    private static final String TAG = "MainTabActivity";

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getStringExtra("msg");
            LogUtil.i(TAG + "onReceive: action = " + action);

            if (null == action) {
                return;
            }

            BroadcastModel model = BroadcastModel.fromJson(action);
            if (null == model) {
                LogUtil.e("mGattUpdateReceiver model is null");
            } else {
                Time time = new Time();
                time.set(model.getTime());

                float hum = model.getHum();
                if (BroadcastModel.Empty != hum) {
                    setPress(time, hum);
                }

                float temp = model.getTemp();
                if (BroadcastModel.Empty != temp) {
                    setTemp(time, temp);
                }
            }
        }
    };
}
