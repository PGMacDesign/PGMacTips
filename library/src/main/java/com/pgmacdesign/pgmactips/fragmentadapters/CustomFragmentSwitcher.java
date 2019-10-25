package com.pgmacdesign.pgmactips.fragmentadapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pgmacdesign.pgmactips.BuildConfig;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities;

import androidx.core.os.ParcelableCompat;
import androidx.core.os.ParcelableCompatCreatorCallbacks;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Custom Fragment State array adapter. This will persist not only the savedInstanceState
 * {@link android.app.Fragment.SavedState} but also any user-chosen objects via the
 * Database Utilities class {@link com.pgmacdesign.pgmactips.utilities.DatabaseUtilities}
 * Built using this as a base: https://github.com/jacobtabak/Fragment-Switcher
 * Created by pmacdowell on 2017-03-06.
 */

public class CustomFragmentSwitcher extends FrameLayout {

    private static final String TAG = "CustomFragmentSwitcher";
    private Fragment mCurrentFragment;
    private PagerAdapter mAdapter;
    private PagerObserver mObserver;
    private int mExpectedAdapterCount;
    private boolean mPopulatePending;
    private boolean mFirstLayout;
    private int mRestoredCurItem;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private boolean mInLayout;
    private int mCurrentPosition;
    private OnPageChangeListener mOnPageChangeListener;

    //Custom objects
    private OnTaskCompleteListener listener;
    private DatabaseUtilities dbUtils;

    public CustomFragmentSwitcher(Context context) {
        super(context);
    }

