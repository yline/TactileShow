package com.tactileshow.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tactileshow.main.R;

public class DetailInfo
{
	private View view;
	
	private TextView temp, hum;
	
	public DetailInfo(Context context)
	{
		view = LayoutInflater.from(context).inflate(R.layout.activity_detail_info, null);
		
		temp = (TextView) view.findViewById(R.id.label_detail_temp);
		hum = (TextView) view.findViewById(R.id.label_detail_hum);
	}
	
	public void setTemp(double number)
	{
		String str = String.format("%.2f", number);
		temp.setText(str);
	}
	
	public void setHum(double number)
	{
		String str = String.format("%.2f", number);
		hum.setText(str);
	}
	
	public View getView()
	{
		return view;
	}
}
