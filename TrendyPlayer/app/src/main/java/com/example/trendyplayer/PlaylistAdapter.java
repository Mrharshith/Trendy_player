package com.example.trendyplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private Context context;
    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;
    private OnPlaylistDeleteListener deleteListener;

    // Define interface for playlist click and delete action
    public interface OnPlaylistClickListener {
        void onPlaylistClick(String playlistName);
    }

    public interface OnPlaylistDeleteListener {
        void onPlaylistDelete(String playlistName);
    }

    // Constructor to initialize adapter with context, playlist list, click, and delete listeners
    public PlaylistAdapter(Context context, List<Playlist> playlists,
                           OnPlaylistClickListener listener,
                           OnPlaylistDeleteListener deleteListener) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each playlist item
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playlistName.setText(playlist.getName());

        // Handle playlist name click (if you need it for navigating to the playlist)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistClick(playlist.getName());
            }
        });

        // Handle delete button click for the playlist
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onPlaylistDelete(playlist.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    // ViewHolder class for the playlist item
    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        Button deleteButton;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_name);
            deleteButton = itemView.findViewById(R.id.delete_playlist_btn); // Reference to delete button
        }
    }
}

