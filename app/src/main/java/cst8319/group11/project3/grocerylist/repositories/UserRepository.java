package cst8319.group11.project3.grocerylist.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cst8319.group11.project3.grocerylist.dao.UserDao;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.User;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private UserDao userDao;
    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    public long insert(User user) {
        Future<Long> future = AppDatabase.databaseWriteExecutor.submit(() -> {
            // Check if user already exists with same email
            User existingUser = userDao.getUserByEmail(user.getEmail());
            if (existingUser != null) {
                Log.w(TAG, "User with email " + user.getEmail() + " already exists");
                return -1L;
            }

            // Insert new user and return ID
            long id = userDao.insert(user);
            Log.d(TAG, "Inserted new user with ID: " + id);
            return id;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error inserting user", e);
            e.printStackTrace();
            return -1;
        }
    }

    public void update(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public User authenticate(String email, String password) {
        Future<User> future = AppDatabase.databaseWriteExecutor.submit(() -> {
            User user = userDao.authenticate(email, password);
            if (user != null) {
                Log.d(TAG, "User authenticated: " + user.getUsername() + " (ID: " + user.getId() + ")");
            } else {
                Log.w(TAG, "Authentication failed for email: " + email);
                // For debugging, check if user exists but password is wrong
                User existingUser = userDao.getUserByEmail(email);
                if (existingUser != null) {
                    Log.d(TAG, "User exists but password incorrect");
                } else {
                    Log.d(TAG, "No user found with email: " + email);
                }
            }
            return user;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error during authentication", e);
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getAllUsers() {
        Future<List<User>> future = AppDatabase.databaseWriteExecutor.submit(() -> {
            List<User> users = userDao.getAllUsers();
            Log.d(TAG, "Retrieved " + (users != null ? users.size() : 0) + " users");
            return users;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error getting all users", e);
            e.printStackTrace();
            return null;
        }
    }

    public int getUserCount() {
        Future<Integer> future = AppDatabase.databaseWriteExecutor.submit(() -> {
            int count = userDao.getUserCount();
            Log.d(TAG, "User count: " + count);
            return count;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error getting user count", e);
            e.printStackTrace();
            return 0;
        }
    }
}