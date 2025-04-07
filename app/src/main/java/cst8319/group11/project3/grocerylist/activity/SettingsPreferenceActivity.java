package cst8319.group11.project3.grocerylist.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.dao.SettingsDao;
import cst8319.group11.project3.grocerylist.database.AppDatabase;
import cst8319.group11.project3.grocerylist.models.Settings;

public class SettingsPreferenceActivity extends AppCompatActivity {

    private RadioButton radioLightTheme;
    private RadioButton radioDarkTheme;
    private Switch switchNotifications;
    private RadioButton radioSpendingTrackerOn;
    private RadioButton radioSpendingTrackerOff;
    private Button buttonSaveSettings;
    private SettingsDao settingsDao;
    private int currentUserId = -1; // 从登录信息或 Intent 获取
    private Settings userSettings;
    private RadioGroup themeRadioGroup;
    private RadioGroup spendingTrackerRadioGroup;
    private boolean isSettingsLoaded = false;

    // 使用 SharedPreferences 保存主题
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在设置布局前，根据 SharedPreferences 设置主题
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme_preference", "light");
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_preference);

        // 启用 ActionBar 的返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editor = sharedPreferences.edit();
        settingsDao = AppDatabase.getDatabase(getApplicationContext()).settingsDao();

        // 从 Intent 获取用户ID
        currentUserId = (int)getIntent().getLongExtra("USER_ID", -1);

        radioLightTheme = findViewById(R.id.radioLightTheme);
        radioDarkTheme = findViewById(R.id.radioDarkTheme);
        switchNotifications = findViewById(R.id.switchNotifications);
        radioSpendingTrackerOn = findViewById(R.id.radioSpendingTrackerOn);
        radioSpendingTrackerOff = findViewById(R.id.radioSpendingTrackerOff);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        spendingTrackerRadioGroup = findViewById(R.id.spendingTrackerRadioGroup);

        // 根据 SharedPreferences 设置主题选项
        if ("dark".equalsIgnoreCase(theme)) {
            radioDarkTheme.setChecked(true);
            radioLightTheme.setChecked(false);
        } else {
            radioLightTheme.setChecked(true);
            radioDarkTheme.setChecked(false);
        }

        // 加载其他设置（例如通知和花费追踪功能）使用数据库
        loadSettings();

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (isSettingsLoaded) {
                if (checkedId == R.id.radioLightTheme) {
                    Log.d("SettingsActivity", "Light Theme selected");
                } else if (checkedId == R.id.radioDarkTheme) {
                    Log.d("SettingsActivity", "Dark Theme selected");
                }
            }
        });

        spendingTrackerRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (isSettingsLoaded) {
                if (checkedId == R.id.radioSpendingTrackerOn) {
                    Log.d("SettingsActivity", "Spending Tracker On selected");
                } else if (checkedId == R.id.radioSpendingTrackerOff) {
                    Log.d("SettingsActivity", "Spending Tracker Off selected");
                }
            }
        });

        buttonSaveSettings.setOnClickListener(view -> {
            saveSettings();
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
            // 保存后刷新界面以应用新主题
            recreate();
        });
    }

    private void loadSettings() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userSettings = settingsDao.getSettingsForUser(currentUserId);
            runOnUiThread(() -> {
                if (userSettings != null) {
                    switchNotifications.setChecked(userSettings.isNotificationsEnabled());
                    if (userSettings.isTrackSpending()) {
                        radioSpendingTrackerOn.setChecked(true);
                        radioSpendingTrackerOff.setChecked(false);
                    } else {
                        radioSpendingTrackerOff.setChecked(true);
                        radioSpendingTrackerOn.setChecked(false);
                    }
                } else {
                    // 初始化默认设置（主题默认采用 SharedPreferences 的值）
                    userSettings = new Settings(currentUserId, false, "light", false, 0, false);
                    switchNotifications.setChecked(false);
                    radioSpendingTrackerOff.setChecked(true);
                    radioSpendingTrackerOn.setChecked(false);
                }
                isSettingsLoaded = true;
            });
        });
    }

    private void saveSettings() {
        boolean notificationsEnabled = switchNotifications.isChecked();
        // 根据选中项确定主题
        String theme = radioDarkTheme.isChecked() ? "dark" : "light";
        boolean trackSpending = radioSpendingTrackerOn.isChecked();

        // 保存主题到 SharedPreferences
        editor.putString("theme_preference", theme);
        editor.apply();

        // 保存其他设置到数据库
        if (userSettings == null) {
            userSettings = new Settings(currentUserId, notificationsEnabled, theme, false, 0, trackSpending);
        } else {
            userSettings.setNotificationsEnabled(notificationsEnabled);
            userSettings.setTheme(theme);
            userSettings.setTrackSpending(trackSpending);
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            settingsDao.insertOrUpdate(userSettings);
        });

        // 立即应用主题更改
        if ("dark".equalsIgnoreCase(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // 处理 ActionBar 返回按钮点击事件，返回主页
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SettingsPreferenceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
