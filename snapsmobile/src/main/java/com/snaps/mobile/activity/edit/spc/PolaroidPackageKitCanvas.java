package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.customui.RotateImageView;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.skin.SnapsSkinRequestAttribute;
import com.snaps.mobile.activity.edit.skin.SnapsSkinUtil;

public class PolaroidPackageKitCanvas extends SnapsPageCanvas {
	private static final String TAG = PolaroidPackageKitCanvas.class.getSimpleName();

	public PolaroidPackageKitCanvas(Context context) {
		super(context);
	}

	@Override
	protected void loadShadowLayer() {
//		try {
//			shadowLayer.setBackgroundResource(R.drawable.img_polaroid_package_skin);
//		} catch (OutOfMemoryError e) {
//			Dlog.e(TAG, e);
//		}
	}

	@Override
	protected void loadPageLayer() {}

	@Override
	protected void loadControlLayer() {
		super.loadControlLayer();

		String productCode = Config.getPROD_CODE();
		if (productCode.equals(Const_PRODUCT.PRODUCT_PACKAGE_NEW_POLAROID_MINI)) {
			int count = layoutLayer.getChildCount();
			for (int i = 0; i < count; i++) {
				View view = layoutLayer.getChildAt(i);
				if (view instanceof RotateImageView) {
					//확대되서 +버튼이 너무 크게 나와서 축소
					view.setScaleX(0.6f);
					view.setScaleY(0.6f);
				}
			}
		}
	}

	@Override
	protected void loadBonusLayer() {
		try {
			if (getSnapsPage().type.equalsIgnoreCase("page")) {
				ImageView skin = new ImageView(getContext());
				LayoutParams param = new LayoutParams(bonusLayer.getLayoutParams());
				param.width = pageLayer.getLayoutParams().width + rightMargin + leftMargin;
				param.height = pageLayer.getLayoutParams().height + topMargin + bottomMargin;
				skin.setLayoutParams(param);

				SnapsSkinUtil.loadSkinImage(new SnapsSkinRequestAttribute.Builder()
						.setContext(getContext())
						.setResourceFileName(getSkinName())
						.setSkinBackgroundView(skin).create());

				bonusLayer.addView(skin);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private String getSkinName() {
		String productCode = Config.getPROD_CODE();
		if (productCode.equals(Const_PRODUCT.PRODUCT_PACKAGE_NEW_POLAROID)) {
			return SnapsSkinConstants.POLAROID_ORIGINAL_SKIN_FILE_NAME;
		}
		else if (productCode.equals(Const_PRODUCT.PRODUCT_PACKAGE_NEW_POLAROID_MINI)) {
			return SnapsSkinConstants.POLAROID_MINI_SKIN_FILE_NAME;
		}
		return "";
	}

	@Override
	protected void initMargin() {
		leftMargin = 1;
		topMargin = 1;
		rightMargin = 1;
		bottomMargin = 1;

		if (isThumbnailView()) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
			bottomMargin = 0;
		}
	}

	@Override
	protected void loadAllLayers() {
		super.loadAllLayers();

		loadBgLayerForegroundImage();
	}

	protected void loadBgLayerForegroundImage() {
//		try {
//			if (bgLayerForegroundBitmap == null || bgLayerForegroundBitmap.isRecycled() || bgLayerForegroundDrawable == null) {
//				bgLayerForegroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_polaroid_texture);
//
//				int width = (int) (bgLayerForegroundBitmap.getWidth() / getFitScaleX());
//				int height = (int) (bgLayerForegroundBitmap.getHeight() / getFitScaleY());
//
//				if (isThumbnailView()) {
//					width *= getThumbnailRatioX();
//					height *= getThumbnailRatioY();
//				}
//
//				bgLayerForegroundBitmap = CropUtil.getScaledBitmap(bgLayerForegroundBitmap, width, height);
//				bgLayerForegroundDrawable = new BitmapDrawable(getResources(), bgLayerForegroundBitmap);
//				bgLayerForegroundDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//			}
//			bgLayerForegroundImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//			bgLayerForegroundImageView.setImageDrawable(bgLayerForegroundDrawable);
//		} catch (Exception e) { Dlog.e(TAG, e); }
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

		try {
			ViewUnbindHelper.unbindReferences(bgLayer, null, false);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		super.onDestroyCanvas();
	}
}
