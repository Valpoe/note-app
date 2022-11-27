package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.noteapp.Models.Notes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editText_otsikko, editText_notes;
    ImageView imageView_tallenna;
    ImageView imageView_camera;
    Notes notes;
    boolean poistaNote = false;

    String[] permissions = new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        imageView_tallenna = findViewById(R.id.imageView_tallenna);
        editText_otsikko = findViewById(R.id.editText_otsikko);
        editText_notes = findViewById(R.id.editText_notes);
        imageView_camera = findViewById(R.id.imageView_camera);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("poista_note");
            editText_otsikko.setText(notes.getOtsikko());
            editText_notes.setText(notes.getNotes());
            poistaNote = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        imageView_tallenna.setOnClickListener(view -> {
            String otsikko = editText_otsikko.getText().toString();
            String description = editText_notes.getText().toString();

            if(description.isEmpty()){
                Toast.makeText(NotesTakerActivity.this, "Lisää jotain tekstiä!", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm a");
            Date date = new Date();

            if (!poistaNote){
                notes = new Notes();
            }

            notes.setOtsikko(otsikko);
            notes.setNotes(description);
            notes.setDate(formatter.format(date));

            Intent intent = new Intent();
            intent.putExtra("note", notes);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        imageView_camera.setOnClickListener(view -> {
            if (allPermissionsGranted()) {
                Log.e("NWK", "all ok");
                startCamera();
            } else {
                Log.e("NWK", "Permissions not okay");
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
                Log.e("NWK", "Permissions requested");
            }
        });
    }

    private boolean allPermissionsGranted() {
        Log.e("NWK", "Permissions");
        return ((ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("NWK", "startCamera");
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Log.e("NWK", "Permissions granted");
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    void startCamera() {
        Log.e("NWK", "starCamera function");
        Intent intent = new Intent(NotesTakerActivity.this, CameraActivity.class);
        NotesTakerActivity.this.startActivity(intent);
    }
}