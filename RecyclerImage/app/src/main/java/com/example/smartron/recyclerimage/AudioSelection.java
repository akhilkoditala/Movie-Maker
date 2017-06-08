package com.example.smartron.recyclerimage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_MUSIC;

public class AudioSelection extends AppCompatActivity {

    Button but1,but2;
    ArrayList<String> imagesPath = new ArrayList<>();
    String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_selection);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");


        String[] proj = {MediaStore.Audio.Media.DATA};
        String sel = MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + MediaStore.Audio.Media.DURATION + " == 0";
        Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,sel,null,null);

        Log.d("Size before Delete : ",cur.getCount()+"");

        File file;
        if(cur != null){
            while(cur.moveToNext()){
                file = new File(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                file.delete();
            }
        }

        // Select Audio from Memory
        but1 = (Button) findViewById(R.id.songButt);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,6);
            }
        });

        // Record Audio
        but2 = (Button) findViewById(R.id.audioButt);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK) {
            // If Selected Song
            if(requestCode == 6) {
                audioPath = getRealPahFromAudioURI(data.getData());
                Intent intent = new Intent(this, ImageSpan1.class);
                intent.putStringArrayListExtra("images", imagesPath);
                intent.putExtra("audio", audioPath);
                startActivity(intent);
            }
        }
    }

    public void onRecord(){
        Intent intent = new Intent(this,Audio_Record.class);
        intent.putStringArrayListExtra("images",imagesPath);
        startActivity(intent);
    }

    public String getRealPahFromAudioURI (Uri contentUri){
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri,proj, null, null, null);
        String path = null;
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                path = cursor.getString(column_index);
            }
        return path;
    }
}
