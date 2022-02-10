package com.snaps.common.customui.dragdrop;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.snaps.common.structure.page.SnapsPage;

public class DSRelativeLayout extends RelativeLayout implements DragSource, DropTarget {
	
	private SnapsPage snapsPage = null;
	int idx = 0;

	// Constructors
	public DSRelativeLayout(Context context) {
		super(context);
	}

	public DSRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DSRelativeLayout(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
	}
	
	public SnapsPage getSnapsPage() {
		return snapsPage;
	}

	public void setSnapsPage(SnapsPage snapsPage) {
		this.snapsPage = snapsPage;
	}
	
	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	@Override
	public boolean allowDrag() {
		return true;
	}

	@Override
	public void setDragController(DragController dragger) {
		
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		
	}

	
	// DropTarget
	@Override
	public boolean onDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		
		return true;
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo) {
		
		return true;
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset, DragView dragView, Object dragInfo, Rect recycle) {
		
		return null;
	}

	public interface OnRemoveMe {
		public void onRemoveMe();
	}
	OnRemoveMe onRemoveMe;
	public void setOnRemoveMe(OnRemoveMe onRemoveMe) {
		this.onRemoveMe = onRemoveMe;
	}

	public void removeMe() {
		if (onRemoveMe != null)
			onRemoveMe.onRemoveMe();
	}

}
