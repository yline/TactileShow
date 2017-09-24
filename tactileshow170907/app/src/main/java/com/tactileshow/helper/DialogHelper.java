package com.tactileshow.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tactileshow.main.R;

/**
 * 解耦并
 * 解决 dialog没有创建就 设置文字的bug；
 *
 * @author yline 2017/9/12 -- 19:35
 * @version 1.0.0
 */
public class DialogHelper
{
	private AlertDialog dialog;
	
	private TextView tvConnectInfo;
	
	private DialogInterface.OnClickListener listener;
	
	public DialogHelper(Context context)
	{
		View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_main_dialog_connect, null);
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle("连接状态");
		dialogBuilder.setView(dialogView);
		dialogBuilder.setNegativeButton("取消连接", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (null != listener)
				{
					listener.onClick(dialog, which);
				}
				dialog.dismiss();
			}
		});
		
		tvConnectInfo = (TextView) dialogView.findViewById(R.id.tv_ble_connect_info);
		tvConnectInfo.setText("正在连接中");
		dialog = dialogBuilder.create();
	}
	
	public void setOnNegativeClickListener(DialogInterface.OnClickListener listener)
	{
		this.listener = listener;
	}
	
	public void show()
	{
		boolean result = (null != dialog && !dialog.isShowing());
		Log.i("xxx-", "show: result = " + result);
		if (null != dialog && !dialog.isShowing())
		{
			dialog.show();
		}
	}
	
	public void dismiss()
	{
		if (null != dialog && dialog.isShowing())
		{
			dialog.dismiss();
		}
	}
	
	public void setText(String text)
	{
		if (null != tvConnectInfo)
		{
			tvConnectInfo.setText(text);
		}
	}
}
