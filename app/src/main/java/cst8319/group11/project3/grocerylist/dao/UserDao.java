package cst8319.group11.project3.grocerylist.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cst8319.group11.project3.grocerylist.models.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User authenticate(String email, String password);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
}