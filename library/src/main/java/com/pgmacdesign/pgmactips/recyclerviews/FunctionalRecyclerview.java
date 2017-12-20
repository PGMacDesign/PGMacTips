package com.pgmacdesign.pgmactips.recyclerviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by pmacdowell on 2017-08-23.
 */

public class FunctionalRecyclerview extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Dataset List
    private List<Object> mListObjects;

    //UI
    private LayoutInflater mInflater;

    //Misc
    private Context context;
    private boolean oneSelectedAnimate;
    private int layoutResourceId;

    private int COLOR_BLACK;
    private Class<? extends RecyclerView.ViewHolder> holderClass;

    public FunctionalRecyclerview(@NonNull Context context, int layoutResourceId,
                                   Class<? extends RecyclerView.ViewHolder> holderClass) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
        this.holderClass = holderClass;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(layoutResourceId, parent, false);
        //holderClass = new RecyclerView.ViewHolder(view);
        //return viewHolder1;
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder0, final int position) {



    }

    @Override
    public int getItemCount() {
        if(mListObjects == null){
            return 0;
        }
        if(mListObjects.size() <= 0){
            return 0;
        }
        return mListObjects.size();
    }

    public void setListObjects(List<Object> mListObjects) {
        this.mListObjects = mListObjects;
        this.notifyDataSetChanged();
    }

    private class SomeClass extends RecyclerView.ViewHolder {

        public SomeClass(View itemView) {
            super(itemView);
        }
    }




}
