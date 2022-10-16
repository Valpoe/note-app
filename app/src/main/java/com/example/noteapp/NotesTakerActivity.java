package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.noteapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editText_otsikko, editText_notes;
    ImageView imageView_tallenna;
    Notes notes;
    boolean poistaNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        imageView_tallenna = findViewById(R.id.imageView_tallenna);
        editText_otsikko = findViewById(R.id.editText_otsikko);
        editText_notes = findViewById(R.id.editText_notes);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("poista_note");
            editText_otsikko.setText(notes.getOtsikko());
            editText_notes.setText(notes.getNotes());
            poistaNote = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        imageView_tallenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }
}