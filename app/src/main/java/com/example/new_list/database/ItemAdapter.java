package com.example.new_list.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.new_list.R;
import com.example.new_list.model.Item;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final ArrayList<Item> items;
    private final OnItemClickListener listener;

    public ItemAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_private, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
        }

        public void bind(final Item item, final OnItemClickListener listener) {
            title.setText(item.title);
            description.setText(item.description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}