package com.tactileshow.maintab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.tactileshow.main.R;
import com.tactileshow.maintab.view.DefinedViewPager;
import com.tactileshow.maintab.viewhelper.BodyViewHelper;
import com.tactileshow.maintab.viewhelper.GeneralViewHelper;
import com.tactileshow.maintab.viewhelper.OriginViewHelper;
import com.tactileshow.maintab.viewhelper.ViewPagerAdapter;
import com.tactileshow.maintab.viewhelper.setting.SettingViewHelper;
import com.tactileshow.maintab.viewhelper.visual.VisualViewHelper;
import com.tactileshow.manager.TactileModel;
import com.tactileshow.util.macro;
import com.yline.log.LogFileUtil;
import com.yline.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends Activity {
    private static final String TAG = "TabActivity";

    private DefinedViewPager viewPager;

    private BodyViewHelper bodyViewHelper; // 人体图
    private GeneralViewHelper generalViewHelper; // 一般信息
    private VisualViewHelper visualViewHelper; // 图像信息
    private OriginViewHelper originViewHelper; // 原始数据；温度、湿度
    private SettingViewHelper settingViewHelper;  // 设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        viewPager = (DefinedViewPager) findViewById(R.id.tab_view_pager);
        viewPager.setOffscreenPageLimit(6);

        bodyViewHelper = new BodyViewHelper(this);
        generalViewHelper = new GeneralViewHelper(this);
        visualViewHelper = new VisualViewHelper(this, viewPager);
        originViewHelper = new OriginViewHelper(this);
        settingViewHelper = new SettingViewHelper(this);

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
        titleList.add("人体\n地图");

        viewList.add(generalViewHelper.getView());
        titleList.add("一般\n信息");

        viewList.add(visualViewHelper.getView());
        titleList.add("图像\n信息");

        viewList.add(originViewHelper.getView());
        titleList.add("原始\n信息");

        viewList.add(settingViewHelper.getView());
        titleList.add("设置");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_tab_layout);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter();
        pagerAdapter.setViews(viewList, titleList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViewClick() {
        // 点击身体
        bodyViewHelper.setOnBodyClickListener(new BodyViewHelper.OnBodyClickListener() {
            @Override
            public void onBodyClick(BodyViewHelper.BodyType bodyType) {
                if (bodyType == BodyViewHelper.BodyType.LeftArm) {
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
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    public void setTempData(long stamp, float data) {
        try {
            generalViewHelper.setTemp(data);
            generalViewHelper.setGerm(data);
            visualViewHelper.setTemp(stamp, data);
            originViewHelper.setTemp(data);
        } catch (NumberFormatException e) {
            LogFileUtil.e("wshg", "Received an error format data!", e);
        }
    }

    public void setPressData(long stamp, float data) {
        try {
            generalViewHelper.setPress(data);
            visualViewHelper.setHum(stamp, data);
            originViewHelper.setHum(data);
        } catch (NumberFormatException e) {
            LogFileUtil.e("wshg", "Received an error format data!", e);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = TactileModel.receiveBroadcast(intent);
            if (TextUtils.isEmpty(action)) {
                return;
            }

            TactileModel model = TactileModel.fromJson(action);
            if (null == model) {
                LogUtil.e("mGattUpdateReceiver model is null");
            } else {
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
