package com.example.trendyplayer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSongsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private UnifiedDatabaseHelper dbHelper;
    private List<Song> songList;
    private int userId;
    private String playlistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new UnifiedDatabaseHelper(this);

        userId = getIntent().getIntExtra("user_id", -1);
        playlistName = getIntent().getStringExtra("playlist_name");

        loadSongs();
    }

    // Refresh and set up adapter with both click and delete functionalities.
    private void loadSongs() {
        songList = dbHelper.getSongsFromPlaylist(userId, playlistName);
        songAdapter = new SongAdapter(
                this,
                songList,
                // OnSongClickListener: launches MusicPlayerActivity when a song item is tapped.
                new SongAdapter.OnSongClickListener() {
                    @Override
                    public void onSongClick(Song song) {
                        Intent intent = new Intent(PlaylistSongsActivity.this, MusicPlayerActivity.class);
                        intent.putParcelableArrayListExtra("song_list", new ArrayList<>(songList));
                        int index = songList.indexOf(song);
                        intent.putExtra("current_index", index);
                        startActivity(intent);
                    }
                },
                // OnSongDeleteListener: removes the song from the playlist.
                new SongAdapter.OnSongDeleteListener() {
                    @Override
                    public void onSongDelete(Song song) {
                        dbHelper.removeSongFromPlaylist(userId, playlistName, song.getTitle());
                        Toast.makeText(PlaylistSongsActivity.this, "Song removed", Toast.LENGTH_SHORT).show();
                        loadSongs(); // Reload the list after deletion.
                    }
                }
        );
        recyclerView.setAdapter(songAdapter);
    }
}

