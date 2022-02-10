package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplateInfo.COVER_TYPE;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

public class SNSBookPageCanvas extends SnapsPageCanvas {
	private static final String TAG = SNSBookPageCanvas.class.getSimpleName();
	public SNSBookPageCanvas(Context context) {
		super(context);
	}

	public SNSBookPageCanvas(Context context, AttributeSet attr) {
		super( context , attr );
	}

	@Override
	protected void loadShadowLayer() {
		try {
			if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER) {
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
			// TODO Auto-generated method stub
			if (getSnapsPage().type.equalsIgnoreCase("page") || getSnapsPage().type.equalsIgnoreCase("title")) {
				pageLayer.setBackgroundResource(R.drawable.skin_a4);// 내지
			} else if (getSnapsPage().type.equalsIgnoreCase("cover")) {
				if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER)
					;// 커버
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	protected void loadBonusLayer() {

	}

	@Override
	protected void initMargin() {
		// 인스타그램북 보정
		Config.SNS_BOOK_HARD_MARGIN_LIST[3] = 14;


		if (getSnapsPage().info.getCoverType() == COVER_TYPE.HARD_COVER) {
			leftMargin = Config.SNS_BOOK_HARD_MARGIN_LIST[0];
			topMargin = Config.SNS_BOOK_HARD_MARGIN_LIST[1];
			rightMargin = Config.SNS_BOOK_HARD_MARGIN_LIST[2];
			bottomMargin = Config.SNS_BOOK_HARD_MARGIN_LIST[3];

			// 와이드 여부 판단. 와이드 보정
			int w = getSnapsPage().getWidth() / 2;
			int h = (int) Float.parseFloat(getSnapsPage().height);

			// 와이드
			int wide_x_margin = 0;
			int wide_y_margin = 0;
			if (w + 20 > h) {
				wide_x_margin = 1;
				wide_y_margin = -1;
			}

			cover_leftMargin = Config.SNS_BOOK_HARDCOVER_MARGIN_LIST[0] + wide_x_margin;
			cover_topMargin = Config.SNS_BOOK_HARDCOVER_MARGIN_LIST[1] + wide_y_margin;
			cover_rightMargin = Config.SNS_BOOK_HARDCOVER_MARGIN_LIST[2] + wide_x_margin;
			cover_bottomMargin = Config.SNS_BOOK_HARDCOVER_MARGIN_LIST[3] + wide_y_margin;

		} else {

			// 와이드 여부 판단. 와이드 보정
			int w = getSnapsPage().getWidth() / 2;
			int h = (int) Float.parseFloat(getSnapsPage().height);

			// 와이드
			int wide_x_margin = 0;
			int wide_y_margin = 0;
			if (w + 20 > h) {
				wide_x_margin = 2;
				wide_y_margin = -4;
			}

			leftMargin = Config.SNS_BOOK_SOFT_MARGIN_LIST[0] + wide_x_margin;
			topMargin = Config.SNS_BOOK_SOFT_MARGIN_LIST[1] + wide_y_margin;
			rightMargin = Config.SNS_BOOK_SOFT_MARGIN_LIST[2] + wide_x_margin;
			bottomMargin = Config.SNS_BOOK_SOFT_MARGIN_LIST[3] + wide_y_margin;
		}
	}

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		// SnapsPageCanvas를 하나만 사용할 경우.
		removeItems(this);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
		layout.setMargins(0, 0, 0, 0);
		// 페이지의 크기를 구한다.
		this.width = page.getWidth();
		this.height = Integer.parseInt(page.height);

		initMargin();

		if (page.type.equals("cover") && page.info.getCoverType() == COVER_TYPE.HARD_COVER) {
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
		RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
		shadowLayer = new FrameLayout(this.getContext());

		shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(shadowLayer);

		ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
		if (page.type.equals("cover") && page.info.getCoverType() == COVER_TYPE.HARD_COVER) {
			containerlayout.setMargins(cover_leftMargin, cover_topMargin, cover_rightMargin, cover_bottomMargin);
		} else {
			containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
		}

		containerLayer = new SnapsFrameLayout(this.getContext());
		containerLayer.setLayout(new RelativeLayout.LayoutParams(containerlayout));
		this.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());
		bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(bonusLayer);

		// bgLayer 초기화.
		RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(this.width, this.height);
		RelativeLayout.LayoutParams kakaobookLayout = null;

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
		showProgressOnCanvas();

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

