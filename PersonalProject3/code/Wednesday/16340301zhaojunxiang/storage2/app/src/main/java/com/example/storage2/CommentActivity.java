package com.example.storage2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

public class CommentActivity extends AppCompatActivity {

    private List<CommentInfo> list;
    private myDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //实例化数据库
        db = new myDB(getApplicationContext());

        //获取用户名和头像
        Intent intent = getIntent();
        final String username = intent.getExtras().getString("username");
        final Bitmap photo = db.getPhoto(username);

        list = new ArrayList<CommentInfo>();
        list = db.queryComment();
        final Adapter myAdapter = new Adapter(CommentActivity.this, list, username, db);
        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(myAdapter);


        //单击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String commentUser = list.get(position).getUsername();

                //读取通讯录
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = \"" +  commentUser + "\"", null, null);
                String number = "\nPhone: ";
                if(cursor.moveToFirst()){
                    do {
                        number += cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "         ";
                    } while (cursor.moveToNext());
                }
                else{
                    number = "\nPhone number not exist.";
                }

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommentActivity.this);
                alertDialog.setTitle("Info").setMessage("Username:" +  commentUser  + number).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        //长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String commentUser = list.get(position).getUsername();
                if(commentUser.equals(username)){
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommentActivity.this);
                    alertDialog.setTitle("Delete or not?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long id = list.get(position).getId();
                            if(db.deleteComment(id) == 1){
                                Toast.makeText(getApplicationContext(),"Delete successfully.", Toast.LENGTH_SHORT).show();
                            }
                            list.remove(position);
                            myAdapter.refresh(list);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return true;
                }else{
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommentActivity.this);
                    alertDialog.setTitle("Report or not?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"Report successfully.",Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    return true;
                }

            }
        });


        Button sendBtn = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText comment = (EditText)findViewById(R.id.commentToSend);
                if(comment.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Comment cannot be empty.", Toast.LENGTH_SHORT).show();
                }else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    CommentInfo newComment = new CommentInfo(username,time,comment.getText().toString(),0, photo);
                    newComment.setId(db.insertComment(newComment));
                    list.add(newComment);
                    myAdapter.refresh(list);
                    comment.setText("");
                }
            }
        });
    }
}
