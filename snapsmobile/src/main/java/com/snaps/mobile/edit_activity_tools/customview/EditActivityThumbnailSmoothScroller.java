package com.snaps.mobile.edit_activity_tools.customview;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.snaps.common.customui.PrefetchDisableLinearLayoutManager;
import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class EditActivityThumbnailSmoothScroller extends PrefetchDisableLinearLayoutManager {
	private static final String TAG = EditActivityThumbnailSmoothScroller.class.getSimpleName();
	private static final float MILLISECONDS_PER_INCH = 180f;
	private Context mContext;

	public EditActivityThumbnailSmoothScroller(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView,
			RecyclerView.State state, final int position) {

		LinearSmoothScroller smoothScroller =
				new LinearSmoothScroller(mContext) {

					//This controls the direction in which smoothScroll looks
					//for your view
					@Override
					public PointF computeScrollVectorForPosition
					(int targetPosition) {
						return EditActivityThumbnailSmoothScroller.this
								.computeScrollVectorForPosition(targetPosition);
					}

					//This returns the milliseconds it takes to
					//scroll one pixel.
					@Override
					protected float calculateSpeedPerPixel
					(DisplayMetrics displayMetrics) {
						return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
					}
				};

		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			super.onLayoutChildren(recycler, state);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}
