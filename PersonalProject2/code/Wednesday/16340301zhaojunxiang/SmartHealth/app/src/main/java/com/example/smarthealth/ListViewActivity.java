package com.example.smarthealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity {

    private List<Food> list;
    final MyListViewAdapter myAdapter = new MyListViewAdapter(ListViewActivity.this,list);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(myAdapter);

        list = new ArrayList<Food>();

        Intent intent = getIntent();
        List<Food> newList = (List<Food>)intent.getSerializableExtra("collectedFood");
        if(newList!=null){
            for(Food f:newList){
                list.add(f);
                myAdapter.refresh(list);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 处理单击事件
                if(i != 0) {
                    Intent intent = new Intent(ListViewActivity.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("food",list.get(i));
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
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListViewActivity.this);
                    final int position = i;
                    alertDialog.setTitle("删除").setMessage("确定删除" + list.get(position).getName()+ "?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.remove(position);
                            myAdapter.refresh(list);
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

        floatingBtnAction();
    }

    private void floatingBtnAction(){
        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.mainpageBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("backList",(Serializable)list);
                Intent resultIntent = new Intent();
                resultIntent.putExtras(bundle);
                setResult(4,resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == 3){
                Food food = (Food)data.getSerializableExtra("backFood");
                list.add(food);
                myAdapter.refresh(list);
            }
        }
    }


}
