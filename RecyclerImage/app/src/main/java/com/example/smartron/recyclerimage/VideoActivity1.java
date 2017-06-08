package com.example.smartron.recyclerimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.net.URI;
import java.net.URLConnection;
import java.security.Timestamp;
import java.util.ArrayList;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static org.bytedeco.javacpp.avcodec.AV_CODEC_ID_AAC;
import static org.bytedeco.javacpp.avcodec.AV_CODEC_ID_H264;
import static org.bytedeco.javacpp.avcodec.AV_CODEC_ID_MPEG4;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

public class VideoActivity1 extends AppCompatActivity {

    VideoView vv;
    ArrayList<String> imagesPath = new ArrayList<String>();
    ArrayList<String> imagesLen = new ArrayList<>();
    String audioPath;

    Long ti = System.currentTimeMillis() / 1000;
    String tim = ti.toString();

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video1);
        vv = (VideoView) findViewById(R.id.vv1);

        Intent intent = getIntent();
        imagesPath = intent.getStringArrayListExtra("images");
        audioPath = intent.getStringExtra("audio");
        imagesLen = intent.getStringArrayListExtra("imagesLen");

        File folder = getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        path = folder.getAbsolutePath();

        MovieMaker mv = new MovieMaker();
        mv.execute();

        Log.d("Size : ",""+imagesPath.size());
    }

    public class MovieMaker extends AsyncTask {

        private ProgressDialog pd = new ProgressDialog(VideoActivity1.this);
        File file = new File(String.valueOf(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)));
        File file1 = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"video"+tim+".mp4");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Creating Video ... Please Wait !!");
            pd.show();
            pd.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pd.dismiss();

            MediaController mediaController = new MediaController(VideoActivity1.this);
            mediaController.setAnchorView(vv);

            vv.setMediaController(mediaController);
            vv.setVideoURI(Uri.parse(file1.getAbsolutePath()));
            vv.requestFocus();
            vv.start();
        }

        @Override
        protected String doInBackground(Object[] params) {
            return createMovie();
        }

        private String createMovie(){

            try {
                file.mkdirs();
                file1.createNewFile();

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                //FrameGrabber grabber = new FFmpegFrameGrabber(getRealPahFromAudioURI(Uri.parse(audioPath)));
                FrameGrabber grabber = new FFmpegFrameGrabber(audioPath);
                grabber.setAudioChannels(1);
                grabber.start();
                FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file1.getAbsolutePath(),metrics.widthPixels,metrics.heightPixels,grabber.getAudioChannels());
                OpenCVFrameConverter converter = new OpenCVFrameConverter() {
                    @Override
                    public Frame convert(Object o) {
                        return null;
                    }

                    @Override
                    public Object convert(Frame frame) {
                        return null;
                    }
                };

                recorder.setVideoCodec(AV_CODEC_ID_MPEG4);
                //recorder.setVideoBitrate(10 * 1024 * 1024);
                recorder.setFrameRate(24.0);
                recorder.setVideoQuality(0);
                recorder.setAudioChannels(2);
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setFormat("mp4");
                recorder.start();

                int imageTime = 2000000;
                long audioTime = (long)(grabber.getLengthInTime()/1000000);

                Frame fr;

                long totalFrames;

                Log.d("Sample Rate : ",""+grabber.getSampleRate());
                Log.d("Bit Rate : ",""+grabber.getAudioBitrate());
                Log.d("Frames : ",""+grabber.getLengthInFrames());
                Log.d("Time : ",""+grabber.getLengthInTime());

                long sum = 0;

                for(int i = 0;i < imagesPath.size(); i++) {
                    imageTime = (Integer.parseInt(imagesLen.get(i)) + 1) * 1000000;
                    Log.d("Image Length of "+(i+1)+" Song : ",""+Integer.parseInt(imagesLen.get(i)) + 1);
                    Uri uri = Uri.parse(imagesPath.get(i));
                    opencv_core.IplImage image = cvLoadImage(getRealPathFromURI(uri));
                    Frame frame = converter.convert(image);
                    //Log.d("Path : ", imagesPath.get(i));
                    recorder.record(frame);
                    //Log.d("Before Set : ",""+recorder.getTimestamp());
                    sum += imageTime;
                    long time = sum;
                    if (time > recorder.getTimestamp())
                        recorder.setTimestamp(time);
                    //Log.d("After Set : ",""+recorder.getTimestamp());
                    fr = grabber.grabFrame();
                }

                long audioLen = (long) (grabber.getLengthInTime()/1000000);
                long audioFrames = (long) (Math.ceil(grabber.getLengthInTime() / (24 * 1000)));
                long videoLen = (long) (recorder.getTimestamp()/1000000);

                long y = (long) Math.floor(videoLen / audioLen);
                long x = (long) videoLen % audioLen;

                for(long i = 0;i < y;i++){
                    //grabber = new FFmpegFrameGrabber(getRealPahFromAudioURI(Uri.parse(audioPath)));
                    grabber = new FFmpegFrameGrabber(audioPath);
                    grabber.setAudioChannels(1);
                    grabber.start();
                    fr = grabber.grabFrame();
                    while((fr = grabber.grabFrame()) != null)
                        recorder.record(fr);
                }

                totalFrames = (long) Math.ceil((x * audioFrames) / audioLen);

                //Log.d("Total Frames : ",""+totalFrames);

                //grabber = new FFmpegFrameGrabber(getRealPahFromAudioURI(Uri.parse(audioPath)));
                grabber = new FFmpegFrameGrabber(audioPath);
                grabber.setAudioChannels(1);
                grabber.start();
                fr = grabber.grabFrame();

                long count = 0;
                while((fr = grabber.grabFrame()) != null && count < totalFrames) {
                    count++;
                    recorder.record(fr);
                }

                Log.d("Count : ",""+count);
                Log.d("Video Length : ",""+recorder.getTimestamp());

                recorder.stop();
                recorder.release();
                grabber.stop();
                //mergeAudioAndVideo(imageTime);
                File file = new File(audioPath);
                file.delete();
            } catch (Exception e) {
                Log.e("Problem","Problem",e);
                e.printStackTrace();
            }
            return path+"/video"+tim+".mp4";
        }

        public String getRealPahFromAudioURI (Uri contentUri){
            String[] proj = { MediaStore.Audio.Media.DATA };
            Cursor cursor = getContentResolver().query(contentUri,proj, null, null, null);
            String path = null;
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    path = cursor.getString(column_index);
                }
                return path;
            }
            return "";
        }

        public String getRealPathFromURI (Uri contentUri) {
            String path = null;
            String[] proj = { MediaStore.MediaColumns.DATA };
            Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
            return path;
        }

        /*private boolean mergeAudioAndVideo(int len){
            boolean isCreated = true;
            File file = new File(file1.getAbsolutePath());
            if(!file.exists())
                return false;
            try{
                FrameGrabber grabber1 = new FFmpegFrameGrabber(file1.getAbsolutePath());
                FrameGrabber grabber2 = new FFmpegFrameGrabber(Environment.getExternalStorageDirectory().getPath()+"/Music/Song.mp3");

                File tempo = new File(Environment.getExternalStorageDirectory().getPath()+"/Music/Song.mp3");
                if(tempo.exists())
                    Log.d("File Status : ","File Exists !!!");
                else
                    Log.d("File status : ","Not Exists !!!");

                grabber1.setFormat("mp4");
                grabber2.setFormat("mp3");
                grabber1.setAudioChannels(0);
                grabber1.start();
                grabber2.start();

                FrameRecorder recorder = new FFmpegFrameRecorder(file2.getAbsolutePath(),grabber1.getImageWidth(),
                        grabber1.getImageHeight(),grabber2.getAudioChannels());

                recorder.setFormat("mp4");
                recorder.setFrameRate(grabber1.getFrameRate());
                recorder.setSampleRate(grabber2.getSampleRate());
                recorder.setVideoQuality(0);
                recorder.setVideoOption("preset","veryFast");
                recorder.setVideoCodec(AV_CODEC_ID_MPEG4);
                recorder.start();

                Frame frame1, frame2 = null;

                int count = 0;

                Log.d("Video Frames : ",""+grabber1.getLengthInFrames());
                Log.d("Video Length : ",""+grabber1.getLengthInTime());
                Log.d("Audio Frames : ",""+grabber2.getLengthInFrames());

                for(int i = 0;i<grabber1.getLengthInFrames();i++){
                    if((frame1 = grabber1.grabFrame()) != null){
                        recorder.record(frame1);
                        recorder.record(grabber2.grabFrame());
                        recorder.record(grabber2.grabFrame());
                    }
                    else
                        Log.d("Frame Number : ",""+i);
                }

                while((frame1 = grabber1.grabFrame()) != null &&
                        (frame2 = grabber2.grabFrame()) != null){
                    if(recorder.getTimestamp() < grabber1.getTimestamp())
                        recorder.setTimestamp(grabber1.getTimestamp());
                    long time = (count+1) * len;
                    if(time > recorder.getTimestamp())
                        recorder.setTimestamp(time);
                    count++;
                    Log.d("Count : ",""+count);
                    recorder.record(frame1);
                    recorder.record(frame2);
                }

                recorder.stop();
                grabber1.stop();
                grabber2.stop();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return isCreated;
        }*/
    }
}
