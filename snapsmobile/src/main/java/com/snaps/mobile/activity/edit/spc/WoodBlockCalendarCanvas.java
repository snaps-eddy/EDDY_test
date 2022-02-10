package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.spc.view.SnapsCalendarTextView;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;

public class WoodBlockCalendarCanvas extends ThemeBookCanvas {
	private static final String TAG = WoodBlockCalendarCanvas.class.getSimpleName();
	float thumbFixOffsetY = 0.f;

	private final int PAGER_MARGIN_VALUE_SIDE = UIUtil.convertDPtoPX(
			getContext(), 32); // 양쪽을 합쳐서 계산 할 것.

	private int shadowTopMargin = 0;
	private int containerBottomMargin = 0;

	public WoodBlockCalendarCanvas(Context context) {
		super(context);
	}

	public WoodBlockCalendarCanvas(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	public void setBgColor(int color) {
		color = 0xFFFFFFFF;
		super.setBgColor(color);
	}

	@Override
	protected void loadShadowLayer() {}

	@Override
	protected void loadPageLayer() {}

	@Override
	protected void initMargin() {
		shadowTopMargin = 120;
		containerBottomMargin = 115;
		if (isThumbnailView()) {
			shadowTopMargin *= getThumbnailRatioY();
			containerBottomMargin *= getThumbnailRatioY();
		}

		if (isThumbnailView()) {
			thumbFixOffsetY =  (int) ((isLandscapeMode() ? 80 : 20) * getThumbnailRatioY());
		}
	}

	private int getTotalMargin() {
		return shadowTopMargin + containerBottomMargin;
	}

	@Override
	protected float getBestRatio() {
		float fRatio = 1.f;
		int iFixMarginRat = 1;

		int defaultHeight = getDefaultHeight() - getTotalMargin();

		while (true) {
			if(isLandscapeMode()) {
				if (getDefaultWidth() > defaultHeight) {
					fRatio = (getWidth() -LEFT_TRAY_SIZE)
							/ (float) (getDefaultWidth() + (PAGER_MARGIN_VALUE_SIDE * iFixMarginRat));
				} else {
					fRatio = getHeight()
							/ (float) (defaultHeight + (PAGER_MARGIN_VALUE_SIDE * iFixMarginRat));
				}
			} else {
				if (getDefaultWidth() > defaultHeight) {
					fRatio = getWidth()
							/ (float) (getDefaultWidth() + (PAGER_MARGIN_VALUE_SIDE * iFixMarginRat));
				} else {
					fRatio = getHeight()
							/ (float) (defaultHeight + (PAGER_MARGIN_VALUE_SIDE * iFixMarginRat));
				}
			}

			float scaledWidth = (getDefaultWidth() * fRatio) + (PAGER_MARGIN_VALUE_SIDE);
			float scaledHeight = (defaultHeight * fRatio) + (isLandscapeMode() ?
					PAGER_MARGIN_VALUE_SIDE : PAGER_MARGIN_VALUE_SIDE);
			if(scaledWidth > getWidth() || scaledHeight > getHeight())
				iFixMarginRat++;
			else
				break;
		}

		return fRatio;
	}

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		removeItems(this);

		initMargin();

		this.width = page.getWidth();
		this.height = Integer.parseInt(page.height);

		edWidth = this.width + leftMargin + rightMargin;
		edHeight = this.height + containerBottomMargin + shadowTopMargin;

		this.setLayoutParams(new ARelativeLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		LayoutParams shadowLayoutParams = new LayoutParams(edWidth, edHeight);
		shadowLayer2 = new SnapsFrameLayout(this.getContext());

		MarginLayoutParams shadowParams = new MarginLayoutParams(shadowLayoutParams);
		shadowParams.setMargins(0, shadowTopMargin, 0, 0);
		shadowLayer2.setLayoutParams(new LayoutParams(shadowParams));

		MarginLayoutParams containerlayout = new MarginLayoutParams(this.width, this.height);
		containerlayout.setMargins(0, 0, 0, containerBottomMargin);

		containerLayer = new SnapsFrameLayout(this.getContext());
		containerLayer.setLayout(new LayoutParams(containerlayout));
		this.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());
		bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowLayoutParams));
		this.addView(bonusLayer);

		// bgLayer ���������.
		LayoutParams baseLayout = new LayoutParams(this.width, this.height);
		LayoutParams kakaobookLayout = null;

		bgLayer = new FrameLayout(this.getContext());
		bgLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));

		bgLayer.setBackgroundColor(Color.WHITE);

		containerLayer.addView(bgLayer);

		// layoutLayer ���������.
		layoutLayer = new FrameLayout(this.getContext());
		layoutLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
		containerLayer.addView(layoutLayer);

		// controllLayer ���������. ppppoint
		controlLayer = new FrameLayout(this.getContext());
		controlLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
		containerLayer.addView(controlLayer);

		layoutLayer.setPadding(0, 0, 0, 0);
		controlLayer.setPadding(0, 0, 0, 0);

		if (page.getBgList().size() <= 0)
			layoutLayer.setBackgroundColor(Color.WHITE);

		// formLayer ���������.
		formLayer = new FrameLayout(this.getContext());
		formLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(formLayer);

		// pageLayer ���������.
		pageLayer = new FrameLayout(this.getContext());
		pageLayer.setLayoutParams(new ARelativeLayoutParams(baseLayout));
		containerLayer.addView(pageLayer);

		this.addView(shadowLayer2);

		//이미지 로딩 완료 체크 객체 생성
		initImageLoadCheckTask();

		// Back Ground ������.
		loadBgLayer(previewBgColor);

		requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);

		Dlog.d("setSnapsPage() page number:" + number);
		setBackgroundColorIfSmartSnapsSearching();
	}

	@Override
	protected void loadAllLayers() {
		if (isSuspendedLayerLoad()) {
			hideProgressOnCanvas();
			return;
		}

		// Layout ������
		loadLayoutLayer();

		// Control ������.
		loadControlLayer();

		// Form ������.
		loadFormLayer();

		// Page ��������� ������.
		loadPageLayer();

		// ������ Layer ������.
		loadBonusLayer();

		setScaleValue();

		this.bringToFront();

		setPinchZoomScaleLimit(_snapsPage);

		imageLoadCheck();
	}

	@Override
	protected void loadClipartControlList() {
		SnapsCalendarTextView calendarTextView = new SnapsCalendarTextView(this.getContext(), containerLayer);
		calendarTextView.setIsThumbnailView(isThumbnailView());

		for (SnapsControl control : _snapsPage.getClipartControlList()) {

			switch (control._controlType) {

				case SnapsControl.CONTROLTYPE_STICKER: // 스티커..
					ImageLoadView view = new ImageLoadView(this.getContext(), (SnapsClipartControl) control);
					view.setSnapsControl(control);

					String url = SnapsAPI.DOMAIN(false) + ((SnapsClipartControl) control).resourceURL;
					loadImage(url, view, Const_VALUES.SELECT_SNAPS, 0, null);

					// angleclip적용
					if (!control.angle.isEmpty()) {
						// view.setPivotX(0);
						// view.setPivotY(0);
						view.setRotation(Float.parseFloat(control.angle));
					}
					SnapsClipartControl clipart = (SnapsClipartControl) control;
					Dlog.d("loadClipartControlList() clipart.alpha:" + clipart.alpha);
					float alpha = Float.parseFloat(clipart.alpha);
					view.setAlpha(alpha);

					controlLayer.addView(view);

					break;

				case SnapsControl.CONTROLTYPE_TEXT: //Clipart에서 왜 텍스트 로딩하는 코드가 들어있는 지 모르겠다...
				case SnapsControl.CONTROLTYPE_GRID:
					SnapsTextControl textControl = (SnapsTextControl) control;
					calendarTextView.addTextControl(textControl);
					break;
			}

		}

		//Control에 있는 날짜 및 기념일을 그린다.
		calendarTextView.setDrawTextOffsetY(-thumbFixOffsetY);
		calendarTextView.drawText();

		//XML에 작성할 정보들..(달력은 어차피 모든 썸네일을 다 따니까..)
		calendarTextView.writeXMLInfo();

		controlLayer.addView(calendarTextView);
		controlLayer.bringToFront();
	}

	@Override
	protected void loadTextControlList() {
		SnapsCalendarTextView calendarTextView = new SnapsCalendarTextView(this.getContext(), containerLayer);
		calendarTextView.setIsThumbnailView(isThumbnailView());

		for (SnapsControl control : _snapsPage.getTextControlList()) {

			switch (control._controlType) {
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
					Dlog.d("loadTextControlList() clipart.alpha:" + clipart.alpha);
					float alpha = Float.parseFloat(clipart.alpha);
					view.setAlpha(alpha);

					controlLayer.addView(view);

					break;
				case SnapsControl.CONTROLTYPE_TEXT:
				case SnapsControl.CONTROLTYPE_GRID:
					SnapsTextControl textControl = (SnapsTextControl) control;
					calendarTextView.addTextControl(textControl);
					break;
			}
		}

		//Control에 있는 날짜 및 기념일을 그린다.
		calendarTextView.setDrawTextOffsetY(-thumbFixOffsetY);
		calendarTextView.drawText();

		//XML에 작성할 정보들..(달력은 어차피 모든 썸네일을 다 따니까..)
		calendarTextView.writeXMLInfo();

		controlLayer.addView(calendarTextView);
		controlLayer.bringToFront();
	}

    @Override
    protected void loadBonusLayer() {
        try {
			ImageView skin = new ImageView(getContext());
			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			skin.setLayoutParams(param);

			SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
					.setContext(getContext())
					.setResourceFileName(SnapsSkinConstants.WOOD_BLOCK_CALENDAR_SKIN_FILE_NAME)
					.setSkinBackgroundView(skin).create());

			shadowLayer2.addView( skin );
        } catch (Exception e) {
			Dlog.e(TAG, e);
        }
    }

	@Override
	public void onDestroyCanvas() {
		if(shadowLayer2 != null) {
			Drawable d = shadowLayer2.getBackground();
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
