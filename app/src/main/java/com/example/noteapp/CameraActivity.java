package com.example.noteapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.example.noteapp.databinding.ActivityCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    ImageView takePicture;
    PreviewView viewFinder;
    ImageCapture imageCapture;
    ExecutorService cameraExecutor;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    ActivityCameraBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        takePicture = findViewById(R.id.takePicture);
        viewFinder = findViewById(R.id.viewFinder);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                            this, cameraSelector, preview, imageCapture);
                } catch(Exception ex) {
                    // empty
                }
            } catch (ExecutionException | InterruptedException e) {
                // empty
            }
        }, ContextCompat.getMainExecutor(this));

        viewBinding.takePicture.setOnClickListener(view -> takePhoto());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private void takePhoto() {
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.UK).format(System.currentTimeMillis());
        String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + "NoteApp";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativeLocation);
        }

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture
                .OutputFileOptions.Builder(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                .build();

        imageCapture.takePicture(outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this, "Kuva otettu", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent(CameraActivity.this, NotesTakerActivity.class);
                        returnIntent.putExtra("imageUrl", String.valueOf(outputFileResults.getSavedUri()));
                        Log.e("NWK", String.valueOf(outputFileResults.getSavedUri()));
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // empty
                    }
                });
    }
}