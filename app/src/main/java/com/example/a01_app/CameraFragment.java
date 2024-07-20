package com.example.a01_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    private String response ;
    private PreviewView previewView;
    private ImageView imgPhoto;
    private Bitmap selectedBitmap;
    private Button btnTakePhoto;
    private ImageCapture imageCapture;
    private Camera camera;
    private ImageButton btnFlipCamera;
    private CameraSelector cameraSelector;
    private Button btnAnalyseCamera;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private boolean improves ;
    private Uri selectedImageUri;


    private TextView apiTextView;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        previewView = view.findViewById(R.id.previewView);
        imgPhoto = view.findViewById(R.id.imgPhoto);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnFlipCamera = view.findViewById(R.id.btnFlipCamera);
        btnAnalyseCamera = view.findViewById(R.id.btnAnalyseCamera);
        btnTakePhoto.setOnClickListener(v -> takePhoto());
        btnFlipCamera.setOnClickListener(v -> flipCamera());
        btnAnalyseCamera.setOnClickListener(v -> analyseAndSave());

        startCamera(CameraSelector.DEFAULT_BACK_CAMERA);

        return view;
    }

    private void startCamera(CameraSelector selector) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, selector);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation) here.
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, CameraSelector selector) {
        Preview preview = new Preview.Builder().build();

        imageCapture = new ImageCapture.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(this, selector, preview, imageCapture);
            cameraSelector = selector; // Update the current camera selector
        } catch (Exception e) {
            Log.e("CameraFragment", "Use case binding failed", e);
        }
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        File photoFile = new File(requireContext().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(new ImageCapture.Metadata())
                .build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(requireContext(), "Photo captured successfully", Toast.LENGTH_SHORT).show();
                        previewView.setVisibility(View.GONE);
                        imgPhoto.setVisibility(View.VISIBLE);
                        displayImage(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(requireContext(), "Failed to capture photo", Toast.LENGTH_SHORT).show();
                        Log.e("CameraFragment", "Photo capture failed: " + exception.getMessage(), exception);
                    }
                }
        );
    }

    private void displayImage(File photoFile) {
        try {
            ExifInterface exifInterface = new ExifInterface(photoFile);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            FileInputStream fis = new FileInputStream(photoFile);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }

            selectedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imgPhoto.setImageBitmap(selectedBitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analyseAndSave() {


        if (selectedBitmap != null) {

            // ---------------- Just mock code for now -------------------- //

           /* String title = "Sample Title"; // Modify this to get the actual title
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String imagePath = getImageUri(requireContext(), selectedBitmap).toString();

            HistoryItem newItem = new HistoryItem(title, date, "Result of the API comes here!", imagePath);

            // Load existing history items
            List<HistoryItem> historyItems = FileUtils.loadHistoryItems(requireContext());

            // Add new item to history
            historyItems.add(newItem);

            // Save updated history items
            FileUtils.saveHistoryItems(requireContext(), historyItems);

            showToast("Analysis and save completed!");
            // ---------------- Just mock code for now -------------------- // */
            storeBitmap(); //store the bitmap to then get it in the DetailActivity for improvments
            ImageStorage.path =  getImageUri(requireContext(), selectedBitmap).toString();;


            GeminiAPI api = new GeminiAPI();
            String question = "so here is a photo,it should be a uml-diagram sketch, if it is not,just say that its not,otherwise.\n" +
                    "i need for your answer to be as following with no additional infos, the structure should be as following\n" +
                    "\n" +
                    " i want the plant uml of this diagram\n" +
                    "\n"
                   ;
            String improvmentsAvailable = "So here is a Uml-Diagram or a Uml diagram sketch" +
                    "make a deep analysis and understand it.Depending on that, i want you " +
                    "to just tell me if this Uml-Diagram can be further improved or not " +
                    "if yes, just answer with true, and if not, just answer with false" +
                    "remember, your response should consist of only one word, either true or false";
            api.generateTextAsync(improvmentsAvailable, selectedBitmap).thenAccept(result -> {

                if (result == null || result.isEmpty()) {
                    showToast("Failed to generate text. Please try again.");
                }
                else improves = Boolean.parseBoolean(improvmentsAvailable);


            });
            api.generateTextAsync(question, selectedBitmap).thenAccept(result -> {
                // Handle the generated text result

                if (result == null || result.isEmpty()) {
                    showToast("Failed to generate text. Please try again.");
                } else {
                    response = result ;
                    String title = "Sample Title"; // Modify this to get the actual title
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String imagePath = getImageUri(requireContext(), selectedBitmap).toString();

                    HistoryItem newItem = new HistoryItem(title, date, result, imagePath,improves);

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
            //-------------- Here comes the real code instead of the above snippet -------------- //








        } else {
            showToast("No Photo has been taken!");
        }
    }


    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, generateUniqueFilename(), null);
        return Uri.parse(path);
    }

    private String generateUniqueFilename() {
        return "IMG_" + System.currentTimeMillis() + ".jpg";
    }



    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(cameraSelector);
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }

    private void flipCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            startCamera(CameraSelector.DEFAULT_FRONT_CAMERA);
        } else {
            startCamera(CameraSelector.DEFAULT_BACK_CAMERA);
        }
    }

    public void storeBitmap(){
        if(selectedBitmap!=null){
            ImageStorage.setBitmap(selectedBitmap);

        }
    }




}
