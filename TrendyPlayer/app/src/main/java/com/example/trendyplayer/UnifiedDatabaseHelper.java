package com.example.trendyplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class UnifiedDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "trendyplayer.db";
    private static final int DATABASE_VERSION = 7;
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_PLAYLISTS = "Playlists";
    public static final String TABLE_PLAYLIST_SONGS = "Playlist_Songs";
    public static final String TABLE_TRENDING = "Trending_Songs";

    public UnifiedDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "Creating unified tables...");
        String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT)";
        db.execSQL(createUsersTable);

        String createPlaylistsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLISTS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, name TEXT)";
        db.execSQL(createPlaylistsTable);

        String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLIST_SONGS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, playlist_name TEXT, title TEXT, artist TEXT, url TEXT)";
        db.execSQL(createPlaylistSongsTable);

        String createTrendingTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TRENDING +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, artist TEXT, url TEXT)";
        db.execSQL(createTrendingTable);
        Log.d("Database", "Unified tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRENDING);
        onCreate(db);
    }

    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            User user = new User(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("email")),
                    cursor.getString(cursor.getColumnIndex("password"))
            );
            cursor.close();
            db.close();
            return user;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public void addPlaylist(int userId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        long result = db.insert(TABLE_PLAYLISTS, null, values);
        if (result != -1) {
            Log.d("Database", "Playlist added: " + name + " for user " + userId);
        } else {
            Log.e("Database", "Failed to add playlist: " + name);
        }
        db.close();
    }

    public List<Playlist> getAllPlaylists(int userId) {
        List<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLAYLISTS + " WHERE user_id = ?", new String[]{ String.valueOf(userId) });
        if (cursor.moveToFirst()) {
            do {
                playlists.add(new Playlist(cursor.getInt(0), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return playlists;
    }

    public void deletePlaylist(int userId, String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST_SONGS, "user_id = ? AND playlist_name = ?", new String[]{ String.valueOf(userId), playlistName });
        Log.d("Database", "Deleted songs from playlist: " + playlistName);
        db.delete(TABLE_PLAYLISTS, "user_id = ? AND name = ?", new String[]{ String.valueOf(userId), playlistName });
        Log.d("Database", "Deleted playlist: " + playlistName);
        db.close();
    }

    public void addSongToPlaylist(int userId, String playlistName, String title, String artist, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("playlist_name", playlistName);
        values.put("title", title);
        values.put("artist", artist);
        values.put("url", url);
        long result = db.insert(TABLE_PLAYLIST_SONGS, null, values);
        if (result != -1) {
            Log.d("Database", "Added song " + title + " to playlist " + playlistName + " for user " + userId);
        } else {
            Log.e("Database", "Failed to add song " + title + " to playlist " + playlistName);
        }
        db.close();
    }

    // New method to remove a song from a playlist.
    public void removeSongFromPlaylist(int userId, String playlistName, String songTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PLAYLIST_SONGS, "user_id = ? AND playlist_name = ? AND title = ?",
                new String[]{ String.valueOf(userId), playlistName, songTitle });
        if (rows > 0) {
            Log.d("Database", "Removed song " + songTitle + " from playlist " + playlistName);
        } else {
            Log.e("Database", "Failed to remove song " + songTitle + " from playlist " + playlistName);
        }
        db.close();
    }

    public List<Song> getSongsFromPlaylist(int userId, String playlistName) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT title, artist, url FROM " + TABLE_PLAYLIST_SONGS + " WHERE user_id = ? AND playlist_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{ String.valueOf(userId), playlistName });
        if (cursor.moveToFirst()) {
            do {
                songs.add(new Song(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    public boolean isSongInPlaylist(int userId, String playlistName, String songTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST_SONGS + " WHERE user_id = ? AND playlist_name = ? AND title = ?";
        Cursor cursor = db.rawQuery(query, new String[]{ String.valueOf(userId), playlistName, songTitle });
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public void addTrendingSong(String title, String artist, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("artist", artist);
        values.put("url", url);
        long result = db.insert(TABLE_TRENDING, null, values);
        if (result != -1) {
            Log.d("Database", "Trending Song added: " + title);
        } else {
            Log.e("Database", "Failed to add trending song: " + title);
        }
        db.close();
    }

    public List<Song> getTrendingSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRENDING, null);
        if (cursor.moveToFirst()) {
            do {
                songs.add(new Song(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    public void updateTrendingSongs(List<Song> newTrendingSongs) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Song> currentTrending = getTrendingSongs();
        for (Song song : currentTrending) {
            boolean found = false;
            for (Song newSong : newTrendingSongs) {
                if (song.getTitle().equals(newSong.getTitle())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                db.delete(TABLE_TRENDING, "title = ?", new String[]{ song.getTitle() });
            }
        }
        for (Song newSong : newTrendingSongs) {
            addTrendingSong(newSong.getTitle(), newSong.getArtist(), newSong.getUrl());
        }
        db.close();
    }
}
