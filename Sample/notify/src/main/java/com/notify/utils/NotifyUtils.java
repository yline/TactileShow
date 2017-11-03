package com.notify.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.notify.R;

/**
 * 通知栏，工具类
 *
 * @author yline 2017/10/24 -- 21:03
 * @version 1.0.0
 */
public class NotifyUtils {

    /**
     * 创建一个默认的 通知Builder；
     * 默认使用：Notification notification = newDefaultNotifyBuilder.build();
     *
     * @param context 上下文
     * @param title   消息标题
     * @param text    消息内容
     * @param ticker  弹出时，提示文字
     * @return 通知的Builder
     */
    public static NotificationCompat.Builder newDefaultBuilder(Context context, String title, String text, String ticker) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context);

        notifyBuilder.setContentTitle(title);
        notifyBuilder.setContentText(text);
        notifyBuilder.setSmallIcon(R.drawable.notify_small_icon);
        notifyBuilder.setColor(ContextCompat.getColor(context, R.color.notify_color));
        notifyBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notify_large_icon));
        notifyBuilder.setTicker(ticker);
        notifyBuilder.setAutoCancel(true);

        return notifyBuilder;
    }
}
