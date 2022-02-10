package com.snaps.mobile.edit_activity_tools.customview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.snaps.mobile.edit_activity_tools.interfaces.IEditThumbnailBehaviorByOrientation;
import com.snaps.mobile.edit_activity_tools.strategies.EditThumbnailFunctionsForLandscape;
import com.snaps.mobile.edit_activity_tools.strategies.EditThumbnailFunctionsForPortrait;

/**
 * Created by ysjeong on 16. 5. 10..
 */
public class EditActivityThumbnailRecyclerView extends RecyclerView {

	private static final float FIX_VELOCITY = .5f; //썸네일 뷰 스크롤할 때, 너무 빨리 스크롤되어서 보정 함.

	private IEditThumbnailBehaviorByOrientation scrollToIdxBehavior = null;

	private boolean isLandscapeMode = false;

	public EditActivityThumbnailRecyclerView(Context context) {
		super(context);
	}

	public EditActivityThumbnailRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditActivityThumbnailRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setIsLandsacpeMode(boolean isLandscapeMode) {
		this.isLandscapeMode = isLandscapeMode;
	}

	public void destroyView() {
		if (scrollToIdxBehavior != null) {
			scrollToIdxBehavior = null;
		}
		removeAllViews();
	}

	@Override
	public boolean fling(int velocityX, int velocityY) {

		velocityX *= FIX_VELOCITY;
		velocityY *= FIX_VELOCITY;

		return super.fling(velocityX, velocityY);
	}

	public void scrollToIdx(int moveType, int itemTotalCnt, int childIdx) {
		createScrollToIdxBehavior();

		scrollToIdxBehavior.scrollToIdx(this, moveType, itemTotalCnt, childIdx);
	}

	private void createScrollToIdxBehavior() {
		if (isLandscapeMode) {
			scrollToIdxBehavior = new EditThumbnailFunctionsForLandscape();
		} else
			scrollToIdxBehavior = new EditThumbnailFunctionsForPortrait();
	}
}
