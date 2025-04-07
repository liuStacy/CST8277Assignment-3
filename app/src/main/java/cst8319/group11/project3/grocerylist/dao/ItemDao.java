package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cst8319.group11.project3.grocerylist.models.Item;

@Dao
public interface ItemDao {
    @Insert
    long insert(Item item);

    @Update
    void update(Item item);

    @Query("DELETE FROM items WHERE itemID = :itemID")
    void delete(int itemID);

    @Query("SELECT * FROM items WHERE listID = :listID")
    List<Item> getItemsForList(int listID);

    @Query("UPDATE items SET purchased = :purchased WHERE itemID = :itemID")
    void updatePurchaseStatus(int itemID, boolean purchased);

    // Update price, quantity, and brand if needed
    @Query("UPDATE items SET price = :price, quantity = :quantity, brand = :brand WHERE itemID = :itemID")
    void updateItemDetails(int itemID, double price, int quantity, String brand);
}
