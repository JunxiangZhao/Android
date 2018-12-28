package com.example.storage2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.strictmode.SqliteObjectLeakedViolation;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME= "myDatabase";
    private static final String USER_TABLE_NAME = "user_table";
    private static final String COMMENT_TABLE_NAME = "comment_table";
    private static final String LIKE_TABLE_NAME = "like_table";
    private static final int DB_VERSION = 1;

    public myDB(Context c){
        super(c, DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE if not exists " + USER_TABLE_NAME +
                " (username STRING PRIMARY KEY, password STRING, photo BLOB)";
        String CREATE_COMMENT_TABLE = "CREATE TABLE if not exists " + COMMENT_TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, username STRING, time STRING, comment STRING, likeCount INTEGER, photo BLOB)";
        String CREATE_LIKE_TABLE = "CREATE TABLE if not exists " + LIKE_TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, username STRING, commentId STRING)";

        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_LIKE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int ii) {

    }

    public long insertUser(UserInfo user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        //把图片转为字节
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        user.getPhoto().compress(Bitmap.CompressFormat.PNG, 100,byteStream);
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("photo", byteStream.toByteArray());

        long rid = db.insert(USER_TABLE_NAME, null,values);
        db.close();
        return  rid;
    }

    public Boolean getByUsername(String username){
        SQLiteDatabase db = getReadableDatabase();
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(USER_TABLE_NAME, null, selection, selectionArgs,null,null,null);
        return cursor.moveToFirst();
    }

    public String getPassword(String username){
        SQLiteDatabase db = getReadableDatabase();
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(USER_TABLE_NAME, null, selection, selectionArgs,null,null,null);
        if(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("password"));
        }
        return "";
    }

    public Bitmap getPhoto(String username){
        SQLiteDatabase db = getReadableDatabase();
        String selection = "username = ?";
        String [] selectionArgs = {username};
        Cursor cursor = db.query(USER_TABLE_NAME, null,selection,selectionArgs,null,null,null);

        if(cursor.moveToNext()){
            byte[] in = cursor.getBlob(cursor.getColumnIndex("photo"));
            return BitmapFactory.decodeByteArray(in, 0,in.length);
        }
        return null;
    }

    public long insertComment(CommentInfo newComment){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        //把图片转为字节
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        newComment.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        values.put("username", newComment.getUsername());
        values.put("time",newComment.getTime());
        values.put("comment", newComment.getComment());
        values.put("likeCount", newComment.getLikeCount());
        values.put("photo", byteStream.toByteArray());

        long rid = db.insert(COMMENT_TABLE_NAME, null,values);
        db.close();
        return  rid;
    }

    public List<CommentInfo> queryComment(){
        SQLiteDatabase db = getReadableDatabase();
        List<CommentInfo> list = new ArrayList<CommentInfo>();

        Cursor cursor = db.query(COMMENT_TABLE_NAME,null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do{
                byte[] in = cursor.getBlob(5);
                Bitmap bitmap = BitmapFactory.decodeByteArray(in, 0,in.length);
                CommentInfo temp = new CommentInfo(cursor.getString(1),cursor.getString(2),cursor.getString(3),
                        cursor.getInt(4), bitmap);
                temp.setId(cursor.getInt(0));
                list.add(temp);
            }while (cursor.moveToNext());

        }

        return list;
    }

    public void updateComment(long id, int likeCount){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("likeCount", likeCount);

        String whereClause = "id = ?";
        String [] whereArgs = {String.valueOf(id)};
        db.update(COMMENT_TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    public int deleteComment(long id){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereAgrs = {String.valueOf(id)};
        int row = db.delete(COMMENT_TABLE_NAME,whereClause,whereAgrs);
        db.close();
        return row;
    }

    public long insertLike(String username, long commentId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("commentId", commentId);
        long rid = db.insert(LIKE_TABLE_NAME,null,values);
        db.close();
        return rid;
    }

    public int deleteLike(String username, long commentId){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username = ? and commentId = ?";
        String[] whereAgrs = {username, String.valueOf(commentId)};
        int row = db.delete(LIKE_TABLE_NAME, whereClause, whereAgrs);
        db.close();
        return row;
    }

    Boolean isLiked(String username, long commentId){
        SQLiteDatabase db = getReadableDatabase();
        String selection = "username = ? and commentId = ?";
        String [] selectionArgs = {username, String.valueOf(commentId)};
        Cursor cursor = db.query(LIKE_TABLE_NAME,null,selection,selectionArgs,null,null,null);
        return cursor.moveToFirst();
    }
}
