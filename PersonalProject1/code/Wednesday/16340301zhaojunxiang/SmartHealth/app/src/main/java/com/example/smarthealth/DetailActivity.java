package com.example.smarthealth;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    DynamicReceiver dynamicReceiver;
    DynamicReceiver widgetDynamicReceiver;
    private static final String WIDGETDYNAMICACTION = "com.example.hasee.myapplication2.MyWidgetDynamicFilter";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        List<String> opList = new ArrayList<String>();
        opList.add("分享信息");
        opList.add("不感兴趣");
        opList.add("查看更多信息");
        opList.add("出错反馈");
        ListView operation = (ListView)findViewById(R.id.operationList);
        DetailAdapter adapter = new DetailAdapter(DetailActivity.this,opList);
        operation.setAdapter(adapter);

        //接收数据
        Intent intent = getIntent();
        final Food food = (Food)intent.getSerializableExtra("food");

        final TextView detailName = (TextView)findViewById(R.id.detail_name);
        detailName.setText(food.getName());

        TextView type = (TextView)findViewById(R.id.type);
        type.setText(food.getType());

        TextView nutrition = (TextView)findViewById(R.id.nutrition);
        nutrition.setText("富含 " + food.getNutrition());

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.topLayout);
        layout.setBackgroundColor(Color.parseColor(food.getColor()));

        //星标切换
        final ImageView star = (ImageView)findViewById(R.id.star);
        star.setTag(0);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((int)star.getTag() == 0){
                    star.setImageDrawable(getResources().getDrawable(R.drawable.full_star));
                    star.setTag(1);
                }
                else{
                    star.setImageDrawable(getResources().getDrawable(R.drawable.empty_star));
                    star.setTag(0);
                }
            }
        });


        //返回功能
        final ImageView backBtn = (ImageView)findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //收藏功能
        ImageView collectBtn = (ImageView)findViewById(R.id.collect);
        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), food.getName() + "收藏成功" , Toast.LENGTH_SHORT).show();

                //注册动态广播
                if(dynamicReceiver == null){
                    IntentFilter dynamic_filter = new IntentFilter();
                    dynamic_filter.addAction("com.example.hasee.myapplication2.MyDynamicFilter");
                    dynamicReceiver = new DynamicReceiver();
                    registerReceiver(dynamicReceiver, dynamic_filter);
                }

                //发送广播
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction("com.example.hasee.myapplication2.MyDynamicFilter");
                Bundle bundleBroadcast = new Bundle();
                bundleBroadcast.putSerializable("food", food);
                intentBroadcast.putExtras(bundleBroadcast);
                sendBroadcast(intentBroadcast);

                //传递食品信息
                EventBus.getDefault().post(new MessageEvent(food));

                //注册widget动态广播
                if(widgetDynamicReceiver == null){
                    IntentFilter widget_dynamic_filter = new IntentFilter();
                    widget_dynamic_filter.addAction(WIDGETDYNAMICACTION);
                    widgetDynamicReceiver = new DynamicReceiver(); //添加动态广播的Action
                    registerReceiver(widgetDynamicReceiver, widget_dynamic_filter);
                }

                Intent widgetIntentBroadcast = new Intent();   //定义Intent
                widgetIntentBroadcast.setAction(WIDGETDYNAMICACTION);
                widgetIntentBroadcast.putExtras(bundleBroadcast);
                sendBroadcast(widgetIntentBroadcast);

            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(dynamicReceiver != null){
            unregisterReceiver(dynamicReceiver);
            dynamicReceiver = null;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(widgetDynamicReceiver != null){
            unregisterReceiver(widgetDynamicReceiver);
            widgetDynamicReceiver = null;
        }
    }
}
