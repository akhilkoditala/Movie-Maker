package com.example.smartron.recyclerimage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewer extends AppCompatActivity {


    public static void Starter(Context c, Uri imagePath){
        Intent intent = new Intent(c, ImageViewer.class);
        intent.putExtra("image", imagePath.toString());
        //startActivity(intent);
        c.startActivity(intent);
    }

    private PhotoViewAttacher mAttacher;
    ImageView iv;

    private RequestListener<String, GlideDrawable> glideListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            if (mAttacher != null) {
                mAttacher.update();
            } else {
                mAttacher = new PhotoViewAttacher(iv);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);


        iv = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        String path = intent.getStringExtra("image");
        //Log.d("ImagePath : ",path);
        Glide.with(this).load(path).listener(glideListener).into(iv);
        //iv.setImageBitmap(BitmapFactory.decodeFile(getRealPathFromURI(Uri.parse(path))));
        //Log.d("ImagePath : ",getRealPathFromURI(Uri.parse(path)));
        //iv.setImageBitmap(BitmapFactory.decodeFile(path));
    }
}
