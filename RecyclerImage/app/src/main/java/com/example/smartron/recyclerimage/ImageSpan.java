package com.example.smartron.recyclerimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.channguyen.rsv.RangeSliderView;

import java.util.ArrayList;

public class ImageSpan extends AppCompatActivity {

    ArrayList<String> imagesPath = new ArrayList<>();
    String audioPath;
    RangeSliderView slider;
    Button doneButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_span);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");
        audioPath = intent.getStringExtra("audio");

        slider = (RangeSliderView) findViewById(R.id.slider);
        doneButt = (Button) findViewById(R.id.doneButt);

        doneButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick();
            }
        });
    }

    public void onDoneClick(){
        Intent intent = new Intent(this,VideoActivity1.class);
        intent.putStringArrayListExtra("images",imagesPath);
        intent.putExtra("audio",audioPath);
        startActivity(intent);
    }
}
