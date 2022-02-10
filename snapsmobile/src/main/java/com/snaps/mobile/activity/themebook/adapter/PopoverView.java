/**
 * Popover View
 *
 * Copyright 2012 Daniel Lupia�ez Casares <lupidan@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/

package com.snaps.mobile.activity.themebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.spc.view.SnapsCardTextView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.util.HashMap;
import java.util.Map;

public class PopoverView extends RelativeLayout implements OnTouchListener {

	// ********************************************************************
	// INTERFACES
	// ********************************************************************
	/**
	 * Interface to get information from the popover view. Use setDelegate to have access to this methods
	 */
	public static interface PopoverViewDelegate {
		/**
		 * Called when the popover is going to show
		 * 
		 * @param view
		 *            The whole popover view
		 */
		void popoverViewWillShow(PopoverView view);

		/**
		 * Called when the popover did show
		 * 
		 * @param view
		 *            The whole popover view
		 */
		void popoverViewDidShow(PopoverView view);

		/**
		 * Called when the popover is going to be dismissed
		 * 
		 * @param view
		 *            The whole popover view
		 */
		void popoverViewWillDismiss(PopoverView view);

		/**
		 * Called when the popover was dismissed
		 * 
		 * @param view
		 *            The whole popover view
		 */
		void popoverViewDidDismiss(PopoverView view);
		/**
		 * 
		 * @param view
		 * @param viewID
		 */
		void popoverViewDidClick(PopoverView view, int viewID);
	}

	// ********************************************************************
	// STATIC MEMBERS
	// ********************************************************************
	/**
	 * Popover arrow points up. Integer to use with bit operators to tell the popover where the arrow should appear and from where the popover should appear
	 */
	public final static int PopoverArrowDirectionUp = 0x00000001;
	/**
	 * Popover arrow points down. Integer to use with bit operators to tell the popover where the arrow should appear and from where the popover should appear
	 */
	public final static int PopoverArrowDirectionDown = 0x00000002;
	/**
	 * Popover arrow points left. Integer to use with bit operators to tell the popover where the arrow should appear and from where the popover should appear
	 */
	public final static int PopoverArrowDirectionLeft = 0x00000004;
	/**
	 * Popover arrow points right. Integer to use with bit operators to tell the popover where the arrow should appear and from where the popover should appear
	 */
	public final static int PopoverArrowDirectionRight = 0x00000008;
	/**
	 * Popover arrow points any direction. Integer to use with bit operators to tell the popover where the arrow should appear and from where the popover should appear
	 */
	public final static int PopoverArrowDirectionAny = PopoverArrowDirectionUp | PopoverArrowDirectionDown | PopoverArrowDirectionLeft | PopoverArrowDirectionRight;

	/**
	 * The default popover background drawable for all the popovers
	 */
	public static int defaultPopoverBackgroundDrawable = R.drawable.background_popover;

	// ********************************************************************
	// STATIC METHODS
	// ********************************************************************
	/**
	 * Get the Rect frame for a view (relative to the Window of the application)
	 * 
	 * @param v
	 *            The view to get the rect from
	 * @return The rect of the view, relative to the application window
	 */
	public static Rect getFrameForView(View v) {
		int location[] = new int[2];
		v.getLocationOnScreen(location);

		Rect viewRect = new Rect(location[0], location[1] * 2, location[0] + v.getWidth(), location[1] + v.getHeight());
		return viewRect;
	}

	// ********************************************************************
	// MEMBERS
	// ********************************************************************
	/**
	 * The delegate of the view
	 */
	private PopoverViewDelegate delegate;
	/**
	 * The main popover containing the view we want to show
	 */
	private RelativeLayout popoverView;
	
	private ImageView arrowImageView;
	/**
	 * The view group storing this popover. We need this so, when we dismiss the popover, we remove it from the view group
	 */
	private ViewGroup superview;
	/**
	 * The content size for the view in the popover
	 */
	private Point contentSizeForViewInPopover = new Point(0, 0);
	/**
	 * The real content size we will use (it considers the padding)
	 */
	private Point realContentSize = new Point(0, 0);
	/**
	 * A hash containing
	 */
	private Map<Integer, Rect> possibleRects;
	/**
	 * Whether the view is animating or not
	 */
	private boolean isAnimating = false;
	/**
	 * The fade animation time in milliseconds
	 */
	private int fadeAnimationTime = 300;
	/**
	 * The layout Rect, is the same as the superview rect
	 */
	private Rect popoverLayoutRect;
	/**
	 * The popover background drawable
	 */
	private int popoverBackgroundDrawable;
	/**
	 * The popover arrow up drawable
	 */
	private int popoverArrowUpDrawable;
	/**
	 * The popover arrow down drawable
	 */
	private int popoverArrowDownDrawable;
	/**
	 * The popover arrow left drawable
	 */
	private int popoverArrowLeftDrawable;
	/**
	 * The popover arrow down drawable
	 */
	private int popoverArrowRightDrawable;
	
	private View convertView;
	
	private SnapsCardTextView cardTextView;

	// ********************************************************************
	// CONSTRUCTORS
	// ********************************************************************
	/**
	 * Constructor to create a popover with a popover view
	 * 
	 * @param context
	 *            The context where we should create the popover view
	 * @param layoutId
	 *            The ID of the layout we want to put inside the popover
	 */
	public PopoverView(Context context, int layoutId) {
		super(context);
		initPopoverView(inflate(context, layoutId, null));
	}

	/**
	 * Constructor to create a popover with a popover view
	 * 
	 * @param context
	 *            The context where we should create the popover view
	 * @param attrs
	 *            Attribute set to init the view
	 * @param layoutId
	 *            The ID of the layout we want to put inside the popover
	 */
	public PopoverView(Context context, AttributeSet attrs, int layoutId) {
		super(context, attrs);
		initPopoverView(inflate(context, layoutId, null));
	}

	/**
	 * Constructor to create a popover with a popover view
	 * 
	 * @param context
	 *            The context where we should create the popover view
	 * @param attrs
	 *            Attribute set to init the view
	 * @param defStyle
	 *            The default style for this view
	 * @param layoutId
	 *            The ID of the layout we want to put inside the popover
	 */
	public PopoverView(Context context, AttributeSet attrs, int defStyle, int layoutId) {
		super(context, attrs, defStyle);
		initPopoverView(inflate(context, layoutId, null));
	}

	/**
	 * Constructor to create a popover with a popover view
	 * 
	 * @param context
	 *            The context where we should create the popover view
	 * @param popoverView
	 *            The inner view we want to show in a popover
	 */
	public PopoverView(Context context, View popoverView) {
		super(context);
		initPopoverView(popoverView);
	}

	/**
	 * Constructor to create a popover with a popover view
	 * 
	 * @param context
	 *            The context where we should create the popover view
	 * @param attrs
	 *            Attribute set to init the view
	 * @param popoverView
	 *            The inner view we want to show in a popover
	 */
	public PopoverView(Context context, AttributeSet attrs, View popoverView) {
		super(context, attrs);
		initPopoverView(popoverView);
	}

	/**
	 * Constructor to create a popover with a popover view
	 * 
	 * @param context
	 *            The context where we should create the popover view
	 * @param attrs
	 *            Attribute set to init the view
	 * @param defStyle
	 *            The default style for this view
	 * @param popoverView
	 *            The inner view we want to show in a popover
	 */
	public PopoverView(Context context, AttributeSet attrs, int defStyle, View popoverView) {
		super(context, attrs, defStyle);
		initPopoverView(popoverView);
	}

	/**
	 * Init the popover view
	 * 
	 * @param viewToEnclose
	 *            The view we wan to insert inside the popover
	 */
	private void initPopoverView(View viewToEnclose) {

		convertView = viewToEnclose;
		
		// Configure self
		setBackgroundColor(0x00000000);
		setOnTouchListener(this);

		// Set initial drawables
		popoverBackgroundDrawable = PopoverView.defaultPopoverBackgroundDrawable;

		// Init the relative layout
		popoverView = new RelativeLayout(getContext());
		
		arrowImageView = (ImageView) viewToEnclose.findViewById(R.id.pop_menu_uparrow);

		popoverView.addView(viewToEnclose, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	}
	
	public View getConvertView() {
		return convertView;
	}

	// ********************************************************************
	// PRIVATE METHODS
	// ********************************************************************
	/**
	 * Add the popover to the view with a defined rect inside the popover
	 * 
	 * @param insertRect
	 *            The rect we want to insert the view
	 */
	private void addPopoverInRect(Rect insertRect) {
		// Set layout params
		LayoutParams insertParams = new LayoutParams(insertRect.width(), insertRect.height());
		insertParams.leftMargin = insertRect.left;
		insertParams.topMargin = insertRect.top;
		// Add the view
		addView(popoverView, insertParams);

	}

	private void addPopoverInRectByCenter(Rect insertRect) {
		// Set layout params
		LayoutParams insertParams = new LayoutParams(insertRect.width(), insertRect.height());
		insertParams.leftMargin = insertRect.left;
		insertParams.topMargin = insertRect.top;
		// Add the view
		addView(popoverView, insertParams);

	}

	private void addArrow(Rect originRect, Integer arrowDirection) {
		// Add arrow drawable
		ImageView arrowImageView = new ImageView(getContext());
		Drawable arrowDrawable = null;
		int xPos = 0;
		int arrowWidth = 0;
		int yPos = 0;
		int arrowHeight = 0;
		// Get correct drawable, and get Width, Height, Xpos and yPos depending on the selected arrow direction
		if (arrowDirection == PopoverView.PopoverArrowDirectionUp) {
			arrowDrawable = getResources().getDrawable(popoverArrowUpDrawable);
			arrowWidth = arrowDrawable.getIntrinsicWidth();
			arrowHeight = arrowDrawable.getIntrinsicHeight();
			xPos = originRect.centerX() - (arrowWidth / 2) - popoverLayoutRect.left;
			yPos = originRect.bottom - popoverLayoutRect.top;
		} else if (arrowDirection == PopoverView.PopoverArrowDirectionDown) {
			arrowDrawable = getResources().getDrawable(popoverArrowDownDrawable);
			arrowWidth = arrowDrawable.getIntrinsicWidth();
			arrowHeight = arrowDrawable.getIntrinsicHeight();
			xPos = originRect.centerX() - (arrowWidth / 2) - popoverLayoutRect.left;
			yPos = originRect.top - arrowHeight - popoverLayoutRect.top;
		} else if (arrowDirection == PopoverView.PopoverArrowDirectionLeft) {
			arrowDrawable = getResources().getDrawable(popoverArrowLeftDrawable);
			arrowWidth = arrowDrawable.getIntrinsicWidth();
			arrowHeight = arrowDrawable.getIntrinsicHeight();
			xPos = originRect.right - popoverLayoutRect.left;
			yPos = originRect.centerY() - (arrowHeight / 2) - popoverLayoutRect.top;
		} else if (arrowDirection == PopoverView.PopoverArrowDirectionRight) {
			arrowDrawable = getResources().getDrawable(popoverArrowRightDrawable);
			arrowWidth = arrowDrawable.getIntrinsicWidth();
			arrowHeight = arrowDrawable.getIntrinsicHeight();
			xPos = originRect.left - arrowWidth - popoverLayoutRect.left;
			yPos = originRect.centerY() - (arrowHeight / 2) - popoverLayoutRect.top;
		}
		// Set drawable
		arrowImageView.setImageDrawable(arrowDrawable);
		// Init layout params
		LayoutParams arrowParams = new LayoutParams(arrowWidth, arrowHeight);
		arrowParams.leftMargin = xPos;
		arrowParams.topMargin = yPos;
		// add view :)
		addView(arrowImageView, arrowParams);
	}

	/**
	 * Calculates the rect for showing the view with Arrow Up
	 * 
	 * @param originRect
	 *            The origin rect
	 * @return The calculated rect to show the view
	 */
	private Rect getRectForArrowUp(Rect originRect) {

		// Get available space
		int xAvailable = popoverLayoutRect.width();
		if (xAvailable < 0)
			xAvailable = 0;
		int yAvailable = popoverLayoutRect.height() - (originRect.bottom - popoverLayoutRect.top);
		if (yAvailable < 0)
			yAvailable = 0;

		// Get final width and height
		int finalX = xAvailable;
		if ((realContentSize.x > 0) && (realContentSize.x < finalX))
			finalX = realContentSize.x;
		int finalY = yAvailable;
			finalY = realContentSize.y;

		// Get final origin X and Y
		int originX = (originRect.centerX() - popoverLayoutRect.left) - (finalX / 2);
		if (originX < 0)
			originX = 0;
		else if (originX + finalX > popoverLayoutRect.width())
			originX = popoverLayoutRect.width() - finalX;
		int originY = (originRect.bottom - popoverLayoutRect.top);

		int finalRectY = originY - originRect.height() / 2;
		// Create rect
		Rect finalRect = new Rect(originX, finalRectY, originX + finalX, finalRectY + finalY);
		// And return
		return finalRect;

	}

	/**
	 * Calculates the rect for showing the view with Arrow Down
	 * 
	 * @param originRect
	 *            The origin rect
	 * @return The calculated rect to show the view
	 */
	private Rect getRectForArrowDown(Rect originRect) {

		// Get available space
		int xAvailable = popoverLayoutRect.width();
		if (xAvailable < 0)
			xAvailable = 0;
		int yAvailable = (originRect.top - popoverLayoutRect.top);
		if (yAvailable < 0)
			yAvailable = 0;

		// Get final width and height
		int finalX = xAvailable;
		if ((realContentSize.x > 0) && (realContentSize.x < finalX))
			finalX = realContentSize.x;
		int finalY = yAvailable;
		if ((realContentSize.y > 0) && (realContentSize.y < finalY))
			finalY = realContentSize.y;

		// Get final origin X and Y
		int originX = (originRect.centerX() - popoverLayoutRect.left) - (finalX / 2);
		if (originX < 0)
			originX = 0;
		else if (originX + finalX > popoverLayoutRect.width())
			originX = popoverLayoutRect.width() - finalX;
		int originY = (originRect.top - popoverLayoutRect.top) - finalY;

		// Create rect
		Rect finalRect = new Rect(originX, originY, originX + finalX, originY + finalY);
		// And return
		return finalRect;

	}

	/**
	 * Calculates the rect for showing the view with Arrow Right
	 * 
	 * @param originRect
	 *            The origin rect
	 * @return The calculated rect to show the view
	 */
	private Rect getRectForArrowRight(Rect originRect) {
		// Get available space
		int xAvailable = (originRect.left - popoverLayoutRect.left);
		if (xAvailable < 0)
			xAvailable = 0;
		int yAvailable = popoverLayoutRect.height();
		if (yAvailable < 0)
			yAvailable = 0;

		// Get final width and height
		int finalX = xAvailable;
		if ((realContentSize.x > 0) && (realContentSize.x < finalX))
			finalX = realContentSize.x;
		int finalY = yAvailable;
		if ((realContentSize.y > 0) && (realContentSize.y < finalY))
			finalY = realContentSize.y;

		// Get final origin X and Y
		int originX = (originRect.left - popoverLayoutRect.left) - finalX;
		int originY = (originRect.centerY() - popoverLayoutRect.top) - (finalY / 2);
		if (originY < 0)
			originY = 0;
		else if (originY + finalY > popoverLayoutRect.height())
			originY = popoverLayoutRect.height() - finalY;

		// Create rect
		Rect finalRect = new Rect(originX, originY, originX + finalX, originY + finalY);
		// And return
		return finalRect;
	}

	/**
	 * Calculates the rect for showing the view with Arrow Left
	 * 
	 * @param originRect
	 *            The origin rect
	 * @return The calculated rect to show the view
	 */
	private Rect getRectForArrowLeft(Rect originRect) {
		// Get available space
		int xAvailable = popoverLayoutRect.width() - (originRect.right - popoverLayoutRect.left);
		if (xAvailable < 0)
			xAvailable = 0;
		int yAvailable = popoverLayoutRect.height();
		if (yAvailable < 0)
			yAvailable = 0;

		// Get final width and height
		int finalX = xAvailable;
		if ((realContentSize.x > 0) && (realContentSize.x < finalX))
			finalX = realContentSize.x;
		int finalY = yAvailable;
		if ((realContentSize.y > 0) && (realContentSize.y < finalY))
			finalY = realContentSize.y;

		// Get final origin X and Y
		int originX = (originRect.right - popoverLayoutRect.left);
		int originY = (originRect.centerY() - popoverLayoutRect.top) - (finalY / 2);
		if (originY < 0)
			originY = 0;
		else if (originY + finalY > popoverLayoutRect.height())
			originY = popoverLayoutRect.height() - finalY;

		// Create rect
		Rect finalRect = new Rect(originX, originY, originX + finalX, originY + finalY);
		// And return
		return finalRect;
	}

	/**
	 * Add available rects for each selected arrow direction
	 * 
	 * @param originRect
	 *            The rect where the popover will appear from
	 * @param arrowDirections
	 *            The bit mask for the possible arrow directions
	 */
	private void addAvailableRects(Rect originRect, int arrowDirections) {
		// Get popover rects for the available directions
		possibleRects = new HashMap<Integer, Rect>();
		if ((arrowDirections & PopoverView.PopoverArrowDirectionUp) != 0) {
			possibleRects.put(PopoverView.PopoverArrowDirectionUp, getRectForArrowUp(originRect));
		}
		if ((arrowDirections & PopoverView.PopoverArrowDirectionDown) != 0) {
			possibleRects.put(PopoverView.PopoverArrowDirectionDown, getRectForArrowDown(originRect));
		}
		if ((arrowDirections & PopoverView.PopoverArrowDirectionRight) != 0) {
			possibleRects.put(PopoverView.PopoverArrowDirectionRight, getRectForArrowRight(originRect));
		}
		if ((arrowDirections & PopoverView.PopoverArrowDirectionLeft) != 0) {
			possibleRects.put(PopoverView.PopoverArrowDirectionLeft, getRectForArrowLeft(originRect));
		}
	}

	/**
	 * Get the best available rect (bigger area)
	 * 
	 * @return The Integer key to get the Rect from posibleRects (PopoverArrowDirectionUp,PopoverArrowDirectionDown,PopoverArrowDirectionRight or PopoverArrowDirectionLeft)
	 */
	private Integer getBestRect() {
		// Get the best one (bigger area)
		Integer best = null;
		for (Integer arrowDir : possibleRects.keySet()) {
			if (best == null) {
				best = arrowDir;
			} else {
				Rect bestRect = possibleRects.get(best);
				Rect checkRect = possibleRects.get(arrowDir);
				if ((bestRect.width() * bestRect.height()) < (checkRect.width() * checkRect.height()))
					best = arrowDir;
			}
		}
		return best;
	}

	// ********************************************************************
	// GETTERS AND SETTERS
	// ********************************************************************
	/**
	 * Gets the current fade animation time
	 * 
	 * @return The fade animation time, in milliseconds
	 */
	public int getFadeAnimationTime() {
		return fadeAnimationTime;
	}

	/**
	 * Sets the fade animation time
	 * 
	 * @param fadeAnimationTime
	 *            The time in milliseconds
	 */
	public void setFadeAnimationTime(int fadeAnimationTime) {
		this.fadeAnimationTime = fadeAnimationTime;
	}

	/**
	 * Get the content size for view in popover
	 * 
	 * @return The point with the content size
	 */
	public Point getContentSizeForViewInPopover() {
		return contentSizeForViewInPopover;
	}

	/**
	 * Sets the content size for the view in a popover, if point is (0,0) the popover will full the screen
	 * 
	 * @param contentSizeForViewInPopover
	 */
	public void setContentSizeForViewInPopover(Point contentSizeForViewInPopover) {
		this.contentSizeForViewInPopover = contentSizeForViewInPopover;
		// Save the real content size
		realContentSize = new Point(contentSizeForViewInPopover);
		realContentSize.x += popoverView.getPaddingLeft() + popoverView.getPaddingRight();
		// realContentSize.y += popoverView.getPaddingTop() + popoverView.getPaddingBottom();

	}

	/**
	 * Gets the current delegate
	 * 
	 * @return The current delegate
	 */
	public PopoverViewDelegate getDelegate() {
		return delegate;
	}

	/**
	 * Sets the popover delegate
	 * 
	 * @param delegate
	 *            The new popover delegate
	 */
	public void setDelegate(PopoverViewDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * @return Current background drawable
	 */
	public int getPopoverBackgroundDrawable() {
		return popoverBackgroundDrawable;
	}

	/**
	 * @param popoverBackgroundDrawable
	 *            The new background drawable
	 */
	public void setPopoverBackgroundDrawable(int popoverBackgroundDrawable) {
		this.popoverBackgroundDrawable = popoverBackgroundDrawable;
	}

	/**
	 * @return Current arrow up drawable
	 */
	public int getPopoverArrowUpDrawable() {
		return popoverArrowUpDrawable;
	}

	/**
	 * @param popoverArrowUpDrawable
	 *            The new arrow up drawable
	 */
	public void setPopoverArrowUpDrawable(int popoverArrowUpDrawable) {
		this.popoverArrowUpDrawable = popoverArrowUpDrawable;
	}

	/**
	 * @return Current arrow down drawable
	 */
	public int getPopoverArrowDownDrawable() {
		return popoverArrowDownDrawable;
	}

	/**
	 * @param popoverArrowDownDrawable
	 *            The new arrow down drawable
	 */
	public void setPopoverArrowDownDrawable(int popoverArrowDownDrawable) {
		this.popoverArrowDownDrawable = popoverArrowDownDrawable;
	}

	/**
	 * @return Current arrow left drawable
	 */
	public int getPopoverArrowLeftDrawable() {
		return popoverArrowLeftDrawable;
	}

	/**
	 * @param popoverArrowLeftDrawable
	 *            The new arrow left drawable
	 */
	public void setPopoverArrowLeftDrawable(int popoverArrowLeftDrawable) {
		this.popoverArrowLeftDrawable = popoverArrowLeftDrawable;
	}

	/**
	 * @return Current arrow right drawable
	 */
	public int getPopoverArrowRightDrawable() {
		return popoverArrowRightDrawable;
	}

	/**
	 * @param popoverArrowRightDrawable
	 *            The new arrow right drawable
	 */
	public void setPopoverArrowRightDrawable(int popoverArrowRightDrawable) {
		this.popoverArrowRightDrawable = popoverArrowRightDrawable;
	}

	// ********************************************************************
	// PUBLIC METHODS
	// ********************************************************************
	/**
	 * This method shows a popover in a ViewGroup, from an origin rect (relative to the Application Window)
	 * 
	 * @param group
	 *            The group we want to insert the popup. Normally a Relative Layout so it can stand on top of everything
	 * @param originRect
	 *            The rect we want the popup to appear from (relative to the Application Window!)
	 * @param arrowDirections
	 *            The mask of bits to tell in which directions we want the popover to be shown
	 * @param animated
	 *            Whether is animated, or not
	 */
	public void showPopoverFromRectInViewGroup(ViewGroup group, Rect originRect, int arrowDirections, boolean animated) {

		// First, tell delegate we will show
		if (delegate != null)
			delegate.popoverViewWillShow(this);

		// Save superview
		superview = group;

		// First, add the view to the view group. The popover will cover the whole area
		android.view.ViewGroup.LayoutParams insertParams = new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		group.addView(this, insertParams);

		// Now, save rect for the layout (is the same as the superview)
		// popoverLayoutRect =PopoverView.getFrameForView(superview);
		popoverLayoutRect = new Rect();
		superview.getGlobalVisibleRect(popoverLayoutRect);

		// Add available rects
		addAvailableRects(originRect, arrowDirections);
		// Get best rect
		Integer best = getBestRect();

		// Add popover
		Rect bestRect = possibleRects.get(best);
		addPopoverInRect(bestRect);
		// addPopoverInRectByCenter(bestRect);
		// Add arrow image
		// addArrow(originRect, best);

		// If we don't want animation, just tell the delegate
		if (!animated) {
			// Tell delegate we did show
			if (delegate != null)
				delegate.popoverViewDidShow(this);
		}
		// If we want animation, animate it!
		else {
			// Continue only if we are not animating
			if (!isAnimating) {

				// Create alpha animation, with its listener
				AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
				animation.setDuration(fadeAnimationTime);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						// Nothing to do here
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// Nothing to do here
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// End animation
						isAnimating = false;
						// Tell delegate we did show
						if (delegate != null)
							delegate.popoverViewDidShow(PopoverView.this);
					}
				});

				// Start animation
				isAnimating = true;
				startAnimation(animation);

			}
		}
	}

	/**
	 * Dismiss the current shown popover
	 * 
	 * @param animated
	 *            Whether it should be dismissed animated or not
	 */
	public void dissmissPopover(boolean animated) {
		
		setCardTextViewFocus(false);

		Intent broadIntent = new Intent(Const_VALUE.RESET_LAYOUT_ACTION);
		getContext().sendBroadcast(broadIntent);

		// Tell delegate we will dismiss
		if (delegate != null)
			delegate.popoverViewWillDismiss(PopoverView.this);

		// If we don't want animation
		if (!animated) {
			// Just remove views
			popoverView.removeAllViews();
			removeAllViews();
			if(superview != null)
				superview.removeView(this);
			// Tell delegate we did dismiss
			if (delegate != null)
				delegate.popoverViewDidDismiss(PopoverView.this);
		} else {
			// Continue only if there is not an animation in progress
			if (!isAnimating) {
				// Create alpha animation, with its listener
				AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
				animation.setDuration(fadeAnimationTime);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						// Nothing to do here
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// Nothing to do here
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// Remove the view
						popoverView.removeAllViews();
						removeAllViews();
						PopoverView.this.superview.removeView(PopoverView.this);
						// End animation
						isAnimating = false;
						// Tell delegate we did dismiss
						if (delegate != null)
							delegate.popoverViewDidDismiss(PopoverView.this);
					}
				});

				// Start animation
				isAnimating = true;
				startAnimation(animation);
			}

		}

	}

	public void popdismiss() {
		dissmissPopover(true);
	}
	
	public SnapsCardTextView getCardTextView() {
		return cardTextView;
	}
	
	public void setCardTextView(SnapsCardTextView cardTextView) {
		this.cardTextView = cardTextView;
	}
	
	public void setCardTextViewFocus(boolean flag) {
		if(cardTextView != null) {
			cardTextView.setBackgroundResource(flag ? R.drawable.img_card_text_focus_bg : 0);
			cardTextView.bringToFront();
		}
	}
	
	public void setArrowPosition(Rect rect, float moveX, float scale, boolean isLandscapeMode) {
		if(arrowImageView == null) return;

		if(scale > 1.2f || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct()) {
			arrowImageView.setVisibility(View.GONE);
			return;
		}

		arrowImageView.setVisibility(View.VISIBLE);

		float rectCenter = rect.left + (rect.width() / 2) + moveX;
		
		float screenWidth = isLandscapeMode ? UIUtil.getScreenHeight(getContext()) : UIUtil.getScreenWidth(getContext());
		float screenCenter = screenWidth / 2;
		if(isLandscapeMode && Config.isExistThumbnailEditView()) {
			screenWidth -= UIUtil.convertDPtoPX(getContext(), 106);
			screenCenter = (screenWidth / 2) + UIUtil.convertDPtoPX(getContext(), 106);
		}
		
		float screenLeftPer = screenWidth * .3f; //이미지가 화면의 30%이하거나 70%이상에 위치 해 있으면, 화살표가 안 맞으니까 보정 해줌...
		float screenRightPer = screenWidth * .7f;

		float diff = 0.f;
		float per = 0.f;
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) arrowImageView.getLayoutParams();
		if(rectCenter > screenRightPer) {
			diff = rectCenter - screenRightPer;
			per = Math.min(1.f, (diff / (screenWidth - screenRightPer) * (isLandscapeMode ? .8f : 2f))); //잘 안 맞아서...보정치임...
			int margin = (int) ((realContentSize.x/1.9f) * per); //너무 많이 이동하지 못하게 하려고 1/2이 아닌....
			params.setMargins(margin, 0, 0, 0);
		} else if(rectCenter < screenLeftPer) {
			diff = screenLeftPer - rectCenter;
			per = Math.min(1.f, (diff / screenLeftPer) * (isLandscapeMode ? .8f : 2f));
			int margin = (int) ((realContentSize.x/1.9f) * per);
			params.setMargins(0, 0, margin, 0);
		} else 
			return;

		arrowImageView.setLayoutParams(params);
	}

	public void setArrowPosition(Rect rect, float moveX, float scale,float defalutScale, boolean isLandscapeMode) {

		if(arrowImageView == null) return;

		if(scale > defalutScale || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct()) {
			arrowImageView.setVisibility(View.GONE);
			return;
		}

		arrowImageView.setVisibility(View.VISIBLE);

		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int targetViewWidth = rect.right - rect.left;

		int tooltipWidth = contentSizeForViewInPopover.x;
		int x = ((rect.left) + targetViewWidth / 2) - (tooltipWidth / 2);

		int rightMoveMargin = 0;
		int leftMoveMargin = 0;

		if(x < 0) {
			rightMoveMargin = Math.abs(x);
		}else if(x + tooltipWidth > screenWidth) {
			leftMoveMargin = ((x + tooltipWidth) - screenWidth);
		}else {

		}

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) arrowImageView.getLayoutParams();
		params.leftMargin = leftMoveMargin;
		params.rightMargin = rightMoveMargin;
		arrowImageView.setLayoutParams(params);
	}

	// ********************************************************************
	// ON TOUCH LISTENER
	// ********************************************************************
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// If we touched over the background popover view (this)

		Rect containRect = new Rect(popoverView.getLeft(), popoverView.getTop(), popoverView.getRight(), popoverView.getBottom());

		if (!(containRect.contains((int) event.getX(), (int) event.getY()))) {

			if (delegate != null)
				delegate.popoverViewDidClick(this, v.getId());

			dissmissPopover(true);
		}
		return true;
	}

}
