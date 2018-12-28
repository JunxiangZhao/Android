package com.example.smarthealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class RecyclerActivity extends AppCompatActivity {

    private List<Food> data;
    private List<Food> collectedFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collectedFood = new ArrayList<Food>();
        collectedFood.add(new Food("收藏夹","*","","",""));

        data = new ArrayList<Food>();
        data.add(new Food("大豆","粮","粮食","蛋白质","#BB4C3B"));
        data.add(new Food("十字花科蔬菜","蔬","蔬菜","维生素C","#C48D30"));
        data.add(new Food("牛奶","饮","饮品","钙","#4469B0"));
        data.add(new Food("海鱼","肉","肉食","蛋白质","#20A17B"));
        data.add(new Food("菌菇类","蔬","蔬菜","微量元素","#BB4C3B"));
        data.add(new Food("番茄","蔬","蔬菜","番茄红素","#4469B0"));
        data.add(new Food("胡萝卜","蔬","蔬菜","胡萝卜素","#20A17B"));
        data.add(new Food("荞麦","粮","粮食","膳食纤维","#BB4C3B"));
        data.add(new Food("鸡蛋","杂","杂","几乎所有营养物质","#C48D30"));

        final MyRecyclerViewAdapter myAdapter = new MyRecyclerViewAdapter<Food>(RecyclerActivity.this, R.layout.item, data) {
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
                Intent intent = new Intent(RecyclerActivity.this, DetailActivity.class);
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

        //动画
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(myAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter((scaleInAnimationAdapter));
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(RecyclerActivity.this,ListViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("collectedFood",(Serializable)collectedFood);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == 3){
                Food food = (Food)data.getSerializableExtra("backFood");
                collectedFood.add(food);
            }
        }

        if(requestCode == 2){
            if(resultCode == 4){
                List<Food> backList = (List<Food>)data.getSerializableExtra("backList");
                collectedFood = backList;
            }
        }
    }
}
