package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.BRect;
import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageViewDp;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.activity.name_sticker.NameStickerWriteActivity;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import errorhandle.logger.Logg;

import static com.snaps.common.utils.constant.Const_PRODUCT.BABY_NAME_STICKER_LARGE;
import static com.snaps.common.utils.constant.Const_PRODUCT.BABY_NAME_STICKER_MEDIUM;
import static com.snaps.common.utils.constant.Const_PRODUCT.BABY_NAME_STICKER_MINI;
import static com.snaps.common.utils.constant.Const_PRODUCT.BABY_NAME_STICKER_SMALL;

/**
 *
 * com.snaps.kakao.activity.edit.spc
 * SnapsPageCanvas.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 23.
 * @Version : 
 */
public class BabyNameStickerPageCanvas extends SnapsPageCanvas {
	private static final String TAG = BabyNameStickerPageCanvas.class.getSimpleName();

	public BabyNameStickerPageCanvas(Context context) {
		super(context);
	}

	public BabyNameStickerPageCanvas(Context context, AttributeSet attr ) {
		super( context , attr );
	}

	@Override
	protected void loadShadowLayer() {

	}

	@Override
	protected void loadPageLayer() {
		// 책등이나 필요한 부분을 넣어준다.
	}
	
	@Override
	protected void loadBonusLayer() {
		String skinName = getSkinName();
		if( !StringUtil.isEmpty(skinName) ) {
			try {
				SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
						.setContext(getContext())
						.setResourceFileName(skinName)
						.setSkinBackgroundView(bonusLayer)
						.create());
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

	private String getSkinName() {
		switch (Config.getPROD_CODE()) {
			case BABY_NAME_STICKER_MINI:
				return SnapsSkinConstants.BABY_NAME_STICKER_MINI;
			case BABY_NAME_STICKER_SMALL:
				return SnapsSkinConstants.BABY_NAME_STICKER_SMALL;
			case BABY_NAME_STICKER_MEDIUM:
				return SnapsSkinConstants.BABY_NAME_STICKER_MEDIUM;
			case BABY_NAME_STICKER_LARGE:
				return SnapsSkinConstants.BABY_NAME_STICKER_LARGE;
			default:
				return "";
		}
	}


	@Override
	protected void initMargin() {
		leftMargin = UIUtil.convertDPtoPX(getContext(), 0);
		topMargin = UIUtil.convertDPtoPX(getContext(), 0);
		rightMargin = UIUtil.convertDPtoPX(getContext(), 0);
		bottomMargin = UIUtil.convertDPtoPX(getContext(), 0);
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

	@Override
	public void setSnapsPage(SnapsPage page, int number, boolean isBg, String previewBgColor) {
		this._snapsPage = page;
		this._page = number;

		removeItems(this);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());

		this.width = changeDpToPx(getContext(),page.getWidth());
		this.height = changeDpToPx(getContext(),Integer.parseInt(page.height));

		initMargin();

		if (page.type.equals("cover")) {
			layout.width = this.width;
			layout.height = this.height;
		} else {
			layout.width = this.width + leftMargin + rightMargin;
			layout.height = this.height + topMargin + bottomMargin;
		}

		edWidth = layout.width;
		edHeight = layout.height;

		this.setLayoutParams(new ARelativeLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// Shadow 초기화.
		RelativeLayout.LayoutParams shadowlayout = new RelativeLayout.LayoutParams(layout.width, layout.height);
		shadowLayer = new FrameLayout(this.getContext());
		shadowLayer.setLayoutParams(new ARelativeLayoutParams(shadowlayout));
		this.addView(shadowLayer);

		ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);
		if (page.type.equals("cover")) {
			containerlayout.setMargins(0, 0, 0, 0);
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

		if (isBg || previewBgColor != null)
			containerLayer.addView(bgLayer);

		// layoutLayer 초기화.
		layoutLayer = new FrameLayout(this.getContext());
		layoutLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
		containerLayer.addView(layoutLayer);

		// controllLayer 초기화. ppppoint
		controlLayer = new FrameLayout(this.getContext());
		controlLayer.setLayoutParams(new ARelativeLayoutParams(kakaobookLayout == null ? baseLayout : kakaobookLayout));
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
		loadBgLayer(previewBgColor); //형태는 갖추고 있어야 하니까 우선 BG 만 로딩한다.

		requestLoadAllLayerWithDelay(DELAY_TIME_FOR_LOAD_IMG_LAYER);
		Dlog.d("setSnapsPage() page:" + number);
		setBackgroundColorIfSmartSnapsSearching();
	}

	@Override
	protected void setMutableTextControl(SnapsControl control) {
		if (control == null || !(control instanceof SnapsTextControl)) return;

		SnapsTextControl textControl = (SnapsTextControl) control;
		if (textControl.text == null)
			textControl.text = "";

		final SnapsTextToImageViewDp snapsTextToImageView = new SnapsTextToImageViewDp(getContext(), textControl,!isRealPagerView());;

		if (isRealPagerView()) {
			snapsTextToImageView.setTag(textControl);
			snapsTextToImageView.getPlaceHolderTextView().setTag(textControl);
			snapsTextToImageView.addClickEventListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

					Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
					intent.putExtra("control_id", snapsTextToImageView.getId());
					intent.putExtra("dummy_control_id", v.getId());
					intent.putExtra("isEdit", false);

					getContext().sendBroadcast(intent);
				}
			});
			int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
			textControl.setControlId(generatedId);
			snapsTextToImageView.setId(generatedId);

		} else if (isThumbnailView()) {
			snapsTextToImageView.setTag(textControl);
			snapsTextToImageView.getPlaceHolderTextView().setTag(textControl);
			snapsTextToImageView.setThumbnail(getThumbnailRatioX(), getThumbnailRatioY());
			TextView texView = snapsTextToImageView.getPlaceHolderTextView();
			if (texView != null) {
				texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
			}
		}

		if (mHandler != null) {
			Message message = new Message();
			message.obj = snapsTextToImageView;
			message.what = MSG_NOTIFY_TEXT_TO_IMAGEVIEW;
			mHandler.sendMessageDelayed(message, 100);
		}

//		snapsTextToImageView.notifyChildrenControlState();

		controlLayer.addView(snapsTextToImageView);
	}

	@Override
	protected BRect getCanvasLimitOffsetRect(Canvas canvas, int offsetY) {
		final int MARGIN_OFFSET = (int) changeDpToPx(getContext(), (int) PAGING_MARGIN_OFFSET) * 4;
		//좌표 수치가 100% 정확하게 맞지 않아서, 보정 코드..
		int l = MARGIN_OFFSET;
		int t = MARGIN_OFFSET;
		int r = (int) (canvas.getWidth() - MARGIN_OFFSET);
		int b = (int)(canvas.getHeight() - MARGIN_OFFSET);
		if (isLandscapeMode()) {
			t += offsetY;
			b -= offsetY;
		}
		return new BRect(l, t, r, b);
	}
}
