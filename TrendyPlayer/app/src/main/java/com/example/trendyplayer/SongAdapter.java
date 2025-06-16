package com.example.trendyplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final Context context;
    private final List<Song> songList;
    private final OnSongClickListener clickListener;
    private final OnSongDeleteListener deleteListener;

    // Listener for song click events.
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    // Listener for song deletion events.
    public interface OnSongDeleteListener {
        void onSongDelete(Song song);
    }

    /**
     * Four-argument constructor.
     * Use this when you want both click functionality and the remove (delete) functionality.
     */
    public SongAdapter(Context context, List<Song> songList,
                       OnSongClickListener clickListener,
                       OnSongDeleteListener deleteListener) {
        this.context = context;
        this.songList = songList;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    /**
     * Three-argument constructor.
     * This sets the deleteListener to null, so the remove button will be hidden.
     * Use this for contexts like TrendingSongsActivity where removal is not desired.
     */
    public SongAdapter(Context context, List<Song> songList, OnSongClickListener clickListener) {
        this(context, songList, clickListener, null);
    }

    /**
     * Optional two-argument constructor providing default click behavior.
     */
    public SongAdapter(Context context, List<Song> songList) {
        this(context, songList,
                song -> {
                    Intent intent = new Intent(context, MusicPlayerActivity.class);
                    intent.putParcelableArrayListExtra("song_list", new ArrayList<>(songList));
                    int index = songList.indexOf(song);
                    intent.putExtra("current_index", index);
                    context.startActivity(intent);
                },
                song -> {
                    // Default delete behavior: do nothing if delete listener is not provided.
                });
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout that includes the potential delete button.
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        // Set up item click behavior.
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSongClick(song);
            }
        });

        // If a delete listener is provided, make the remove button visible and assign its action.
        // Otherwise, hide the remove button.
        if (deleteListener != null) {
            holder.deleteSongBtn.setVisibility(View.VISIBLE);
            holder.deleteSongBtn.setOnClickListener(v -> deleteListener.onSongDelete(song));
        } else {
            holder.deleteSongBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist;
        Button deleteSongBtn;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            deleteSongBtn = itemView.findViewById(R.id.delete_song_btn);
        }
    }
}




