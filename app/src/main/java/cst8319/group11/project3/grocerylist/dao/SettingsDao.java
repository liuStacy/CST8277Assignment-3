package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import cst8319.group11.project3.grocerylist.models.Settings;

@Dao
public interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Settings settings);

    @Query("SELECT * FROM settings WHERE userID = :userID")
    Settings getSettingsForUser(int userID);
}