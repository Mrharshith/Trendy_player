package com.example.trendyplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView usernameDisplay;
    private Button trendingSongsBtn, playlistsBtn, inDeviceBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the UI components
        usernameDisplay = findViewById(R.id.username_display);
        trendingSongsBtn = findViewById(R.id.trending_songs_btn);
        playlistsBtn = findViewById(R.id.playlists_btn);
        inDeviceBtn = findViewById(R.id.in_device_btn);
        logoutBtn = findViewById(R.id.logout_btn);

        // Retrieve user details from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "User");
        int userId = prefs.getInt("user_id", -1);

        // Remove domain part from email.
        // If the email contains an '@', extract the part before it; otherwise, preserve the whole email.
        String displayName = userEmail.contains("@") ? userEmail.substring(0, userEmail.indexOf("@")) : userEmail;
        usernameDisplay.setText("Welcome, " + displayName + "!");

        // Trending Songs: No extra information needed.
        trendingSongsBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, TrendingSongsActivity.class))
        );

        // Launch PlaylistActivity while passing the user id.
        playlistsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PlaylistActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Launch In-Device Songs Activity.
        inDeviceBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, InDeviceSongsActivity.class))
        );

        // Logout: clear SharedPreferences and return to LoginActivity.
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();  // Clear all user preferences including user id
            editor.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }
}
