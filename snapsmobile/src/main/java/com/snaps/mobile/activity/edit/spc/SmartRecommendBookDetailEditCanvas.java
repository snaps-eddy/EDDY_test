package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.data.img.BRect;
import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

public class SmartRecommendBookDetailEditCanvas extends ThemeBookCanvas {
	private static final String TAG = SmartRecommendBookDetailEditCanvas.class.getSimpleName();

	public SmartRecommendBookDetailEditCanvas(Context context) {
		super(context);
	}

	public SmartRecommendBookDetailEditCanvas(Context context, AttributeSet attr) {
		super( context , attr );
	}

	@Override
	protected void loadShadowLayer() {
		try {
			if (getSnapsPage().info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER) {
				// 투명이미지
				if (getSnapsPage().type.equalsIgnoreCase("cover")) {
					shadowLayer.setBackgroundResource(R.drawable.book_hard_cover_bg);
				} else {
					if (!"160008".equals(getSnapsPage().info.F_PAPER_CODE))
						shadowLayer.setBackgroundResource(R.drawable.skin_a4_cover_);
					else
						shadowLayer.setBackgroundResource(R.drawable.skin_a4_rayflat_cover);
				}
			} else {
				shadowLayer.setBackgroundResource(R.drawable.book_soft_page_bg);
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void loadPageLayer() {
		try {
			if (getSnapsPage().type.equalsIgnoreCase("page") || getSnapsPage().type.equalsIgnoreCase("title")) {
				pageLayer.setBackgroundResource(R.drawable.skin_a4);// 내지
			} else if (getSnapsPage().type.equalsIgnoreCase("cover")) {
				if (getSnapsPage().info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER)
					;// 커버
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
		// SnapsPageCanvas를 하나만 사용할 경우.
		removeItems(this);

		LayoutParams layout = new LayoutParams(this.getLayoutParams());
		layout.setMargins(0, 0, 0, 0);
		// 페이지의 크기를 구한다.
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

		// Shadow 초기화.
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

		// bgLayer 초기화.
		LayoutParams baseLayout = new LayoutParams(this.width, this.height);
		LayoutParams kakaobookLayout = null;

		bgLayer = new FrameLayout(this.getContext());
		bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

		containerLayer.addView(bgLayer);

		// layoutLayer 초기화.
		layoutLayer = new FrameLayout(this.getContext());
		layoutLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
		containerLayer.addView(layoutLayer);

		// controllLayer 초기화. ppppoint
		controlLayer = new FrameLayout(this.getContext());
		controlLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
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

//		if (isCoverPageAndExistTitleText()) {
//		    setVisibility(View.INVISIBLE);
//			initLocationWithOffsetWidth(edWidth, UIUtil.getScreenWidth(getContext()), UIUtil.convertDPtoPX(getContext(), -100));
//		}
	}

	@Override
	protected boolean shouldMoveAfterZoom() {
		return false;
	}

	protected boolean isSupportZoomFunction() {
		return true;
	}

	@Override
	protected BPoint getCanvasOffsetPoint() {
		return new BPoint(0, UIUtil.convertDPtoPX(mContext, 48));
	}

	@Override
	protected BRect getCanvasLimitOffsetRect(Canvas canvas, int offsetY) {
		//좌표 수치가 100% 정확하게 맞지 않아서, 보정 코드..
		int l = (int) PAGING_MARGIN_OFFSET;
		int t = (int) PAGING_MARGIN_OFFSET/2;
		int r = (int) (canvas.getWidth() - (PAGING_MARGIN_OFFSET * 1.2f));
		int b = (int) ((canvas.getHeight() - offsetY) - (PAGING_MARGIN_OFFSET * 1.5f));
		return new BRect(l, t, r, b);
	}

	@Override
	protected void setPinchZoomScaleLimit(SnapsPage page) {
		setScaleLimit(LIMIT_SCALE_RATIO_SINGLE_PAGE_TYPE);
	}

	@Override
    protected void smoothScrollBySwipeTouch(final int LIMIT_OFFSET_LEFT, final int LIMIT_OFFSET_TOP, final int LIMIT_OFFSET_RIGHT, final int LIMIT_OFFSET_BOTTOM) {}

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
