package com.pgmacdesign.pgmacutilities.layoutmanagers;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by pmacdowell on 2017-08-23.
 */

public class CustomGridLayoutManager extends GridLayoutManager {

    private int columnWidthPx;
    private int[] mMeasuredDimension = new int[2];

    public CustomGridLayoutManager(Context context, int orientation,
                                   boolean reverseLayout, int columnWidthUnit,
                                   float columnWidth) {
        super(context, 1, orientation, reverseLayout);
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }

        float colWidthPx = TypedValue.applyDimension(columnWidthUnit,
                columnWidth, r.getDisplayMetrics());

        this.columnWidthPx = Math.round(colWidthPx);
    }

    public CustomGridLayoutManager(Context context, AttributeSet attrs,
                                   int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        int[] requestedValues = {
                android.R.attr.columnWidth,
        };

        TypedArray a = context.obtainStyledAttributes(attrs, requestedValues);
        this.columnWidthPx = a.getDimensionPixelSize(0, -1);
        a.recycle();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler,
                                 RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    private void updateSpanCount() {
        int width = super.getWidth();
        if (columnWidthPx > 0) {
            this.setSpanCount((int)Math.floor(width / this.columnWidthPx));
        }
    }

    /*

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                          int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++) {


            if (getOrientation() == HORIZONTAL) {

                measureScrapChild(recycler, i,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        heightSpec, mMeasuredDimension);

                width = width + mMeasuredDimension[0];
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                measureScrapChild(recycler, i,
                        widthSpec,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        mMeasuredDimension);
                height = height + mMeasuredDimension[1];
                if (i == 0) {
                    width = mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        setMeasuredDimension(width, height);
    }


    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec, int[] measuredDimension) {
        View view = null;
        try {
            view = recycler.getViewForPosition(position);
        } catch (Exception e){}
        if(view != null) {
            recycler.bindViewToPosition(view, position);
        }
        if (view != null) {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                    getPaddingLeft() + getPaddingRight(), p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    this.getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec, childHeightSpec);
            measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
    }

    */

}
