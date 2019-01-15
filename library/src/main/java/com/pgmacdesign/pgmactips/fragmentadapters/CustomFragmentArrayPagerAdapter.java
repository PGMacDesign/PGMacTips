package com.pgmacdesign.pgmactips.fragmentadapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 *
 * Created by pmacdowell on 2017-03-06.
 */
public class CustomFragmentArrayPagerAdapter <T extends Fragment> extends FragmentPagerAdapter {

    private List<T> mItems = new ArrayList<T>();
    private Map<Integer, T> mItemsMap = new HashMap<>();

    public CustomFragmentArrayPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public T getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    /**
     * Adds the specified fragment at the end of the array.
     * @param fragment
     */
    public void add(T fragment) {
        mItems.add(fragment);
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection of fragments at the end of the array.
     * @param fragments
     */
    public void addAll(Collection<T> fragments) {
        if(fragments == null){
            return;
        }
        mItems.addAll(fragments);
        notifyDataSetChanged();
    }

    /**
     * Adds the specified fragments at the end of the array.
     * @param fragments
     */
    public void addAll(T... fragments) {
        for (T fragment : fragments) {
            mItems.add(fragment);
        }
        notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified fragment at the specified index in the array.
     * @param fragment
     * @param index
     */
    public void insert(T fragment, int index) {
        mItems.add(index, fragment);
        notifyDataSetChanged();
    }

}
