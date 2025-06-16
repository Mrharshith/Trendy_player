


package com.example.trendyplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Add this import statement

import android.widget.Toast;

public class InDeviceSongsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> deviceSongs;

    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_device_songs);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize song list
        deviceSongs = new ArrayList<>();

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadDeviceSongs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadDeviceSongs();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadDeviceSongs() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA // File path
        };

        Cursor cursor = contentResolver.query(songUri, projection, null, null, null);

        if (cursor != null) {
            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            if (cursor.moveToFirst()) {
                do {
                    String title = titleIndex != -1 ? cursor.getString(titleIndex) : "Unknown Title";
                    String artist = artistIndex != -1 ? cursor.getString(artistIndex) : "Unknown Artist";
                    String url = dataIndex != -1 ? cursor.getString(dataIndex) : "Unknown URL"; // File path

                    deviceSongs.add(new Song(title, artist, url));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.e("InDeviceSongsActivity", "Cursor is null");
        }

        displaySongs();
    }


    private void displaySongs() {
        if (deviceSongs == null) {
            deviceSongs = new ArrayList<>(); // Ensure the list is initialized
        }

        songAdapter = new SongAdapter(this, deviceSongs);

        recyclerView.setAdapter(songAdapter);
    }

}

