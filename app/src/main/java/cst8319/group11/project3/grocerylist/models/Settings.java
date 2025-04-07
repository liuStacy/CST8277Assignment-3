package cst8319.group11.project3.grocerylist.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userID",
                onDelete = ForeignKey.CASCADE))
public class Settings {
    @PrimaryKey
    private int userID;
    private boolean notificationsEnabled;
    private String theme;
    private boolean reminderEnabled;
    private int reminderTime;
    private boolean trackSpending;

    public Settings() {
    }

    public Settings(int userID, boolean notificationsEnabled, String theme) {
        this.userID = userID;
        this.notificationsEnabled = notificationsEnabled;
        this.theme = theme;
        this.reminderEnabled = false;
        this.reminderTime = 0;
        this.trackSpending = false;
    }

    // Full constructor
    public Settings(int userID, boolean notificationsEnabled, String theme,
                    boolean reminderEnabled, int reminderTime, boolean trackSpending) {
        this.userID = userID;
        this.notificationsEnabled = notificationsEnabled;
        this.theme = theme;
        this.reminderEnabled = reminderEnabled;
        this.reminderTime = reminderTime;
        this.trackSpending = trackSpending;
    }

    // Getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isTrackSpending() {
        return trackSpending;
    }

    public void setTrackSpending(boolean trackSpending) {
        this.trackSpending = trackSpending;
    }
}