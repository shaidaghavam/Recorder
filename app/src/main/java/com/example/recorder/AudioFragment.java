package com.example.recorder;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.recorder.Adapter.AudioAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioFragment extends Fragment implements AudioAdapter.onItemListClick {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView audioList;
    private File[] allFiles;
    private AudioAdapter audioAdapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay;

    // UI Elements
    private ImageView playBtn;
    private TextView playerHead;
    private TextView playerFileName;

    private SeekBar PlayerSeekBar;
    private Handler SeekbarHandler;
    private Runnable updateSeekbar;

    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerSheet=view.findViewById(R.id.player_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(playerSheet);
        audioList=view.findViewById(R.id.audio_list_view);

        playBtn = view.findViewById(R.id.play_btn);
        playerHead = view.findViewById(R.id.text_header_title);
        playerFileName=view.findViewById(R.id.player_file_name);

            PlayerSeekBar = view.findViewById(R.id.player_seekbar);


        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory =new File(path);
        allFiles = directory.listFiles();

        audioAdapter =new AudioAdapter(allFiles,this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioAdapter);



        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState ==BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    pauseAudio();
                }else{
                    if (fileToPlay == null){
                        resumeAudio();
                    }
                }
            }
        });

        PlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

    }

    @Override
    public void onClickListener(File file, int position) {

        fileToPlay = file;
        if (isPlaying){
            stopAudio();
            playAudio(fileToPlay);
            isPlaying = false;
        }else{

            playAudio(fileToPlay);
        }

    }

    private void pauseAudio(){
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.whiteplay, null));
        isPlaying = false;
        SeekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio(){
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_xxl, null));
        isPlaying = true;
        updateRunnable();
        SeekbarHandler.postDelayed(updateSeekbar,0);
    }

    private void stopAudio() {
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.whiteplay, null));
        isPlaying=false;
        mediaPlayer.stop();
        SeekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_xxl, null));
        playerFileName.setText(fileToPlay.getName());
        playerHead.setText("Playing");
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHead.setText("Finished");
            }
        });

        PlayerSeekBar.setMax(mediaPlayer.getDuration());

        SeekbarHandler = new Handler();
        updateRunnable();
        SeekbarHandler.postDelayed(updateSeekbar,0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {

                PlayerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                SeekbarHandler.postDelayed(this,500);

            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAudio();
    }
}