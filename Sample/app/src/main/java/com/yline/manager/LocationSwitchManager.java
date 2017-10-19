package com.yline.manager;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yline.log.LogFileUtil;
import com.yline.model.LocationSwitchModel;
import com.yline.model.LocationSwitchModel.SearchItemModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 城市切换帮助类
 *
 * @author yline 2017/10/10 -- 15:45
 * @version 1.0.0
 */
public class LocationSwitchManager {
    public static LocationSwitchModel.DownLoadModel genDownLoadModel(Context context) {
        long startTime = System.currentTimeMillis();

        // 数据解析
        try {
            InputStream inputStream = context.getAssets().open("regionList.txt");
            Reader reader = new InputStreamReader(inputStream, Charset.forName("utf-8"));
            LocationSwitchModel.DownLoadModel downLoadModel = new Gson().fromJson(reader, LocationSwitchModel.DownLoadModel.class);

            LogFileUtil.v("locationSwitchModel = " + downLoadModel + ", diffTime = " + (System.currentTimeMillis() - startTime));
            return downLoadModel;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param regionMap 下载的Map
     * @return 英文搜索所使用的 Map
     */
    public static Map<String, List<SearchItemModel>> download2EnglishSearchModel(Map<String, List<LocationSwitchModel.DownLoadItemModel>> regionMap) {
        if (null == regionMap) {
            return null;
        }

        HashMap<String, List<SearchItemModel>> resultMap = new HashMap<>();

        for (String key : regionMap.keySet()) {
            List<LocationSwitchModel.DownLoadItemModel> downLoadItemModelList = regionMap.get(key);
            for (LocationSwitchModel.DownLoadItemModel itemModel : downLoadItemModelList) {
                // 更新 英文搜索中 map的数据
                String spellStr = itemModel.getSpell();
                String[] spellStrList = spellStr.split(",");
                for (String tempStr : spellStrList) {
                    List<SearchItemModel> searchItemModelList = resultMap.get(tempStr);
                    if (null == searchItemModelList) {
                        searchItemModelList = new ArrayList<>();
                    }
                    searchItemModelList.add(new SearchItemModel(itemModel.getRegion_id(), itemModel.getName(), itemModel.getParent_name()));
                    resultMap.put(tempStr, searchItemModelList);
                }

            }
        }

        return resultMap;
    }

    /**
     * 中文搜索内容
     *
     * @return 搜索结果
     */
    public static List<SearchItemModel> searchForChinese(String content, Map<String, List<LocationSwitchModel.DownLoadItemModel>> regionMap) {
        if (null == regionMap) {
            return null;
        }

        List<SearchItemModel> resultList = new ArrayList<>();
        for (String key : regionMap.keySet()) {
            List<LocationSwitchModel.DownLoadItemModel> downLoadItemModelList = regionMap.get(key);
            for (LocationSwitchModel.DownLoadItemModel itemModel : downLoadItemModelList) {
                if (isContain(content, itemModel)) {
                    resultList.add(new SearchItemModel(itemModel.getRegion_id(), itemModel.getName(), itemModel.getParent_name()));
                }
            }
        }
        return resultList;
    }

    private static boolean isContain(String content, LocationSwitchModel.DownLoadItemModel itemModel) {
        if (!TextUtils.isEmpty(itemModel.getName())) {
            if (itemModel.getName().contains(content)) {
                return true;
            }
        }

        if (!TextUtils.isEmpty(itemModel.getParent_name())) {
            if (itemModel.getParent_name().contains(content)) {
                return true;
            }
        }

        return false;
    }
}
