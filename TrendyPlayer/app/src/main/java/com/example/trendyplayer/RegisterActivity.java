package com.example.trendyplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, usernameInput, passwordInput;
    private Button registerButton;
    private UnifiedDatabaseHelper userDatabaseHelper;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.email);
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);

        userDatabaseHelper = new UnifiedDatabaseHelper(this);

        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userDatabaseHelper.checkUserExists(email)) {
                Toast.makeText(RegisterActivity.this, "Email already registered!", Toast.LENGTH_SHORT).show();
            } else {
                boolean isAdded = userDatabaseHelper.addUser(email, password);
                Log.d(TAG, "User registration status: " + isAdded);
                if (isAdded) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

//the above code is working correctly for diff users but unable to add songs to playlist