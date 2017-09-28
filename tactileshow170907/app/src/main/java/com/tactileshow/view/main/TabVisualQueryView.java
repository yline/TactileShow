package com.tactileshow.view.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tactileshow.main.R;
import com.tactileshow.view.custom.DefinedDateEditText;
import com.tactileshow.view.custom.DefinedTimeEditText;
import com.yline.view.recycler.holder.ViewHolder;

import java.util.Calendar;

/**
 * 查询 历史信息界面，封装
 * @author yline 2017/9/28 -- 14:56
 * @version 1.0.0
 */
public class TabVisualQueryView extends RelativeLayout {
    private ViewHolder viewHolder;

    private OnVisualQueryCallback onVisualQueryCallback;

    private TextView hourTextView, dayTextView;

    private int timeHourFrom, timeHourTo, timeMinuteFrom, timeMinuteTo;

    private DefinedTimeEditText fromTimeEditText, toTimeEditText;

    private int dateYearFrom, dateYearTo, dateMonthFrom, dateMonthTo, dateDayFrom, dateDayTo;

    private DefinedDateEditText fromDateEditText, toDateEditText;

    public TabVisualQueryView(Context context) {
        super(context);
        initView(context);
    }

    public TabVisualQueryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TabVisualQueryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_query, this, true);

        viewHolder = new ViewHolder(view);
        hourTextView = viewHolder.get(R.id.visual_query_btn_hour);
        dayTextView = viewHolder.get(R.id.visual_query_btn_day);

        fromTimeEditText = viewHolder.get(R.id.visual_query_time_et_from);
        toTimeEditText = viewHolder.get(R.id.visual_query_time_et_to);

        fromDateEditText = viewHolder.get(R.id.visual_query_date_et_from);
        toDateEditText = viewHolder.get(R.id.visual_query_date_et_to);

        initState(context);
        initVisible(context);
        initViewClick();
    }

    // 初始化状态
    private void initState(Context context) {
        // 头部
        TextView tvSwitch = viewHolder.get(R.id.visual_query_switch);
        tvSwitch.setText(R.string.label_history_area_str);
        tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.history_area_unvisible, 0);
        viewHolder.get(R.id.visual_query_ll_duration).setVisibility(GONE);
        viewHolder.get(R.id.visual_query_rl_slot).setVisibility(GONE);

        // 查询分类
        hourTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
        dayTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        viewHolder.get(R.id.visual_query_ll_hour).setVisibility(VISIBLE);
        viewHolder.get(R.id.visual_query_ll_day).setVisibility(GONE);

        // 时间初始化
        Calendar calendar = Calendar.getInstance();
        long stampTime = System.currentTimeMillis();
        calendar.setTimeInMillis(stampTime);
        timeMinuteTo = calendar.get(Calendar.MINUTE);
        timeHourTo = calendar.get(Calendar.HOUR_OF_DAY);
        toTimeEditText.setText(timeHourTo, timeMinuteTo);

        dateYearTo = calendar.get(Calendar.YEAR);
        dateMonthTo = calendar.get(Calendar.MONTH) + 1;
        dateDayTo = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        toDateEditText.setText(dateYearTo, dateMonthTo, dateDayTo);

        calendar.setTimeInMillis(stampTime - 360_1000);
        timeMinuteFrom = calendar.get(Calendar.MINUTE);
        timeHourFrom = calendar.get(Calendar.HOUR_OF_DAY);
        fromTimeEditText.setText(timeHourFrom, timeMinuteFrom);

        calendar.setTimeInMillis(stampTime - 8640_1000);
        dateYearFrom = calendar.get(Calendar.YEAR);
        dateMonthFrom = calendar.get(Calendar.MONTH) + 1;
        dateDayFrom = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        fromDateEditText.setText(dateYearFrom, dateMonthFrom, dateDayFrom);
    }

    // 初始化，点击切换视图
    private void initVisible(final Context context) {
        viewHolder.setOnClickListener(R.id.visual_query_switch, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.get(R.id.visual_query_ll_duration).getVisibility() == VISIBLE) {   // 可见 --> 不可见
                    viewHolder.get(R.id.visual_query_ll_duration).setVisibility(GONE);
                    viewHolder.get(R.id.visual_query_rl_slot).setVisibility(GONE);

                    TextView tvSwitch = viewHolder.get(R.id.visual_query_switch);
                    tvSwitch.setText(R.string.label_history_area_str);
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.history_area_unvisible, 0);

                    if (null != onVisualQueryCallback) {
                        onVisualQueryCallback.onModeChange(true);
                    }
                } else {
                    viewHolder.get(R.id.visual_query_ll_duration).setVisibility(VISIBLE);
                    viewHolder.get(R.id.visual_query_rl_slot).setVisibility(VISIBLE);

                    TextView tvSwitch = viewHolder.get(R.id.visual_query_switch);
                    tvSwitch.setText(R.string.label_realtime_area_str);
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.history_area_visible, 0);

                    if (null != onVisualQueryCallback) {
                        onVisualQueryCallback.onModeChange(false);
                    }
                }
            }
        });

        // 按小时查询
        hourTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hourTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                dayTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));

                viewHolder.get(R.id.visual_query_ll_hour).setVisibility(VISIBLE);
                viewHolder.get(R.id.visual_query_ll_day).setVisibility(GONE);
            }
        });

        // 按天查询
        dayTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hourTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                dayTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));

                viewHolder.get(R.id.visual_query_ll_hour).setVisibility(GONE);
                viewHolder.get(R.id.visual_query_ll_day).setVisibility(VISIBLE);
            }
        });
    }

    // 初始化，控件点击事件（与外界相关）
    private void initViewClick() {
        // 一小时内
        viewHolder.setOnClickListener(R.id.visual_query_hour, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onVisualQueryCallback) {
                    onVisualQueryCallback.onQueryHour(v, System.currentTimeMillis());
                }
            }
        });

        // 一天内
        viewHolder.setOnClickListener(R.id.visual_query_day, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onVisualQueryCallback) {
                    onVisualQueryCallback.onQueryDay(v, System.currentTimeMillis());
                }
            }
        });

        // 一个月内
        viewHolder.setOnClickListener(R.id.visual_query_month, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onVisualQueryCallback) {
                    onVisualQueryCallback.onQueryMonth(v, System.currentTimeMillis());
                }
            }
        });

        // 按小时查询
        viewHolder.setOnClickListener(R.id.visual_query_hour_query, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onVisualQueryCallback) {
                    long currentStamp = System.currentTimeMillis();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentStamp);
                    calendar.set(Calendar.HOUR_OF_DAY, timeHourFrom);
                    calendar.set(Calendar.MINUTE, timeMinuteFrom);
                    long fromStamp = calendar.getTimeInMillis();

                    calendar.setTimeInMillis(currentStamp);
                    calendar.set(Calendar.HOUR_OF_DAY, timeHourTo);
                    calendar.set(Calendar.MINUTE, timeMinuteTo);
                    long toStamp = calendar.getTimeInMillis();

                    onVisualQueryCallback.onQueryTimeSelect(v, fromStamp, toStamp);
                }
            }
        });
        fromTimeEditText.setOnTimeSelectCallback(new DefinedTimeEditText.OnTimeSelectCallback() {
            @Override
            public void onSelected(int hour, int minute) {
                timeHourFrom = hour;
                timeMinuteFrom = minute;
            }
        });
        toTimeEditText.setOnTimeSelectCallback(new DefinedTimeEditText.OnTimeSelectCallback() {
            @Override
            public void onSelected(int hour, int minute) {
                timeHourTo = hour;
                timeMinuteTo = minute;
            }
        });

        // 按天查询
        viewHolder.setOnClickListener(R.id.visual_query_day_query, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onVisualQueryCallback) {
                    long currentStamp = System.currentTimeMillis();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentStamp);
                    calendar.set(dateYearFrom, dateMonthFrom - 1, dateDayFrom);
                    long fromStamp = calendar.getTimeInMillis();

                    calendar.setTimeInMillis(currentStamp);
                    calendar.set(dateYearTo, dateMonthTo - 1, dateDayTo);
                    long toStamp = calendar.getTimeInMillis();

                    onVisualQueryCallback.onQueryDateSelect(v, fromStamp, toStamp);
                }
            }
        });
        fromDateEditText.setOnDateSelectCallback(new DefinedDateEditText.OnDateSelectCallback() {
            @Override
            public void onSelected(int year, int month, int day) {
                dateYearFrom = year;
                dateMonthFrom = month;
                dateDayFrom = day;
            }
        });
        toDateEditText.setOnDateSelectCallback(new DefinedDateEditText.OnDateSelectCallback() {
            @Override
            public void onSelected(int year, int month, int day) {
                dateYearTo = year;
                dateMonthTo = month;
                dateDayTo = day;
            }
        });
    }

    public void setOnVisualQueryCallback(OnVisualQueryCallback onVisualQueryCallback) {
        this.onVisualQueryCallback = onVisualQueryCallback;
    }

    public interface OnVisualQueryCallback {
        /**
         * 查看模式转换
         *
         * @param isNow 是否展示当前信息
         */
        void onModeChange(boolean isNow);

        /**
         * 查询一小时内
         *
         * @param view         点击的控件
         * @param currentStamp 点击的时间戳； ms
         */
        void onQueryHour(View view, long currentStamp);

        /**
         * 查询一天内
         *
         * @param view         点击的控件
         * @param currentStamp 点击的时间戳；ms
         */
        void onQueryDay(View view, long currentStamp);

        /**
         * 查询一个月内
         *
         * @param view         点击的控件
         * @param currentStamp 点击的时间戳；ms
         */
        void onQueryMonth(View view, long currentStamp);

        /**
         * 时间选择器，选择的查询
         *
         * @param fromStamp 开始时间戳；ms
         * @param toStamp   结束时间戳；ms
         */
        void onQueryTimeSelect(View view, long fromStamp, long toStamp);

        /**
         * 日期选择器，选择的查询
         *
         * @param fromStamp 开始时间戳；ms
         * @param toStamp   结束时间戳；ms
         */
        void onQueryDateSelect(View view, long fromStamp, long toStamp);
    }
}
