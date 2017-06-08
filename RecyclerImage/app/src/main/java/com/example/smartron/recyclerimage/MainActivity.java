package com.example.smartron.recyclerimage;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    public static final int Overlay_Perm = 1234;
    final int numberOfColumns = 3;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    // RecyclerView.LayoutManager layout;
    //List<Uri> imageUrl = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    private GridLayoutManager layout;
    Toolbar toolbar;
    static int x = 0;
    static TextView tv;
    //Button doneButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*doneButt = (Button) findViewById(R.id.doneButt);
        doneButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUrl.size() == 0){
                    Toast.makeText(MainActivity.this, "Please Select Images !!", Toast.LENGTH_SHORT).show();
                }
                else{
                    onDoneButt();
                }
            }
        });*/

        tv = (TextView) findViewById(R.id.tv);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layout = new GridLayoutManager(MainActivity.this, numberOfColumns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);
        //adapter = new RecyclerAdapter(imageUrl, this);
        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 6);
            }
        });*/

        // Intent from RecyclerAdapter when delete is selected
        //Intent intent = getIntent();
        /*images = intent.getStringArrayListExtra("images");
        if(images != null) {
            if(images.size() > 0) {
                imageUrl.clear();
                for (String s : images)
                    imageUrl.add(Uri.parse(s));
                adapter.notifyDataSetChanged();
            }
        }*/
    }

    void update(int delta){
        x += delta;
        Log.d("X : ",""+x);
        if(x != 0)
            tv.setVisibility(View.GONE);
        else
            tv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_images,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.addButt:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 6);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 6) {
            //imageUrl.add(data.getData());
            //adapter.setData(imageUrl);
            adapter.setData(data.getData());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        boolean Accepted;
        switch(permsRequestCode) {
            case 200:
                Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;

            case 210:
                Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.e("Permission Status : ",""+Accepted);
                break;

            case 220:
                Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    public void onDoneButt(){
        //Toast.makeText(MainActivity.this, "Intent must be Triggered !!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,AudioSelection.class);
        intent.putStringArrayListExtra("images", images);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("onResume : ","I was called");

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        }

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.e("Read Permission : ","No Permission");
            String perms[] = {"android.permission.READ_EXTERNAL_STORAGE"};
            int permsRequestCode = 210;
            ActivityCompat.requestPermissions(this,perms, permsRequestCode);
        }

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            String perms[] = {"android.permission.RECORD_AUDIO"};
            int permsRequestCode = 220;
            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        }

        if(Build.VERSION.SDK_INT >= 23){
            if(!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,Overlay_Perm);
            }
        }

        //TextView tv = (TextView) findViewById(R.id.tv);
        //if (imageUrl.size() == 0)
        if(x == 0)
            tv.setVisibility(View.VISIBLE);
        else
            tv.setVisibility(View.GONE);
    }
}
