package com.yline.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.yline.log.LogFileUtil;
import com.yline.manager.LocationSwitchManager;
import com.yline.model.LocationSwitchModel;
import com.yline.test.BaseTestActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class LocationActivity extends BaseTestActivity {

    @Override
    public void testStart(View view, Bundle savedInstanceState) {
        addButton("加载json时间", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startTime = System.currentTimeMillis();

                try {
                    InputStream inputStream = getAssets().open("regionList.txt");

                    Reader reader = new InputStreamReader(inputStream, Charset.forName("utf-8"));
                    LocationSwitchModel.DownLoadModel downLoadModel = new Gson().fromJson(reader, LocationSwitchModel.DownLoadModel.class);

                    // 486ms
                    LogFileUtil.v("downLoadModel = " + downLoadModel + ", diffTime = " + (System.currentTimeMillis() - startTime));
                    startTime = System.currentTimeMillis();

                    // 英文搜索使用：englishSearchModelMap.get(String);   103ms
                    Map<String, List<LocationSwitchModel.SearchItemModel>> englishSearchModelMap = LocationSwitchManager.download2EnglishSearchModel(downLoadModel.getRegion_list());
                    LogFileUtil.v("english gen diffTime = " + (System.currentTimeMillis() - startTime));
                    startTime = System.currentTimeMillis();

                    // 0ms
                    List<LocationSwitchModel.SearchItemModel> englishResult = englishSearchModelMap.get("bj");
                    LogFileUtil.v("english search diffTime = " + (System.currentTimeMillis() - startTime));
                    logResult(englishResult);
                    startTime = System.currentTimeMillis();

                    // 中文搜索使用： 17ms
                    List<LocationSwitchModel.SearchItemModel> chineseResult = LocationSwitchManager.searchForChinese("江", downLoadModel.getRegion_list());
                    LogFileUtil.v("chinese search diffTime = " + (System.currentTimeMillis() - startTime));
                    logResult(chineseResult);
                    startTime = System.currentTimeMillis();
                } catch (IOException e) {
                    LogFileUtil.e("startTime", "IOException", e);
                }
            }
        });

        addButton("列表展示页", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSwitchActivity.launcher(LocationActivity.this);
            }
        });
    }

    private void logResult(List<LocationSwitchModel.SearchItemModel> result){
        StringBuilder builder = new StringBuilder();
        for (LocationSwitchModel.SearchItemModel model: result) {
            builder.append(model.getName());
            builder.append("-");
            builder.append(model.getParent_name());
            builder.append(";");
        }
        LogFileUtil.v("logResult = " + builder.toString());
    }

    public static void launcher(Context context) {
        if (null != context) {
            Intent intent = new Intent(context, LocationActivity.class);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }
}
