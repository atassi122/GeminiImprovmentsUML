package com.example.a01_app;

import android.graphics.Bitmap;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiAPI {

    private GenerativeModelFutures model;

    public GeminiAPI() {
        // Initialize the generative model
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDudh-xlePFz6blHTZncqeKpQ8SubRyQ00");
        model = GenerativeModelFutures.from(gm);
    }

    public CompletableFuture<String> generateTextAsync(String question, Bitmap image) {
        // Create content with text and image
        Content content = new Content.Builder()
                .addText(question)
                .addImage(image)
                .build();

        // Executor for handling asynchronous tasks
        Executor executor = Executors.newSingleThreadExecutor();

        // Generate content
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // CompletableFuture to hold the result
        CompletableFuture<String> future = new CompletableFuture<>();

        // Run the task on the background executor
        executor.execute(() -> {
            try {
                // Blocking call to get the result
                GenerateContentResponse result = response.get();
                String resultText = result.getText();
                System.out.println(resultText);
                future.complete(resultText); // Complete the future with the result
            } catch (Exception e) {
                System.err.println("Exception while generating content: " + e.getMessage());
                future.completeExceptionally(e); // Complete the future exceptionally if there's an error
            }
        });

        return future;
    }
}
