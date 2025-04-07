package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cst8319.group11.project3.grocerylist.models.GroceryList;

@Dao
public interface GroceryListDao {
    @Insert
    long insert(GroceryList groceryList);

    @Update
    void update(GroceryList groceryList);

    @Query("DELETE FROM grocery_lists WHERE listID = :listID")
    void delete(int listID);

    @Query("SELECT * FROM grocery_lists WHERE userID = :userID")
    List<GroceryList> getListsForUser(int userID);

    @Query("SELECT * FROM grocery_lists WHERE listID = :listID")
    GroceryList getListById(int listID);
}
