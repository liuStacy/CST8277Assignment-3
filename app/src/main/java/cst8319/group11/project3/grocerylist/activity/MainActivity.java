package cst8319.group11.project3.grocerylist.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.preference.PreferenceManager;

import cst8319.group11.project3.grocerylist.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonListManagement;
    private Button buttonShoppingMode;
    private Button buttonSettings;
    private long currentUserId; // 不再硬编码
    private Button buttonLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load theme from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_preference", "light");

        // Apply theme
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // 关键修改点2：正确初始化成员变量（去掉前面的Button声明）
        buttonListManagement = findViewById(R.id.buttonListManagement);
        buttonShoppingMode = findViewById(R.id.buttonShoppingMode);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonLogout = findViewById(R.id.buttonLogout);

        // 关键修改点3：从Intent获取真实用户ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID")) {
            currentUserId = intent.getLongExtra("USER_ID", -1);
        }

        if (currentUserId == -1) {
            // 处理用户未登录的情况（例如返回登录界面）
            finish();
            return;
        }

        // 关键修改点4：统一使用全大写键名 "USER_ID"
        buttonListManagement.setOnClickListener(v -> {
            Intent listIntent = new Intent(MainActivity.this, ListManagementActivity.class);
            listIntent.putExtra("USER_ID", currentUserId); // 键名与LoginActivity一致
            startActivity(listIntent);
        });

        buttonShoppingMode.setOnClickListener(v -> {
            // 需要从ListManagementActivity获取真实listID
            // 此处仅为演示，建议在ListManagementActivity中跳转时传递listID
            Intent shoppingIntent = new Intent(MainActivity.this, ShoppingModeActivity.class);
            shoppingIntent.putExtra("LIST_ID", 123); // 使用明确的键名
            startActivity(shoppingIntent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsPreferenceActivity.class);
            settingsIntent.putExtra("USER_ID", currentUserId);
            startActivity(settingsIntent);
        });

        buttonLogout.setOnClickListener(v -> {
            // Optional: Clear SharedPreferences if you store login data
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.clear(); // Or just remove specific login keys
            editor.apply();

            // Return to login activity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
            startActivity(loginIntent);
            finish(); // Finish MainActivity
        });
    }
}