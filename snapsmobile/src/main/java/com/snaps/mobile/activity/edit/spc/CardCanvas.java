package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;

public class CardCanvas extends SnapsPageCanvas {
	private static final String TAG = CardCanvas.class.getSimpleName();

	public CardCanvas(Context context) {
		super(context);
	}

	public CardCanvas(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	protected void loadShadowLayer() {
		try {
			String pageType = getSnapsPage().type;
			if (StringUtil.isEmpty(pageType)) return;

			if (pageType.equalsIgnoreCase("half") || pageType.equalsIgnoreCase("cover")) {
				;
			} else if (pageType.equalsIgnoreCase("page")) {
				int coverSkinRes = 0;
				if(Const_PRODUCT.isCardShapeFolder()) {
					if(getOrgWidth() > getOrgHeight() ) {
						coverSkinRes = R.drawable.img_card_skin_folder_in;
					} else {
						coverSkinRes = R.drawable.img_card_skin_folder_height;
					}

				} else {
					if(getOrgWidth() > getOrgHeight() ) {
						coverSkinRes = R.drawable.img_card_skin_one_flat;
					} else {
						coverSkinRes = R.drawable.img_card_skin_one_height;
					}
				}

				if (shadowLayer != null)
					shadowLayer.setBackgroundResource(coverSkinRes);
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}

    @Override
    public void setBgColor(int color) {
        color = 0xFFEEEEEE;
        super.setBgColor(color);
    }

    @Override
	protected void loadPageLayer() {
		//TODO  duckwon
		if(Const_PRODUCT.isCardShapeFolder()) {
			String pageType = getSnapsPage().type;
			if (pageType != null && pageType.equalsIgnoreCase("page")) {
				ImageView lineImageView = new ImageView(getContext());
				boolean isVertical = getOrgWidth() >getOrgHeight() ;
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pageLayer.getLayoutParams());
				int width = pageLayer.getLayoutParams().width ;
				int height = pageLayer.getLayoutParams().height;
				int offSetY =  height / 2;
				int margin = UIUtil.convertDPtoPX(getContext(),10);
				int lineWidth = UIUtil.convertDPtoPX(getContext(),2);
				FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams(isVertical? height - ( margin * 2 ) : width - ( margin * 2 ), lineWidth);

				if (isVertical) {
					int leftMargin = (width - (height - ( margin * 2 )))  / 2;
					imageViewLayoutParams.setMargins(leftMargin,offSetY,0,0);
					lineImageView.setImageResource(R.drawable.card_line_horizontal);
					lineImageView.setRotation(90);
				} else {
					imageViewLayoutParams.setMargins(margin,offSetY,margin,0);
					lineImageView.setImageResource(R.drawable.card_line_horizontal);
				}

				lineImageView.setLayoutParams(imageViewLayoutParams);
				pageLayer.addView(lineImageView);
			}
		}
	}

	@Override
	protected void loadBonusLayer() {
		String pageType = getSnapsPage().type;
		if (StringUtil.isEmpty(pageType)) return;

        if (pageType.equalsIgnoreCase("half") || pageType.equalsIgnoreCase("cover")) {
			if(mContext != null) {
				String skinImageName = "";
				if(Const_PRODUCT.isCardShapeFolder())
					skinImageName = Const_PRODUCT.isCardShapeWide() ? SnapsSkinConstants.CARD_FOLDER_HORIZONTAL_FILE_NAME : SnapsSkinConstants.CARD_FOLDER_VERTICAL_FILE_NAME;
				else
					skinImageName = Const_PRODUCT.isCardShapeWide() ? SnapsSkinConstants.CARD_FLAT_HORIZONTAL_FILE_NAME : SnapsSkinConstants.CARD_FLAT_VERTICAL_FILE_NAME;

				try {
					ImageView skinView = new ImageView(getContext());
					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(bonusLayer.getLayoutParams());
					param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
					param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
					skinView.setLayoutParams( param );

					SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
							.setContext(getContext())
							.setResourceFileName(skinImageName)
							.setSkinBackgroundView(skinView).create());

					bonusLayer.addView(skinView);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}
        } else if(pageType.equalsIgnoreCase("page")) {
			;
		}
	}

	@Override
	protected void initMargin() {
		String pageType = getSnapsPage().type;
		if (StringUtil.isEmpty(pageType)) return;

		if (pageType.equalsIgnoreCase("half") || pageType.equalsIgnoreCase("cover")) {
			if(Const_PRODUCT.isCardShapeFolder()) {
				if(Const_PRODUCT.isCardShapeWide()) {
					leftMargin = Const_PRODUCT.CARD_COVER_FOLDER_WIDE_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_COVER_FOLDER_WIDE_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_COVER_FOLDER_WIDE_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_COVER_FOLDER_WIDE_MARGIN_LIST[3];
				} else {
					leftMargin = Const_PRODUCT.CARD_COVER_FOLDER_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_COVER_FOLDER_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_COVER_FOLDER_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_COVER_FOLDER_MARGIN_LIST[3];
				}
			} else {
				if(Const_PRODUCT.isCardShapeWide()) {
					leftMargin = Const_PRODUCT.CARD_COVER_NORMAL_WIDE_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_COVER_NORMAL_WIDE_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_COVER_NORMAL_WIDE_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_COVER_NORMAL_WIDE_MARGIN_LIST[3];
				} else {
					leftMargin = Const_PRODUCT.CARD_COVER_NORMAL_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_COVER_NORMAL_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_COVER_NORMAL_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_COVER_NORMAL_MARGIN_LIST[3];
				}
			}
			
		} else if (pageType.equalsIgnoreCase("page")) {
			if(Const_PRODUCT.isCardShapeFolder()) {
				if(Const_PRODUCT.isCardShapeWide()) {
					leftMargin = Const_PRODUCT.CARD_PAGE_FOLDER_WIDE_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_PAGE_FOLDER_WIDE_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_PAGE_FOLDER_WIDE_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_PAGE_FOLDER_WIDE_MARGIN_LIST[3];
				} else {
					leftMargin = Const_PRODUCT.CARD_PAGE_FOLDER_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_PAGE_FOLDER_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_PAGE_FOLDER_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_PAGE_FOLDER_MARGIN_LIST[3];
				}
			} else {
				if(Const_PRODUCT.isCardShapeWide()) {
					leftMargin = Const_PRODUCT.CARD_PAGE_NORMAL_WIDE_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_PAGE_NORMAL_WIDE_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_PAGE_NORMAL_WIDE_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_PAGE_NORMAL_WIDE_MARGIN_LIST[3];
				} else {
					leftMargin = Const_PRODUCT.CARD_PAGE_NORMAL_MARGIN_LIST[0];
					topMargin = Const_PRODUCT.CARD_PAGE_NORMAL_MARGIN_LIST[1];
					rightMargin = Const_PRODUCT.CARD_PAGE_NORMAL_MARGIN_LIST[2];
					bottomMargin = Const_PRODUCT.CARD_PAGE_NORMAL_MARGIN_LIST[3];
				}
			}
		}

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
	}
	
	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		// SnapsPageCanvas를 하나만 사용할 경우.
		removeItems(this);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());

		this.width = page.getWidth();
		this.height = Integer.parseInt(page.height);

		initMargin();

		layout.width = this.width + leftMargin + rightMargin;
		layout.height = this.height + topMargin + bottomMargin;

		edWidth = layout.width;
		edHeight = layout.height;

		this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// Shadow 초기화.
		RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
		shadowLayer = new FrameLayout(this.getContext());
		shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(shadowLayer);

		ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
        containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

		containerLayer = new SnapsFrameLayout(this.getContext());
        ARelativeLayoutParams params = new ARelativeLayoutParams(containerlayout);
		containerLayer.setLayout( params );
		this.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());
		bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(bonusLayer);

