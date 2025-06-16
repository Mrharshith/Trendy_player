package com.example.trendyplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    private static final String TAG = "PlaylistActivity";
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private List<Playlist> playlists;
    private UnifiedDatabaseHelper dbHelper;
    private EditText playlistNameInput;
    private Button createPlaylistButton;
    // Song details extras; if provided, we're in "add song" mode.
    private String songTitle, songArtist, songUrl;
    private int userId;
    private boolean isAddMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = findViewById(R.id.recyclerView);
        playlistNameInput = findViewById(R.id.playlist_name_input);
        createPlaylistButton = findViewById(R.id.create_playlist_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new UnifiedDatabaseHelper(this);

        // Retrieve user_id either from Intent extras or SharedPreferences.
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userId = prefs.getInt("user_id", -1);
        }
        Log.d(TAG, "Current user id: " + userId);

        // Retrieve song details from the Intent.
        Intent intent = getIntent();
        songTitle = intent.getStringExtra("song_title");
        songArtist = intent.getStringExtra("song_artist");
        songUrl = intent.getStringExtra("song_url");

        // Determine mode: if song details are provided then we are adding a song.
        isAddMode = (songTitle != null && songArtist != null && songUrl != null);
        Log.d(TAG, "isAddMode: " + isAddMode + " | Song details: title=" + songTitle +
                ", artist=" + songArtist + ", url=" + songUrl);

        loadPlaylists();

        createPlaylistButton.setOnClickListener(v -> {
            String name = playlistNameInput.getText().toString().trim();
            if (!name.isEmpty()) {
                dbHelper.addPlaylist(userId, name);
                playlistNameInput.setText("");
                loadPlaylists();
                Toast.makeText(PlaylistActivity.this, "Playlist Created!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlaylistActivity.this, "Enter a playlist name!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlaylists() {
        playlists = dbHelper.getAllPlaylists(userId);
        Log.d(TAG, "Loaded " + playlists.size() + " playlists for user id " + userId);
        if (playlists.isEmpty()) {
            Toast.makeText(this, "No Playlists Found! Create one first.", Toast.LENGTH_SHORT).show();
        }

        // Setting up the playlist adapter with the delete and click listener
        playlistAdapter = new PlaylistAdapter(this, playlists, playlistName -> {
            if (isAddMode) {
                // In "add song" mode, add the song to the selected playlist.
                dbHelper.addSongToPlaylist(userId, playlistName, songTitle, songArtist, songUrl);
                Toast.makeText(PlaylistActivity.this, "Song added to " + playlistName, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Otherwise, open PlaylistSongsActivity to view the songs in the selected playlist.
                Intent intnt = new Intent(PlaylistActivity.this, PlaylistSongsActivity.class);
                intnt.putExtra("playlist_name", playlistName);
                intnt.putExtra("user_id", userId);
                startActivity(intnt);
            }
        }, playlistName -> {
            // Delete the playlist when delete button is clicked
            dbHelper.deletePlaylist(userId, playlistName);
            loadPlaylists();  // Refresh the playlist list after deletion
            Toast.makeText(PlaylistActivity.this, "Playlist Deleted", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(playlistAdapter);
    }
}
