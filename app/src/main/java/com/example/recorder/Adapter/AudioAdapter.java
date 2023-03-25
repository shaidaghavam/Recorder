package com.example.recorder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recorder.R;
import com.example.recorder.TimeAgo;

import java.io.File;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private File[] allFiles;
    private TimeAgo timeAgo;
    private onItemListClick onItemListClick;

    public AudioAdapter (File[] allFiles, onItemListClick onItemListClick){
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;


    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item,parent,false);
        timeAgo=new TimeAgo();

        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {

        holder.listTitle.setText(allFiles[position].getName());
        holder.listDate.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView listImage;
        private TextView listTitle;
        private TextView listDate;



        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            listImage=itemView.findViewById(R.id.play_recycle_btn);
            listDate=itemView.findViewById(R.id.Date_name_recycle);
            listTitle=itemView.findViewById(R.id.File_name_recycle);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()],getAdapterPosition());
        }
    }

    public interface onItemListClick{

        void onClickListener(File file,int position);
    }
}
