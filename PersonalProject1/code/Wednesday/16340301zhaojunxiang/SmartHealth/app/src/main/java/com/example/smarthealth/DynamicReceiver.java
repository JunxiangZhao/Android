package com.example.smarthealth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.Random;

public class DynamicReceiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "com.example.hasee.myapplication2.MyDynamicFilter";
    private static final String WIDGETDYNAMICACTION = "com.example.hasee.myapplication2.MyWidgetDynamicFilter";  //Widget动态广播的Action字符串
    private static int count = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DYNAMICACTION)) {
            Bundle bundle = intent.getExtras();
            Notification.Builder builder = new Notification.Builder(context);

            //对Builder进行配置
            Food food = (Food)intent.getExtras().getSerializable("food");
            builder.setContentTitle("已收藏")
                    .setContentText(food.getName())
                    .setTicker("您有一条新消息")
                    .setSmallIcon(R.mipmap.full_star)
                    .setAutoCancel(true);

            //传递内容
            Intent myIntent = new Intent(context, MainActivity.class);

            PendingIntent myPendingIntent = PendingIntent.getActivity(context,0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(myPendingIntent);

            //获取状态通知栏管理
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            //绑定Notification，发送通知请求
            Notification notify = builder.build();
            manager.notify(count,notify);
            count++;
        }

        if (intent.getAction().equals(WIDGETDYNAMICACTION)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Bundle bundle = intent.getExtras();
            Food food = (Food)bundle.getSerializable("food");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.appwidget_text, "已收藏 " + food.getName());
            views.setImageViewResource(R.id.widget_image,R.mipmap.full_star);
            Intent i = new Intent(context,MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_image, pi); //设置点击事件
            ComponentName me = new ComponentName(context, NewAppWidget.class);
            appWidgetManager.updateAppWidget(me,views);
        }
    }
}
