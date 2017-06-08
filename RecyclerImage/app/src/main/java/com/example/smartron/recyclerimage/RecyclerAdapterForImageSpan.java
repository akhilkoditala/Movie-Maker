package com.example.smartron.recyclerimage;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Smartron on 6/7/2017.
 */

public class RecyclerAdapterForImageSpan extends RecyclerView.Adapter<RecyclerAdapterForImageSpan.RecyclerViewHolder>{
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> imagesLen = new ArrayList<>();
    Context c;
    int index;

    public RecyclerAdapterForImageSpan(Context c){
        this.c = c;
    }

    public void setData(ArrayList<String> images,ArrayList<String> imagesLen){
        this.images = images;
        this.imagesLen = imagesLen;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_span, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if(position < images.size()) {
            Glide.with(c).load(Uri.parse(images.get(position))).into(holder.image);
            index = position;
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(c, R.array.imageLen, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
            holder.spinner.setSelection(Integer.parseInt(imagesLen.get(position)));
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    //onSpinnerChange(pos);
                    imagesLen.set(index,""+pos);
                    ImageSpan1 is1 = new ImageSpan1();
                    is1.update(index,pos);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void onSpinnerChange(int val){
        imagesLen.set(index,""+val);
        ImageSpan1 is1 = new ImageSpan1();
        is1.update(index,val);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        Spinner spinner;

        public RecyclerViewHolder(View view){
            super(view);
            spinner = (Spinner) view.findViewById(R.id.spinner);
            image = (ImageView) view.findViewById(R.id.img);
        }
    }

}