		// bgLayer 초기화.
		RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(this.width, this.height);


		bgLayer = new FrameLayout(this.getContext());
		bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

		if (isBg || previewBgColor != null)
			containerLayer.addView(bgLayer);

		// layoutLayer 초기화.
		layoutLayer = new FrameLayout(this.getContext());
		layoutLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(layoutLayer);

		// controllLayer 초기화. ppppoint
		controlLayer = new FrameLayout(this.getContext());
		controlLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(controlLayer);

		// formLayer 초기화.
		formLayer = new FrameLayout(this.getContext());
		formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(formLayer);

		// pageLayer 초기화.
		pageLayer = new FrameLayout(this.getContext());
		pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(pageLayer);

		/*
		 * 임의 색상 적용. if( Config.PROD_CODE.equalsIgnoreCase(
		 * Config.PRODUCT_STICKER ) ) { this.setBackgroundColor( Color.argb(
		 * 255, 24, 162, 235 ) ); }
		 */

		//showProgressOnCanvas();

		//이미지 로딩 완료 체크 객체 생성
		initImageLoadCheckTask();

		// Back Ground 설정.
		loadBgLayer(previewBgColor);

		// Layout 설정
		loadLayoutLayer();

		// Control 설정.
		loadControlLayer();

		// Form 설정.
		loadFormLayer();

