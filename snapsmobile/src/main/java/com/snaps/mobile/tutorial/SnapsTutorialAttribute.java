package com.snaps.mobile.tutorial;

import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by ysjeong on 2017. 7. 31..
 */

public class SnapsTutorialAttribute {
	private View targetView = null;
	private FrameLayout tooltipTutorialLayout = null;
	private SnapsTutorialConstants.eTUTORIAL_ID tutorialId = null;
	private SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION viewPosition = null;
	private boolean isLandscapeMode = false;
	private String tooltipMsg = null;
	private GIF_TYPE gifType = null;
	private eCustomTutorialType customTutorialType = null;
	private int topMargin = 0;
	private int leftMargin = 0;
	private float scale = 0f;
	private ShowResultListener showResultListener = null;
	private boolean isForceSetTargetView = false;

	public enum GIF_TYPE {
		PINCH_ZOOM_AND_DRAG,
		PINCH_ZOOM,
		MOVE_HAND,
		RECOMMEND_BOOK_MAIN_LIST_PINCH_ZOOM,
		KT_BOOK_EDITOR,
		ACRYLIC_KEYING_EDITOR,
		ACRYLIC_STAND_EDITOR
	}

	public enum eCustomTutorialType {
		RECOMMEND_BOOK_IMAGE_SELECT
	}

	private SnapsTutorialAttribute(Builder builder) {
		this.targetView = builder.targetView;
		this.tooltipTutorialLayout = builder.tooltipTutorialLayout;
		this.tutorialId = builder.tutorialId;
		this.isLandscapeMode = builder.isLandscapeMode;
		this.tooltipMsg = builder.tooltipMsg;
		this.viewPosition = builder.viewPosition;
		this.gifType = builder.gifType;
		this.customTutorialType = builder.customTutorialType;
		this.topMargin = builder.topMargin;
		this.leftMargin = builder.leftMargin;
		this.scale = builder.scale;
		this.showResultListener = builder.showResultListener;
		this.isForceSetTargetView = builder.isForceSetTargetView;
	}

	public eCustomTutorialType getCustomTutorialType() {
		return customTutorialType;
	}

	public boolean isForceSetTargetView() {
		return isForceSetTargetView;
	}

	public boolean isLandscapeMode() {
		return isLandscapeMode;
	}

	public FrameLayout getTooltipTutorialLayout() {
		return tooltipTutorialLayout;
	}

	public View getTargetView() {
		return targetView;
	}

	public SnapsTutorialConstants.eTUTORIAL_ID getTutorialId() {
		return tutorialId;
	}

	public SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION getViewPosition() {
		return viewPosition;
	}

	public String getText() {
		return tooltipMsg;
	}

	public GIF_TYPE getGifType() {
		return gifType;
	}

	public int getTopMargin() {
		return topMargin;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public float getScale() {
		return scale;
	}

	public ShowResultListener getShowResultListener() {
		return showResultListener;
	}

	public static class Builder {
		private View targetView = null;
		private FrameLayout tooltipTutorialLayout = null;
		private SnapsTutorialConstants.eTUTORIAL_ID tutorialId = null;
		private SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION viewPosition = null;
		private boolean isLandscapeMode = false;
		private String tooltipMsg = null;
		private GIF_TYPE gifType = null;
		private eCustomTutorialType customTutorialType = null;
		private int topMargin = 0;
		private int leftMargin = 0;
		private float scale = 0;
		private ShowResultListener showResultListener = null;
		private boolean isForceSetTargetView = false;

		public Builder setForceSetTargetView(boolean forceSetTargetView) {
			isForceSetTargetView = forceSetTargetView;
			return this;
		}

		public Builder setLandscapeMode(boolean landscapeMode) {
			isLandscapeMode = landscapeMode;
			return this;
		}

		public Builder setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID tutorialId) {
			this.tutorialId = tutorialId;
			return this;
		}

		public Builder setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION viewPosition) {
			this.viewPosition = viewPosition;
			return this;
		}

		public Builder setTooltipTutorialLayout(FrameLayout tooltipTutorialLayout) {
			this.tooltipTutorialLayout = tooltipTutorialLayout;
			return this;
		}

		public Builder setTargetView(View targetView) {
			this.targetView = targetView;
			return this;
		}

		public Builder setText(String tooltipMsg) {
			this.tooltipMsg = tooltipMsg;
			return this;
		}

		public Builder setGifType(GIF_TYPE gifType) {
			this.gifType = gifType;
			return this;
		}

		public Builder setCustomTutorialType(eCustomTutorialType customTutorialType) {
			this.customTutorialType = customTutorialType;
			return this;
		}

		public Builder setTopMargin(int topMargin) {
			this.topMargin = topMargin;
			return this;
		}

		public Builder setLeftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}

		public Builder setScale(float scale) {
			this.scale = scale;
			return this;
		}

		public Builder setShowResultListener(ShowResultListener showResultListener) {
			this.showResultListener = showResultListener;
			return this;
		}

		public SnapsTutorialAttribute create() {
			return new SnapsTutorialAttribute(this);
		}
	}

	public interface ShowResultListener {
		boolean result();
	}
}
