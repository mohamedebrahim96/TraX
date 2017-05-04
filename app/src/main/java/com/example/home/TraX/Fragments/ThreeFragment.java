package com.example.home.TraX.Fragments;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.home.TraX.R;
import com.yalantis.audio.lib.AudioUtil;
import com.yalantis.waves.util.Horizon;

/**
 * Created by Home on 2017-05-02.
 */

public class ThreeFragment extends Fragment{


    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 1;

    private static final int RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS = 1;
    private static final int RECORDER_ENCODING_BIT = 16;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MAX_DECIBELS = 120;

    private AudioRecord audioRecord;
    private Horizon mHorizon;
    private GLSurfaceView glSurfaceView;

    private Thread recordingThread;
    private byte[] buffer;
    ImageView voice;
    Vibrator vibe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_three, container, false);
        voice = (ImageView)v.findViewById(R.id.voice);

        vibe = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        glSurfaceView = (GLSurfaceView) v.findViewById(R.id.gl_surface);


        mHorizon = new Horizon(glSurfaceView, getResources().getColor(R.color.colorPrimary),
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);
        mHorizon.setMaxVolumeDb(500);

        /*
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHorizon.setMaxVolumeDb(MAX_DECIBELS);
                Toast.makeText(v.getContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });*/

        voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Toast.makeText(v.getContext(), "pressed", Toast.LENGTH_SHORT).show();
                    vibe.vibrate(100);
                    mHorizon.setMaxVolumeDb(MAX_DECIBELS);
                }else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(v.getContext(), "relese", Toast.LENGTH_SHORT).show();
                    mHorizon.setMaxVolumeDb(500);
                    //vibe.vibrate(100);
                }
                return true;
            }
        });

        return v;
    }



    @Override
    public void onStart() {
        super.onStart();
        checkPermissionsAndStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (audioRecord != null) {
            audioRecord.release();
        }
        AudioUtil.disposeProcessor();
    }


    private AudioRecord.OnRecordPositionUpdateListener recordPositionUpdateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioRecord recorder) {
            //empty for now
        }

        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                    && audioRecord.read(buffer, 0, buffer.length) != -1) {
                mHorizon.updateView(buffer);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_RECORD_AUDIO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   checkPermissionsAndStart();
                } else {
                    //finish();
                }
        }

    }

    private void checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_RECORD_AUDIO);
        } else {
            initRecorder();
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                startRecording();
            }
        }
    }

    private void initRecorder() {
        final int bufferSize = 2 * AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
        AudioUtil.initProcessor(RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);

        recordingThread = new Thread("recorder") {
            @Override
            public void run() {
                super.run();
                buffer = new byte[bufferSize];
                Looper.prepare();
                audioRecord.setRecordPositionUpdateListener(recordPositionUpdateListener, new Handler(Looper.myLooper()));
                int bytePerSample = RECORDER_ENCODING_BIT / 8;
                float samplesToDraw = bufferSize / bytePerSample;
                audioRecord.setPositionNotificationPeriod((int) samplesToDraw);
                //We need to read first chunk to motivate recordPositionUpdateListener.
                //Mostly, for lower versions - https://code.google.com/p/android/issues/detail?id=53996
                audioRecord.read(buffer, 0, bufferSize);
                Looper.loop();
            }
        };
    }

    private void startRecording() {
        if (audioRecord != null) {
            audioRecord.startRecording();
        }
        recordingThread.start();
    }
}
