package com.example.storage2;

import android.graphics.Bitmap;

public class CommentInfo {
    private long id;
    private String username;
    private String time;
    private String comment;
    private int likeCount;
    private Bitmap photo;

    CommentInfo(String _username, String _time, String _comment, int _likeCount, Bitmap _photo){
        username = _username;
        time = _time;
        comment = _comment;
        likeCount = _likeCount;
        photo = _photo;
        id = 1;
    }

    public long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getTime(){
        return time;
    }

    public String getComment(){
        return comment;
    }

    public int getLikeCount(){
        return likeCount;
    }

    public Bitmap getPhoto(){
        return photo;
    }

    public void setId(long _id){
        id = _id;
    }

    public void setLikeCount(int count){
        likeCount = count;
    }
}
