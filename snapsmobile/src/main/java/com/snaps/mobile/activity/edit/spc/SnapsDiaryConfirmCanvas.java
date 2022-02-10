package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.ImageLoadView;
import com.snaps.common.spc.view.SnapsDiaryTextView;
import com.snaps.common.spc.view.SnapsTextView;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BRect;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryCanvasDimensChangeListener;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryFontInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;

public class SnapsDiaryConfirmCanvas extends SnapsPageCanvas {
	private static final String TAG = SnapsDiaryConfirmCanvas.class.getSimpleName();
	private SnapsDiaryTextView.ISnapsDiaryTextControlListener diaryTextControlListener = null;
	private ISnapsDiaryCanvasDimensChangeListener canvasDimensChangedListener = null;

	public SnapsDiaryConfirmCanvas(Context context) {
		super(context);
	}

	public SnapsDiaryConfirmCanvas(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void setOnCanvasDimensChangedListener(ISnapsDiaryCanvasDimensChangeListener canvasDimensChangedListener) {
		this.canvasDimensChangedListener = canvasDimensChangedListener;
	}

	@Override
	protected void loadShadowLayer() {
	}

	@Override
	protected void loadPageLayer() {
	}

	@Override
	protected void loadBonusLayer() {
	}

	@Override
	protected void initMargin() {
	}

	public SnapsDiaryTextView.ISnapsDiaryTextControlListener getDiaryTextControlListener() {
		return diaryTextControlListener;
	}

	public void setDiaryTextControlListener(SnapsDiaryTextView.ISnapsDiaryTextControlListener diaryTextLoadedListener) {
		this.diaryTextControlListener = diaryTextLoadedListener;
	}

	/**
	 * 일기가 흐릿하게 보인다는 이슈로 화면 크기에 맞게 ed사이즈를 확대 시킨다.
     */
	private void setLayoutControlScaledOffset(float scaleX, float scaleY) {
		if (_snapsPage == null || _snapsPage.getLayoutList() == null) return;

		for (SnapsControl layer : _snapsPage.getLayoutList()) {
			if (layer != null && layer instanceof SnapsLayoutControl) {
				if (!_snapsPage.type.equalsIgnoreCase("hidden")) {
					SnapsLayoutControl layoutControl = (SnapsLayoutControl) layer;
					layoutControl.scaledX = String.valueOf((int) (Float.parseFloat(layoutControl.x) * scaleX));
					layoutControl.scaledY = String.valueOf((int) (Float.parseFloat(layoutControl.y) * scaleY));
					layoutControl.scaledWidth = String.valueOf((int) (Float.parseFloat(layoutControl.width) * scaleX));
					layoutControl.scaledHeight = String.valueOf((int) (Float.parseFloat(layoutControl.height) * scaleY));
				}
			}
		}
	}

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;
		// SnapsPageCanvas를 하나만 사용할 경우.
		removeItems(this);

		LayoutParams layout = new LayoutParams(this.getLayoutParams());

		BRect rect = page.getImageLayerRect(); //팀플릿에 기록된 이미지 영역
		if (rect != null) {
			this.width = rect.width();
			this.height = rect.height();
			leftMargin -= rect.left;
			topMargin -= rect.top;
		}

		float ratioCanvasWH = this.width / (float) this.height;
		float screenWidth = 0;
		int fixedCanvasHeight = 0;

		if (isScaledThumbnailMakeMode()) {
			if (width < height) {
				fixedCanvasHeight = SCALE_THUMBNAIL_MAX_OFFSET;
				screenWidth = fixedCanvasHeight * ratioCanvasWH;
			} else {
				screenWidth = SCALE_THUMBNAIL_MAX_OFFSET;
				fixedCanvasHeight = (int) (screenWidth / ratioCanvasWH);
			}
		} else {
			screenWidth = UIUtil.getScreenWidth(mContext) - (int) getResources().getDimension(R.dimen.snaps_diary_list_margin);
			fixedCanvasHeight = (int) (screenWidth / ratioCanvasWH);
		}

		mScaleX = screenWidth / (float) this.width;
		mScaleY = fixedCanvasHeight / (float) this.height;

		/**
		 * 일기는 화면 크기에 맞춰서 ed사이즈를 늘린다.
		 */
		this.width = (int) (this.width * mScaleX);
		this.height = (int) (this.height * mScaleY);
		this.leftMargin = (int) (this.leftMargin * mScaleX);
		this.topMargin = (int) (this.topMargin * mScaleY);

		setLayoutControlScaledOffset(mScaleX, mScaleY);

		initMargin();

		layout.width = this.width;
		layout.height = this.height;
		edWidth = layout.width;
		edHeight = layout.height;


		this.setLayoutParams(new ARelativeLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// Shadow 초기화.
		LayoutParams shadowlayout = new LayoutParams(layout.width, layout.height);
		shadowLayer = new FrameLayout(this.getContext());
		shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(shadowLayer);

		MarginLayoutParams containerlayout = new MarginLayoutParams(this.width, this.height);
		containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

		containerLayer = new SnapsFrameLayout(this.getContext());
		containerLayer.setLayout(new ARelativeLayoutParams(containerlayout));
		this.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());
		bonusLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(bonusLayer);

		// bgLayer 초기화.
		LayoutParams baseLayout = new LayoutParams(this.width, this.height);

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

		Dlog.d("setSnapsPage() page number:" + number);
		
		setPinchZoomScaleLimit(_snapsPage);

		if (canvasDimensChangedListener != null) {
			canvasDimensChangedListener.onCanvasDimensChanged(fixedCanvasHeight);
		}
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

				case SnapsControl.CONTROLTYPE_TEXT:
				case SnapsControl.CONTROLTYPE_GRID:
					// 텍스트.

					SnapsTextControl textControl = (SnapsTextControl) control;

					SnapsTextView text = new SnapsTextView(_snapsPage.type, textControl.controType, this.getContext(), textControl, this._callback);

					if (isRealPagerView()) {
						text.setSnapsControl(textControl);

						AutoSaveManager saveMan = AutoSaveManager.getInstance();
						if (saveMan != null && saveMan.isRecoveryMode()) {
//							textControl.setControlId(-1);
						}

						int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
						textControl.setControlId(generatedId);
						text.setId(generatedId);
					}

					controlLayer.addView(text);
					if (Config.isCalendar(Config.getPROD_CODE()))
						controlLayer.bringToFront();

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
						// view.setPivotX(0);
						// view.setPivotY(0);
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
				case SnapsControl.CONTROLTYPE_GRID:
					// 텍스트.
					SnapsTextControl textControl = (SnapsTextControl) control;

					if (textControl.text == null)
						textControl.text = "";

					final SnapsDiaryTextView text = new SnapsDiaryTextView(this.getContext(), textControl, _callback);
					SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
					SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
					if(writeInfo != null && writeInfo.getContents() != null)
						textControl.text = writeInfo.getContents();
					else
						textControl.text = "";

					SnapsDiaryFontInfo fontInfo = new SnapsDiaryFontInfo();
					fontInfo.setFontSize(textControl.format.fontSize);
					fontInfo.setFontFace(textControl.format.fontFace);
					fontInfo.setTextControlWidth(textControl.width);
					dataManager.setSnapsDiaryFont(fontInfo);

					text.setTextLoadedListener(getDiaryTextControlListener());

					text.setTag(textControl);
					text.getTextView().setTag(textControl);

					TextView textView = text.getTextView();
					textView.setText(textControl.text);
					textView.setGravity(Gravity.TOP | Gravity.LEFT);

					textView.setTextColor(SnapsDiaryConstants.DESIGN_TEST_FUNCTION  ?  Color.argb(255, 0, 0, 0) : Color.TRANSPARENT);

					if (isRealPagerView()) {
						int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
						textControl.setControlId(generatedId);
						text.setId(generatedId);
					}

					controlLayer.addView(text);
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
