package com.tactileshow.viewhelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tactileshow.R;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;

import java.util.List;

public class TXTViewHelper
{
	private final static float TXT_MIN_AXIS = -500;

	private final static float TXT_MAX_AXIS = 500;

	private TXTLineChartBuilder lineChartBuilder;

	private View contentView;

	private List<Integer> multiChoiceId;

	private AlertDialog dialog;

	@SuppressLint("InflateParams")
	public TXTViewHelper(Context context, DefinedViewPager pager)
	{
		contentView = LayoutInflater.from(context).inflate(R.layout.view_maintab_txt, null);

		initChartView(context, pager, contentView);
		initOtherView(context, contentView);
	}

	private void initChartView(Context context, DefinedViewPager pager, View view)
	{
		DefinedScrollView scroll = (DefinedScrollView) view.findViewById(R.id.scroll_txt);
		LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.visual_txt_chart_layout);

		lineChartBuilder = new TXTLineChartBuilder(context, chartLayout, pager, scroll);
		lineChartBuilder.setYRange(TXT_MIN_AXIS, TXT_MAX_AXIS);
	}

	private void initOtherView(Context context, View view)
	{
		view.findViewById(R.id.btn_choose_cnt).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dialog.show();
			}
		});

		initChooseCntDialog(context);
	}

	private void initChooseCntDialog(Context context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle("请选择通道");
		builder.setMultiChoiceItems(lineChartBuilder.getCntNames(),
				lineChartBuilder.getCntFirstState(),
				new DialogInterface.OnMultiChoiceClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked)
					{
						// 数组越界
						if (isChecked)
						{
							lineChartBuilder.addSeries(which);
						}
						else
						{
							lineChartBuilder.removeSeries(which);
						}
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{ // 确定按钮
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				lineChartBuilder.repaint();
			}
		});

		dialog = builder.create();
	}

	public View getView()
	{
		return contentView;
	}

	public void onSaveInstanceState(Bundle outState)
	{
		lineChartBuilder.onSaveInstanceState(outState);
	}

	public void onRestoreInstanceState(Bundle savedState)
	{
		lineChartBuilder.onRestoreInstanceState(savedState);
	}
}
