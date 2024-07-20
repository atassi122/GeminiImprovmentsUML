package com.example.a01_app;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    public static List<HistoryItem> loadHistoryItems(Context context) {
        List<HistoryItem> items = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput("history.json");
            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            fis.close();
            items = new Gson().fromJson(stringBuilder.toString(), new TypeToken<List<HistoryItem>>() {}.getType());
        } catch (FileNotFoundException e) {
            // Handle file not found (empty list)
        } catch (Exception e) {
            e.printStackTrace(); // Handle other errors gracefully
        }
        return items;
    }

    public static void saveHistoryItems(Context context, List<HistoryItem> items) {
        String serializedData = new Gson().toJson(items);
        try {
            FileOutputStream fos = context.openFileOutput("history.json", Context.MODE_PRIVATE);
            fos.write(serializedData.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle file write errors gracefully
        }
    }
}
