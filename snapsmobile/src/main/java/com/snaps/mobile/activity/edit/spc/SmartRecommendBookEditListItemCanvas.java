package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

public class SmartRecommendBookEditListItemCanvas extends ThemeBookCanvas {
	private static final String TAG = SmartRecommendBookEditListItemCanvas.class.getSimpleName();
	private View selectorView = null;
	private boolean isClickAction = false;

	public SmartRecommendBookEditListItemCanvas(Context context) {
		super(context);
	}

	public SmartRecommendBookEditListItemCanvas(Context context, AttributeSet attr) {
		super( context , attr );
	}

	@Override
	protected void onPostTouchEvent(MotionEvent e) {
		if (selectorView != null && e != null) {
		    if (e.getPointerCount() > 1) {
				setSelectorViewAlpha(0.f);
                isClickAction = false;
                return;
            }

			switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					selectorView.bringToFront();
					setSelectorViewAlpha(1.f);
					isClickAction = true;
					checkLongClick();
					break;
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					setSelectorViewAlpha(0.f);
					isClickAction = false;
					break;
				case MotionEvent.ACTION_UP:
					setSelectorViewAlpha(0.f);

					if (isLongClicked()) {
						if (getSnapsPageLongClickListener() != null){
							getSnapsPageLongClickListener().onClick(this);
						}
					} else if (isClickAction) {
						if (getSnapsPageClickListener() != null)
							getSnapsPageClickListener().onClick(this);
					}

					isClickAction = false;
					break;
			}
		}
	}

	public void setSelectorViewAlpha(float alphaFloatValue) {
		if (selectorView == null) return;
		selectorView.setAlpha(alphaFloatValue);
	}

	private void checkLongClick() {
		if (isPreview()) return;
		setLongClicked(false);
		if (mHandler != null)
			mHandler.sendEmptyMessageDelayed(MSG_CHECK_LONG_CLICK, TIME_OF_LONG_PRESS);
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg == null)
			return;
		try {
			switch (msg.what) {
				case MSG_CHECK_LONG_CLICK:
					if (isClickAction) {
						setLongClicked(true);
						UIUtil.performWeakVibration(getContext(), 80);
					}
					break;
			}
		} catch (Exception e) { Dlog.e(TAG, e); }
	}

	@Override
	protected void initMargin() {
		leftMargin = Config.SMART_RECOMMEND_BOOK_MARGIN_LIST[0];
		topMargin = Config.SMART_RECOMMEND_BOOK_MARGIN_LIST[1];
		rightMargin = Config.SMART_RECOMMEND_BOOK_MARGIN_LIST[2];
		bottomMargin = Config.SMART_RECOMMEND_BOOK_MARGIN_LIST[3];

//		// ????????? ?????? ??????. ????????? ??????
//		int w = getSnapsPage().getUserSelectWidth() / 2;
//		int h = (int) Float.parseFloat(getSnapsPage().height);
//
//		// ?????????
//		int wide_x_margin = 0;
//		int wide_y_margin = 0;
//		if (w + 20 > h) {
//			wide_x_margin = 1;
//			wide_y_margin = -1;
//		}
//
//		cover_leftMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[0] + wide_x_margin;
//		cover_topMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[1] + wide_y_margin;
//		cover_rightMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[2] + wide_x_margin;
//		cover_bottomMargin = Config.THEMEBOOK_HARDCOVER_MARGIN_LIST[3] + wide_y_margin;
	}

	@Override
	protected void loadPageLayer() {
		try {
			if (getSnapsPage().type.equalsIgnoreCase("page") || getSnapsPage().type.equalsIgnoreCase("title")) {
				if (!getSnapsPage().info.F_PAPER_CODE.equals("160008"))
					pageLayer.setBackgroundResource(R.drawable.skin_a4);// ??????
				else
					pageLayer.setBackgroundResource(R.drawable.skin_a4_rayflat);// ??????

			} else if (getSnapsPage().type.equalsIgnoreCase("cover")) {
				if (getSnapsPage().info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER)
					;// ??????
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		this._isBg = isBg;
		this._previewBgColor = previewBgColor;
		// SnapsPageCanvas??? ????????? ????????? ??????.
		removeItems(this);

		LayoutParams layout = new LayoutParams(this.getLayoutParams());
		layout.setMargins(0, 0, 0, 0);
		// ???????????? ????????? ?????????.
		this.width = page.getWidth();
		this.height = Integer.parseInt(page.height);

		initMargin();

		if (page.type.equals("cover") && page.info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER) {
			layout.width = this.width + cover_leftMargin + cover_rightMargin;
			layout.height = this.height + cover_topMargin + cover_bottomMargin;
		} else {
			layout.width = this.width + leftMargin + rightMargin;
			layout.height = this.height + topMargin + bottomMargin;
		}

		edWidth = layout.width;
		edHeight = layout.height;

		this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// Shadow ?????????.
		LayoutParams shadowlayout = new LayoutParams(layout.width, layout.height);
		shadowLayer = new FrameLayout(this.getContext());

		shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(shadowLayer);

		MarginLayoutParams containerlayout = new MarginLayoutParams(this.width, this.height);
		if (page.type.equals("cover") && page.info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER) {
			containerlayout.setMargins(cover_leftMargin, cover_topMargin, cover_rightMargin, cover_bottomMargin);
		} else {
			containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
		}

		containerLayer = new SnapsFrameLayout(this.getContext());
		containerLayer.setLayout(new LayoutParams(containerlayout));
		this.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());
		bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(bonusLayer);

		// bgLayer ?????????.
		LayoutParams baseLayout = new LayoutParams(this.width, this.height);
		LayoutParams kakaobookLayout = null;

		bgLayer = new FrameLayout(this.getContext());
		bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

		containerLayer.addView(bgLayer);

		// layoutLayer ?????????.
		layoutLayer = new FrameLayout(this.getContext());
		layoutLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
		containerLayer.addView(layoutLayer);

		// controllLayer ?????????. ppppoint
		controlLayer = new FrameLayout(this.getContext());
		controlLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
		containerLayer.addView(controlLayer);

		layoutLayer.setPadding(0, 0, 0, 0);
		controlLayer.setPadding(0, 0, 0, 0);

		if (Config.isThemeBook() && page.getBgList().size() <= 0)
			layoutLayer.setBackgroundColor(Color.WHITE);

		// formLayer ?????????.
		formLayer = new FrameLayout(this.getContext());
		formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(formLayer);

		// pageLayer ?????????.
		pageLayer = new FrameLayout(this.getContext());
		pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(pageLayer);

		selectorView = new View(getContext());
		FrameLayout.LayoutParams selectorViewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		selectorView.setLayoutParams( selectorViewParams );
		selectorView.setClickable(false);
		selectorView.setFocusable(false);
		selectorView.setBackgroundColor(Color.parseColor("#19191919"));
		selectorView.setAlpha(0.f);

		containerLayer.addView( selectorView );

		/*
		 * ?????? ?????? ??????. if( Config.PROD_CODE.equalsIgnoreCase( Config.PRODUCT_STICKER ) ) { this.setBackgroundColor( Color.argb( 255, 24, 162, 235 ) ); }
		 */
//		showProgressOnCanvas();

		//????????? ?????? ?????? ?????? ?????? ??????
		initImageLoadCheckTask();

		// Back Ground ??????.
		loadBgLayer(previewBgColor);

		// Layout ??????
		loadLayoutLayer();

		loadFormLayer();

		// Page ????????? ??????.
		loadPageLayer();

		// ?????? Layer ??????.
		loadBonusLayer();

		// Control ??????.
		loadControlLayer();

		// ????????? ?????? ?????? ??????.
		imageLoadCheck();

		setPinchZoomScaleLimit(_snapsPage);

		if (isPreview()) {
			initLocationWithOffsetHeight(edHeight, (UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 48)), UIUtil.convertDPtoPX(getContext(), 16));
		} else {
//			initLocationWithOffsetWidth(edWidth, UIUtil.getScreenWidth(getContext()), UIUtil.convertDPtoPX(getContext(), 16));
			initLocationWithOffsetHeight(edHeight, UIUtil.convertDPtoPX(getContext(), 163), UIUtil.convertDPtoPX(getContext(), 0));
		}
	}

	protected boolean isSupportZoomFunction() {
		return false;
	}

	@Override
	public void setZoomable(boolean m_isZoomable) {}

	@Override
	public  void onDestroyCanvas() {
		if(shadowLayer != null) {
			Drawable d = shadowLayer.getBackground();
			if (d != null) {
				try {
					d.setCallback(null);
				} catch (Exception ignore) {
				}
			}
		}

		if(pageLayer != null) {
			Drawable d = pageLayer.getBackground();
			if (d != null) {
				try {
					d.setCallback(null);
				} catch (Exception ignore) {
				}
			}
		}

		super.onDestroyCanvas();
	}
}
