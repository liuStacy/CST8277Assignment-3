package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cst8319.group11.project3.grocerylist.models.Category;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
}