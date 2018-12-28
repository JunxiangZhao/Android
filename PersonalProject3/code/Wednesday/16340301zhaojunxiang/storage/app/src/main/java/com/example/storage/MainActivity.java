package com.example.storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button okButton = (Button)findViewById(R.id.okBtn);
        Button clearButton = (Button)findViewById(R.id.clearBtn);
        EditText newPassword = (EditText)findViewById(R.id.newPw);
        EditText confirmPassword = (EditText)findViewById(R.id.comfirmPw);

        SharedPreferences myPreference = getSharedPreferences("MY_PREFERENCE",MODE_PRIVATE);
        password = myPreference.getString("password", "default");

        if(!password.equals("default")){
            newPassword.setHint("Password");
            confirmPassword.setVisibility(View.GONE);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newPassword = (EditText)findViewById(R.id.newPw);
                EditText confirmPassword = (EditText)findViewById(R.id.comfirmPw);

                if(password.equals("default")){
                    if(newPassword.length() == 0){
                        Toast.makeText(getApplicationContext(),"Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Password Mismatch.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //保存密码
                        SharedPreferences myPreference = getSharedPreferences("MY_PREFERENCE",MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPreference.edit();
                        editor.putString("password", newPassword.getText().toString());
                        editor.commit();
                        //跳转
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    if(newPassword.getText().toString().equals(password)){
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newPassword = (EditText)findViewById(R.id.newPw);
                EditText confirmPassword = (EditText)findViewById(R.id.comfirmPw);
                newPassword.setText("");
                confirmPassword.setText("");
            }
        });
    }
}
