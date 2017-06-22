package com.example.smartron.recyclerimage;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class ImageSpan1 extends AppCompatActivity {

    private ArrayList<String> imagesPath = new ArrayList<>();
    private String audioPath;
    private final ArrayList<String> imagesLen = new ArrayList<>();

    public static void Starter(Context c, ArrayList<String> imagesPath, String audioPath){
        Intent intent = new Intent(c,ImageSpan1.class);
        intent.putStringArrayListExtra("images",imagesPath);
        intent.putExtra("audio",audioPath);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_span1);

        final int numberOfColumns = 2;

        RecyclerView recyclerView;
        RecyclerAdapterForImageSpan adapter;
        GridLayoutManager layout;

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");
        audioPath = intent.getStringExtra("audio");

        long duration = getDuration();
        Log.d("Duration in Seconds : ",""+duration);
        long ratio = (long)Math.ceil(duration/imagesPath.size());
        ratio = Math.min(ratio,(long)4);
        ratio--;

        for(String s : imagesPath)
            imagesLen.add(ratio+"");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewImageSpan);
        layout = new GridLayoutManager(ImageSpan1.this,numberOfColumns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);
        adapter = new RecyclerAdapterForImageSpan(this);
        adapter.setData(imagesPath,imagesLen);
        recyclerView.setAdapter(adapter);
    }

    public long getDuration(){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(audioPath);
        long duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        long seconds = (long) Math.ceil(duration/1000);
        return seconds;
    }

    public void update(int position,int val){
        if(position < imagesLen.size())
            imagesLen.set(position,""+val);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nextButt:
                VideoActivity1.Starter(this,imagesPath,audioPath,imagesLen);
                /*Intent intent = new Intent(this,VideoActivity1.class);
                intent.putStringArrayListExtra("images",imagesPath);
                intent.putExtra("audio",audioPath);
                intent.putStringArrayListExtra("imagesLen",imagesLen);
                startActivity(intent);*/
        }
        return super.onOptionsItemSelected(item);
    }
}
