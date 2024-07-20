package com.example.a01_app;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_IMAGE_PATH = "extra_image_path";
    public static final String Extra_bool_improve = "canBeImproved";


    public Bitmap bitmap = ImageStorage.getBitmap();
    public String path = ImageStorage.path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        TextView titleTextView = findViewById(R.id.detail_title_text_view);
        TextView dateTextView = findViewById(R.id.detail_date_text_view);
        TextView descriptionTextView = findViewById(R.id.detail_description_text_view);
        ImageView imageView = findViewById(R.id.detail_image_view);
        Button improve = findViewById(R.id.improve);

        // Get the data from the intent
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        boolean canBeimproved = getIntent().getBooleanExtra(HistoryDetailActivity.Extra_bool_improve, false);


        // Set the data to the views
        titleTextView.setText(title);
        dateTextView.setText(date);
        descriptionTextView.setText(description);

        improve.setOnClickListener(v -> ImproveAndSave());

        if(canBeimproved){
            improve.setVisibility(View.VISIBLE);
        }

        // Load image using Glide
        Glide.with(this)
                .load(imagePath)
                .into(imageView);
    }

    public void ImproveAndSave(){
        GeminiAPI api = new GeminiAPI();
        String question = "This is a Uml-Diagram-sketch/Uml Diagram that can be improved" +
                "First of all, tell what needs to be improved and general suggestions, and then " +
                "deliver the corresponding new improved Plant-UML." +
                "your response should be structured as following: " +
                "1.Your suggestion of improvments in Words " +
                "2.The Plant-UMl of the improvemnets"
                ;
        api.generateTextAsync(question, bitmap).thenAccept(result -> {

            if (result == null || result.isEmpty()) {
                showToast("Failed to generate text. Please try again.");
            } else {
               String title = "Improved"; // Modify this to get the actual title
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String imagePath = path ;

                HistoryItem newItem = new HistoryItem(title, date, result, imagePath,false);

                // Load existing history items
                List<HistoryItem> historyItems = FileUtils.loadHistoryItems(this);

                // Add new item to history
                historyItems.add(newItem);

                // Save updated history items
                FileUtils.saveHistoryItems(this, historyItems);

                showToast("Improvments Done!");
            }

        }).exceptionally(e -> {
            e.printStackTrace();
            showToast("Failed to generate text. Please try again.");
            return null;
        });
    }


    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }



}
