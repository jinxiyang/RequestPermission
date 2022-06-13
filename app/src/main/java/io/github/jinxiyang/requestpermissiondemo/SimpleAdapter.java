package io.github.jinxiyang.requestpermissiondemo;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleVH> {

    private int itemWidth;
    private int itemHeight;
    private int count;

    private List<String> list;

    private OnClickItemListener onClickItemListener;

    public SimpleAdapter(int itemWidth, int itemHeight, int count) {
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.count = count;

        list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(String.valueOf(i));
        }
    }

    public SimpleAdapter(List<String> list) {
        this.itemWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        this.itemHeight = 240;
        this.list = list;
        this.count = list.size();
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    @NonNull
    @Override
    public SimpleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Button button = new Button(parent.getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(itemWidth, itemHeight);
        button.setLayoutParams(params);
        button.setTextSize(14f);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(Color.BLACK);
        return new SimpleVH(button);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleVH holder, int position) {
        holder.button.setText(list.get(position));
        final int p = position;
        holder.itemView.setOnClickListener(v -> {
            if (onClickItemListener != null) {
                onClickItemListener.onClickItem(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    static class SimpleVH extends RecyclerView.ViewHolder{
        Button button;

        public SimpleVH(@NonNull View itemView) {
            super(itemView);
            button = (Button) itemView;
        }
    }

    public interface OnClickItemListener {
        void onClickItem(int position);
    }

}
