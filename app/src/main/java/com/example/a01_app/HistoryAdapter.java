package com.example.a01_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private List<HistoryItem> historyItems;
    private final OnItemClickListener listener;

    public HistoryAdapter(List<HistoryItem> historyItems, OnItemClickListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.dateTextView.setText(item.getDate());
        holder.descriptionTextView.setText(item.getDescription());



        // Load image from path using Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getImagePath())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), HistoryDetailActivity.class);
            intent.putExtra(HistoryDetailActivity.EXTRA_TITLE, item.getTitle());
            intent.putExtra(HistoryDetailActivity.EXTRA_DATE, item.getDate());
            intent.putExtra(HistoryDetailActivity.EXTRA_DESCRIPTION, item.getDescription());
            intent.putExtra(HistoryDetailActivity.EXTRA_IMAGE_PATH, item.getImagePath());
            intent.putExtra(HistoryDetailActivity.Extra_bool_improve, item.improves());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public List<HistoryItem> getHistoryItems() {
        return historyItems;
    }

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public void updateHistoryItems(List<HistoryItem> newHistoryItems) {
        this.historyItems = newHistoryItems;
        notifyDataSetChanged();
    }
}
