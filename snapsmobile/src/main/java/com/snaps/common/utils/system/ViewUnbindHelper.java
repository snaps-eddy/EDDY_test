package com.snaps.common.utils.system;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.interfaces.ISnapsControl;

import errorhandle.logger.Logg;

public class ViewUnbindHelper {
	private static final String TAG = ViewUnbindHelper.class.getSimpleName();

	/**
	 * Removes the reference to the activity from every view in a view hierarchy (listeners, images etc.). This method should be called in the onDestroy() method of each activity. This code may
	 * stinks, but better than worse - suspiciously, Android framework does not free resources immediately which are consumed by Views and this leads to OutOfMemoryError sometimes although there are
	 * no user mistakes.
	 * 
	 * @param view
	 *            View to free from memory
	 */
	public static void unbindReferences(View view) {
//		 unbindReferences(view, null, true);
	}

	public static void unbindReferences(View view, int[] except, boolean recycle) {
		try {
			if (view != null) {
				if (view instanceof ISnapsControl) { //glide가 처리 하도록 한다
					return;
				}

				unbindViewReferences(view, except, recycle);
				if (view instanceof ViewGroup) {
					unbindViewGroupReferences((ViewGroup) view, except, recycle);
				}
			}
		} catch (Exception ignore) {
			/*
			 * whatever exception is thrown just ignore it because a crash is always worse than this method not doing what it's supposed to do
			 */
		}
	}

	/**
	 * Removes the reference to the activity from every view in a view hierarchy (listeners, images etc.). This method should be called in the onDestroy() method of each activity. This code may
	 * stinks, but better than worse - suspiciously, Android framework does not free resources immediately which are consumed by Views and this leads to OutOfMemoryError sometimes although there are
	 * no user mistakes.
	 * 
	 */
	public static void unbindReferences(Activity activity, int viewID, boolean recycle) {
		try {
			View view = activity.findViewById(viewID);
			if (view != null) {
				unbindViewReferences(view, recycle);
				if (view instanceof ViewGroup) {
					unbindViewGroupReferences((ViewGroup) view);
				}
			}
		} catch (Exception ignore) {
			/*
			 * whatever exception is thrown just ignore it because a crash is always worse than this method not doing what it's supposed to do.
			 */
		}
	}

	private static void unbindViewGroupReferences(ViewGroup viewGroup) {
		unbindViewGroupReferences(viewGroup, null, true);
	}

	private static void unbindViewGroupReferences(ViewGroup viewGroup, int[] except, boolean recycle) {
		int nrOfChildren = viewGroup.getChildCount();
		for (int i = 0; i < nrOfChildren; i++) {
			View view = viewGroup.getChildAt(i);
			if (view instanceof ISnapsControl) { //glide가 처리 하도록 한다
				continue;
			}

			unbindViewReferences(view, except, recycle);
			if (view instanceof ViewGroup) {
				unbindViewGroupReferences((ViewGroup) view, except, recycle);
			}
		}

		try {
			viewGroup.removeAllViews();
		} catch (Exception ignore) {
			// AdapterViews, ListViews and potentially other ViewGroups don't support the removeAllViews operation
		}
	}

	private static void unbindViewReferences(View view, boolean recycle) {
		unbindReferences(view, null, recycle);
	}

	private static void unbindViewReferences(View view, int[] except, boolean recycle) {
		try {

			if (view == null || view instanceof ISnapsControl) { //glide가 처리 하도록 한다
				return;
			}

			// Set everything to null (API Level 8)
			try {
				view.setOnClickListener(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setOnCreateContextMenuListener(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setOnFocusChangeListener(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setOnKeyListener(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setOnLongClickListener(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setOnClickListener(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setTouchDelegate(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			Drawable d = view.getBackground();
			if (d != null) {
				try {
					d.setCallback(null);
				} catch (Exception ignore) {
					Dlog.e(TAG, ignore);
				}
			}
			boolean isRecycle = true;
			if (except != null) {
				for (int i = 0; i < except.length; i++) {
					if (view.getId() == except[i]) {
						isRecycle = false;
						break;
					}
				}
			}

			if (view instanceof ImageView) {
				ImageView imageView = (ImageView) view;
				
				imageView.setImageDrawable(null);
				imageView.setImageBitmap(null);
				
				d = imageView.getDrawable();
				if (d != null) {
					d.setCallback(null);
				}

				if (d != null && recycle && isRecycle && d instanceof BitmapDrawable) {
					Bitmap bm = ((BitmapDrawable) d).getBitmap();
					if(bm != null && !bm.isRecycled()) {
						bm.recycle();
						bm = null;
					}
					Dlog.d("unbindViewReferences() Bitmap.recycle() - view instanceof ImageView");
				}

				imageView.destroyDrawingCache();

			} else if (view instanceof WebView) {
				((WebView) view).destroyDrawingCache();
				((WebView) view).destroy();
			}

			try {
				view.setBackgroundDrawable(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setAnimation(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setContentDescription(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}

			try {
				view.setTag(null);
			} catch (Exception ignore) {
				Dlog.e(TAG, ignore);
			}
			
			if (recycle && isRecycle) {
				try {
					if (d != null && d instanceof BitmapDrawable) {
						Bitmap bm = ((BitmapDrawable) d).getBitmap();
						if(bm != null && !bm.isRecycled()) {
							bm.recycle();
							bm = null;
						}
						Dlog.d("unbindViewReferences() Bitmap.recycle()");
					}
				} catch (Exception ignore) {
					Dlog.e(TAG, ignore);
				}
			}
			
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}
