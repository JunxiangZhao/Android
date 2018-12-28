package com.example.storage2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    boolean isLogin = true;
    private Bitmap photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化数据库
        final myDB db = new myDB(getApplicationContext());
        Resources res = getResources();
        photo = BitmapFactory.decodeResource(res,R.mipmap.me);

        //启动应用，默认为登陆状态
        RadioButton loginBtn = (RadioButton)findViewById(R.id.loginBtn);
        loginBtn.setChecked(true);
        EditText newPassword = (EditText)findViewById(R.id.newPassword);
        EditText comfirmPassword = (EditText)findViewById(R.id.comfirmPassword);
        ImageView add = (ImageView)findViewById(R.id.add);
        add.setVisibility(View.GONE);
        comfirmPassword.setVisibility(View.GONE);

        //切换按钮
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(isLogin){
                    EditText username = (EditText)findViewById(R.id.username);
                    EditText newPassword = (EditText)findViewById(R.id.newPassword);
                    EditText comfirmPassword = (EditText)findViewById(R.id.comfirmPassword);
                    ImageView add = (ImageView)findViewById(R.id.add);
                    newPassword.setHint("New Password");
                    comfirmPassword.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    newPassword.setText("");
                    comfirmPassword.setText("");
                    username.setText("");
                    isLogin = false;
                }else{
                    EditText username = (EditText)findViewById(R.id.username);
                    EditText newPassword = (EditText)findViewById(R.id.newPassword);
                    EditText comfirmPassword = (EditText)findViewById(R.id.comfirmPassword);
                    ImageView add = (ImageView)findViewById(R.id.add);
                    newPassword.setHint("Password");
                    comfirmPassword.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    newPassword.setText("");
                    username.setText("");
                    isLogin = true;
                }

            }
        });

        //ok按钮
        Button okBtn = (Button)findViewById(R.id.okButton);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin){
                    EditText username = (EditText)findViewById(R.id.username);
                    EditText newPassword = (EditText)findViewById(R.id.newPassword);
                    if(username.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Username cannot be empty.",Toast.LENGTH_SHORT).show();
                    }
                    else if(newPassword.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Password cannot be empty.",Toast.LENGTH_SHORT).show();
                    }
                    else if(!db.getByUsername(username.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Username not existed.",Toast.LENGTH_SHORT).show();
                    }
                    else if(!db.getPassword(username.getText().toString()).equals(newPassword.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Invalid password.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username.getText().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else{
                    EditText username = (EditText)findViewById(R.id.username);
                    EditText newPassword = (EditText)findViewById(R.id.newPassword);
                    EditText comfirmPassword = (EditText)findViewById(R.id.comfirmPassword);

                    if(username.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Username cannot be empty.",Toast.LENGTH_SHORT).show();
                    }
                    else if(newPassword.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Password cannot be empty.",Toast.LENGTH_SHORT).show();
                    }
                    else if(!newPassword.getText().toString().equals(comfirmPassword.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Password Mismatch.",Toast.LENGTH_SHORT).show();
                    }
                    else if(db.getByUsername(username.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Username already existed.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        UserInfo user = new UserInfo(username.getText().toString(),newPassword.getText().toString(),photo);
                        db.insertUser(user);
                        Toast.makeText(getApplicationContext(),"Register successfully.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //clear按钮
        Button clearBtn = (Button)findViewById(R.id.clearButton);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText)findViewById(R.id.username);
                EditText newPassword = (EditText)findViewById(R.id.newPassword);
                EditText comfirmPassword = (EditText)findViewById(R.id.comfirmPassword);
                username.setText("");
                newPassword.setText("");
                comfirmPassword.setText("");
            }
        });

        //选择图片
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data != null){
            Uri uri = data.getData();
            ImageView add = (ImageView)findViewById(R.id.add);
            try {
                //photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                photo =  getBitmapFormUri(MainActivity.this,uri);
                add.setImageBitmap(photo);
            }catch (Exception e){

            }
        }
    }

    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
