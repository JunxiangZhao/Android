package com.example.httpapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class IssuesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Issue> issues;
    private MyAdapter myAdapter;
    private String username;
    private String reponame;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues);

        init();
    }


    //initialize recycler view
    private void init(){
        issues = new ArrayList<Issue>();
        recyclerView = (RecyclerView)findViewById(R.id.issuesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new MyAdapter<Issue>(IssuesActivity.this, R.layout.issue_item, issues) {
            @Override
            public void convert(MyViewHolder holder, Issue issue) {
                TextView issueTitle = holder.getView(R.id.title);
                TextView createTime = holder.getView(R.id.createTime);
                TextView issuesState = holder.getView(R.id.issuesState);
                TextView issueDescription = holder.getView(R.id.issueDescription);

                issueTitle.setText("Title: " + issue.getTitle());
                createTime.setText("创建时间: " + issue.getCreate_at());
                issuesState.setText("问题状态: " + issue.getState());
                issueDescription.setText("问题描述: " + issue.getBody());
            }
        };
        recyclerView.setAdapter(myAdapter);

        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleEdit = (EditText)findViewById(R.id.issueTitle);
                EditText bodyEdit = (EditText)findViewById(R.id.issueBody);
                String title = titleEdit.getText().toString();
                String body = bodyEdit.getText().toString();
                if(title.equals("")){
                    Toast.makeText(getApplicationContext(),"Title 不能为空",Toast.LENGTH_SHORT).show();
                }else if(body.equals("")){
                    Toast.makeText(getApplicationContext(),"Body 不能为空",Toast.LENGTH_SHORT).show();
                }else{

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.github.com/") // 设置网络请求的Url地址
                            .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava平台
                            .build();

                    GitHubService service = retrofit.create(GitHubService.class);
                    Issue postIssue = new Issue(title,"","",body);
                    service.postIssue(username, reponame, postIssue)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Issue>() {
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
                                public void onNext(Issue issue) {
                                    issues.add(issue);
                                    myAdapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(),"Issue created successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        queryIssues();
    }

    //query all issues of the repository and refresh the recyclerview
    private void queryIssues(){
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("userName");
        reponame = bundle.getString("repoName");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/") // 设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava平台
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        service.getIssues(username, reponame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Issue>>() {
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
                    public void onNext(List<Issue> list) {
                        if(list.isEmpty()){
                            Toast.makeText(getApplicationContext(),"This repo has no issue.",Toast.LENGTH_SHORT).show();
                        }else{
                            issues.addAll(list);
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
