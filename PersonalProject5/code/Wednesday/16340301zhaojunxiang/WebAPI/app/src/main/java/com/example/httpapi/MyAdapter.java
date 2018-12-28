package com.example.httpapi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class MyAdapter<T> extends RecyclerView.Adapter<MyViewHolder>{
    private Context context;
    private int layoutId;
    private List<T> data;
    private OnItemClickListener onItemClickListener;

    public MyAdapter(Context _context, int _layoutId, List<T> _data) {
        this.context = _context;
        this.layoutId = _layoutId;
        this.data = _data;
    }

    public abstract void convert(MyViewHolder holder, T t);

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(context, parent, layoutId);
        return holder;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        convert(holder, data.get(position));
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
