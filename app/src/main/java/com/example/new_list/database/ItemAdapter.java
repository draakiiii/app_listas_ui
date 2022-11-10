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

                if (!item.dateStart.matches("") && item.dateEnd.matches("")) {
                    date1 = LocalDate.parse(item.dateStart, formatter);
                    Month date1Month = date1.getMonth().plus(1);
                    int date1Day = date1.getDayOfMonth();
                    int date1Year = date1.getYear();
                    LocalDate date1Format = LocalDate.of(date1Year,date1Month,date1Day);
                    date.setText(date1Format.format(formatter));
                }
                if (!item.dateEnd.matches("") && item.dateStart.matches("")) {
                    date2 = LocalDate.parse(item.dateEnd, formatter);
                    Month date2Month = date2.getMonth().plus(1);
                    int date2Day = date2.getDayOfMonth();
                    int date2Year = date2.getYear();
                    LocalDate date2Format = LocalDate.of(date2Year,date2Month,date2Day);
                    date.setText(date2Format.format(formatter));
                }
                if (!item.dateStart.matches("") && !item.dateEnd.matches("")) {
                    date1 = LocalDate.parse(item.dateStart, formatter);
                    Month date1Month = date1.getMonth().plus(1);
                    int date1Day = date1.getDayOfMonth();
                    int date1Year = date1.getYear();
                    LocalDate date1Format = LocalDate.of(date1Year,date1Month,date1Day);
                    date2 = LocalDate.parse(item.dateEnd, formatter);
                    Month date2Month = date2.getMonth().plus(1);
                    int date2Day = date2.getDayOfMonth();
                    int date2Year = date2.getYear();
                    LocalDate date2Format = LocalDate.of(date2Year,date2Month,date2Day);
                    date.setText(date1Format.format(formatter) + " - " + date2Format.format(formatter));
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
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}