/*
 * Copyright (C) 2014 Balys Valentukevicius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pgmacdesign.pgmactips.customui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.pgmacdesign.pgmactips.R;

/**
 * Material Ripple layout used in custom layouts (API 14+) that require a ripple effect behind it
 * Code pulled from Balysv's project (all credit to them!) here - https://github.com/balysv/material-ripple.
 * I should note that they did an exceptional job and I cannot take credit for any of this class
 * as they wrote it all; I simple included here so I could not require the dependency
 */
public class PGMaterialRippleLayout extends FrameLayout {
	
	/**
	From the team over at the Github repo - https://github.com/balysv/material-ripple
	app:pgmaterial_rippleOverlay="true"              // if true, ripple is drawn in foreground; false - background
	app:pgmaterial_rippleColor="#ff0000"             // color of ripple
	app:pgmaterial_rippleAlpha="0.1"                 // alpha of ripple
	app:pgmaterial_rippleDimension="10dp"            // radius of hover and starting ripple
	app:pgmaterial_rippleHover="true"                // if true, a hover effect is drawn when view is touched
	app:pgmaterial_rippleRoundedCorners="10dp"       // radius of corners of ripples. Note: it uses software rendering pipeline for API 17 and below
	app:pgmaterial_rippleInAdapter="true"            // if true, PGMaterialRippleLayout will optimize for use in AdapterViews
	app:pgmaterial_rippleDuration="350"              // duration of ripple animation
	app:pgmaterial_rippleFadeDuration="75"           // duration of fade out effect on ripple
	app:pgmaterial_rippleDelayClick="true"           // if true, delays calls to OnClickListeners until ripple effect ends
	app:pgmaterial_rippleBackground="#FFFFFF"        // background under ripple drawable; used with rippleOverlay="false"
	app:pgmaterial_ripplePersistent="true"           // if true, ripple background color persists after animation, until setRadius(0) is called
	
	 */
	
	
	private static final int     DEFAULT_DURATION        = 350;
	private static final int     DEFAULT_FADE_DURATION   = 75;
	private static final float   DEFAULT_DIAMETER_DP     = 35;
	private static final float   DEFAULT_ALPHA           = 0.2f;
	private static final int     DEFAULT_COLOR           = Color.BLACK;
	private static final int     DEFAULT_BACKGROUND      = Color.TRANSPARENT;
	private static final boolean DEFAULT_HOVER           = true;
	private static final boolean DEFAULT_DELAY_CLICK     = true;
	private static final boolean DEFAULT_PERSISTENT      = false;
	private static final boolean DEFAULT_SEARCH_ADAPTER  = false;
	private static final boolean DEFAULT_RIPPLE_OVERLAY  = false;
	private static final int     DEFAULT_ROUNDED_CORNERS = 0;
	
	private static final int  FADE_EXTRA_DELAY = 50;
	private static final long HOVER_DURATION   = 2500;
	
	private final Paint paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Rect bounds = new Rect();
	
	private int      rippleColor;
	private boolean  rippleOverlay;
	private boolean  rippleHover;
	private int      rippleDiameter;
	private int      rippleDuration;
	private int      rippleAlpha;
	private boolean  rippleDelayClick;
	private int      rippleFadeDuration;
	private boolean  ripplePersistent;
	private Drawable rippleBackground;
	private boolean  rippleInAdapter;
	private float    rippleRoundedCorners;
	
	private float radius;
	
	private AdapterView parentAdapter;
	private View childView;
	
	private AnimatorSet rippleAnimator;
	private ObjectAnimator hoverAnimator;
	
	private Point currentCoords  = new Point();
	private Point previousCoords = new Point();
	
	private int layerType;
	
	private boolean eventCancelled;
	private boolean prepressed;
	private int     positionInAdapter;
	
	private GestureDetector gestureDetector;
	private PerformClickEvent pendingClickEvent;
	private PressedEvent      pendingPressEvent;
	
	public static RippleBuilder on(View view) {
		return new RippleBuilder(view);
	}
	
	public PGMaterialRippleLayout(Context context) {
		this(context, null, 0);
	}
	
