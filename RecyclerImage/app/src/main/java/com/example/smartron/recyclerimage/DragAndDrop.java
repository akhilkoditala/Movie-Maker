package com.example.smartron.recyclerimage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class DragAndDrop extends AppCompatActivity {

    private ArrayList<String> imagesPath = new ArrayList<>();
    private ArrayList<String> imagesTitle = new ArrayList<>();
    private RecyclerAdapterForDragAndDrop adapter;
    ItemTouchHelper touchHelper;
    ItemTouchHelperCallback callback;

    public static void Starter(Context c, ArrayList<String> imagesPath, ArrayList<String> imagesTitle){
        Intent intent = new Intent(c, DragAndDrop.class);
        intent.putStringArrayListExtra("images",imagesPath);
        intent.putStringArrayListExtra("imagesTitle",imagesTitle);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");
        imagesTitle = intent.getStringArrayListExtra("imagesTitle");

        final int numberOfColumns = 1;
        RecyclerView recyclerView;
        LinearLayoutManager layout;

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layout = new LinearLayoutManager(DragAndDrop.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);

        adapter = new RecyclerAdapterForDragAndDrop(this);
        adapter.setData(imagesPath,imagesTitle);
        recyclerView.setAdapter(adapter);

        callback = new ItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        RecyclerAdapterForDragAndDrop adapter = new RecyclerAdapterForDragAndDrop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nextButt:
                AudioSelection.Starter(this, adapter.getPath());
        }
        return super.onOptionsItemSelected(item);
    }
}
