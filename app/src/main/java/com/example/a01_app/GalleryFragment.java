package com.example.a01_app;

import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GalleryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection
    private ImageView selectedImage;
    private Button selectPhotoButton;
    private Button analyseButton;
    private Uri selectedImageUri;
    private Bitmap selectedBitmap;
    private boolean improves = true  ; ;
    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        selectedImage = view.findViewById(R.id.selected_image);
        selectPhotoButton = view.findViewById(R.id.select_photo);
        analyseButton = view.findViewById(R.id.btnAnalyseGallery);


        selectPhotoButton.setOnClickListener(v -> openGallery());

        analyseButton.setOnClickListener(v -> analyseAndSave());


        // Inflate the layout for this fragment
        return view;
    }



    private void analyseAndSave() {
        if (selectedImageUri != null && selectedBitmap != null) {


            storeBitmap(); //store the bitmap to then get it in the DetailActivity for improvments
            ImageStorage.path = selectedImageUri.toString() ; // store the path too for displaying




            // ---------------- Just mock code for now -------------------- //
           /* String title = "Sample Title"; // Modify this to get the actual title
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String imagePath = selectedImageUri.toString();

            HistoryItem newItem = new HistoryItem(title, date, "Result of the API comes here!", imagePath);

            // Load existing history items
            List<HistoryItem> historyItems = FileUtils.loadHistoryItems(requireContext());

            // Add new item to history
            historyItems.add(newItem);

            // Save updated history items
            FileUtils.saveHistoryItems(requireContext(), historyItems);

            showToast("Analysis and save completed!");
            // ---------------- Just mock code for now -------------------- // */



            //-------------- Here comes the real code instead of the above snippet -------------- //
            GeminiAPI api = new GeminiAPI();
            // Replace "What is this image?" with your question
            String question = "so here is a photo,it should be a uml-diagram sketch, if it is not,just say that its not,otherwise.\n" +
                    "i need for your answer to be as following with no additional infos, the structure should be as following\n" +
                    "\n" +
                    " i want the plant uml of this diagram\n" +
                    "\n"
                    ;

//
//            String improvmentsAvailable = "So here is a Uml-Diagram or a Uml diagram sketch" +
//                   "make a deep analysis and understand it.Depending on that, i want you " +
//                 "to just tell me if this Uml-Diagram can be further improved or not " +
//                  "if yes, just answer with true, and if not, just answer with false" +
//                   "remember, your response should consist of only one word, either true or false";
//           api.generateTextAsync(improvmentsAvailable, selectedBitmap).thenAccept(result -> {
//               if (result == null || result.isEmpty()) {
//                    showToast("Failed to generate text. Please try again.");
//              }
//               else improves = Boolean.parseBoolean(improvmentsAvailable);
//
//
//           });


                api.generateTextAsync(question, selectedBitmap).thenAccept(result -> {
                // Handle the generated text result
                if (result == null || result.isEmpty()) {
                    showToast("Failed to generate text. Please try again.");
                } else {
                    String title = "Sample Title"; // Modify this to get the actual title
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String imagePath = selectedImageUri.toString();

                    HistoryItem newItem = new HistoryItem(title, date, improves+result, imagePath,improves);

                    // Load existing history items
                    List<HistoryItem> historyItems = FileUtils.loadHistoryItems(requireContext());

                    // Add new item to history
                    historyItems.add(newItem);

                    // Save updated history items
                    FileUtils.saveHistoryItems(requireContext(), historyItems);

                    showToast("Analysis and save completed!");
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                showToast("Failed to generate text. Please try again.");
                return null;
            });



        } else {
            showToast("No Image has been selected!");
        }
    }


    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the title for the fragment
        if (getActivity() != null) {
            getActivity().setTitle("Gallery");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                selectedImage.setImageURI(selectedImageUri);
                selectedImage.setVisibility(View.VISIBLE); // Show image after selection

                // Load the Bitmap from the URI
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Failed to load image.");
                }
            }
        }
    }
    public void storeBitmap(){
        if(selectedBitmap!=null){
            ImageStorage.setBitmap(selectedBitmap);

        }
    }

}
