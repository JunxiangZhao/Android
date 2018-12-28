package com.example.smarthealth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class StaticReceiver extends BroadcastReceiver {
    private static final String STATICACTION = "com.example.hasee.myapplication2.MyStaticFilter";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)){
            Bundle bundle = intent.getExtras();
            Notification.Builder builder = new Notification.Builder(context);

            //对Builder进行配置
            Food food = (Food)intent.getExtras().getSerializable("food");
            builder.setContentTitle("今日推荐")
                    .setContentText(food.getName())
                    .setTicker("您有一条新消息")
                    .setSmallIcon(R.mipmap.empty_star)
                    .setAutoCancel(true);

            //传递内容
            Intent myIntent = new Intent(context, DetailActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Bundle bundles = new Bundle();
            bundles.putSerializable("food",food);
            myIntent.putExtras(bundles);
            PendingIntent myPendingIntent = PendingIntent.getActivity(context,0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(myPendingIntent);

            //获取状态通知栏管理
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            //绑定Notification，发送通知请求
            Notification notify = builder.build();
            manager.notify(0,notify);
        }
    }
}
