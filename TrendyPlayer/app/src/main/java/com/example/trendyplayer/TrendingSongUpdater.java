package com.example.trendyplayer;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrendingSongUpdater {
    private Context context;
    private UnifiedDatabaseHelper dbHelper;

    public TrendingSongUpdater(Context context) {
        this.context = context;
        this.dbHelper = new UnifiedDatabaseHelper(context);
    }

    public void checkAndUpdateTrendingSongs() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int lastUpdatedMonth = prefs.getInt("lastTrendingUpdateMonth", -1);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        if (lastUpdatedMonth != currentMonth) {
            updateTrendingSongs();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("lastTrendingUpdateMonth", currentMonth);
            editor.apply();
        }
    }

    private void updateTrendingSongs() {
        List<Song> newTrendingSongs = new ArrayList<>();

        // Add 30 Free Testing Songs (Replace with admin-controlled songs)
        for (int i = 1; i <= 30; i++) {
            newTrendingSongs.add(new Song("Test Song " + i, "Test Artist", "https://www.example.com/test_song_" + i + ".mp3"));
        }

        dbHelper.updateTrendingSongs(newTrendingSongs);
    }
}