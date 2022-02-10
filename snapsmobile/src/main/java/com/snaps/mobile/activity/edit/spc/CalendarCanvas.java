package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
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
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_ThumbNail;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;

public class CalendarCanvas extends ThemeBookCanvas {
	private static final String TAG = CalendarCanvas.class.getSimpleName();
	protected int pageCover_w = 0;
	protected int pageCover_h = 0;

	ImageView mSpringImgView = null;
	
	public CalendarCanvas(Context context) {
		super(context);
	}

	public CalendarCanvas(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	protected void loadShadowLayer() {
	}

	@Override
	protected void loadPageLayer() {
		// TODO Auto-generated method stub
		if (getSnapsPage().type.equalsIgnoreCase("page") || getSnapsPage().type.equalsIgnoreCase("title")) {
		} else if (getSnapsPage().type.equalsIgnoreCase("cover")) {
		}

	}

	@Override
	protected void initMargin() {
		topMargin = Const_PRODUCT.CALENDAR_PAGE_MARGIN_LIST[1];
		if (isThumbnailView()) {
			topMargin = 0;
		}
	}

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		removeItems(this);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
		layout.setMargins(0, 0, 0, 0);
		this.width = page.getWidth();
		this.height = Integer.parseInt(page.height);

		initMargin();

		layout.width = this.width;
		layout.height = this.height + topMargin + bottomMargin;

		edWidth = layout.width;
		edHeight = layout.height;

		//미니 스몰세로
		int bottomMargin = (Config.PRODUCT_CALENDAR_MINI.equalsIgnoreCase(Config.getPROD_CODE()) || Config.isCalendarVert(Config.getPROD_CODE())) ? 33 : 40;
		if (isThumbnailView())
			bottomMargin *= getThumbnailRatioY();

		this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// Shadow ���������.
		RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height + bottomMargin);
		shadowLayer2 = new FrameLayout(this.getContext());
		ViewGroup.MarginLayoutParams shadowParams = new ARelativeLayoutParams(shadowlayout);
		shadowLayer2.setLayoutParams(shadowParams); 
	
		ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);

		containerlayout.setMargins(0, topMargin, 0, bottomMargin);
		
		containerLayer = new SnapsFrameLayout(this.getContext());
		containerLayer.setLayout(new RelativeLayout.LayoutParams(containerlayout));
		this.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());
		bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(bonusLayer);

		// bgLayer ���������.
		RelativeLayout.LayoutParams baseLayout = new RelativeLayout.LayoutParams(this.width, this.height);
		RelativeLayout.LayoutParams kakaobookLayout = null;

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

		//이미지 로딩 완료 체크 객체 생성
		initImageLoadCheckTask();

		// Back Ground ������.
		loadBgLayer(previewBgColor);

		requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);

		Dlog.d("setSnapsPage() page:" + number);
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

		this.addView(shadowLayer2);

		this.bringToFront();

		try {
			if (Config.isCalendarWide(Config.getPROD_CODE()))
				shadowLayer2.setBackgroundResource(R.drawable.calendar_wide);
			else if (Config.PRODUCT_CALENDAR_MINI.equalsIgnoreCase(Config.getPROD_CODE()))
				shadowLayer2.setBackgroundResource(R.drawable.calendar_mini);

			else if (Config.PRODUCT_CALENDAR_VERTICAL.equalsIgnoreCase(Config.getPROD_CODE()))
				shadowLayer2.setBackgroundResource(R.drawable.calendar_vert);
			else if (Config.isCalendarNormalVert(Config.getPROD_CODE()))
				shadowLayer2.setBackgroundResource(R.drawable.calendar_normal_vert);
			else if (Config.isCalenderWall(Config.getPROD_CODE()) || Config.isCalenderSchedule(Config.getPROD_CODE())) {
				mSpringImgView = null;
				mSpringImgView = new ImageView(getContext());
				int marginX  = 30;
				int marginY = 20;
				if (isThumbnailView()) {
					marginX *= getThumbnailRatioX();
					marginY *= getThumbnailRatioY();
				}

				LayoutParams parm = Config.isCalenderWall(Config.getPROD_CODE()) ? new LayoutParams(LayoutParams.MATCH_PARENT, marginX) : new LayoutParams(LayoutParams.MATCH_PARENT, marginY);
				mSpringImgView.setLayoutParams(parm);
				if (Config.isCalenderWall(Config.getPROD_CODE())) {
					if (Const_ThumbNail.PRODUCT_WALL_CALENDAR_LARGE.equals(Config.getPROD_CODE())) {
						//벽걸이 달력 라지
						mSpringImgView.setBackgroundResource(R.drawable.wall_skin_large);
					}
					else {
						mSpringImgView.setBackgroundResource(R.drawable.wall_skin);
					}
				}
				else {
					mSpringImgView.setBackgroundResource(R.drawable.schedule_skin);
				}
				shadowLayer2.addView(mSpringImgView);

			} else
				shadowLayer2.setBackgroundResource(R.drawable.calendar_normal);
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}

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
		calendarTextView.drawText();

		//XML에 작성할 정보들..(달력은 어차피 모든 썸네일을 다 따니까..)
		calendarTextView.writeXMLInfo();

		controlLayer.addView(calendarTextView);
		controlLayer.bringToFront();
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
		if(mSpringImgView != null) {
			Drawable d = mSpringImgView.getBackground();
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
