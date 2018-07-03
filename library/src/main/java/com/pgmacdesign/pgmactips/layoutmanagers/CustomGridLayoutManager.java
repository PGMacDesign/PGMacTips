package com.pgmacdesign.pgmactips.layoutmanagers;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;

/**
 * Custom Grid layout manager {@link RecyclerView.LayoutManager} that can be used like a grid layout
 * Created by pmacdowell on 2017-08-23.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.AndroidSupport_Design)
public class CustomGridLayoutManager extends GridLayoutManager {

    private int mColumnWidth, mSpanCount;
    private boolean mColumnWidthChanged = true;

    public CustomGridLayoutManager(Context context, int orientation,
                                   boolean reverseLayout, int columnWidthUnit,
                                   float columnWidth) {
        super(context, 1, orientation, reverseLayout);
        this.mColumnWidth = 0;
        this.mSpanCount = 1;
        int xx = checkedColumnWidth(context, columnWidthUnit, columnWidth);
        setColumnWidth(xx);
    }

    private int checkedColumnWidth(Context context,
                                   int columnWidthUnit, float columnWidth) {
        try {
            DisplayMetrics displayMetrics = (context == null) ? Resources.getSystem()
                    .getDisplayMetrics() : context.getResources().getDisplayMetrics();
            float xx = (TypedValue.applyDimension(columnWidthUnit, columnWidth, displayMetrics));
            return (int) xx;
        } catch (Exception e){
            return mColumnWidth;
        }
    }

    public void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth;
            mColumnWidthChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int width = this.getWidth();
        int height = this.getHeight();
        if (mColumnWidthChanged && (mColumnWidth > 0) && (width > 0) && (height > 0)) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                try {
                    totalSpace = width - getPaddingEnd() - getPaddingStart();
                } catch (Exception e){
                    //Will trigger if SDK level < 17 && android:supportsRtl="false"
                    totalSpace = width - getPaddingRight() - getPaddingLeft();
                }
            } else {
                totalSpace = height - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, (totalSpace / mColumnWidth));
            mSpanCount = spanCount;
            setSpanCount(spanCount);
            mColumnWidthChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }

    public int getCustomSpanCount() {
        return mSpanCount;
    }

    /**
     * Calculate the number of columns. Reference link:
     * https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     * @param context
     * @param sizeOfDataList
     * @return
     */
    public static int calculateNumberOfColumns(Context context, int sizeOfDataList) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth =((displayMetrics.widthPixels) / (displayMetrics.density));
        if(sizeOfDataList < 1){
            return 1;
        } else {
            return ((int)(dpWidth / sizeOfDataList));
        }
    }

}
