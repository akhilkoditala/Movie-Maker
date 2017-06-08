package com.example.smartron.recyclerimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.getExternalStoragePublicDirectory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Audio_Record extends AppCompatActivity {

    Button startButt,stopButt,doneButt;
    TextView status;
    Chronometer timer;
    ArrayList<String> imagesPath = new ArrayList<>();
    long tim = System.currentTimeMillis() / 1000;
    static int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");

        setContentView(R.layout.activity_audio__record);

        startButt = (Button) findViewById(R.id.startButt);
        stopButt = (Button) findViewById(R.id.stopButt);
        doneButt = (Button) findViewById(R.id.doneButt);

        timer = (Chronometer) findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime());
        status = (TextView) findViewById(R.id.recordStatus);
        status.setVisibility(View.GONE);

        stopButt.setEnabled(false);
        doneButt.setEnabled(false);

        File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC),"Audio"+tim+".mp3");
        final MediaRecorder recorder = new MediaRecorder();

        try {
            file.createNewFile();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath() + "Audio"+tim+".mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        startButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    recorder.prepare();
                    recorder.start();
                    onRecordStart();
                    timer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        stopButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();
                timer.stop();
                timer.setBase(SystemClock.elapsedRealtime());
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath() + "Audio"+tim+".mp3");
                onRecordStop();
            }
        });

        doneButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.release();
                flag = 1;
                timer.stop();
                timer.setBase(SystemClock.elapsedRealtime());
                onRecordDone();
            }
        });

    }

    public void onRecordStart(){
        startButt.setEnabled(false);
        stopButt.setEnabled(true);
        doneButt.setEnabled(true);
        status.setVisibility(View.VISIBLE);
    }

    public void onRecordStop(){
        startButt.setEnabled(true);
        stopButt.setEnabled(false);
        doneButt.setEnabled(false);
        status.setVisibility(View.GONE);
    }

    public void onRecordDone(){
        status.setVisibility(View.GONE);
        startButt.setEnabled(false);
        Intent intent1 = new Intent(this,ImageSpan1.class);
        intent1.putStringArrayListExtra("images",imagesPath);
        intent1.putExtra("audio",Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath() + "Audio" + tim + ".mp3");
        startActivity(intent1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag == 1){
            onRecordStop();
            Toast.makeText(this,"The Recorded Audio has been deleted. Create a new one.",Toast.LENGTH_SHORT).show();
        }
    }
}
