package com.example.a01_app;

import android.graphics.Bitmap;

public class HistoryItem {
    private String title;
    private String date;
    private String description;
    private String imagePath; // Path to the image file (optional)
    private boolean canBeImproved ;

    public HistoryItem(String title, String date, String description, String imagePath,boolean ifImprove) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imagePath = imagePath;
        this.canBeImproved = ifImprove ;


    }

    public String getTitle() {
        return this.title;
    }

    public String getDate() {    return this.date;    }

    public String getDescription() {     return this.description;    }

    public String getImagePath() {
        return this.imagePath;
    }


    public boolean improves(){
        return this.canBeImproved;
    }


}
