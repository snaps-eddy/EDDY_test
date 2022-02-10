package com.snaps.common.spc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * com.snaps.spc.view SnapsTextView.java
 * 
 * @author JaeMyung Park
 * @Date : 2013. 6. 3.
 * @Version :
 */
public class SnapsCalendarTextView extends RelativeLayout {
	private static final String TAG = SnapsCalendarTextView.class.getSimpleName();

	private List<SnapsTextControl> arrTextControls = null;
	private Context context = null;
	private boolean isThumbnail = false;
	private boolean isWritedXMLInfo = false;
	private CustomCalendarTextView drawTextView;

	public SnapsCalendarTextView(Context context) {
		super(context);
	}

	public SnapsCalendarTextView(Context context, SnapsFrameLayout container) {
		super(context);
		init(context, container);
	}

	public void init(Context context, SnapsFrameLayout container) {
		this.context = context;
		this.arrTextControls = new ArrayList<>();
		this.isWritedXMLInfo = false;
		this.drawTextView = new CustomCalendarTextView(context);

//		MarginLayoutParams params = (MarginLayoutParams) container.getLayoutParams();
		setLayoutParams(new RelativeLayout.LayoutParams(container.getLayoutParams()));
	}

	public void addTextControl(SnapsTextControl textControl) {
		if (arrTextControls == null || textControl == null) return;
		arrTextControls.add(textControl);
	}

	public void setIsThumbnailView(boolean isThumbnail) {
		this.isThumbnail = isThumbnail;
	}

	/**
	 * UI 처리
	 */
	public void drawText() {
		drawTextView.setLayoutParams(new ViewGroup.LayoutParams(getLayoutParams().width, getLayoutParams().height));
		drawTextView.drawText(arrTextControls);
		this.addView(drawTextView);
	}

	public void setDrawTextOffsetY(float offsetY) {
		if (drawTextView == null) return;
		drawTextView.setFixDrawTextOffsetY(offsetY);
	}

	/**
	 * XML에 작성할 각 텍스트 컨트롤의 좌표
	 */
	public void writeXMLInfo() {
		if (isThumbnail() || isWritedXMLInfo) return;

		LineText lineText;
		for (SnapsTextControl textControl : arrTextControls) {
			if (textControl != null && textControl.textList != null && textControl.textList.isEmpty()) {
				lineText = new LineText();
				lineText.text = textControl.text != null ? textControl.text : "";
				lineText.width = textControl.width;
				lineText.height = textControl.height;
				lineText.x = textControl.getX() + "";
				lineText.y = textControl.y + "";

				textControl.textList.add(lineText);
			}
		}

		isWritedXMLInfo = true;
	}

	public boolean isThumbnail() {
		return isThumbnail;
	}

	private int getControlTextColor(SnapsTextControl control) {
		String color = null;
		if (control.format.fontColor.compareTo("0") == 0)
			color = "ff000000";
		else {
			if (control.format.fontColor.length() == 8) {
				color = control.format.fontColor;
			} else if (control.format.fontColor.length() > 0) {
				if (!control.format.fontColor.startsWith("#"))
					color = "ff" + control.format.fontColor;
			} else {
				color = "ff000000";
			}
		}

		return !StringUtil.isEmpty(color) ? Color.parseColor("#" + color) : Color.parseColor("#ff000000");
	}

	private float getControlTextSize(SnapsTextControl control) {
		float size = 1.f;
		try {
			if (!StringUtil.isEmpty(control.format.fontSize))
				size = Float.parseFloat(control.format.fontSize);
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}

		return UIUtil.convertPixelsToSp(getContext(), size);
	}

	private class CustomCalendarTextView extends androidx.appcompat.widget.AppCompatTextView {

		Paint paint = null;
		List<SnapsTextControl> arrTextControls = null;
		private final Paint mPaint = new Paint();
		private Rect textBounds = new Rect();
		private float fixDrawTextOffsetY = 0.f;

		public CustomCalendarTextView(Context context) {
			super(context);
			setWillNotDraw(false);
		}

		public void setFixDrawTextOffsetY(float fixDrawTextOffsetY) {
			this.fixDrawTextOffsetY = fixDrawTextOffsetY;
		}

