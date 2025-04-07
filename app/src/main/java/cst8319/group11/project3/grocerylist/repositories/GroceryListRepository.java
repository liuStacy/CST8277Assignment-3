package cst8319.group11.project3.grocerylist.repositories;

import android.app.Application;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cst8319.group11.project3.grocerylist.dao.GroceryListDao;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.GroceryList;

public class GroceryListRepository {
    private GroceryListDao groceryListDao;

    public GroceryListRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        groceryListDao = db.groceryListDao();
    }

    public GroceryList createList(long userID, String listName) {
        GroceryList newList = new GroceryList((int) userID, listName, new Date());
        Future<Long> future = AppDatabase.databaseWriteExecutor.submit(() ->
                groceryListDao.insert(newList));
        try {
            long listID = future.get();
            newList.setListID((int) listID);
            return newList;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateList(GroceryList list) {
        AppDatabase.databaseWriteExecutor.execute(() -> groceryListDao.update(list));
    }

    public void deleteList(int listID) {
        AppDatabase.databaseWriteExecutor.execute(() -> groceryListDao.delete(listID));
    }

    public List<GroceryList> getListsForUser(int userID) {
        Future<List<GroceryList>> future = AppDatabase.databaseWriteExecutor.submit(() ->
                groceryListDao.getListsForUser(userID));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GroceryList getListById(int listID) {
        Future<GroceryList> future = AppDatabase.databaseWriteExecutor.submit(() ->
                groceryListDao.getListById(listID));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
