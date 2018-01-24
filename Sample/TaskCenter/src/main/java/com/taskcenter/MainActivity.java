package com.taskcenter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.taskcenter.layout.DailyWelfareLayout;
import com.taskcenter.layout.NewlyWelfareLayout;
import com.taskcenter.layout.SpreadWelfareLayout;
import com.yline.view.recycler.adapter.AbstractPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private DailyWelfareLayout mDailyWelfareLayout;
    private NewlyWelfareLayout mNewlyWelfareLayout;
    private SpreadWelfareLayout mSpreadWelfareLayout;

    private AbstractPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        ViewPager viewPager = findViewById(R.id.main_view_pager);

        mDailyWelfareLayout = new DailyWelfareLayout(this);
        mNewlyWelfareLayout = new NewlyWelfareLayout(this);
        mSpreadWelfareLayout = new SpreadWelfareLayout(this);

        mPagerAdapter = new AbstractPagerAdapter();
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }
}
