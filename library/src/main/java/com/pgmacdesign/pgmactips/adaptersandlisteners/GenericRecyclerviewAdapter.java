package com.pgmacdesign.pgmactips.adaptersandlisteners;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import java.lang.reflect.Constructor;
import java.util.List;


/**
 * Created by Patrick-SSD2 on 11/9/2017.
 */
// TODO: 11/9/2017 research this - https://github.com/mikepenz/FastAdapter , looks significantly easier
public class GenericRecyclerviewAdapter<T extends Object>
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	public static interface MultipurposeRecyclerviewLink {
		public void onBindViewTriggered(RecyclerView.ViewHolder holder0, final int position);
	}
	
	//Dataset List
	private List<T> mListObjects;
	
	private int layoutResourceId;
	private Context context;
	private LayoutInflater layoutInflater;
	private MultipurposeRecyclerviewLink link;
	//private C viewHolderClass;
	private Class<? extends RecyclerView.ViewHolder> holderClass;
	//private C holderClass;
	
	public GenericRecyclerviewAdapter(@NonNull MultipurposeRecyclerviewLink link,
	                                  @NonNull Context context,
	                                  int layoutResourceId,
	                                  @NonNull Class<? extends RecyclerView.ViewHolder> holderClass) {
		//this.viewHolderClass = viewHolderClass;
		this.layoutResourceId = layoutResourceId;
		this.holderClass = holderClass;
		this.context = context;
		this.link = link;
		this.layoutInflater = LayoutInflater.from(this.context);
	}
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = layoutInflater.inflate(layoutResourceId, parent, false);
		try {
			Constructor constructor = holderClass.getConstructor(View.class);
			RecyclerView.ViewHolder obj = (RecyclerView.ViewHolder) constructor.newInstance(view);
			L.m("Success @ 56");
			return obj;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder0, final int position) {
		L.m("on bind view triggered");
		link.onBindViewTriggered(holder0, position);
	}
	
	/**
	 * Get the item count (Overridden)
	 * @return int
	 */
	@Override
	public int getItemCount() {
		if(MiscUtilities.isListNullOrEmpty(mListObjects)){
			return 0;
		} else {
			return mListObjects.size();
		}
	}
	
	/**
	 * Set the list of objects
	 * @param mListObjects List of data
	 */
	@SuppressWarnings("unchecked")
	public void setListObjects(List<T> mListObjects) {
		this.mListObjects = mListObjects;
		this.notifyDataSetChanged();
	}
	
	/**
	 * Update a single object in the {@link GenericRecyclerviewAdapter#mListObjects} list.
	 * @param position Position in the list
	 * @param t Object to add into the list
	 */
	@SuppressWarnings("unchecked")
	public void updateOneObject(int position, T t){
		if(MiscUtilities.isListNullOrEmpty(mListObjects)){
			return;
		}
		if(position >= 0 && position < mListObjects.size()){
			mListObjects.set(position, t);
			this.notifyItemChanged(position);
		}
	}
	
	/**
	 * Remove one object from the list
	 * @param position position in {@link GenericRecyclerviewAdapter#mListObjects}
	 */
	@SuppressWarnings("unchecked")
	public void removeOneObject(int position){
		if(MiscUtilities.isListNullOrEmpty(mListObjects)){
			return;
		}
		if(position >= 0 && position < mListObjects.size()){
			mListObjects.remove(position);
			this.notifyItemChanged(position);
		}
	}
	
	/*
	Unused:
		ParameterizedType pt = (ParameterizedType) viewHolderClass.getGenericSuperclass();
		String paramClassName = pt.getActualTypeArguments()[0].toString().trim();
		return (C) (Class.forName(paramClassName).newInstance());
		C c = (C) viewHolderClass.newInstance();
	 */
}
