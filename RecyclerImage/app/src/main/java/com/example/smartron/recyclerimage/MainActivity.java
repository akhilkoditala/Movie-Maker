package com.example.smartron.recyclerimage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerAdapter adapter;
    private String mCurrentPhotoPath;
    private final ArrayList<String> imagesTitle = new ArrayList<>();
    TextView tv;
    /*ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;*/

    // RecyclerView.LayoutManager layout;
    //List<Uri> imageUrl = new ArrayList<>();
    //private ArrayList<String> images = new ArrayList<>();
    //private static int x = 0;
    //Button doneButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int numberOfColumns = 2;
        RecyclerView recyclerView;
        GridLayoutManager layout;
        Toolbar toolbar;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layout = new GridLayoutManager(MainActivity.this, numberOfColumns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);
        //adapter = new RecyclerAdapter(imageUrl, this);
        adapter = new RecyclerAdapter(this);
        /*tv = (TextView) findViewById(R.id.tv);
        tv.setText("No Images Selected !!");
        tv.setVisibility(View.VISIBLE);*/
        recyclerView.setAdapter(adapter);

        /*callback = new ItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        //getMenuInflater().inflate(R.menu.capture_image,menu);
        //getMenuInflater().inflate(R.menu.next_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.addButt:
                /*Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image !"),6);*/
                Intent galIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galIntent, 6);
                return true;

            case R.id.captureButt:
                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String tim = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File file = null;
                try {
                    file = createImageFile(tim);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri imgUri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID+".provider",file);
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
                startActivityForResult(camIntent, 7);
                return true;

            case R.id.nextButt:
                if(adapter.getSize() == 0)
                    Toast.makeText(this, "Please Select Images !!", Toast.LENGTH_SHORT).show();
                else {
                    imagesTitle.clear();
                    for(String u : adapter.getPath()){
                        File f = new File(getRealPathFromURI(Uri.parse(u)));
                        imagesTitle.add(f.getName());
                    }
                    //AudioSelection.Starter(this, adapter.getPath());
                    DragAndDrop.Starter(this, adapter.getPath(), imagesTitle);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == 6) {
                adapter.setData(data.getData());
                File f = new File(getRealPathFromURI(data.getData()));
                Toast.makeText(this, f.getName(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
            if(requestCode == 7){
                try {
                    Uri uri = Uri.parse(
                            MediaStore.Images.Media.insertImage(
                            getContentResolver(),
                            mCurrentPhotoPath, null, null));
                    adapter.setData(uri);
                    adapter.notifyDataSetChanged();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        return path;
    }

    private File createImageFile(String tim) throws IOException {
        // Create an image file name
        String imageFileName = "image"+ tim;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        boolean Accepted;
        switch(permsRequestCode) {
            case 200:
                Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d("Permission Status : ",""+Accepted);
                break;

            case 210:
                Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d("Permission Status : ",""+Accepted);
                break;

            case 220:
                Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d("Permission Status : ",""+Accepted);
                break;

        }
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
                startActivityForResult(intent,1234);
            }
        }
    }
}
