package com.example.smartron.recyclerimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class ImageSpan1 extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapterForImageSpan adapter;
    private GridLayoutManager layout;
    ArrayList<String> imagesPath = new ArrayList<>();
    String audioPath;
    ArrayList<String> imagesLen = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_span1);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");
        audioPath = intent.getStringExtra("audio");
        for(String s : imagesPath)
            imagesLen.add(1+"");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewImageSpan);
        layout = new GridLayoutManager(ImageSpan1.this,3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);
        adapter = new RecyclerAdapterForImageSpan(this);
        adapter.setData(imagesPath,imagesLen);
        recyclerView.setAdapter(adapter);
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
                Intent intent = new Intent(this,VideoActivity1.class);
                intent.putStringArrayListExtra("images",imagesPath);
                intent.putExtra("audio",audioPath);
                intent.putStringArrayListExtra("imagesLen",imagesLen);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
