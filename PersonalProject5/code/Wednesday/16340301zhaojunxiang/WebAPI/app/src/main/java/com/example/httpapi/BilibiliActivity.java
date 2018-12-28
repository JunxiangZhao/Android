package com.example.httpapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BilibiliActivity extends AppCompatActivity {
    private ArrayList<RecyclerObj> list;
    private RecyclerView recyclerView;
    private Observable observable;
    private String userID;
    private Bitmap bitmap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilibili);

        list = new ArrayList<RecyclerObj>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyAdapter myAdapter = new MyAdapter<RecyclerObj>(BilibiliActivity.this, R.layout.item, list) {
            @Override
            public void convert(MyViewHolder holder, final RecyclerObj obj) {
                final ImageView image = holder.getView(R.id.image);
                final TextView plays = holder.getView(R.id.plays);
                final TextView comments = holder.getView(R.id.comments);
                final TextView duration = holder.getView(R.id.duration);
                final TextView createTime = holder.getView(R.id.createTime);
                final TextView title = holder.getView(R.id.title);
                final TextView introduction = holder.getView(R.id.introduction);
                final ProgressBar progressBar = holder.getView(R.id.progressBar);

                plays.setText("播放：" + Integer.toString(obj.getData().getPlay()));
                comments.setText("评论：" +Integer.toString(obj.getData().getVideo_review()));
                duration.setText("时长："+ obj.getData().getDuration());
                createTime.setText("创建时间：" + obj.getData().getCreate());
                title.setText(obj.getData().getTitle());
                introduction.setText(obj.getData().getContent());
                progressBar.bringToFront();

                int index = list.indexOf(obj);
                if(index ==list.size() - 1){
                    progressBar.setVisibility(View.VISIBLE);
                }


                Observable.create(new ObservableOnSubscribe<Bitmap>() {
                    @Override
                    public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                        try {
                            String path = obj.getData().getCover();
                            StringBuilder stringBuilder = new StringBuilder(path);
                            stringBuilder.insert(4,'s');
                            path = stringBuilder.toString();
                            URL imgUrl = new URL(path);
                            HttpURLConnection conn = (HttpURLConnection) imgUrl
                                    .openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            conn.disconnect();
                            emitter.onNext(bitmap);
                            emitter.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            //progressBar.setVisibility(View.GONE);
                            plays.setText("播放：" + Integer.toString(obj.getData().getPlay()));
                            comments.setText("评论：" +Integer.toString(obj.getData().getVideo_review()));
                            duration.setText("时长："+ obj.getData().getDuration());
                            createTime.setText("创建时间：" + obj.getData().getCreate());
                            title.setText(obj.getData().getTitle());
                            introduction.setText(obj.getData().getContent());
                            image.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            }
        };

        recyclerView.setAdapter(myAdapter);

        Button searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);
                userID = editText.getText().toString();
                if(!userID.matches("[0-9]+")){
                    Toast.makeText(getApplicationContext(),"输入必须为正整数",Toast.LENGTH_SHORT).show();
                }else if(Integer.valueOf(userID) > 40){
                    Toast.makeText(getApplicationContext(),"请输入小于或等于40的id",Toast.LENGTH_SHORT).show();
                }else if(!isNetworkConnected(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"网络连接不可用",Toast.LENGTH_SHORT).show();
                }
                else{
                    Observable.create(new ObservableOnSubscribe<String>() {
                        @Override
                        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                            try{
                                //send request
                                URL url = new URL("https://space.bilibili.com/ajax/top/showTop?mid="+ userID);
                                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                                httpURLConnection.setRequestMethod("GET");
                                httpURLConnection.setConnectTimeout(5*1000);
                                httpURLConnection.connect();
                                InputStream inputStream= httpURLConnection.getInputStream();

                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                StringBuilder stringBuilder = new StringBuilder();
                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(line+"\n");
                                }
                                bufferedReader.close();
                                inputStream.close();
                                httpURLConnection.disconnect();

                                String jsonString = stringBuilder.toString();

                                if(jsonString.indexOf("false") != -1){
                                    emitter.onComplete();
                                }else{
                                    emitter.onNext(jsonString);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"数据库中不存在记录",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String jsonString) {
                            RecyclerObj recyclerObj = new Gson().fromJson(jsonString, RecyclerObj.class);
                            list.add(recyclerObj);
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onComplete() {
                            Toast.makeText(getApplicationContext(),"数据库中不存在记录",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
