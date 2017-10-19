package com.yline.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import com.yline.R;
import com.yline.application.SDKManager;
import com.yline.base.BaseAppCompatActivity;
import com.yline.log.LogFileUtil;
import com.yline.manager.EasyTransitionManager;
import com.yline.manager.LocationSwitchManager;
import com.yline.model.LocationSwitchModel;
import com.yline.utils.LogUtil;
import com.yline.view.recycler.adapter.CommonRecyclerAdapter;
import com.yline.view.recycler.holder.Callback;
import com.yline.view.recycler.holder.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 城市 搜索；输入中文或英文
 *
 * @author yline 2017/10/9 -- 21:26
 * @version 1.0.0
 */
public class LocationSearchActivity extends BaseAppCompatActivity {
    public static void launcher(Activity activity, View... views) {
        Intent intent = new Intent(activity, LocationSearchActivity.class);
        intent.putParcelableArrayListExtra(EasyTransitionManager.EASY_TRANSITION_OPTIONS, EasyTransitionManager.genViewAttrs(views));
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    private static void enter(Activity activity, long duration, TimeInterpolator interpolator, Animator.AnimatorListener listener) {
        Intent intent = activity.getIntent();
        ArrayList<EasyTransitionManager.ViewAttrs> attrs = intent.getParcelableArrayListExtra(EasyTransitionManager.EASY_TRANSITION_OPTIONS);
        EasyTransitionManager.runEnterAnimation(activity, attrs, duration, interpolator, listener);
    }

    private static boolean exit(Activity activity, long duration, TimeInterpolator interpolator) {
        Intent intent = activity.getIntent();
        ArrayList<EasyTransitionManager.ViewAttrs> attrs = intent.getParcelableArrayListExtra(EasyTransitionManager.EASY_TRANSITION_OPTIONS);
        return EasyTransitionManager.runExitAnimation(activity, attrs, duration, interpolator);
    }

    private static final int InputTypeLetter = 0;
    private static final int InputTypeChinese = 1;
    private static final int InputTypeError = 2;

    private LocationSwitchModel.DownLoadModel downLoadModel;
    private Map<String, List<LocationSwitchModel.SearchItemModel>> englishSearchModelMap;

    private Handler mSearchHandler;

    private LocationSearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        initView();
        initData();
    }

    private void initView() {
        // 进入时，动画效果
        enter(this, 500, new DecelerateInterpolator(), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                findViewById(R.id.location_search_divide).setVisibility(View.VISIBLE);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.location_search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new LocationSearchAdapter();
        recyclerView.setAdapter(searchAdapter);

        // 搜索效果
        EditText etSearch = (EditText) findViewById(R.id.location_search_et);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = s.toString().trim();

                int inputType = InputTypeError;
                Pattern pattern = Pattern.compile("[a-zA-Z]+");
                Matcher matcher = pattern.matcher(txt);
                if (matcher.matches()) { // 字母
                    inputType = InputTypeLetter;
                }

                pattern = Pattern.compile("[\u4e00-\u9fa5]+");
                matcher = pattern.matcher(txt);
                if (matcher.matches()) { // 汉字
                    inputType = InputTypeChinese;
                }

                pattern = Pattern.compile("[0-9]*+");
                matcher = pattern.matcher(txt);
                if (matcher.matches()) { // 数字
                    inputType = InputTypeError;
                }

                mSearchHandler.obtainMessage(inputType, txt).sendToTarget();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initViewClick();
    }

    private void initViewClick() {
        // 点击取消
        findViewById(R.id.location_search_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 点击选择
        searchAdapter.setOnRecyclerItemClickListener(new Callback.OnRecyclerItemClickListener<LocationSwitchModel.SearchItemModel>() {
            @Override
            public void onItemClick(RecyclerViewHolder viewHolder, LocationSwitchModel.SearchItemModel searchItemModel, int position) {
                SDKManager.toast("letter = " + searchItemModel.getName());
                LogFileUtil.v("letter = " + searchItemModel.getName());
            }
        });
    }

    private void initData() {
        // 中文搜索
        downLoadModel = LocationSwitchManager.genDownLoadModel(this);
        if (null != downLoadModel) {
            // 英文搜索
            englishSearchModelMap = LocationSwitchManager.download2EnglishSearchModel(downLoadModel.getRegion_list());
        }

        mSearchHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                String content = (String) msg.obj;
                LogUtil.v("content = " + content + ", what = " + msg.what);

                List<LocationSwitchModel.SearchItemModel> result = null;
                switch (msg.what) {
                    case InputTypeLetter: // 英文
                        if (null != englishSearchModelMap) {
                            result = englishSearchModelMap.get(content);
                        }
                        break;
                    case InputTypeChinese: // 中文
                        if (null != downLoadModel) {
                            result = LocationSwitchManager.searchForChinese(content, downLoadModel.getRegion_list());
                        }
                        break;
                    default:
                        break;
                }

                if (null == result || result.size() == 0) {
                    searchAdapter.setDataList(new ArrayList<LocationSwitchModel.SearchItemModel>(), true);
                } else {
                    searchAdapter.setDataList(result, true);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.location_search_divide).setVisibility(View.INVISIBLE);
        boolean isExited = exit(LocationSearchActivity.this, 200, new DecelerateInterpolator());
        if (!isExited) {
            super.onBackPressed();
        }
    }

    private class LocationSearchAdapter extends CommonRecyclerAdapter<LocationSwitchModel.SearchItemModel> {
        private Callback.OnRecyclerItemClickListener<LocationSwitchModel.SearchItemModel> onRecyclerItemClickListener;

        private void setOnRecyclerItemClickListener(Callback.OnRecyclerItemClickListener<LocationSwitchModel.SearchItemModel> onRecyclerItemClickListener) {
            this.onRecyclerItemClickListener = onRecyclerItemClickListener;
        }

        @Override
        public int getItemRes() {
            return R.layout.item_location_switch;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
            holder.setText(R.id.item_location_switch_title, sList.get(position).getName());
            holder.setText(R.id.item_location_switch_sub, sList.get(position).getParent_name());

            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onRecyclerItemClickListener) {
                        onRecyclerItemClickListener.onItemClick(holder, sList.get(position), position);
                    }
                }
            });
        }
    }
}
