package com.example.recorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordFragment extends Fragment implements View.OnClickListener {

    private TextView fileNameText;
    private NavController navController;
    private ImageView listBtn;
    private ImageView recordBtn;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer chronometer;


    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        fileNameText = view.findViewById(R.id.massage_record);
        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        chronometer = view.findViewById(R.id.record_timer);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_btn:
                /* Navigation Controller
                * Part of Android jetpack , used for navigation between both fragments
                */

                if (isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.setTitle("Audio Still recording");
                    alertDialog.setMessage("Are you sure you want to stop recording?");
                    alertDialog.create().show();
                }else{
                    navController.navigate(R.id.action_recordFragment_to_audioFragment);
                }
                break;

            case R.id.record_btn:
                //Stop Recording
                recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.voice_2_, null));
                if (isRecording) {
                    stopRecording();

                    isRecording = false;
                } else {
                    //Start Recording
                    if (checkPermission()) {
                        startRecording();

                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.voice, null));
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void stopRecording() {

        fileNameText.setText("Recording Stopped, File Saved : " + recordFile);

        chronometer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

    }

    private void startRecording() {

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date now = new Date();

        recordFile = "Recording_" + formatter.format(now) + ".3gp";
        fileNameText.setText("Recording, File Name : " + recordFile);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }


    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording){
            stopRecording();

        }
    }
}