package com.example.noteapp.Database;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.noteapp.Models.Images;
import java.util.List;

@Dao
public interface ImagesDAO {
    @Insert(onConflict = REPLACE)
    void insert(Images images);

    @Query("SELECT * FROM images")
    List<Images> getAllImages();

    @Query("SELECT * FROM images WHERE noteId = :noteId")
    List<Images> getImagesById(int noteId);

    @Delete
    void delete(Images images);
}
