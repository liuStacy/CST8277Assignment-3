package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "items",
        foreignKeys = @ForeignKey(entity = GroceryList.class,
                parentColumns = "listID",
                childColumns = "listID",
                onDelete = ForeignKey.CASCADE))
public class Item {
    @PrimaryKey(autoGenerate = true)
    private int itemID;
    private int listID;
    private String itemName;
    private String brand;
    private double price;
    private boolean purchased;
    private int quantity;
    private int categoryID;

    public Item() {
    }

    public Item(int listID, String itemName, String brand, double price, boolean purchased, int quantity, int categoryID) {
        this.listID = listID;
        this.itemName = itemName;
        this.brand = brand;
        this.price = price;
        this.purchased = purchased;
        this.quantity = quantity;
        this.categoryID = categoryID;
    }

    public Item(int itemID, int listID, String itemName, String brand, double price, boolean purchased, int quantity, int categoryID) {
        this.itemID = itemID;
        this.listID = listID;
        this.itemName = itemName;
        this.brand = brand;
        this.price = price;
        this.purchased = purchased;
        this.quantity = quantity;
        this.categoryID = categoryID;
    }

    // Methods
    public void togglePurchased() {
        this.purchased = !this.purchased;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    // Getters and setters
    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }



}