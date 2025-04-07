package cst8319.group11.project3.grocerylist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.models.User;
import cst8319.group11.project3.grocerylist.repositories.UserRepository;

public class LoginActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private EditText editTextUsername;  // For login email
    private EditText editTextPassword;  // For login password
    private Button buttonLogin;         // Login button
    private Button buttonRegister;      // New Register button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize view components
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister); // Register button

        userRepository = new UserRepository(getApplication());

        // Login button click event
        buttonLogin.setOnClickListener(v -> {
            String email = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email/Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Debug output to verify input
            Toast.makeText(this, "Attempting login with: " + email, Toast.LENGTH_SHORT).show();

            new Thread(() -> {
                User user = userRepository.authenticate(email, password);
                runOnUiThread(() -> {
                    if (user != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USER_ID", user.getId());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "User not found or invalid credentials", Toast.LENGTH_SHORT).show();
                        // Debug output to check if any users exist
                        checkUserDatabase();
                    }
                });
            }).start();
        });

        // Register button click event: display a dialog to collect registration info
        buttonRegister.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Register");

            // Load custom dialog layout
            View view = getLayoutInflater().inflate(R.layout.dialog_register, null);
            EditText editTextName = view.findViewById(R.id.editTextName);
            EditText editTextEmail = view.findViewById(R.id.editTextEmail);
            EditText editTextPasswordReg = view.findViewById(R.id.editTextPasswordReg);
            builder.setView(view);

            builder.setPositiveButton("Register", (dialog, which) -> {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPasswordReg.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user with default role "USER"
                User newUser = new User(name, email, password, "USER");

                new Thread(() -> {
                    long id = userRepository.insert(newUser);
                    runOnUiThread(() -> {
                        if (id != -1) {
                            Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            // Auto-fill the login fields with the newly registered email and password
                            editTextUsername.setText(email);
                            editTextPassword.setText(password);

                            // Show debug info
                            Toast.makeText(LoginActivity.this, "User ID " + id + " created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Registration failed, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    // Helper method to check if users exist in the database for debugging
    private void checkUserDatabase() {
        new Thread(() -> {
            int userCount = userRepository.getUserCount();
            runOnUiThread(() -> {
                Toast.makeText(LoginActivity.this, "Database has " + userCount + " users", Toast.LENGTH_LONG).show();
            });
        }).start();
    }
}