		public void drawText(List<SnapsTextControl> controls) {
			setTextControls(controls);
			invalidate();
		}

		private List<SnapsTextControl> getTextControls() {
			return this.arrTextControls;
		}

		private void setTextControls(List<SnapsTextControl> controls) {
			this.arrTextControls = controls;
		}
		
		private Typeface getControlTypeFace(SnapsTextControl control) {
			if (control == null || control.format == null || isThumbnail) return Const_VALUE.SNAPS_TYPEFACE_YG033;
			
			Typeface fontFace = null;
			// KR버전만 소망체 유효함. 닷컴버전은 meiryo로 전부설정
			if (control.format.alterFontFace.equalsIgnoreCase("스냅스 소망2 M") && Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR)) {
				try {
					fontFace = Const_VALUE.SNAPS_TYPEFACE_SOMANG;
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			} else {
				try {
					fontFace = Const_VALUE.SNAPS_TYPEFACE_YG033;
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}

			if (control.type.compareTo("calendar") == 0) {
				String uiFont = "true".equalsIgnoreCase(control.format.bold) ? (control.format.fontFace + " Bold") : control.format.fontFace;
				fontFace = Const_VALUE.sTypefaceMap.get(uiFont);
			}

			return fontFace;
		}

		private Rect getTextRect(String text) {
			if (mPaint != null)
				mPaint.getTextBounds(text, 0, text.length(), textBounds);
			return textBounds;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			synchronized (getTextControls()) {

				for (SnapsTextControl textControl : getTextControls()) {
					if (textControl == null || textControl.text == null || textControl.text.equalsIgnoreCase("null")) continue;

					String text = textControl.text;
					float x = textControl.getX();
					float y = textControl.getIntY();
					float width = textControl.getIntWidth();
					float height = textControl.getIntHeight();
					float textSize = isThumbnail() ? UIUtil.convertPixelsToSp(getContext(), 3.f) : getControlTextSize(textControl);
					this.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

					mPaint.setTextSize(this.getTextSize());
					mPaint.setColor(getControlTextColor(textControl));
					mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
					mPaint.setAntiAlias(true);

					Typeface typeFace = null;
					try {
						typeFace = getControlTypeFace(textControl);
						if (typeFace != null)
							mPaint.setTypeface(typeFace);
						else
							mPaint.setTypeface(Const_VALUE.SNAPS_TYPEFACE_YG033);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}

					float textX = x;
					float textY = y;
					Rect textOffset = getTextRect(text);
					if (textControl.format != null) {
						if (textControl.format.align.equalsIgnoreCase("right")) {
							textX = (x + width) - textOffset.width();
						} else if (textControl.format.align.equalsIgnoreCase("left")) {
							textX = x;
						} else {
							textX = x + ((width - textOffset.width()) / 2);
						}

						if (textControl.isCalendarFrontText) { //Front 텍스트는 일렬로 쭉 늘어서 있는데, 폰트에 따라서 높이가 다르기 때문에 삐뚤어져 보여서 꼼수를 썼다..
							textY = y + (Math.max(1, textControl.getIntHeight()) / 2);
						} else {
							textY = (y + textOffset.height()) - (Math.max(1, textOffset.exactCenterY()) / 2);
						}
					}

					//CS대응
					//정확히는 CS대응이 아니고 디자인팀에서 작업한 템플릿에서 사용하는 폰트가 없는 경우 이를 쉽게 찾기 위한 목적
					if (Config.useDrawUndefinedFontSearchArea()) {
						if (typeFace == null && isThumbnail() == false) {
							float left = textX - 1;
							float top = textY - textOffset.height() - 1;
							float right = left + textOffset.width() + 2;
							float bottom = textY + 2;
							int preColor = mPaint.getColor();
							mPaint.setColor(Color.MAGENTA);
							canvas.drawRect(left, top, right, bottom, mPaint);
							mPaint.setColor(preColor);
						}
					}

					canvas.drawText(text, textX, textY + fixDrawTextOffsetY, mPaint);
				}
			}
		}
	}
}
