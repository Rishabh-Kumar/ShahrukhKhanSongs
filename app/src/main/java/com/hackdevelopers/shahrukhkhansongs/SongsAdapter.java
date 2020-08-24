package com.hackdevelopers.shahrukhkhansongs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VHSong>{

    private ArrayList<Song> songs;
    private Context context;


    public SongsAdapter(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public VHSong onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new VHSong(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VHSong holder, final int position) {
        holder.setName(songs.get(position).getName());
        holder.setThumbnail(songs.get(position).getThumbnail());
        holder.setYear(songs.get(position).getYear());

        //songs.add(new Song("Jinke Liye", "0NhiNqI0SFs", "2020"));

        Log.d("Songs", "songs.add(new Song(\"" + songs.get(position).getName() + "\", \"" + songs.get(position).getId() + "\", \"" + songs.get(position).getYear() + "\"));");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.document(APP_NAME_ID + "/" + songs.get(position).getId()).update("year", "2018");
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("id", songs.get(position).getId());
                holder.itemView.getContext().startActivity(intent);
                Answers.getInstance().logCustom(new CustomEvent("Played Song")
                .putCustomAttribute("Song Name", songs.get(position).getName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class VHSong extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView name, year;

        public VHSong(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.name);
            year = itemView.findViewById(R.id.year);
        }

        public void setThumbnail(String thumbnail_s) {
            Glide.with(context).load(thumbnail_s).into(thumbnail);

        }

        public void setName(String name_s) {
            name.setText(name_s);
        }

        public void setYear(String year_s) {
            year.setText(year_s);
        }
    }
}
