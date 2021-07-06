package com.pgmacdesign.pgmactips.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A RecyclerArrayAdapter implementation very similar to an ArrayAdapter.  This was taken from the sample provided in the StickyHeadersRecyclerView library.
 * https://github.com/timehop/sticky-headers-recyclerview/blob/master/sample/src/main/java/com/timehop/stickyheadersrecyclerview/sample/RecyclerArrayAdapter.java
 * Updated Pull from: https://github.com/percolate/mentions/blob/master/Mentions/sample/src/main/java/com/percolate/mentions/sample/adapters/RecyclerArrayAdapter.java
 */
public abstract class RecyclerArrayAdapter<M, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
	
	private final ArrayList<M> items = new ArrayList<M>();
	
	public RecyclerArrayAdapter() {
		setHasStableIds(true);
	}
	
	/**
	 * Add a single item
	 * @param object
	 */
	public void add(M object) {
		items.add(object);
		notifyDataSetChanged();
	}
	
	/**
	 * Add a single item in a specific position
	 * @param index
	 * @param object
	 */
	public void add(int index, M object) {
		try {
			items.add(index, object);
		} catch (Exception e){
			L.e(e);
		}
		notifyDataSetChanged();
	}
	
	/**
	 * Add multiple items
	 * @param collection
	 */
	public void addAll(Collection<? extends M> collection) {
		if (collection != null) {
			items.addAll(collection);
			notifyDataSetChanged();
		}
	}
	
	/**
	 * Add multiple items
	 * @param items
	 */
	public void addAll(M... items) {
		addAll(Arrays.asList(items));
	}
	
	/**
	 * Clear and set new items (wipe the list and reset it)
	 * @param updatedItems
	 */
	public void setItems(Collection<? extends M> updatedItems){
		if(MiscUtilities.isCollectionNullOrEmpty(updatedItems)){
			return;
		}
		try {
			this.items.clear();
			this.addAll(updatedItems);
		} catch (Exception e){
			L.e(e);
		}
	}
	
	/**
	 * Update an existing item
	 * @param index
	 * @param object
	 */
	public void update(int index, M object){
		if(object == null){
			return;
		}
		if(index < 0 || index >= this.getItemCount()){
			return;
		}
		try {
			this.items.set(index, object);
			this.notifyItemChanged(index, object);
		} catch (Exception e){
			L.e(e);
		}
	}
	
	/**
	 * Clear all items
	 */
	public void clear() {
		try {
			items.clear();
			notifyDataSetChanged();
		} catch (Exception e){
			L.e(e);
		}
	}
	
	/**
	 * Remove an item
	 * @param object
	 */
	public void remove(M object) {
		try {
			items.remove(object);
			notifyDataSetChanged();
		} catch (Exception e){
			L.e(e);
		}
	}
	
	/**
	 * Remove an item
	 * @param index
	 */
	public void remove(int index) {
		try {
			items.remove(index);
			notifyDataSetChanged();
		} catch (Exception e){
			L.e(e);
		}
	}
	
	/**
	 * Get an item from the position passed
	 * @param position
	 * @return
	 */
	public M getItem(int position) {
		try {
			return items.get(position);
		} catch (Exception e){
			L.e(e);
			return null;
		}
	}
	
	/**
	 * Get the item ID (Position)
	 * @param position
	 * @return
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Get the count
	 * @return
	 */
	@Override
	public int getItemCount() {
		return (items == null) ? 0 : items.size();
	}
}