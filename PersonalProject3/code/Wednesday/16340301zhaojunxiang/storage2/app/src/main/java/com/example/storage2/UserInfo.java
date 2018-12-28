package com.example.storage2;

import android.graphics.Bitmap;

public class UserInfo {
    private String _username;
    private String _password;
    private Bitmap _photo;

    UserInfo(String username, String password, Bitmap photo){
        _username = username;
        _password = password;
        _photo = photo;
    }

    public String getUsername(){
        return _username;
    }

    public String getPassword(){
        return _password;
    }

    public Bitmap getPhoto(){
        return _photo;
    }
}
