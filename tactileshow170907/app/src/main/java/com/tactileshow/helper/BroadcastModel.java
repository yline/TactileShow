package com.tactileshow.helper;

import com.google.gson.Gson;

/**
 * App内，本地储存，使用的协议
 *
 * @author yline 2017/9/22 -- 8:16
 * @version 1.0.0
 */
public class BroadcastModel {
    public static final int Empty = -1024; // 数据为空

    private long time; // 时间戳

    private float hum = Empty; // 湿度

    private float temp = Empty; // 温度

    public BroadcastModel(long time) {
        this.time = time;
    }

    public BroadcastModel(long time, float hum, float temp) {
        this.time = time;
        this.hum = hum;
        this.temp = temp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getHum() {
        return hum;
    }

    public void setHum(float hum) {
        this.hum = hum;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public boolean isDataEmpty() {
        return (Empty == hum) && (Empty == temp);
    }

    private static Gson gson;

    public static String toJson(BroadcastModel model) {
        if (null == gson) {
            gson = new Gson();
        }
        return gson.toJson(model, BroadcastModel.class);
    }

    public static BroadcastModel fromJson(String jsonStr) {
        if (null == gson) {
            gson = new Gson();
        }
        return new Gson().fromJson(jsonStr, BroadcastModel.class);
    }
}
