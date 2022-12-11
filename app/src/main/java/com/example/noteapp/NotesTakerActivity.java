package com.example.noteapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.noteapp.Models.Notes;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class NotesTakerActivity extends AppCompatActivity {
    // layout
    EditText editText_otsikko, editText_notes;
    ImageView imageView_tallenna;
    ImageView imageView_camera;
    // note
    Notes notes;
    boolean poistaNote = false;
    // kamera
    String imageUrl = "";
    String[] permissions = new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        // layout
        imageView_tallenna = findViewById(R.id.imageView_tallenna);
        editText_otsikko = findViewById(R.id.editText_otsikko);
        editText_notes = findViewById(R.id.editText_notes);
        imageView_camera = findViewById(R.id.imageView_camera);

        // note
        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("poista_note");
            editText_otsikko.setText(notes.getOtsikko());
            editText_notes.setText(notes.getNotes());
            poistaNote = true;
            imageUrl = notes.getImageUrl();

            showImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // noten tallennus
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
            notes.setImageUrl(imageUrl); // jos kuvaa ei otettu, tämä on ""

            Intent intent = new Intent();
            intent.putExtra("note", notes);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        // kamera
        imageView_camera.setOnClickListener(view -> {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    // onko luvat ok
    private boolean allPermissionsGranted() {
        return ((ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED));
    }

    // pyydetään luvat
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Ei lupaa käyttää kameraa", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // start camera activity
    void startCamera() {
        // jos notella on jo kuva, korvataan se uudella kuvalla
        if (hasImage()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Korvataanko olemassa oleva kuva? Vanhaa kuvaa ei poisteta puhelimesta, mutta sitä ei voida enää palauttaa notelle.");
            builder1.setCancelable(true);

            // jos käyttäjä haluaa korvata kuvan
            builder1.setPositiveButton(
                    "Kyllä",
                    (dialog, id) -> {
                        LinearLayout linearLayout = findViewById(R.id.rootContainer);
                        linearLayout.removeViewAt(linearLayout.getChildCount() - 1);
                        notes.setImageUrl("");
                        imageUrl = ""; // vanhan kuvan sijainti pois
                        Intent intent = new Intent(this, CameraActivity.class);
                        activityResultLauncher.launch(intent);
                    });

            // peruutettu
            builder1.setNegativeButton(
                    "Ei",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
            Intent intent = new Intent(this, CameraActivity.class);
            activityResultLauncher.launch(intent);
        }
    }

    // camera activity result
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    try {
                        imageUrl = (String) data.getSerializableExtra("imageUrl");
                        Log.e("NWK", "otettu kuva " + imageUrl);
                        showImage(); // lisätään kuva jos sellainen löytyy
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    void showImage() {
        if (hasImage()) {
            if (fileExist()) {

                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setImageURI(Uri.parse(imageUrl));

                LinearLayout linearLayout = findViewById(R.id.rootContainer);
                linearLayout.addView(imageView);
            } else {
                imageUrl = "";
            }
        }
    }

    boolean hasImage() {
        return !Objects.equals(imageUrl, "");
    }

    boolean fileExist() {
        Log.e("NWK", "File exists " + imageUrl);
        File file = new File(imageUrl);
        Log.e("NWK", "is true " + file.exists());
        return file.exists();
    }
}