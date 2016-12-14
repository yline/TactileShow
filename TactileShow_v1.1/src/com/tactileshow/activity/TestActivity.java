package com.tactileshow.activity;

import java.util.List;

import com.tactileshow.base.BaseActivity;
import com.tactileshow.log.LogFileUtil;
import com.tactileshow.main.R;
import com.tactileshow.util.FileHelper;

import android.os.Bundle;
import android.view.View;

public class TestActivity extends BaseActivity
{
    private static final String TAG = "test";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        findViewById(R.id.btn_read).setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
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
