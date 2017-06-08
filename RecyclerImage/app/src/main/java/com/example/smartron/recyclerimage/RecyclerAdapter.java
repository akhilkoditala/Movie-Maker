package com.example.smartron.recyclerimage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Smartron on 5/18/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    List<Uri> images = new ArrayList<>();
    ArrayList<String> imagesPath = new ArrayList<String>();
    Context c;

    /*public RecyclerAdapter(List<Uri> images, Context c) {
        this.images = images;
        imagesPath.clear();
        for(Uri uri : images)
            imagesPath.add(uri.toString());
        this.c = c;
    }*/

    public RecyclerAdapter(Context c){
        this.c = c;
    }

    /*public void setData(List<Uri> images) {
        this.images = images;
        imagesPath.clear();
        for(Uri uri : images)
            imagesPath.add(uri.toString());
    }*/

    public void setData(Uri image) {
        images.add(image);
        imagesPath.add(image.toString());
        MainActivity ma = new MainActivity();
        ma.update(1);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == R.layout.item_layout)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_layout,parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == images.size()) ? R.layout.button_layout : R.layout.item_layout;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        if(position == images.size()) holder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images.size() == 0)
                    Toast.makeText(c, "Please Select Images !!", Toast.LENGTH_SHORT).show();
                else {
                    //Log.d("Number of Images",String.valueOf(images.size()));
                    //Intent intent = new Intent(c, VideoActivity.class);
                    //intent.putStringArrayListExtra("images", imagesPath);
                    //Intent intent = new Intent(c,VideoActivity1.class);
                    Intent intent = new Intent(c,AudioSelection.class);
                    intent.putStringArrayListExtra("images", imagesPath);
                    c.startActivity(intent);
                }
            }
        });
        else {
            if (position < images.size()) {
                Glide.with(c).load(images.get(position)).into(holder.image);
                holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //Toast.makeText(c,"Long Click !!",Toast.LENGTH_SHORT).show();
                        final CharSequence[] items = {"Delete"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                        //builder.setTitle("Select an Option");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    images.remove(position);
                                    imagesPath.remove(position);
                                    //Intent intent = new Intent(c, MainActivity.class);
                                    //intent.putStringArrayListExtra("images", imagesPath);
                                    notifyDataSetChanged();
                                    MainActivity ma = new MainActivity();
                                    ma.update(-1);
                                    /*intent.putExtra("position",position);
                                    c.startActivity(intent);*/
                                    //Toast.makeText(c, "I should be deleted !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();
                    /*final Dialog dialog = new Dialog(c);
                    dialog.setContentView(R.layout.spinner);
                    dialog.setTitle("Select an Option !!");

                    TextView tv = (TextView) dialog.findViewById(R.id.tv);
                    tv.setText("Delete");*/
                        return true;
                    }
                });
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.image.setSelected(false);
                    Intent intent = new Intent(c, ImageViewer.class);
                    intent.putExtra("image", String.valueOf(images.get(position)));
                    //Log.e("Path : ",images.get(position));
                    c.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return images.size() + 1;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        Button but;

        public RecyclerViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            but = (Button) view.findViewById(R.id.butt);
        }
    }
}