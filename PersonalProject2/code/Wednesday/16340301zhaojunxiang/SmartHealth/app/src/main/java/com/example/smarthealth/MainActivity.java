package com.example.smarthealth;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText)findViewById(R.id.search_text);
        Button btn = (Button)findViewById(R.id.button);
        final RadioGroup radioGroup = findViewById(R.id.radio_group);
        RadioButton defaultBtn = (RadioButton)findViewById(R.id.btn1);
        defaultBtn.setChecked(true);

        //搜索按钮点击事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(editText.getText().toString().equals("Health")){
                    RadioButton checkedBtn = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("提示").setMessage(checkedBtn.getText().toString() + "搜索成功").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "对话框“确定”按钮被点击。", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "对话框“取消”按钮被点击。", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                }
                else{
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("提示").setMessage("搜索失败").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "对话框“确定”按钮被点击。", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "对话框“取消”按钮被点击。", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                }

            }
        });

        //切换按钮
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedBtn = (RadioButton)findViewById(checkedId);
                Toast.makeText(getApplicationContext(), checkedBtn.getText().toString() + "被选中", Toast.LENGTH_SHORT).show();
            }
        });

        //跳转函数
        Button navigateBtn = (Button)findViewById(R.id.navigateBTN);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,RecyclerActivity.class);
                startActivity(intent);
            }
        });

    }

}
