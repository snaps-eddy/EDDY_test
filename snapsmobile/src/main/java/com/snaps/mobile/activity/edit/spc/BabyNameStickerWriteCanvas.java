package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.spc.SnapsFrameLayout;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.text.SnapsTextToImageViewDp;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.common.utils.ui.ViewIDGenerator;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

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
public class BabyNameStickerWriteCanvas extends SnapsPageCanvas {
	private static final String TAG = BabyNameStickerWriteCanvas.class.getSimpleName();
	public BabyNameStickerWriteCanvas(Context context) {
		super(context);
	}

	public BabyNameStickerWriteCanvas(Context context, AttributeSet attr ) {
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
		int skinName = getSkinName();
		if( skinName != 0) {
			try {
				bonusLayer.setBackgroundResource(skinName);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

	private int getSkinName() {
		switch (Config.getPROD_CODE()) {
			case BABY_NAME_STICKER_MINI:
				return R.drawable.baby_name_sticker_mini;
			case BABY_NAME_STICKER_SMALL:
				return R.drawable.baby_name_sticker_small;
			case BABY_NAME_STICKER_MEDIUM:
				return R.drawable.baby_name_sticker_medium;
			case BABY_NAME_STICKER_LARGE:
				return R.drawable.baby_name_sticker_large;
			default:
				return 0;

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
		SnapsControl snapsControl = page.getLayerControls().get(0);
		if(snapsControl == null) return;
		// SnapsPageCanvas를 하나만 사용할 경우.
		removeItems(this);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(this.getLayoutParams());
		layout.setMargins(0, 0, 0, 0);

		final int shadowDimension = 10;
		final int spineDimension = 16;

		// 페이지의 크기를 구한다.
		this.width = UIUtil.convertDPtoPXBabyNameSticker(getContext(),page.getWidth());
		this.height = UIUtil.convertDPtoPXBabyNameSticker(getContext(),Integer.parseInt(page.height));

		int cellWidth = UIUtil.convertDPtoPXBabyNameSticker(getContext(),snapsControl.getIntWidth());
		int cellHeight = UIUtil.convertDPtoPXBabyNameSticker(getContext(),snapsControl.getIntHeight());
		int offSetX = UIUtil.convertDPtoPXBabyNameSticker(getContext(),snapsControl.getIntX());
		int offSetY = UIUtil.convertDPtoPXBabyNameSticker(getContext(),snapsControl.getIntY());
		FrameLayout fakeLayout = new FrameLayout(getContext());
		ARelativeLayoutParams fakeLayoutParams = new ARelativeLayoutParams( cellWidth, cellHeight);
		fakeLayout.setLayoutParams(fakeLayoutParams);
		this.addView(fakeLayout);

		initMargin();

			layout.width = this.width + leftMargin + rightMargin;
			layout.height = this.height + topMargin + bottomMargin;

		edWidth = layout.width;
		edHeight = layout.height;

		this.setLayoutParams(new ARelativeLayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// Shadow 초기화.
		FrameLayout.LayoutParams shadowlayout = new FrameLayout.LayoutParams(layout.width, layout.height);

		ViewGroup.MarginLayoutParams containerlayout = new ViewGroup.MarginLayoutParams(this.width, this.height);

		containerlayout.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

		containerLayer = new SnapsFrameLayout(this.getContext());
		AFrameLayoutParams containerLayoutParams = new AFrameLayoutParams(shadowlayout);
		containerLayoutParams.leftMargin = -offSetX;
		containerLayoutParams.topMargin = -offSetY;
		containerLayer.setLayout(containerLayoutParams);
		fakeLayout.addView(containerLayer);

		bonusLayer = new FrameLayout(this.getContext());

		AFrameLayoutParams bonusLayoutParams = new AFrameLayoutParams(shadowlayout);
		bonusLayoutParams.width = cellWidth ;
		bonusLayoutParams.height = cellHeight ;

		bonusLayer.setLayoutParams(bonusLayoutParams);
		bonusLayer.setPadding(0, 0, 0, 0);
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
		this.setPadding(0, 0, 0, 0);
	}

	@Override
	protected void setMutableTextControl(SnapsControl control) {
		if (control == null || !(control instanceof SnapsTextControl)) return;

		SnapsTextControl textControl = (SnapsTextControl) control;
		if (textControl.text == null)
			textControl.text = "";

		final SnapsTextToImageViewDp snapsTextToImageView = new SnapsTextToImageViewDp(getContext(), textControl);
		snapsTextToImageView.setTag(textControl);
		snapsTextToImageView.getPlaceHolderTextView().setTag(textControl);

		if (isRealPagerView()) {
			int generatedId = ViewIDGenerator.generateViewId(textControl.getControlId());
			textControl.setControlId(generatedId);
			snapsTextToImageView.setId(generatedId);

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
		} else if (isThumbnailView()) {
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
}
