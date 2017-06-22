package com.example.smartron.recyclerimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.channguyen.rsv.RangeSliderView;

import java.util.ArrayList;

public class ImageSpan extends AppCompatActivity {

    private ArrayList<String> imagesPath = new ArrayList<>();
    private String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_span);

        RangeSliderView slider;
        Button doneButt;

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");
        audioPath = intent.getStringExtra("audio");

        slider = (RangeSliderView) findViewById(R.id.slider);
        slider.setEnabled(true);
        doneButt = (Button) findViewById(R.id.doneButt);
        doneButt.setText(R.string.done);

        doneButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick();
            }
        });
    }

    private void onDoneClick(){
        Intent intent = new Intent(this,VideoActivity1.class);
        intent.putStringArrayListExtra("images",imagesPath);
        intent.putExtra("audio",audioPath);
        startActivity(intent);
    }
}
