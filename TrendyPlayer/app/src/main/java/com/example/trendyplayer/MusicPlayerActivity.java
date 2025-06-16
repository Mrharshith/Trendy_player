package com.example.trendyplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private Button playPauseButton, nextButton, prevButton, addToPlaylistButton;
    private SeekBar seekBar;
    private boolean isPlaying = false;
    private int currentSongIndex;
    private ArrayList<Song> playlist;
    private UnifiedDatabaseHelper playlistDatabaseHelper;
    private TextView songTitle;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        playPauseButton = findViewById(R.id.play_pause_btn);
        nextButton = findViewById(R.id.next_btn);
        prevButton = findViewById(R.id.prev_btn);
        addToPlaylistButton = findViewById(R.id.add_to_playlist_btn);
        seekBar = findViewById(R.id.seek_bar);
        songTitle = findViewById(R.id.song_title);

        playlistDatabaseHelper = new UnifiedDatabaseHelper(this);

        // Receive playlist and current song index
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            @SuppressWarnings("deprecation")
            ArrayList<Song> songs = bundle.getParcelableArrayList("song_list");
            playlist = songs != null ? songs : new ArrayList<>();
        } else {
            playlist = new ArrayList<>();
        }
        currentSongIndex = intent.getIntExtra("current_index", 0);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        loadSong(currentSongIndex);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
        }

        playPauseButton.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                playPauseButton.setText("Play");
            } else {
                player.play();
                playPauseButton.setText("Pause");
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentSongIndex < playlist.size() - 1) {
                currentSongIndex++;
                loadSong(currentSongIndex);
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                loadSong(currentSongIndex);
            }
        });

        addToPlaylistButton.setOnClickListener(v -> {
            if (playlistDatabaseHelper == null) {
                playlistDatabaseHelper = new UnifiedDatabaseHelper(MusicPlayerActivity.this);
            }

            userId = getIntent().getIntExtra("user_id", -1);

            // Check if any playlist exists
            List<Playlist> playlists = playlistDatabaseHelper.getAllPlaylists(userId);

            if (playlists.isEmpty()) {
                // If no playlists exist, prompt the user to create one
                Toast.makeText(MusicPlayerActivity.this, "No playlists found. Create a playlist first!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MusicPlayerActivity.this, PlaylistActivity.class));
            } else {
                // If playlists exist, navigate to PlaylistActivity to select one
                Intent intent2 = new Intent(MusicPlayerActivity.this, PlaylistActivity.class);
                intent2.putExtra("song_title", playlist.get(currentSongIndex).getTitle());
                intent2.putExtra("song_artist", playlist.get(currentSongIndex).getArtist());
                intent2.putExtra("song_url", playlist.get(currentSongIndex).getUrl());
                startActivity(intent2);
            }
        });

        // Update SeekBar
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo((progress * player.getDuration()) / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        handler.postDelayed(updateSeekBar, 1000);
    }

    private void loadSong(int index) {
        if (player != null) {
            player.stop();
            player.clearMediaItems();
        }

        Uri songUri = Uri.parse(playlist.get(index).getUrl());
        MediaItem mediaItem = MediaItem.fromUri(songUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        // Ensure maximum volume
        player.setVolume(1.0f);

        playPauseButton.setText("Pause");
        songTitle.setText(playlist.get(index).getTitle());

        // Reset SeekBar
        seekBar.setProgress(0);
        seekBar.setMax(100);
    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying() && player.getDuration() > 0) {
                int progress = (int) ((player.getCurrentPosition() * 100) / player.getDuration());
                seekBar.setProgress(progress);
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        player.release();
    }
}



