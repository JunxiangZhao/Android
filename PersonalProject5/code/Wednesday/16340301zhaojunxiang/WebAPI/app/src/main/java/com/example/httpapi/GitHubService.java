package com.example.httpapi;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubService {
    @GET("/users/{user_name}/repos")
    Observable<List<Repo>> getRepo(@Path("user_name") String user_name);

    @GET("/repos/{user_name}/{repo_name}/issues")
    Observable<List<Issue>> getIssues(@Path("user_name") String user_name, @Path("repo_name") String repo_name);

    @Headers("Authorization: token ...")
    @POST("/repos/{user_name}/{repo_name}/issues")
    Observable<Issue> postIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name, @Body Issue postIssue);

}
