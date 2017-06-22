package com.example.smartron.recyclerimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class VideoActivity extends AppCompatActivity {

    private VideoView vv;
    private ArrayList<String> imagesPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vv = (VideoView) findViewById(R.id.videoView);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");

        makeVideo mv = new makeVideo();
        mv.execute();
    }

    public class makeVideo extends AsyncTask<Object,Void,Object> {
        final ProgressDialog pd = new ProgressDialog(VideoActivity.this);

        final Long ti = System.currentTimeMillis() / 1000;
        final String tim = ti.toString();
        final File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Video" + tim + ".mp4");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Making Video .. Please Wait");
            pd.show();
            pd.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);
            pd.dismiss();

            MediaController mediaController = new MediaController(VideoActivity.this);
            mediaController.setAnchorView(vv);

            //Uri uri = Uri.parse(getFilesDir().getPath()+"/Video" + tim + ".mp4");

            vv.setMediaController(mediaController);
            vv.setVideoURI(Uri.parse(file.getAbsolutePath()));
            vv.requestFocus();
            vv.start();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SequenceEncoder encoder;
            Log.e("Video thread",""+imagesPath.size());
            try {
                boolean stat = file.createNewFile();
                Log.d("File Creation status : ",stat+"");
                encoder = new SequenceEncoder(file);
                Log.d("Size : ",String.valueOf(imagesPath.size()));
                for (int i = 0; i < imagesPath.size(); i++) {
                    Log.d("Index : ",""+i);
                    encoder.encodeImage(MediaStore.Images.Media.getBitmap(VideoActivity.this.getContentResolver(), Uri.parse(imagesPath.get(i))));
                }
                encoder.finish();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
