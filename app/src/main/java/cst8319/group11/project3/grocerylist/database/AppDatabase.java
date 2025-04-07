package cst8319.group11.project3.grocerylist.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import cst8319.group11.project3.grocerylist.dao.*;
import cst8319.group11.project3.grocerylist.models.*;

@Database(
        entities = {
                User.class,
                GroceryList.class,
                Item.class,
                Category.class,
                Settings.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract GroceryListDao groceryListDao();
    public abstract ItemDao itemDao();
    public abstract CategoryDao categoryDao();
    public abstract SettingsDao settingsDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "grocery.sqlite"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
