package com.example.smartron.recyclerimage;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

public class AudioSelection extends AppCompatActivity {

    private ArrayList<String> imagesPath = new ArrayList<>();

    public static void Starter(Context c,ArrayList<String> imagesPath){
        Intent intent = new Intent(c, AudioSelection.class);
        intent.putStringArrayListExtra("images",imagesPath);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_selection);

        Button but1,but2;

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");

        String[] proj = {MediaStore.Audio.Media.DATA};
        String sel = MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + MediaStore.Audio.Media.DURATION + " == 0";
        Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,sel,null,null);

        if(cur != null)
            Log.d("Size before Delete : ",cur.getCount()+"");

        File file;
            while(cur != null && cur.moveToNext()){
                file = new File(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                boolean stat = file.delete();
                Log.d("Audio File deleted : ",""+stat);
            }
            if(cur != null)
                cur.close();

        // Select Audio from Memory
        but1 = (Button) findViewById(R.id.songButt);
        but1.setText(R.string.selectSong);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(AudioSelection.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    /*Intent i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    i.setType("audio/mpeg");
                    startActivityForResult(Intent.createChooser(i,"Select Audio File"), 6);*/
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 6);
                }
            }
        });

        // Record Audio
        but2 = (Button) findViewById(R.id.audioButt);
        but2.setText(R.string.recordAudio);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String audioPath;

        // If Selected Song
        if(resultCode == Activity.RESULT_OK && requestCode == 6) {
                try {
                    audioPath = getPath(this,data.getData());
                    Log.d("AudioPath : ",audioPath);
                    ImageSpan1.Starter(this,imagesPath,audioPath);
                    /*Intent intent = new Intent(this, ImageSpan1.class);
                    intent.putStringArrayListExtra("images", imagesPath);
                    intent.putExtra("audio", audioPath);
                    startActivity(intent);*/
                }
                catch (Exception e){
                    ActivityCompat.requestPermissions(AudioSelection.this,new String[] {"android.permission.READ_EXTERNAL_STORAGE"},7);
                }
        }
    }

    private void onRecord(){
        Audio_Record.Starter(this,imagesPath);
        /*Intent intent = new Intent(this,Audio_Record.class);
        intent.putStringArrayListExtra("images",imagesPath);
        startActivity(intent);*/
    }

    /*private String getRealPahFromAudioURI (Uri contentUri){
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri,proj, null, null, null);
        String path = null;
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                path = cursor.getString(column_index);
            }
        cursor.close();
        return path;
    }*/

    //

    private static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
