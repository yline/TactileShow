package com.tactileshow.maintab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.tactileshow.manager.TactileModel;
import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;
import com.tactileshow.maintab.view.DefinedViewPager;
import com.tactileshow.maintab.viewhelper.TabBodyViewHelper;
import com.tactileshow.maintab.viewhelper.TabGeneralViewHelper;
import com.tactileshow.maintab.viewhelper.TabOriginViewHelper;
import com.tactileshow.maintab.viewhelper.TabSettingViewHelper;
import com.tactileshow.maintab.viewhelper.TabVisualViewHelper;
import com.tactileshow.maintab.viewhelper.ViewPagerAdapter;
import com.yline.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends Activity {
    private static final String TAG = "TabActivity";

    private DefinedViewPager viewPager;

    private TabBodyViewHelper bodyViewHelper; // 人体图
    private TabGeneralViewHelper generalViewHelper; // 一般信息
    private TabVisualViewHelper visualViewHelper; // 图像信息
    private TabOriginViewHelper originViewHelper; // 原始数据；温度、湿度
    private TabSettingViewHelper settingViewHelper;  // 设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        getScreenMetrics();

        viewPager = (DefinedViewPager) findViewById(R.id.tab_view_pager);
        viewPager.setOffscreenPageLimit(6);

        bodyViewHelper = new TabBodyViewHelper(this);
        generalViewHelper = new TabGeneralViewHelper(this);
        visualViewHelper = new TabVisualViewHelper(this, viewPager);
        originViewHelper = new TabOriginViewHelper(this);
        settingViewHelper = new TabSettingViewHelper(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(macro.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);

        initView();
        initViewClick();
    }

    private void initView() {
        final List<View> viewList = new ArrayList<>();
        final List<String> titleList = new ArrayList<>();

        viewList.add(bodyViewHelper.getView());
        titleList.add(StaticValue.bodymap_info_tab_name);

        viewList.add(generalViewHelper.getView());
        titleList.add(StaticValue.general_info_tab_name);

        viewList.add(visualViewHelper.getView());
        titleList.add(StaticValue.visual_info_tab_name);

        viewList.add(originViewHelper.getView());
        titleList.add(StaticValue.detail_info_tab_name);

        viewList.add(settingViewHelper.getView());
        titleList.add(StaticValue.set_tab_name);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_tab_layout);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter();
        pagerAdapter.setViews(viewList, titleList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViewClick() {
        // 点击身体
        bodyViewHelper.setOnBodyClickListener(new TabBodyViewHelper.OnBodyClickListener() {
            @Override
            public void onBodyClick(TabBodyViewHelper.BodyType bodyType) {
                if (bodyType == TabBodyViewHelper.BodyType.LeftArm) {
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            settingViewHelper.showExitDialog();
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
        //visualViewHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        //visualViewHelper.onRestoreInstanceState(savedState);
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

    public void setTempData(long stamp, double data) {
        try {
            generalViewHelper.setTemp(data);
            generalViewHelper.setGerm(data);

            visualViewHelper.setTemp(stamp, data);

            originViewHelper.setTemp(data);
        } catch (NumberFormatException e) {
            Log.e("wshg", "Received an error format data!");
        }
    }

    public void setPressData(long stamp, double data) {
        try {
            generalViewHelper.setPress(data);

            visualViewHelper.setHum(stamp, data);

            originViewHelper.setHum(data);
        } catch (NumberFormatException e) {
            Log.e("wshg", "Received an error format data!");
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getStringExtra("msg");
            LogUtil.i(TAG + "onReceive: action = " + action);

            if (null == action) {
                return;
            }

            TactileModel model = TactileModel.fromJson(action);
            if (null == model) {
                LogUtil.e("mGattUpdateReceiver model is null");
            } else {
               /* Time time = new Time();
                time.set(model.getTime());*/

                float hum = model.getHum();
                if (TactileModel.Empty != hum) {
                    setPressData(model.getTime(), hum);
                }

                float temp = model.getTemp();
                if (TactileModel.Empty != temp) {
                    setTempData(model.getTime(), temp);
                }
            }
        }
    };
}