    public CustomFragmentSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFragmentSwitcher(Context context, AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomFragmentSwitcher(Context context, AttributeSet attrs, @AttrRes int defStyleAttr,
                                  @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCustom();
    }

    private void initCustom(){
        this.listener = null;
        this.dbUtils = null;
    }

    public void setCustomListener(@NonNull OnTaskCompleteListener listener){
        this.listener = listener;
    }

    public void setCustomDB(@NonNull DatabaseUtilities dbUtils){
        this.dbUtils = dbUtils;
    }

    /**
     * Set a PagerAdapter that will supply views for this pager as needed.
     *
     * @param adapter Adapter to use
     */
    public void setAdapter(@NonNull PagerAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
            mAdapter.startUpdate(this);
            mAdapter.destroyItem(this, mCurrentPosition, mCurrentFragment);
            mAdapter.finishUpdate(this);
            mCurrentPosition = 0;
        }

        mAdapter = adapter;
        mExpectedAdapterCount = 0;

        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new PagerObserver();
            }
            mAdapter.registerDataSetObserver(mObserver);
            mPopulatePending = false;
            final boolean wasFirstLayout = mFirstLayout;
            mFirstLayout = true;
            mExpectedAdapterCount = mAdapter.getCount();
            if (mRestoredCurItem >= 0) {
                mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
                setCurrentItemInternal(mRestoredCurItem, true);
                mRestoredCurItem = -1;
                mRestoredAdapterState = null;
                mRestoredClassLoader = null;
            } else if (!wasFirstLayout) {
                populate();
            } else {
                requestLayout();
            }
        }
    }

    private class PagerObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }
        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    public int getCurrentItem() {
        return mCurrentPosition;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    /**
     * Set the currently selected page.
     *
     * @param item Item index to select
     */
    public void setCurrentItem(int item) {
        try {
            setCurrentItemInternal(item, false);
        } catch (Exception e){
            e.printStackTrace();
            if(this.listener != null){
                this.listener.onTaskComplete(e.getMessage(),
                        PGMacTipsConstants.TAG_FRAGMENT_SWITCHER_ERROR);
            }
        }
    }

    private void setCurrentItemInternal(int item, boolean always) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            return;
        }
        if (!always && mCurrentPosition == item && mCurrentFragment != null) {
            return;
        }

        if (item < 0) {
            item = 0;
        } else if (item >= mAdapter.getCount()) {
            item = mAdapter.getCount() - 1;
        }

        if (mFirstLayout) {
            // We don't have any idea how big we are yet and shouldn't have any pages either.
            // Just set things up and let the pending layout handle things.
            mCurrentPosition = item;
            requestLayout();
        } else {
            populate(item);
        }
    }

    /**
     * Callback interface for responding to changing state of the selected page.
     */
    public interface OnPageChangeListener {
        public void onPageChanged(int page);
    }

    private Fragment addNewItem(int position) throws IllegalStateException {
        try {
            return (Fragment) mAdapter.instantiateItem(this, position);
        } catch (ClassCastException e) {
            throw new IllegalStateException("FragmentSwitcher's adapter must instantiate fragments");
        }
    }

    private void dataSetChanged() {
        // This method only gets called if our observer is attached, so mAdapter is non-null.

        final int adapterCount = mAdapter.getCount();
        mExpectedAdapterCount = adapterCount;
        boolean needPopulate = mCurrentFragment == null;
        int newCurrItem = mCurrentPosition;

        boolean isUpdating = false;
        final int newPos = mAdapter.getItemPosition(mCurrentFragment);

        if (newPos == PagerAdapter.POSITION_NONE) {
            if (!isUpdating) {
                mAdapter.startUpdate(this);
                isUpdating = true;
            }

            mAdapter.destroyItem(this, mCurrentPosition, mCurrentFragment);
            mCurrentFragment = null;

            // Keep the current item in the valid range
            newCurrItem = Math.max(0, Math.min(mCurrentPosition, adapterCount - 1));
            needPopulate = true;
        } else if (mCurrentPosition != newPos) {
            // Our current item changed position. Follow it.
            newCurrItem = newPos;
            needPopulate = true;
        }

        if (isUpdating) {
            mAdapter.finishUpdate(this);
        }

        if (needPopulate) {
            setCurrentItemInternal(newCurrItem, true);
            requestLayout();
        }
    }

    private void populate() {
        populate(mCurrentPosition);
    }

    private void populate(int position) {
        if (mAdapter == null) {
            return;
        }

        // Bail now if we are waiting to populate.  This is to hold off
        // on creating views from the time the user releases their finger to
        // fling to a new position until we have finished the scroll to
        // that position, avoiding glitches from happening at that point.
        if (mPopulatePending) {
            if (BuildConfig.DEBUG) Log.i(TAG, "populate is pending, skipping for now...");
            return;
        }

        // Also, don't populate until we are attached to a window.  This is to
        // avoid trying to populate before we have restored our view hierarchy
        // state and conflicting with what is restored.
        if (getWindowToken() == null) {
            return;
        }

        final int N = mAdapter.getCount();

        if (N != mExpectedAdapterCount) {
            String resName;
            try {
                resName = getResources().getResourceName(getId());
            } catch (Resources.NotFoundException e) {
                resName = Integer.toHexString(getId());
            }
            throw new IllegalStateException("The application's PagerAdapter changed the adapter's" +
                    " contents without calling PagerAdapter#notifyDataSetChanged!" +
                    " Expected adapter item count: " + mExpectedAdapterCount + ", found: " + N +
                    " Pager id: " + resName +
                    " Pager class: " + getClass() +
                    " Problematic adapter: " + mAdapter.getClass());
        }

        mAdapter.startUpdate(this);

        if (mCurrentFragment != null && mCurrentPosition != position) {
            mAdapter.destroyItem(this, mCurrentPosition, mCurrentFragment);
        }

        // Locate the currently focused item or add it if needed.
        if ((mCurrentFragment == null || mCurrentPosition != position) && mAdapter.getCount() > 0) {
            mCurrentFragment = addNewItem(position);
            mCurrentPosition = position;
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageChanged(mCurrentPosition);
            }
        }

        mAdapter.setPrimaryItem(this, mCurrentPosition, mCurrentFragment);

        mAdapter.finishUpdate(this);

        if(this.dbUtils != null && this.listener != null){
            /*
            todo this is where code should go for DB info passed back
            As of now, these will throw exceptions due to fragments not being serializable. Need to research other methods
            try {
                Object object = dbUtils.getPersistedObject(mCurrentFragment.getClass());
                if(object != null){
                    listener.onTaskComplete(object, PGMacTipsConstants.TAG_FRAGMENT_SWITCHER_OBJECT);
                }
            } catch (Exception e){
                e.printStackTrace();
                listener.onTaskComplete(null, PGMacTipsConstants.TAG_FRAGMENT_SWITCHER_NO_OBJECT);
            }
            */
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mInLayout = true;
        populate();
        mInLayout = false;
    }

    /**
     * This is the persistent state that is saved by FragmentSwitcher.
     */
    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "FragmentSwitcher.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = mCurrentPosition;
        if (mAdapter != null) {
            ss.adapterState = mAdapter.saveState();
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mAdapter != null) {
            mAdapter.restoreState(ss.adapterState, ss.loader);
            setCurrentItemInternal(ss.position, true);
        } else {
            mRestoredCurItem = ss.position;
            mRestoredAdapterState = ss.adapterState;
            mRestoredClassLoader = ss.loader;
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        }
        if (mInLayout) {
            addViewInLayout(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    @Override
    public void removeView(View view) {
        if (mInLayout) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mFirstLayout = false;
    }

    /**
     * Retrieve the current adapter supplying pages.
     *
     * @return The currently registered PagerAdapter
     */
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Set a listener that will be invoked whenever the page changes or is incrementally
     * scrolled. See {@link OnPageChangeListener}.
     *
     * @param listener Listener to set
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }
}
