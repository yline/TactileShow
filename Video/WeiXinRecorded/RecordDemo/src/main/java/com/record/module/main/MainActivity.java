package com.record.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.record.R;
import com.record.module.player.PlayerActivity;
import com.record.module.record.WeChatRecordActivity;
import com.yline.base.BaseActivity;
import com.yline.log.LogFileUtil;

/**
 * 程序入口
 *
 * @author yline 2017/11/9 -- 9:26
 * @version 1.0.0
 */
public class MainActivity extends BaseActivity {
    private static final int REQ_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_we_chat_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatRecordActivity.launcherForResult(MainActivity.this, REQ_CODE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogFileUtil.v("resultCode = " + resultCode + ", requestCode = " + requestCode + ", data = " + data);

        if (RESULT_OK == resultCode) {
            if (requestCode == REQ_CODE) {
                String videoPath = data.getStringExtra(WeChatRecordActivity.VIDEO_PATH);
                PlayerActivity.launcher(MainActivity.this, videoPath);
            }
        }
    }
}
