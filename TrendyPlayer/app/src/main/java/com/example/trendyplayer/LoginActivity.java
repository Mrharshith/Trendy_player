package com.example.trendyplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private UnifiedDatabaseHelper dbHelper;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        dbHelper = new UnifiedDatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all details!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                User user = dbHelper.getUser(email);
                if (user != null && user.getPassword().equals(password)) {
                    Log.d(TAG, "Login successful for user: " + email + ", userID: " + user.getId());

                    // Save user info in SharedPreferences.
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", user.getId());
                    editor.putString("user_email", user.getEmail());
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Launch HomeActivity with the user_id.
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("user_id", user.getId());
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, "Invalid credentials for user: " + email);
                    Toast.makeText(LoginActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during login", e);
                Toast.makeText(LoginActivity.this, "Login failed due to an error!", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }
}

