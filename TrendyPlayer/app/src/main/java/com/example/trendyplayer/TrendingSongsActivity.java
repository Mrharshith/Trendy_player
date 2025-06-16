package com.example.trendyplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TrendingSongsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> trendingSongs;
    private UnifiedDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_songs);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new UnifiedDatabaseHelper(this);

        // Get trending songs; if none exist, add sample songs.
        trendingSongs = dbHelper.getTrendingSongs();
        if(trendingSongs.isEmpty()){
            dbHelper.addTrendingSong("Nannu Nenu Adiga", "Bablu",
                    "https://praneeth04kamatham.github.io/songs_for_player/Nannu%20Nenu%20Adiga.mp3");
            dbHelper.addTrendingSong("Brindavanam Nunchi", "bobby",
                    "https://praneeth04kamatham.github.io/songs_for_player/Brindavanam%20Nunchi.mp3");
            dbHelper.addTrendingSong("Andhamaina Chandhamaama", "ravi",
                    "https://praneeth04kamatham.github.io/songs_for_player/Andhamaina%20Chandhamaama%20-%20SenSongsMp3.Co.mp3");
            dbHelper.addTrendingSong("Peddha Peddha Kallathoti", "venky",
                    "https://praneeth04kamatham.github.io/songs_for_player/Peddha%20Peddha%20Kallathoti%20-%20SenSongsMp3.Co.mp3");
            dbHelper.addTrendingSong("Urime Manase", "nani",
                    "https://praneeth04kamatham.github.io/songs_for_player/Urime%20Manase%20-%20SenSongsMp3.Co.mp3");
            dbHelper.addTrendingSong("Yedurangula Vaana", "nani",
                    "https://praneeth04kamatham.github.io/songs_for_player/Yedurangula%20Vaana.mp3");
            trendingSongs = dbHelper.getTrendingSongs();
        }

        // When a trending song is clicked, launch MusicPlayerActivity
        songAdapter = new SongAdapter(this, trendingSongs, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                // Retrieve the current user id (assuming the user is logged in)
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = prefs.getInt("user_id", -1);

                // Create an Intent with song details extras
                Intent intent = new Intent(TrendingSongsActivity.this, MusicPlayerActivity.class);
                ArrayList<Song> selectedSongList = new ArrayList<>();
                selectedSongList.add(song);  // Add the clicked song to the list
                intent.putParcelableArrayListExtra("song_list", selectedSongList);  // Pass song list
                intent.putExtra("current_index", 0);  // The clicked song is the first in the list
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(songAdapter);
    }
}



