package com.example.smartron.recyclerimage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smartron on 6/23/2017.
 */

public class RecyclerAdapterForDragAndDrop extends RecyclerView.Adapter<RecyclerAdapterForDragAndDrop.RecyclerViewHolder>
                                            implements ItemTouchHelperAdapter{

    private ArrayList<String> imagesPath = new ArrayList<>();
    private ArrayList<String> imagesTitle = new ArrayList<>();
    private final Context c;

    public RecyclerAdapterForDragAndDrop(Context c) {
        this.c = c;
    }

    public void setData(ArrayList<String> path, ArrayList<String> title){
        imagesTitle = title;
        imagesPath = path;
    }

    @Override
    public RecyclerAdapterForDragAndDrop.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drag_and_drop_layout, parent, false);
        /*view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vib = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(1000);
                return true;
            }
        });*/
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterForDragAndDrop.RecyclerViewHolder holder, int position) {
        if(position < imagesPath.size()){
            Glide.with(c).load(Uri.parse(imagesPath.get(position))).into(holder.image);
            holder.title.setText(imagesTitle.get(position));
            holder.title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return imagesPath.size();
    }

    public ArrayList<String> getPath(){
        return imagesPath;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        int i;
        String title = imagesTitle.get(fromPosition);
        String path = imagesPath.get(fromPosition);
        if(fromPosition < toPosition){
            for(i=fromPosition;i<toPosition;i++){
                imagesTitle.set(i,imagesTitle.get(i+1));
                imagesPath.set(i,imagesPath.get(i+1));
            }
        }
        else{
            for (i = fromPosition; i > toPosition; i--) {
                imagesTitle.set(i,imagesTitle.get(i-1));
                imagesPath.set(i,imagesPath.get(i-1));
            }
        }
        imagesPath.set(toPosition,path);
        imagesTitle.set(toPosition,title);
        notifyItemMoved(fromPosition, toPosition);
    }

    /*@Override
    public void onItemDismiss(int position) {
        *//*imagesTitle.remove(position);
        imagesPath.remove(position);
        notifyItemRemoved(position);*//*
    }*/

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;

        public RecyclerViewHolder (View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.iv);
            title = (TextView) view.findViewById(R.id.tv);
        }
    }
}
