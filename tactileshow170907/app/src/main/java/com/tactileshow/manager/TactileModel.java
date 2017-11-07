package com.tactileshow.manager;

import com.google.gson.Gson;

/**
 * App内，蓝牙传输数据--界面展示数据，使用的数据结构
 *
 * @author yline 2017/9/22 -- 8:16
 * @version 1.0.0
 */
public class TactileModel {
    public static final int Empty = -1024; // 数据为空

    private long time; // 时间戳
    private float hum = Empty; // 湿度
    private float temp = Empty; // 温度
    private float header = Empty; // 第三通道：压力

    public TactileModel(long time) {
        this.time = time;
    }

    public TactileModel(long time, float hum, float temp, float header) {
        this.time = time;
        this.hum = hum;
        this.temp = temp;
        this.header = header;
    }

    public float getHeader() {
        return header;
    }

    public void setHeader(float header) {
        this.header = header;
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

    public static String toJson(TactileModel model) {
        if (null == gson) {
            gson = new Gson();
        }
        return gson.toJson(model, TactileModel.class);
    }

    public static TactileModel fromJson(String jsonStr) {
        if (null == gson) {
            gson = new Gson();
        }
        return new Gson().fromJson(jsonStr, TactileModel.class);
    }
}
