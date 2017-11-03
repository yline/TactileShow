package com.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.notify.utils.NotifyUtils;
import com.yline.log.LogFileUtil;
import com.yline.test.BaseTestActivity;

public class MainActivity extends BaseTestActivity {
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void testStart(View view, Bundle savedInstanceState) {
        addButton("notify", new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder notifyBuilder = NotifyUtils.newDefaultBuilder(MainActivity.this,
                        "本地头条", "您的评论获得1积分奖励，请前往查看", "ticker");
                Notification notification = notifyBuilder.build();

                mNotificationManager.notify(0, notification);
            }
        });

        addButton("定位", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogFileUtil.v("定位 = ");
                final AMapLocationClient mapLocationClient = new AMapLocationClient(MainActivity.this);
                mapLocationClient.setLocationOption(getDefaultOptions());
                mapLocationClient.setLocationListener(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
//                        if (locationListener != null) {
//                            locationListener.onLocationChanged(aMapLocation);
//                        }
                        mapLocationClient.unRegisterLocationListener(this);
                    }
                });
                mapLocationClient.startLocation();
            }
        });
    }

    private static AMapLocationClientOption getDefaultOptions() {
        AMapLocationClientOption options = new AMapLocationClientOption();
        options.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度模式
        options.setOnceLocation(true);
        return options;
    }
}
