package cst8319.group11.project3.grocerylist.repositories;

import android.app.Application;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cst8319.group11.project3.grocerylist.dao.ItemDao;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.Item;

public class ItemRepository {
    private ItemDao itemDao;

    public ItemRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        itemDao = db.itemDao();
    }

    public long addItem(Item item) {
        Future<Long> future = AppDatabase.databaseWriteExecutor.submit(() -> itemDao.insert(item));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void updateItem(Item item) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.update(item));
    }

    public void deleteItem(int itemID) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.delete(itemID));
    }

    public List<Item> getItemsForList(int listID) {
        Future<List<Item>> future = AppDatabase.databaseWriteExecutor.submit(() -> itemDao.getItemsForList(listID));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updatePurchaseStatus(int itemID, boolean purchased) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.updatePurchaseStatus(itemID, purchased));
    }

    public void updateItemDetails(int itemID, double price, int quantity, String brand) {
        AppDatabase.databaseWriteExecutor.execute(() -> itemDao.updateItemDetails(itemID, price, quantity, brand));
    }
}