		// Page 이미지 설정.
		loadPageLayer();

		// 추가 Layer 설정.
		loadBonusLayer();

		setScaleValue();

		// 이미지 로드 완료 설정.
		imageLoadCheck();
		//loadAllLayerWithDelay();

		Dlog.d("setSnapsPage() page:" + number);

		setPinchZoomScaleLimit(_snapsPage);
    }

	@Override
	protected void loadControlLayer() {
		for (SnapsControl control : _snapsPage.getClipartControlList()) {

			switch (control._controlType) {
				case SnapsControl.CONTROLTYPE_IMAGE:
					// 이미지
					break;

				case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
					ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
					view.setSnapsControl(control);

					String url = SnapsAPI.DOMAIN(false) + ((SnapsClipartControl) control).resourceURL;
					loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

					// angleclip적용
					if (!control.angle.isEmpty()) {
						view.setRotation(Float.parseFloat(control.angle));
					}
					SnapsClipartControl clipart = (SnapsClipartControl) control;
					Dlog.d("loadControlLayer() clipart.alpha:" + clipart.alpha);
					float alpha = Float.parseFloat(clipart.alpha);
					view.setAlpha(alpha);

					controlLayer.addView(view);

					break;

				case SnapsControl.CONTROLTYPE_BALLOON:
					// 말풍선.
					break;
			}

		}

		for (SnapsControl control : _snapsPage.getTextControlList()) {

			switch (control._controlType) {
				case SnapsControl.CONTROLTYPE_IMAGE:
					// 이미지
					break;

				case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
					ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
					view.setSnapsControl(control);

					String url = SnapsAPI.DOMAIN() + ((SnapsClipartControl) control).resourceURL;
					loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

					// angleclip적용
					if (!control.angle.isEmpty()) {
						view.setRotation(Float.parseFloat(control.angle));
					}
					SnapsClipartControl clipart = (SnapsClipartControl) control;
					Dlog.d("loadControlLayer() clipart.alpha:" + clipart.alpha);
					float alpha = Float.parseFloat(clipart.alpha);
					view.setAlpha(alpha);

					controlLayer.addView(view);

					break;

				case SnapsControl.CONTROLTYPE_BALLOON:
					// 말풍선.
					break;

				case SnapsControl.CONTROLTYPE_TEXT:
					setMutableTextControl(control);
					break;

			}
		}
	}

	@Override
	public void onDestroyCanvas() {
		if(shadowLayer != null) {
			Drawable d = shadowLayer.getBackground();
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
