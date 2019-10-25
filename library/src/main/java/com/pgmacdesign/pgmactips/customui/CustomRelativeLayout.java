package com.pgmacdesign.pgmactips.customui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * A custom Relative layout built to include the option to customize the height and width including the
 * ability to set a max value for them
 */
public class CustomRelativeLayout extends RelativeLayout {
	
	//region Vars
	private Context context;
	private AttributeSet attributeSet;
	private boolean checkedValuesOnce;
	private int originalHeight, originalWidth, customHeightPixels, customWidthPixels;
	private float heightPercent, widthPercent;
	private DisplayMetrics displayMetrics;
	//endregion
	
	//region Constructors
	
	public CustomRelativeLayout(@NonNull Context context) {
		super(context);
		this.init(context, null);
	}
	
	public CustomRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.init(context, attrs);
	}
	
	public CustomRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public CustomRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.init(context, attrs);
	}
	
	/**
	 * Initialize
	 *
	 * @param context
	 * @param attributeSet
	 */
	private void init(Context context, AttributeSet attributeSet) {
		this.context = context;
		this.attributeSet = attributeSet;
		this.checkedValuesOnce = false;
		this.setVarDefaults();
		this.setDisplayMetrics();
	}
	
	//endregion
	
	//region Override Functions
	
	/**
	 * Measure to max out max height or width
	 *
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (this.context == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		if (!this.checkedValuesOnce) {
			this.checkedValuesOnce = true;
			this.originalWidth = MeasureSpec.getSize(widthMeasureSpec);
			this.originalHeight = MeasureSpec.getSize(heightMeasureSpec);
		}
		int widthMeasureSpec1 = this.calculateWidth();
		int heightMeasureSpec1 = this.calculateHeight();
		this.setMeasuredDimension(widthMeasureSpec1, heightMeasureSpec1);
		super.onMeasure(widthMeasureSpec1, heightMeasureSpec1);
	}
	
	//endregion
	
	//region Public Methods
	
	/**
	 * Simple overloaded utility that passes 1.0F, 1.0F to resizeViewByPercent(float, float)
	 */
	public void resizeViewToMax() {
		this.resizeViewByPercent(1.0F, 1.0F);
	}
	
	/**
	 * Resize the view to match the percentages passed in
	 *
	 * @param widthPercent
	 * @param heightPercent
	 */
	public void resizeViewByPercent(@FloatRange(from = 0, to = 1) float widthPercent,
	                                @FloatRange(from = 0, to = 1) float heightPercent) {
		this.setVarDefaults(false);
		this.widthPercent = (widthPercent >= 0 && widthPercent <= 1) ? widthPercent : 1.0F;
		this.heightPercent = (heightPercent >= 0 && heightPercent <= 1) ? heightPercent : 1.0F;
		this.requestLayout();
		this.invalidate();
	}
	
	/**
	 * Resize the view by pixels instead of float percentages
	 *
	 * @param pixelsWidth  if value is < 0, will use percent of 100 instead
	 * @param pixelsHeight if value is < 0, will use percent of 100 instead
	 */
	public void resizeViewByPixels(int pixelsWidth,
	                               int pixelsHeight) {
		this.setVarDefaults(true);
		if (pixelsWidth >= 0) {
			this.customWidthPixels = pixelsWidth;
		} else {
			this.widthPercent = 1.0F;
		}
		if (pixelsHeight >= 0) {
			this.customHeightPixels = pixelsHeight;
		} else {
			this.heightPercent = 1.0F;
		}
		this.requestLayout();
		this.invalidate();
	}
	
	/**
	 * Resize the view by Density Pixels instead of float percentages
	 *
	 * @param dpWidth  if value is < 0, will use percent of 100 instead
	 * @param dpHeight if value is < 0, will use percent of 100 instead
	 */
	public void resizeViewByDP(int dpWidth,
	                           int dpHeight) {
		this.setVarDefaults(true);
		if (dpWidth >= 0) {
			this.customWidthPixels = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, dpWidth, this.displayMetrics);
		} else {
			this.widthPercent = 1.0F;
		}
		if (dpHeight >= 0) {
			this.customHeightPixels = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, dpHeight, this.displayMetrics);
		} else {
			this.heightPercent = 1.0F;
		}
		this.requestLayout();
		this.invalidate();
	}
	
	/**
	 * Get the original Height in pixels
	 *
	 * @return original height in pixels
	 */
	public int getOriginalHeight() {
		return this.originalHeight;
	}
	
	/**
	 * Get the original width in pixels
	 *
	 * @return Original width in pixels
	 */
	public int getOriginalWidth() {
		return this.originalWidth;
	}
	
	/**
	 * Get the current Height in pixels
	 *
	 * @return current height in pixels
	 */
	public int getCurrentHeight() {
		return MeasureSpec.getSize(this.calculateHeight());
	}
	
	/**
	 * Get the current width in pixels
	 *
	 * @return current width in pixels
	 */
	public int getCurrentWidth() {
		return MeasureSpec.getSize(this.calculateWidth());
	}
	
	//endregion
	
	//region Private Methods
	
	
	/**
	 * Calculate the Height
	 *
	 * @return
	 */
	private int calculateHeight() {
		if (this.heightPercent >= 0 && this.heightPercent <= 1) {
			return MeasureSpec.makeMeasureSpec(((int) (this.originalHeight * this.heightPercent)), MeasureSpec.EXACTLY);
		} else if (this.customHeightPixels >= 0) {
			return MeasureSpec.makeMeasureSpec(((int) (this.customHeightPixels)), MeasureSpec.EXACTLY);
		} else {
			return MeasureSpec.makeMeasureSpec(((int) (this.originalHeight)), MeasureSpec.EXACTLY);
		}
	}
	
	/**
	 * Calculate the Height
	 *
	 * @return
	 */
	private int calculateWidth() {
		if (this.widthPercent >= 0 && this.widthPercent <= 1) {
			return MeasureSpec.makeMeasureSpec(((int) (this.originalWidth * this.widthPercent)), MeasureSpec.EXACTLY);
		} else if (this.customWidthPixels >= 0) {
			return MeasureSpec.makeMeasureSpec(((int) (this.customWidthPixels)), MeasureSpec.EXACTLY);
		} else {
			return MeasureSpec.makeMeasureSpec(((int) (this.originalWidth)), MeasureSpec.EXACTLY);
		}
	}
	
	/**
	 * Set the defaults whenever an update UI method is called or the view initially loads
	 */
	private void setVarDefaults() {
		this.setVarDefaults(false);
	}
	
	/**
	 * Set the defaults whenever an update UI method is called or the view initially loads
	 *
	 * @param clearPercentsToo if true, resets the widthPercent and heightPercent to zero
	 */
	private void setVarDefaults(boolean clearPercentsToo) {
		if (clearPercentsToo) {
			this.widthPercent = -1F;
			this.heightPercent = -1F;
		} else {
			this.widthPercent = 1.0F;
			this.heightPercent = 1.0F;
		}
		this.customHeightPixels = -1;
		this.customWidthPixels = -1;
	}
	
	/**
	 * Initialize the Display Metrics
	 */
	private void setDisplayMetrics() {
		this.displayMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(this.displayMetrics);
		Display display = windowManager.getDefaultDisplay();
//		Configuration configuration = this.context.getResources().getConfiguration();
		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealMetrics(this.displayMetrics);
		}
	}
	//endregion
}
