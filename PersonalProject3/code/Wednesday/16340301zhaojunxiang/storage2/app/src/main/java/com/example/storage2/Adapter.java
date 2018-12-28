package com.example.storage2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter extends BaseAdapter {
    private List<CommentInfo> list;
    private Context context;
    private String username;
    private myDB db;

    public Adapter(Context _context, List<CommentInfo> _list, String _username, myDB _db){
        list = _list;
        context = _context;
        username = _username;
        db = _db;
    }

    public void refresh(List<CommentInfo> _list){
        list = _list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        if(list == null){
            return null;
        }
        return list.get(i);
    }

    private class ViewHolder {
        public ImageView photo;
        public TextView username;
        public TextView time;
        public TextView comment;
        public TextView likeCount;
        public ImageView likeBtn;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // 新声明一个View变量和ViewHoleder变量,ViewHolder类在下面定义。
        View convertView;
        final ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (view == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.likeCount = (TextView) convertView.findViewById(R.id.likeCount);
            viewHolder.likeBtn = (ImageView) convertView.findViewById(R.id.likeBtn);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            convertView = view;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 从viewHolder中取出对应的对象，然后赋值给他们
        viewHolder.username.setText(list.get(i).getUsername());
        viewHolder.time.setText(list.get(i).getTime());
        viewHolder.comment.setText(list.get(i).getComment());
        viewHolder.likeCount.setText(list.get(i).getLikeCount()+"");
        viewHolder.photo.setImageBitmap(list.get(i).getPhoto());

        if(db.isLiked(username, list.get(i).getId())){
            viewHolder.likeBtn.setImageResource(R.mipmap.red);
            viewHolder.likeBtn.setTag("like");
        }else{
            viewHolder.likeBtn.setImageResource(R.mipmap.white);
            viewHolder.likeBtn.setTag("dislike");
        }

        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String likeCount = viewHolder.likeCount.getText().toString();
                int count  = Integer.parseInt(likeCount);
                if (viewHolder.likeBtn.getTag().equals("like")){
                    viewHolder.likeBtn.setImageResource(R.mipmap.white);
                    viewHolder.likeBtn.setTag("dislike");
                    count--;
                    db.deleteLike(username, list.get(i).getId());
                }else{
                    viewHolder.likeBtn.setImageResource(R.mipmap.red);
                    viewHolder.likeBtn.setTag("like");
                    count++;
                    db.insertLike(username, list.get(i).getId());
                }
                db.updateComment(list.get(i).getId(), count);
                likeCount = String.valueOf(count);
                viewHolder.likeCount.setText(likeCount);
                list.get(i).setLikeCount(count);
            }
        });

        // 将这个处理好的view返回
        return convertView;
    }

    public interface onItemLikeListener {
        void onLikeClick(int i);
    }

    private onItemLikeListener mOnItemLikeListener;

    public void setOnItemLikeClickListener(onItemLikeListener mOnItemLikeListener) {
        this.mOnItemLikeListener = mOnItemLikeListener;
    }

}
