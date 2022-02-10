package com.snaps.mobile.edit_activity_tools.utils;

import android.graphics.Canvas;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.activity.selectimage.helper.ItemTouchHelperAdapter;
import com.snaps.mobile.activity.selectimage.helper.ItemTouchHelperViewHolder;
import com.snaps.mobile.edit_activity_tools.interfaces.IEditThumbnailItemTouchHelperAdapter;

import java.util.List;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to listen for {@link
 * ItemTouchHelperAdapter} callbacks and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link ItemTouchHelperViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class EditActivityThumbItemTouchHelperCallback extends ItemTouchHelper.Callback {

	public static final float ALPHA_FULL = 1.0f;
	public static final long MAX_MS_SINCE_START_SCROLL_TIME = 1400;  //드래깅 상태에서 스크롤링 가속도를 제어하기 위함.
	public static final long MIN_MS_SINCE_START_SCROLL_TIME = 1100;

	private final IEditThumbnailItemTouchHelperAdapter mAdapter;
	private boolean isLandScapeMode = false;

	private boolean isSupportDragAndDrop = false;

	public EditActivityThumbItemTouchHelperCallback(IEditThumbnailItemTouchHelperAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public boolean isLongPressDragEnabled() {
		return isSupportDragAndDrop;
	}

	@Override
	public boolean isItemViewSwipeEnabled() {
		return false;
	}

	public boolean isLandScapeMode() {
		return isLandScapeMode;
	}

	public void setIsLandScapeMode(boolean isLandScapeMode) {
		this.isLandScapeMode = isLandScapeMode;
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		isSupportDragAndDrop = Config.isPhotobooks() && viewHolder != null && viewHolder.getItemId() > 1;

		if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
			final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
			final int swipeFlags = 0;
			return makeMovementFlags(dragFlags, swipeFlags);
		} else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
			LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
			int dragFlags = 0;
			final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
			if (isLandScapeMode()) {
				dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
			} else {
				dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
			}
			return makeMovementFlags(dragFlags, swipeFlags);
		} else {
			final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
			final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
			return makeMovementFlags(dragFlags, swipeFlags);
		}
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
		if (source.getItemViewType() != target.getItemViewType()) {
			return false;
		}

		if (target.getItemId() < 2) {
			return false;
		}

		mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
		return true;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
		mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
			final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
			viewHolder.itemView.setAlpha(alpha);
			viewHolder.itemView.setTranslationX(dX);
		} else {
			super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
		}
	}

	@Override
	public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		// We only want the active item to change
		if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
			if (viewHolder instanceof ItemTouchHelperViewHolder) {
				// Let the view holder know that this item is being moved or dragged
				ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
				itemViewHolder.onItemSelected();
			}
		}

		mAdapter.onSelectedChanged(viewHolder, actionState);

		super.onSelectedChanged(viewHolder, actionState);
	}

	@Override
	public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		super.clearView(recyclerView, viewHolder);
		viewHolder.itemView.setAlpha(ALPHA_FULL);

		if (viewHolder instanceof ItemTouchHelperViewHolder) {
			// Tell the view holder it's time to restore the idle state
			ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
			itemViewHolder.onItemClear();
		}
	}

	@Override
	public int convertToAbsoluteDirection(int flags, int layoutDirection) {
		return super.convertToAbsoluteDirection(flags, layoutDirection);
	}

	@Override
	public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
		return super.canDropOver(recyclerView, current, target);
	}

	@Override
	public int getBoundingBoxMargin() {
		return super.getBoundingBoxMargin();
	}

	@Override
	public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
		return super.getSwipeThreshold(viewHolder);
	}

	@Override
	public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
		return super.getMoveThreshold(viewHolder);
	}

	@Override
	public float getSwipeEscapeVelocity(float defaultValue) {
		return super.getSwipeEscapeVelocity(defaultValue);
	}

	@Override
	public float getSwipeVelocityThreshold(float defaultValue) {
		return super.getSwipeVelocityThreshold(defaultValue);
	}

	@Override
	public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
		return super.chooseDropTarget(selected, dropTargets, curX, curY);
	}

	@Override
	public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
		super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
	}

	@Override
	public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}

	@Override
	public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
		return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
	}

	@Override
	public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
		viewSizeOutOfBounds *= 2f; //드래그 상태에서 바깥쪽으로 조금만 나가더라도 스크롤링이 되게 하기 위해..

		long ms = Math.max(MIN_MS_SINCE_START_SCROLL_TIME, msSinceStartScroll);
		ms = Math.min(MAX_MS_SINCE_START_SCROLL_TIME, ms);

		return super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, ms);
	}

}


