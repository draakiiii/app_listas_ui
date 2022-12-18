package com.example.new_list.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.new_list.R;
import com.example.new_list.model.Item;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
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

        private TextView title, description, date;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            date = (TextView) itemView.findViewById(R.id.tvDate);
        }

        public void bind(final Item item, final OnItemClickListener listener) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                LocalDate date1 = null;
                LocalDate date2 = null;
                description.setVisibility(View.VISIBLE);

                if (!item.dateStart.matches("") && item.dateEnd.matches("")) {
                    date1 = LocalDate.parse(item.dateStart, formatter);
                    date.setText(date1.format(formatter));
                }

                if (!item.dateEnd.matches("") && item.dateStart.matches("")) {
                    date2 = LocalDate.parse(item.dateEnd, formatter);
                    date.setText(date2.format(formatter));
                }
                if (!item.dateStart.matches("") && !item.dateEnd.matches("")) {
                    date1 = LocalDate.parse(item.dateStart, formatter);
                    date2 = LocalDate.parse(item.dateEnd, formatter);
                    date.setText(date1.format(formatter) + " - " + date2.format(formatter));
                }

                if (item.dateStart.matches("") && item.dateEnd.matches("")) date.setVisibility(View.GONE);
                else date.setVisibility(View.VISIBLE);

                title.setText(item.title);
                if (!item.description.matches("")) {
                    description.setText(item.description);
                    description.setVisibility(View.VISIBLE);
                }
                else description.setVisibility(View.GONE);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            } catch (Exception e) {
                System.out.println("!!!!!!!!!!! ERROR ITEM ADAPTER: " + e.getLocalizedMessage());
            }
        }
    }
}