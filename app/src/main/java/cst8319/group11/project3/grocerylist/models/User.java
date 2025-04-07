package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")  // Important: Specify table name to match SQL queries
public class User {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String username;
    private String email;
    private String password;
    private String role;

    public User() {}

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean verifyPassword(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

}
