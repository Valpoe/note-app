package com.example.noteapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class CameraActivity extends AppCompatActivity {
    ImageView takePicture;
    PreviewView viewFinder;
    // ImageCapture imageCapture = null;
    ExecutorService cameraExecutor;
    // private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";


    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        takePicture = findViewById(R.id.takePicture);
        viewFinder = findViewById(R.id.viewFinder);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        Log.e("NWK", "OnCreate");

        cameraProviderFuture.addListener(() -> {
            try {
                Log.e("NWK", "CameraProvider");
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        takePicture.setOnClickListener(view -> takePhoto());
    }

    void takePhoto() {

    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Log.e("NWK", "bindPreview");
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}