	public PGMaterialRippleLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PGMaterialRippleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setWillNotDraw(false);
		gestureDetector = new GestureDetector(context, longClickListener);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PGMaterialRippleLayout);
		this.rippleColor = a.getColor(R.styleable.PGMaterialRippleLayout_pg_rippleColor, DEFAULT_COLOR);
	this.rippleDiameter = a.getDimensionPixelSize(
				R.styleable.PGMaterialRippleLayout_pg_rippleDimension,
				(int) dpToPx(getResources(), DEFAULT_DIAMETER_DP)
		);
	    this.rippleOverlay = a.getBoolean(R.styleable.PGMaterialRippleLayout_pg_rippleOverlay, DEFAULT_RIPPLE_OVERLAY);
	    this.rippleHover = a.getBoolean(R.styleable.PGMaterialRippleLayout_pg_rippleHover, DEFAULT_HOVER);
	    this.rippleDuration = a.getInt(R.styleable.PGMaterialRippleLayout_pg_rippleDuration, DEFAULT_DURATION);
	    this.rippleAlpha = (int) (255 * a.getFloat(R.styleable.PGMaterialRippleLayout_pg_rippleAlpha, DEFAULT_ALPHA));
	    this.rippleDelayClick = a.getBoolean(R.styleable.PGMaterialRippleLayout_pg_rippleDelayClick, DEFAULT_DELAY_CLICK);
	    this.rippleFadeDuration = a.getInteger(R.styleable.PGMaterialRippleLayout_pg_rippleFadeDuration, DEFAULT_FADE_DURATION);
	    this.rippleBackground = new ColorDrawable(a.getColor(R.styleable.PGMaterialRippleLayout_pg_rippleBackground, DEFAULT_BACKGROUND));
	    this.ripplePersistent = a.getBoolean(R.styleable.PGMaterialRippleLayout_pg_ripplePersistent, DEFAULT_PERSISTENT);
	    this.rippleInAdapter = a.getBoolean(R.styleable.PGMaterialRippleLayout_pg_rippleInAdapter, DEFAULT_SEARCH_ADAPTER);
	    this.rippleRoundedCorners = a.getDimensionPixelSize(R.styleable.PGMaterialRippleLayout_pg_rippleRoundedCorners, DEFAULT_ROUNDED_CORNERS);
		
		a.recycle();
		
		paint.setColor(rippleColor);
		paint.setAlpha(rippleAlpha);
		
		enableClipPathSupportIfNecessary();
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends View> T getChildView() {
		return (T) childView;
	}
	
	@Override
	public final void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (getChildCount() > 0) {
			throw new IllegalStateException("PGMaterialRippleLayout can host only one child");
		}
		//noinspection unchecked
		childView = child;
		super.addView(child, index, params);
	}
	
	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		if (childView == null) {
			throw new IllegalStateException("PGMaterialRippleLayout must have a child view to handle clicks");
		}
		childView.setOnClickListener(onClickListener);
	}
	
	@Override
	public void setOnLongClickListener(OnLongClickListener onClickListener) {
		if (childView == null) {
			throw new IllegalStateException("PGMaterialRippleLayout must have a child view to handle clicks");
		}
		childView.setOnLongClickListener(onClickListener);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return !findClickableViewInChild(childView, (int) event.getX(), (int) event.getY());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean superOnTouchEvent = super.onTouchEvent(event);
		
		if (!isEnabled() || !childView.isEnabled()) return superOnTouchEvent;
		
		boolean isEventInBounds = bounds.contains((int) event.getX(), (int) event.getY());
		
		if (isEventInBounds) {
			this.previousCoords.set(this.currentCoords.x, this.currentCoords.y);
			this.currentCoords.set((int) event.getX(), (int) event.getY());
		}
		
		boolean gestureResult = this.gestureDetector.onTouchEvent(event);
		if (gestureResult || this.hasPerformedLongPress) {
			return true;
		} else {
			int action = event.getActionMasked();
			switch (action) {
				case MotionEvent.ACTION_UP:
					this.pendingClickEvent = new PerformClickEvent();
					
					if (this.prepressed) {
						this.childView.setPressed(true);
						postDelayed(
								new Runnable() {
									@Override public void run() {
										childView.setPressed(false);
									}
								}, ViewConfiguration.getPressedStateDuration());
					}
					
					if (isEventInBounds) {
						startRipple(this.pendingClickEvent);
					} else if (!this.rippleHover) {
						setRadius(0);
					}
					if (!this.rippleDelayClick && isEventInBounds) {
						this.pendingClickEvent.run();
					}
					cancelPressedEvent();
					break;
				case MotionEvent.ACTION_DOWN:
					setPositionInAdapter();
					this.eventCancelled = false;
					this.pendingPressEvent = new PressedEvent(event);
					if (isInScrollingContainer()) {
						cancelPressedEvent();
						this.prepressed = true;
						postDelayed(this.pendingPressEvent, ViewConfiguration.getTapTimeout());
					} else {
						this.pendingPressEvent.run();
					}
					break;
				case MotionEvent.ACTION_CANCEL:
					if (this.rippleInAdapter) {
						// dont use current coords in adapter since they tend to jump drastically on scroll
						this.currentCoords.set(this.previousCoords.x, this.previousCoords.y);
						this.previousCoords = new Point();
					}
					this.childView.onTouchEvent(event);
					if (this.rippleHover) {
						if (!this.prepressed) {
							startRipple(null);
						}
					} else {
						this.childView.setPressed(false);
					}
					cancelPressedEvent();
					break;
				case MotionEvent.ACTION_MOVE:
					if (this.rippleHover) {
						if (isEventInBounds && !this.eventCancelled) {
							invalidate();
						} else if (!isEventInBounds) {
							startRipple(null);
						}
					}
					
					if (!isEventInBounds) {
						cancelPressedEvent();
						if (this.hoverAnimator != null) {
							this.hoverAnimator.cancel();
						}
						this.childView.onTouchEvent(event);
						this.eventCancelled = true;
					}
					break;
			}
			return true;
		}
	}
	
	private void cancelPressedEvent() {
		if (this.pendingPressEvent != null) {
			removeCallbacks(this.pendingPressEvent);
			this.prepressed = false;
		}
	}
	
	private boolean hasPerformedLongPress;
	private GestureDetector.SimpleOnGestureListener longClickListener = new GestureDetector.SimpleOnGestureListener() {
		public void onLongPress(MotionEvent e) {
			hasPerformedLongPress = childView.performLongClick();
			if (hasPerformedLongPress) {
				if (rippleHover) {
					startRipple(null);
				}
				cancelPressedEvent();
			}
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			hasPerformedLongPress = false;
			return super.onDown(e);
		}
	};
	
	private void startHover() {
		if (this.eventCancelled) return;
		
		if (this.hoverAnimator != null) {
			this.hoverAnimator.cancel();
		}
		final float radius = (float) (Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2)) * 1.2f);
		this.hoverAnimator = ObjectAnimator.ofFloat(this, this.radiusProperty, this.rippleDiameter, radius)
				.setDuration(HOVER_DURATION);
		this.hoverAnimator.setInterpolator(new LinearInterpolator());
		this.hoverAnimator.start();
	}
	
	private void startRipple(final Runnable animationEndRunnable) {
		if (this.eventCancelled){
			return;
		}
		
		float endRadius = getEndRadius();
		
		cancelAnimations();
		
	    this.rippleAnimator = new AnimatorSet();
	    this.rippleAnimator.addListener(new AnimatorListenerAdapter() {
			@Override public void onAnimationEnd(Animator animation) {
				if (!ripplePersistent) {
					setRadius(0);
					setRippleAlpha(rippleAlpha);
				}
				if (animationEndRunnable != null && rippleDelayClick) {
					animationEndRunnable.run();
				}
				childView.setPressed(false);
			}
		});
		
		ObjectAnimator ripple = ObjectAnimator.ofFloat(this, radiusProperty, radius, endRadius);
		ripple.setDuration(rippleDuration);
		ripple.setInterpolator(new DecelerateInterpolator());
		ObjectAnimator fade = ObjectAnimator.ofInt(this, circleAlphaProperty, rippleAlpha, 0);
		fade.setDuration(rippleFadeDuration);
		fade.setInterpolator(new AccelerateInterpolator());
		fade.setStartDelay(rippleDuration - rippleFadeDuration - FADE_EXTRA_DELAY);
		
		if (ripplePersistent) {
		    this.rippleAnimator.play(ripple);
		} else if (getRadius() > endRadius) {
			fade.setStartDelay(0);
		    this.rippleAnimator.play(fade);
		} else {
		    this.rippleAnimator.playTogether(ripple, fade);
		}
	    this.rippleAnimator.start();
	}
	
	private void cancelAnimations() {
		if (rippleAnimator != null) {
		    this.rippleAnimator.cancel();
		    this.rippleAnimator.removeAllListeners();
		}
		
		if (hoverAnimator != null) {
			hoverAnimator.cancel();
		}
	}
	
	private float getEndRadius() {
		final int width = getWidth();
		final int height = getHeight();
		
		final int halfWidth = width / 2;
		final int halfHeight = height / 2;
		
		final float radiusX = halfWidth >this.currentCoords.x ? width -this.currentCoords.x :this.currentCoords.x;
		final float radiusY = halfHeight >this.currentCoords.y ? height -this.currentCoords.y :this.currentCoords.y;
		
		return (float) Math.sqrt(Math.pow(radiusX, 2) + Math.pow(radiusY, 2)) * 1.2f;
	}
	
	private boolean isInScrollingContainer() {
		ViewParent p = getParent();
		while (p != null && p instanceof ViewGroup) {
			if (((ViewGroup) p).shouldDelayChildPressedState()) {
				return true;
			}
			p = p.getParent();
		}
		return false;
	}
	
	private AdapterView findParentAdapterView() {
		if (this.parentAdapter != null) {
			return this.parentAdapter;
		}
		ViewParent current = getParent();
		while (true) {
			if (current instanceof AdapterView) {
				this.parentAdapter = (AdapterView) current;
				return this.parentAdapter;
			} else {
				try {
					current = current.getParent();
				} catch (NullPointerException npe) {
					throw new RuntimeException("Could not find a parent AdapterView");
				}
			}
		}
	}
	
	private void setPositionInAdapter() {
		if (this.rippleInAdapter) {
			this.positionInAdapter = findParentAdapterView().getPositionForView(PGMaterialRippleLayout.this);
		}
	}
	
	private boolean adapterPositionChanged() {
		if (this.rippleInAdapter) {
			int newPosition = findParentAdapterView().getPositionForView(PGMaterialRippleLayout.this);
			final boolean changed = newPosition != positionInAdapter;
			this.positionInAdapter = newPosition;
			if (changed) {
				cancelPressedEvent();
				cancelAnimations();
				this.childView.setPressed(false);
				setRadius(0);
			}
			return changed;
		}
		return false;
	}
	
	private boolean findClickableViewInChild(View view, int x, int y) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				View child = viewGroup.getChildAt(i);
				final Rect rect = new Rect();
				child.getHitRect(rect);
				
				final boolean contains = rect.contains(x, y);
				if (contains) {
					return findClickableViewInChild(child, x - rect.left, y - rect.top);
				}
			}
		} else if (view != this.childView) {
			return (view.isEnabled() && (view.isClickable() || view.isLongClickable() || view.isFocusableInTouchMode()));
		}
		
		return view.isFocusableInTouchMode();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.bounds.set(0, 0, w, h);
	    this.rippleBackground.setBounds(bounds);
	}
	
	@Override
	public boolean isInEditMode() {
		return true;
	}
	
	/*
	 * Drawing
	 */
	@Override
	public void draw(Canvas canvas) {
		final boolean positionChanged = adapterPositionChanged();
		if (this.rippleOverlay) {
			if (!positionChanged) {
			    this.rippleBackground.draw(canvas);
			}
			super.draw(canvas);
			if (!positionChanged) {
				if (this.rippleRoundedCorners != 0) {
					Path clipPath = new Path();
					RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
					clipPath.addRoundRect(rect, this.rippleRoundedCorners, this.rippleRoundedCorners, Path.Direction.CW);
					canvas.clipPath(clipPath);
				}
				canvas.drawCircle(this.currentCoords.x, this.currentCoords.y, this.radius, this.paint);
			}
		} else {
			if (!positionChanged) {
			    this.rippleBackground.draw(canvas);
				canvas.drawCircle(this.currentCoords.x, this.currentCoords.y, this.radius, this.paint);
			}
			super.draw(canvas);
		}
	}
	
	/*
	 * Animations
	 */
	private Property<PGMaterialRippleLayout, Float> radiusProperty
			= new Property<PGMaterialRippleLayout, Float>(Float.class, "radius") {
		@Override
		public Float get(PGMaterialRippleLayout object) {
			return object.getRadius();
		}
		
		@Override
		public void set(PGMaterialRippleLayout object, Float value) {
			object.setRadius(value);
		}
	};
	
	private float getRadius() {
		return this.radius;
	}
	
	
	public void setRadius(float radius) {
		this.radius = radius;
		invalidate();
	}
	
	private Property<PGMaterialRippleLayout, Integer> circleAlphaProperty
			= new Property<PGMaterialRippleLayout, Integer>(Integer.class, "rippleAlpha") {
		@Override
		public Integer get(PGMaterialRippleLayout object) {
			return object.getRippleAlpha();
		}
		
		@Override
		public void set(PGMaterialRippleLayout object, Integer value) {
			object.setRippleAlpha(value);
		}
	};
	
	public int getRippleAlpha() {
		return this.paint.getAlpha();
	}
	
	public void setRippleAlpha(Integer rippleAlpha) {
		paint.setAlpha(rippleAlpha);
		invalidate();
	}
	
	/*
	 * Accessor
	 */
	public void setRippleColor(int rippleColor) {
		this.rippleColor = rippleColor;
		this.paint.setColor(rippleColor);
		this.paint.setAlpha(this.rippleAlpha);
		invalidate();
	}
	
	public void setRippleOverlay(boolean rippleOverlay) {
		this.rippleOverlay = rippleOverlay;
	}
	
	public void setRippleDiameter(int rippleDiameter) {
		this.rippleDiameter = rippleDiameter;
	}
	
	public void setRippleDuration(int rippleDuration) {
		this.rippleDuration = rippleDuration;
	}
	
	public void setRippleBackground(int color) {
	    this.rippleBackground = new ColorDrawable(color);
	    this.rippleBackground.setBounds(bounds);
		invalidate();
	}
	
	public void setRippleHover(boolean rippleHover) {
		this.rippleHover = rippleHover;
	}
	
	public void setRippleDelayClick(boolean rippleDelayClick) {
		this.rippleDelayClick = rippleDelayClick;
	}
	
	public void setRippleFadeDuration(int rippleFadeDuration) {
		this.rippleFadeDuration = rippleFadeDuration;
	}
	
	public void setRipplePersistent(boolean ripplePersistent) {
		this.ripplePersistent = ripplePersistent;
	}
	
	public void setRippleInAdapter(boolean rippleInAdapter) {
		this.rippleInAdapter = rippleInAdapter;
	}
	
	public void setRippleRoundedCorners(int rippleRoundedCorner) {
		this.rippleRoundedCorners = rippleRoundedCorner;
		enableClipPathSupportIfNecessary();
	}
	
	public void setDefaultRippleAlpha(float alpha) {
		this.rippleAlpha = (int) (255 * alpha);
		paint.setAlpha(rippleAlpha);
		invalidate();
	}
	
	public void performRipple() {
		currentCoords = new Point(getWidth() / 2, getHeight() / 2);
		startRipple(null);
	}
	
	public void performRipple(Point anchor) {
		currentCoords = new Point(anchor.x, anchor.y);
		startRipple(null);
	}
	
	/**
	 * {@link Canvas#clipPath(Path)} is not supported in hardware accelerated layers
	 * before API 18. Use software layer instead
	 * <p/>
	 * https://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
	 */
	private void enableClipPathSupportIfNecessary() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (rippleRoundedCorners != 0) {
				layerType = getLayerType();
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			} else {
				setLayerType(layerType, null);
			}
		}
	}
	
	/*
	 * Helper
	 */
	private class PerformClickEvent implements Runnable {
		
		@Override public void run() {
			if (hasPerformedLongPress) return;
			
			// if parent is an AdapterView, try to call its ItemClickListener
			if (getParent() instanceof AdapterView) {
				// try clicking direct child first
				if (!childView.performClick())
					// if it did not handle it dispatch to adapterView
					clickAdapterView((AdapterView) getParent());
			} else if (rippleInAdapter) {
				// find adapter view
				clickAdapterView(findParentAdapterView());
			} else {
				// otherwise, just perform click on child
				childView.performClick();
			}
		}
		
		private void clickAdapterView(AdapterView parent) {
			final int position = parent.getPositionForView(PGMaterialRippleLayout.this);
			final long itemId = parent.getAdapter() != null
					? parent.getAdapter().getItemId(position)
					: 0;
			if (position != AdapterView.INVALID_POSITION) {
				parent.performItemClick(PGMaterialRippleLayout.this, position, itemId);
			}
		}
	}
	
	private final class PressedEvent implements Runnable {
		
		private final MotionEvent event;
		
		public PressedEvent(MotionEvent event) {
			this.event = event;
		}
		
		@Override
		public void run() {
			prepressed = false;
			childView.setLongClickable(false);//prevent the child's long click,let's the ripple layout call it's performLongClick
			childView.onTouchEvent(event);
			childView.setPressed(true);
			if (rippleHover) {
				startHover();
			}
		}
	}
	
	static float dpToPx(Resources resources, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
	}
	
	/*
	 * Builder
	 */
	
	public static class RippleBuilder {
		
		private final Context context;
		private final View    child;
		
		private int     rippleColor         = DEFAULT_COLOR;
		private boolean rippleOverlay       = DEFAULT_RIPPLE_OVERLAY;
		private boolean rippleHover         = DEFAULT_HOVER;
		private float   rippleDiameter      = DEFAULT_DIAMETER_DP;
		private int     rippleDuration      = DEFAULT_DURATION;
		private float   rippleAlpha         = DEFAULT_ALPHA;
		private boolean rippleDelayClick    = DEFAULT_DELAY_CLICK;
		private int     rippleFadeDuration  = DEFAULT_FADE_DURATION;
		private boolean ripplePersistent    = DEFAULT_PERSISTENT;
		private int     rippleBackground    = DEFAULT_BACKGROUND;
		private boolean rippleSearchAdapter = DEFAULT_SEARCH_ADAPTER;
		private float   rippleRoundedCorner = DEFAULT_ROUNDED_CORNERS;
		
		public RippleBuilder(View child) {
			this.child = child;
			this.context = child.getContext();
		}
		
		public RippleBuilder rippleColor(int color) {
			this.rippleColor = color;
			return this;
		}
		
		public RippleBuilder rippleOverlay(boolean overlay) {
			this.rippleOverlay = overlay;
			return this;
		}
		
		public RippleBuilder rippleHover(boolean hover) {
			this.rippleHover = hover;
			return this;
		}
		
		public RippleBuilder rippleDiameterDp(int diameterDp) {
			this.rippleDiameter = diameterDp;
			return this;
		}
		
		public RippleBuilder rippleDuration(int duration) {
			this.rippleDuration = duration;
			return this;
		}
		
		public RippleBuilder rippleAlpha(float alpha) {
			this.rippleAlpha = alpha;
			return this;
		}
		
		public RippleBuilder rippleDelayClick(boolean delayClick) {
			this.rippleDelayClick = delayClick;
			return this;
		}
		
		public RippleBuilder rippleFadeDuration(int fadeDuration) {
			this.rippleFadeDuration = fadeDuration;
			return this;
		}
		
		public RippleBuilder ripplePersistent(boolean persistent) {
			this.ripplePersistent = persistent;
			return this;
		}
		
		public RippleBuilder rippleBackground(int color) {
			this.rippleBackground = color;
			return this;
		}
		
		public RippleBuilder rippleInAdapter(boolean inAdapter) {
			this.rippleSearchAdapter = inAdapter;
			return this;
		}
		
		public RippleBuilder rippleRoundedCorners(int radiusDp) {
			this.rippleRoundedCorner = radiusDp;
			return this;
		}
		
		public PGMaterialRippleLayout create() {
			PGMaterialRippleLayout layout = new PGMaterialRippleLayout(context);
			layout.setRippleColor(rippleColor);
			layout.setDefaultRippleAlpha(rippleAlpha);
			layout.setRippleDelayClick(rippleDelayClick);
			layout.setRippleDiameter((int) dpToPx(context.getResources(), rippleDiameter));
			layout.setRippleDuration(rippleDuration);
			layout.setRippleFadeDuration(rippleFadeDuration);
			layout.setRippleHover(rippleHover);
			layout.setRipplePersistent(ripplePersistent);
			layout.setRippleOverlay(rippleOverlay);
			layout.setRippleBackground(rippleBackground);
			layout.setRippleInAdapter(rippleSearchAdapter);
			layout.setRippleRoundedCorners((int) dpToPx(context.getResources(), rippleRoundedCorner));
			
			ViewGroup.LayoutParams params = child.getLayoutParams();
			ViewGroup parent = (ViewGroup) child.getParent();
			int index = 0;
			
			if (parent != null && parent instanceof PGMaterialRippleLayout) {
				throw new IllegalStateException("PGMaterialRippleLayout could not be created: parent of the view already is a PGMaterialRippleLayout");
			}
			
			if (parent != null) {
				index = parent.indexOfChild(child);
				parent.removeView(child);
			}
			
			layout.addView(child, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			
			if (parent != null) {
				parent.addView(layout, index, params);
			}
			
			return layout;
		}
	}
}
