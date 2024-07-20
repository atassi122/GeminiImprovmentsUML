package com.example.a01_app;

import android.graphics.Bitmap;

public class ImageStorage {
    private static Bitmap bitmap;
    public static String path ;

    public static void setBitmap(Bitmap bitmap) {
        ImageStorage.bitmap = bitmap;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }



}
