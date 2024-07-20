package com.example.a01_app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView;
    public TextView dateTextView;
    public TextView descriptionTextView;
    public ImageView imageView; // Optional for image display

    public HistoryViewHolder(View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.title_text_view);
        dateTextView = itemView.findViewById(R.id.date_text_view);
        descriptionTextView = itemView.findViewById(R.id.description_text_view);
        imageView = itemView.findViewById(R.id.history_image_view); // Optional for image display
    }
}
