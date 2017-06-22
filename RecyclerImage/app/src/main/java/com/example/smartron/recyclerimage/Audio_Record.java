package com.example.smartron.recyclerimage;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import static android.os.Environment.DIRECTORY_MUSIC;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Audio_Record extends AppCompatActivity {

    private Button startButt,stopButt,doneButt;
    private TextView status;
    private Chronometer timer;
    private ArrayList<String> imagesPath = new ArrayList<>();
    private String audioPath;
    private final long tim = System.currentTimeMillis() / 1000;
    //private static int flag = 0;

    public static void Starter(Context c,ArrayList<String> imagesPath){
        Intent intent = new Intent(c,Audio_Record.class);
        intent.putStringArrayListExtra("images",imagesPath);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");

        setContentView(R.layout.activity_audio__record);

        startButt = (Button) findViewById(R.id.startButt);
        startButt.setText(R.string.start);
        stopButt = (Button) findViewById(R.id.stopButt);
        stopButt.setText(R.string.stop);
        doneButt = (Button) findViewById(R.id.doneButt);
        doneButt.setText(R.string.done);

        timer = (Chronometer) findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime());
        status = (TextView) findViewById(R.id.recordStatus);
        status.setText(R.string.audioRecord);
        status.setVisibility(View.GONE);

        stopButt.setEnabled(false);
        doneButt.setEnabled(false);

        File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC),"Audio"+tim+".mp3");
        //AudioRecorder recorder1 = AudioRecorderBuilder.with(this).fileName("Audio"+tim).config();
        final MediaRecorder recorder = new MediaRecorder();

        try {
            boolean stat = file.createNewFile();
            Log.d("File Status : ",""+stat);
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
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                    recorder.prepare();
                    recorder.start();
                    onRecordStart();
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
                //flag = 1;
                timer.stop();
                timer.setBase(SystemClock.elapsedRealtime());
                onRecordDone();
            }
        });

    }

    private void onRecordStart(){
        startButt.setEnabled(false);
        stopButt.setEnabled(true);
        doneButt.setEnabled(true);
        status.setVisibility(View.VISIBLE);
    }

    private void onRecordStop(){
        startButt.setEnabled(true);
        stopButt.setEnabled(false);
        doneButt.setEnabled(false);
        status.setVisibility(View.GONE);
    }

    private void onRecordDone(){
        status.setVisibility(View.GONE);
        startButt.setEnabled(false);
        audioPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath() + "Audio" + tim + ".mp3";
        ImageSpan1.Starter(this,imagesPath,audioPath);
        /*Intent intent1 = new Intent(this,ImageSpan1.class);
        intent1.putStringArrayListExtra("images",imagesPath);
        intent1.putExtra("audio",audioPath);
        startActivity(intent1);*/
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(flag == 1){
            onRecordStop();
            Toast.makeText(this,"The Recorded Audio has been deleted. Create a new one.",Toast.LENGTH_SHORT).show();
        }
    }*/
}
