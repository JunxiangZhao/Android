package com.example.smarthealth;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {

    private List<Food> data;
    private List<Food> collectedFood;
    final MyListViewAdapter listViewAdapter = new MyListViewAdapter(MainActivity.this, collectedFood);
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<Food>();
        data.add(new Food("大豆","粮","粮食","蛋白质","#BB4C3B"));
        data.add(new Food("十字花科蔬菜","蔬","蔬菜","维生素C","#C48D30"));
        data.add(new Food("牛奶","饮","饮品","钙","#4469B0"));
        data.add(new Food("海鱼","肉","肉食","蛋白质","#20A17B"));
        data.add(new Food("菌菇类","蔬","蔬菜","微量元素","#BB4C3B"));
        data.add(new Food("番茄","蔬","蔬菜","番茄红素","#4469B0"));
        data.add(new Food("胡萝卜","蔬",      "蔬菜","胡萝卜素","#20A17B"));
        data.add(new Food("荞麦","粮","粮食","膳食纤维","#BB4C3B"));
        data.add(new Food("鸡蛋","杂","杂","几乎所有营养物质","#C48D30"));

        collectedFood = new ArrayList<Food>();
        collectedFood.add(new Food("收藏夹","*","","",""));
        listViewAdapter.refresh(collectedFood);

        //食品列表
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyRecyclerViewAdapter myAdapter = new MyRecyclerViewAdapter<Food>(MainActivity.this, R.layout.item, data) {
            @Override
            public void convert(MyViewHolder holder, Food s) {
                TextView name = holder.getView(R.id.recipeName);
                name.setText(s.getName().toString());
                Button first = holder.getView(R.id.icon);
                first.setText(s.getContent().toString());
            }
        };
        myAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("food",data.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onLongClick(int position) {
                data.remove(position);
                myAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "删除成功。", Toast.LENGTH_SHORT).show();

            }
        });

        //食品列表动画
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(myAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter((scaleInAnimationAdapter));
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());


        //收藏夹列表
        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(listViewAdapter);
        listView.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 处理单击事件
                if(i != 0) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("food",collectedFood.get(i));
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 处理长按事件
                if(i != 0) {
                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                    final int position = i;
                    alertDialog.setTitle("删除").setMessage("确定删除" + collectedFood.get(position).getName()+ "?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            collectedFood.remove(position);
                            listViewAdapter.refresh(collectedFood);
                            Toast.makeText(getApplicationContext(), "删除成功。", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    }).show();
                }
                return true;
            }
        });

        //悬浮按钮
        final FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    recyclerView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    flag = false;
                    btn.setImageResource(R.drawable.mainpage);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    flag = true;
                    btn.setImageResource(R.drawable.collect);
                }
            }
        });

        //发送静态广播
        Random random = new Random();
        int ranNum = random.nextInt(data.size()); //返回一个0到n-1的整数
        Intent intentBroadcast = new Intent("com.example.hasee.myapplication2.MyStaticFilter");
        Bundle bundles = new Bundle();
        bundles.putSerializable("food",data.get(ranNum));
        intentBroadcast.putExtras(bundles);
        sendBroadcast(intentBroadcast);


        //发送widget广播
        Intent widgetIntentBroadcast = new Intent();
        widgetIntentBroadcast.setAction("com.example.hasee.myapplication2.MyWidgetStaticFilter");
        widgetIntentBroadcast.putExtras(bundles);
        sendBroadcast(widgetIntentBroadcast);


        EventBus.getDefault().register(this);

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setVisibility(View.VISIBLE);
        flag = false;
        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.btn);
        btn.setImageResource(R.drawable.mainpage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Food f = (Food) event.getFood();
        collectedFood.add(f);
        listViewAdapter.refresh(collectedFood);

    };

}
