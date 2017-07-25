package com.tactileshow.activity;

import android.os.Bundle;
import android.view.View;

import com.tactileshow.util.FileHelper;
import com.yline.log.LogFileUtil;
import com.yline.test.BaseTestActivity;

import java.util.List;

public class TestActivity extends BaseTestActivity
{
	private static final String TAG = "test";

	@Override
	public void testStart(View view, Bundle savedInstanceState)
	{
		addButton("Read File", new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				LogFileUtil.v(TAG, "btn_read onClick");
				List<List<String>> result = FileHelper.getInstance().readMapData();
				for (int i = 0; i < result.size(); i++)
				{
					LogFileUtil.v(TAG, "i = " + result.get(i).toString());
				}
			}
		});
	}
}
