package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import cst8319.group11.project3.grocerylist.database.DateConverter;

@Entity(tableName = "grocery_lists",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userID",
                onDelete = ForeignKey.CASCADE))
@TypeConverters(DateConverter.class)
public class GroceryList {
    @PrimaryKey(autoGenerate = true)
    private int listID;
    private int userID;
    private String listName;
    private Date creationDate;

    public GroceryList() {
    }

    public GroceryList(int userID, String listName, Date creationDate) {
        this.userID = userID;
        this.listName = listName;
        this.creationDate = creationDate;
    }

    public GroceryList(int listID, int userID, String listName, Date creationDate) {
        this.listID = listID;
        this.userID = userID;
        this.listName = listName;
        this.creationDate = creationDate;
    }

    // Getters and setters
    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}