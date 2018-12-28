package com.example.httpapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.ArrayList;
import java.util.List;


public class GayhubActivity extends AppCompatActivity {

    private Button searchButton;
    private EditText editText;
    private List<Repo> repos;
    private RecyclerView recyclerView;
    private String username;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gayhub);

        repos = new ArrayList<Repo>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyAdapter myAdapter = new MyAdapter<Repo>(GayhubActivity.this, R.layout.repo_item, repos) {
            @Override
            public void convert(MyViewHolder holder, Repo repo) {
                TextView repoName = holder.getView(R.id.repoName);
                TextView repoId = holder.getView(R.id.repoId);
                TextView issuesNumber = holder.getView(R.id.issuesNumber);
                TextView repoDescription = holder.getView(R.id.repoDescription);

                repoName.setText("项目名：" + repo.getName());
                repoId.setText("项目ID：" + repo.getId());
                issuesNumber.setText("存在问题：" + repo.getOpen_issues().toString());
                repoDescription.setText("项目描述：" + repo.getDescription());
            }
        };

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener(){
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(GayhubActivity.this, IssuesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("repoName", repos.get(position).getName());
                bundle.putString("userName", username);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        recyclerView.setAdapter(myAdapter);

        searchButton = (Button)findViewById(R.id.searchButton2);
        editText = (EditText)findViewById(R.id.editText2);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editText.getText().toString();
                if(username.equals("")){
                    Toast.makeText(getApplicationContext(), "搜索内容不能为空。",Toast.LENGTH_SHORT).show();
                }else{
                    repos.clear();
                    myAdapter.notifyDataSetChanged();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.github.com/") // 设置网络请求的Url地址
                            .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava平台
                            .build();

                    GitHubService service = retrofit.create(GitHubService.class);
                    service.getRepo(username)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<Repo>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {

                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Repo> list) {
                                    if(list.isEmpty()){
                                        Toast.makeText(getApplicationContext(),"User has no repositories.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        for(Repo r:list){
                                            if(r.getHas_issues()){
                                                repos.add(r);
                                            }
                                        }
                                        myAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                }
            }
        });
    }
}
