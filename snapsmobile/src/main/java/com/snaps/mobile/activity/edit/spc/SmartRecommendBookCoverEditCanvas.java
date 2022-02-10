package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

public class SmartRecommendBookCoverEditCanvas extends SnapsPageCanvas {

	private static final int DIMENS_COVER_SHADOW = 20;

	public SmartRecommendBookCoverEditCanvas(Context context) {
		super(context);
	}

	public SmartRecommendBookCoverEditCanvas(Context context, AttributeSet attr) {
		super( context , attr );
	}

	@Override
	protected void loadShadowLayer() {}

	@Override
	protected void loadPageLayer() {}

	@Override
	protected void loadBonusLayer() {
		ImageView skinView = new ImageView(getContext());
		FrameLayout.LayoutParams skinViewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		skinView.setLayoutParams( skinViewParams );
		skinView.setClickable(false);
		skinView.setFocusable(false);
		skinView.setBackgroundResource(R.drawable.skin_smart_photobook_half_cover);
		skinView.setImageResource(R.drawable.selector_analysis_book_theme_select);

		bonusLayer.addView( skinView );
	}

	@Override
	protected void onPostTouchEvent(MotionEvent e) {
		if (e != null) {
			switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isClickAction = true;
					break;
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					isClickAction = false;
					break;
				case MotionEvent.ACTION_UP:
					if (isClickAction && !isLongClicked()) {
						if (getSnapsPageClickListener() != null)
							getSnapsPageClickListener().onClick(this);
					}
					break;
			}
		}
	}

	@Override
	protected void initMargin() {}

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		// SnapsPageCanvas를 하나만 사용할 경우.
		removeItems(this);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
		layout.setMargins(0, 0, 0, 0);

		final int shadowDimension = 10;
		final int spineDimension = 16;

		// 페이지의 크기를 구한다.
		this.width = page.getWidth();
		this.height = Integer.parseInt(page.height);

		int coverHalfWidth = (width/2) + spineDimension + shadowDimension;
		int coverHalfMoveMargin = (width/2) - spineDimension;
        FrameLayout fakeLayout = new FrameLayout(getContext());
		ARelativeLayoutParams fakeLayoutParams = new ARelativeLayoutParams( coverHalfWidth, this.height);
		fakeLayout.setLayoutParams(fakeLayoutParams);
		this.addView(fakeLayout);

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

		// Shadow 초기화.
		FrameLayout.LayoutParams shadowlayout = new FrameLayout.LayoutParams(layout.width, layout.height);

		ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
		if (page.type.equals("cover") && page.info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER) {
			containerlayout.setMargins(cover_leftMargin, cover_topMargin, cover_rightMargin, cover_bottomMargin);
		} else {
			containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
		}

		containerLayer = new SnapsFrameLayout(this.getContext());
		AFrameLayoutParams containerLayoutParams = new AFrameLayoutParams(shadowlayout);
		containerLayoutParams.leftMargin = -(coverHalfMoveMargin + shadowDimension);
		containerLayoutParams.topMargin = -shadowDimension;
		containerLayer.setLayout(containerLayoutParams);
		fakeLayout.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());

		AFrameLayoutParams bonusLayoutParams = new AFrameLayoutParams(shadowlayout);
		bonusLayoutParams.width = coverHalfWidth + DIMENS_COVER_SHADOW;
		bonusLayoutParams.height = height + DIMENS_COVER_SHADOW;

		bonusLayer.setLayoutParams(bonusLayoutParams);
		bonusLayer.setPadding(shadowDimension, shadowDimension, 0, 0);
		this.addView(bonusLayer);

		// bgLayer 초기화.
		RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(this.width, this.height);

		bgLayer = new FrameLayout(this.getContext());
		bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

		containerLayer.addView(bgLayer);

		// layoutLayer 초기화.
		layoutLayer = new FrameLayout(this.getContext());
		layoutLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(layoutLayer);

		// controllLayer 초기화. ppppoint
		controlLayer = new FrameLayout(this.getContext());
		controlLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(controlLayer);

		layoutLayer.setPadding(0, 0, 0, 0);
		controlLayer.setPadding(0, 0, 0, 0);

		if (Config.isThemeBook() && page.getBgList().size() <= 0)
			layoutLayer.setBackgroundColor(Color.WHITE);

		// formLayer 초기화.
		formLayer = new FrameLayout(this.getContext());
		formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(formLayer);

		// pageLayer 초기화.
		pageLayer = new FrameLayout(this.getContext());
		pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(pageLayer);

		/*
		 * 임의 색상 적용. if( Config.PROD_CODE.equalsIgnoreCase( Config.PRODUCT_STICKER ) ) { this.setBackgroundColor( Color.argb( 255, 24, 162, 235 ) ); }
		 */
//		showProgressOnCanvas();

		//이미지 로딩 완료 체크 객체 생성
		initImageLoadCheckTask();

		// Back Ground 설정.
		loadBgLayer(previewBgColor);

		// Layout 설정
		loadLayoutLayer();

		loadFormLayer();

		// Page 이미지 설정.
		loadPageLayer();

		// 추가 Layer 설정.
		loadBonusLayer();

		// Control 설정.
		loadControlLayer();

		// 이미지 로드 완료 설정.
		imageLoadCheck();

		setPinchZoomScaleLimit(_snapsPage);

		initLocationWithOffsetHeight(edHeight, UIUtil.convertDPtoPX(getContext(), 223), UIUtil.convertDPtoPX(getContext(), 0));
		this.setPadding(20, 0, 0, 0);
	}

	@Override
	protected boolean isSupportZoomFunction() {
		return false;
	}

	@Override
	protected boolean shouldDrawSpineText() {
		return false;
	}

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
