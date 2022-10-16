package com.example.noteapp.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.noteapp.Models.Notes;

import java.util.List;

@Dao
public interface MainDAO {
    @Insert(onConflict = REPLACE)
    void insert(Notes notes);

    // Listaa kaikki notet
    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Notes> getAll();

    // Päivittää noten ID:n perusteella
    @Query("UPDATE notes SET otsikko = :otsikko, notes = :notes WHERE ID = :id")
    void update(int id, String otsikko, String notes);

    // Poistaa noten
    @Delete
    void delete(Notes notes);

    // Asettaa pinnin noteen
    @Query("UPDATE notes SET pinned = :pin WHERE ID = :id")
    void pin(int id, boolean pin);
}
