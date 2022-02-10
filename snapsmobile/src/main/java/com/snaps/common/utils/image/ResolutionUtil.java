package com.snaps.common.utils.image;

import android.graphics.Rect;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

public class ResolutionUtil {
	private static final String TAG = ResolutionUtil.class.getSimpleName();
	/***isEnableResolution
	 * dpi를 구해서. 권장 해상도인 경우는 true를 리턴하는 함수..
	 *
	 * @return
	 */
	static public boolean isEnableResolution(float mmPageWidth, int pxPageWidth, SnapsLayoutControl control) throws Exception {
		boolean retValue = isEnableResolution(mmPageWidth, pxPageWidth, control, control.imgData, getScaleFromImgData(control.imgData), getAngleFromImgData(control.imgData));
		control.isNoPrintImage = retValue;
		return retValue;
	}

	static public boolean isEnableResolution(float mmPageWidth, int pxPageWidth, String layoutWdith, MyPhotoSelectImageData imgData) throws Exception {
		boolean retValue = isEnableResolution(mmPageWidth, pxPageWidth, layoutWdith, imgData, getScaleFromImgData(imgData), getAngleFromImgData(imgData));
		return retValue;
	}

	static private float getScaleFromImgData(MyPhotoSelectImageData imgData) {
		return imgData != null && imgData.ADJ_CROP_INFO != null && imgData.ADJ_CROP_INFO.getImgRect() != null ? imgData.ADJ_CROP_INFO.getImgRect().scaleX : 1.f;
	}

	static private float getAngleFromImgData(MyPhotoSelectImageData imgData) {
		return (imgData != null && imgData.ADJ_CROP_INFO != null && imgData.ADJ_CROP_INFO.getImgRect() != null ? imgData.ADJ_CROP_INFO.getImgRect().angle : 0.f);
	}

	static public boolean isEnableResolution(float mmPageWidth, int pxPageWidth, SnapsLayoutControl control, MyPhotoSelectImageData imgData, float scale, float angle) throws Exception {
		return control != null && isEnableResolution(mmPageWidth, pxPageWidth, control.width, imgData, scale, angle);
	}

	static public boolean isEnableResolution(float mmPageWidth, int pxPageWidth, String layoutWidth, MyPhotoSelectImageData imgData, float scale, float angle) throws Exception {
		if (StringUtil.isEmpty(layoutWidth) || imgData == null) {
			Dlog.d("isEnableResolution() result:false [invalid params]");
			return false;
		}

		// 페이지 MM 사이즈를 CM으로 변경한다.
		float cmPage = mmPageWidth / 10.f;

		// image px
		float pxControlWidth = Float.parseFloat(layoutWidth);

		if (0 == pxPageWidth) {
			Dlog.d("isEnableResolution() result:false [0 == pxPageWidth]");
			return false;
		}

		float img_cm = (cmPage * pxControlWidth) / pxPageWidth;

		if (0 >= img_cm) {
			Dlog.d("isEnableResolution() result:false [0 >= img_cm]");
			return false;
		}

		float img_dpc = 0;

		Dlog.d("isEnableResolution() w:" + imgData.F_IMG_WIDTH + ", h:" + imgData.F_IMG_HEIGHT + ", rotate:" + imgData.ROTATE_ANGLE);

		float fAngle = 0.f, fScale = 1.f, fImgWidth, fImgHeight, fResult;
		try {
			if (!StringUtil.isEmpty(imgData.F_IMG_WIDTH) && !StringUtil.isEmpty(imgData.F_IMG_HEIGHT)) {
				if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
					fImgHeight = Float.parseFloat(imgData.F_IMG_WIDTH);
					fImgWidth = Float.parseFloat(imgData.F_IMG_HEIGHT);
				} else {
					fImgWidth = Float.parseFloat(imgData.F_IMG_WIDTH);
					fImgHeight = Float.parseFloat(imgData.F_IMG_HEIGHT);
				}
			} else {
				return false;
			}

			if (imgData.isAdjustableCropMode) {
				fScale = scale;//imgData.ADJ_CROP_INFO.getImgRect() != null ? imgData.ADJ_CROP_INFO.getImgRect().scaleX : imgData.tempEditScale;
				fAngle = imgData.ROTATE_ANGLE + Math.abs(angle);//imgData.ROTATE_ANGLE + Math.abs((imgData.ADJ_CROP_INFO.getImgRect() != null ? imgData.ADJ_CROP_INFO.getImgRect().angle : imgData.tempEditAngle));
			} else {
				fAngle = imgData.ROTATE_ANGLE;
			}

			if (fScale <= 0) {
				fScale = 1;
			}

			fAngle %= 180; //180도 돌았을때는 다시 원점이다.

			if (fAngle > 90) {
				fAngle = 180 - fAngle; //90에 가까울 수록 세로 비율에 가깝다.
			}

//			float diff = fImgHeight - fImgWidth;

			/**
			 * 0도 기준으로 이미지의 가로 90도일 때, 세로를 기준으로 맞추기 때문에
			 * 0~90도를 기준으로 가로에 세로 사이즈 배율을 더 한다.
			 */
//			fResult = (fImgWidth + (diff * Math.min(1, (fAngle / 90.f)))) / fScale;

            /**
             * 회전 시 제대로 반영되지 않기에 수정.
             */
            fResult = (fImgWidth + Math.min(1, (fAngle / 90.f))) / fScale;

			img_dpc = fResult / img_cm;

		} catch (Exception e) {
			// 이미지의 사이즈는 0도일때 사이즈 이므로 회전이 있다면 바꿔서 계산을 해야한다.
			if (!StringUtil.isEmpty(imgData.F_IMG_WIDTH) && !StringUtil.isEmpty(imgData.F_IMG_HEIGHT)) {
				if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
					img_dpc = Float.parseFloat(imgData.F_IMG_HEIGHT) / img_cm;
				} else {
					img_dpc = Float.parseFloat(imgData.F_IMG_WIDTH) / img_cm;
				}
			}
		}

		Dlog.d("isEnableResolution() image DPC:" + img_dpc);
		imgData.isNoPrint = img_dpc < getEnableSize();

		if (img_dpc > getEnableSize()) {
			return false;
		} else {
			return true;
		}
	}

	/***
	 * 권장 이미지 사이즈를 구하는 함수...
	 *
	 * @param mmPageWidth
	 * @param pxPageWidth
	 * @param control
	 * @return
	 */
	static public Rect getEnableResolution(String mmPageWidth, String pxPageWidth, SnapsLayoutControl control) {
		// 페이지 MM 사이즈를 CM으로 변경한다.
		float cmPage = Float.parseFloat(mmPageWidth) / 10.f;
		int nPxPageWidth = Integer.parseInt(pxPageWidth);

		// image px
		float pxControlWidth = Float.parseFloat(control.width);
		float pxControlHeight = Float.parseFloat(control.height);

		if (0 == nPxPageWidth) {
			Dlog.d("getEnableResolution() result:false [0 == nPxPageWidth]");
			return null;
		}

		float w_img_cm = pxControlWidth * (cmPage / nPxPageWidth);
		float h_img_cm = pxControlHeight * (cmPage / nPxPageWidth);

		if (0 >= w_img_cm || 0 > h_img_cm) {
			Dlog.d("getEnableResolution() result:false [0 >= w_img_cm || 0 > h_img_cm]");
			return null;
		}

		int imgWidth = (int) (getEnableSize() * w_img_cm);
		int imgHeight = (int) (getEnableSize() * h_img_cm);

		return new Rect(0, 0, imgWidth, imgHeight);
	}

	static public Rect getEnableResolution(String mmPageWidth, String pxPageWidth, String width, String height) {
		// 페이지 MM 사이즈를 CM으로 변경한다.
		float cmPage = Float.parseFloat(mmPageWidth) / 10.f;
		int nPxPageWidth = Integer.parseInt(pxPageWidth);

		// image px
		float pxControlWidth = Float.parseFloat(width);
		float pxControlHeight = Float.parseFloat(height);

		if (0 == nPxPageWidth) {
			return null;
		}

		float w_img_cm = pxControlWidth * (cmPage / nPxPageWidth);
		float h_img_cm = pxControlHeight * (cmPage / nPxPageWidth);

		if (0 >= w_img_cm || 0 > h_img_cm) {
			return null;
		}

		int imgWidth = (int) (getEnableSize() * w_img_cm);
		int imgHeight = (int) (getEnableSize() * h_img_cm);

		return new Rect(0, 0, imgWidth, imgHeight);
	}

//    private static float getEnableSize() {
//        if (Const_PRODUCT.isMarvelFrame()) //TODO  차 후에 적용한다고 함.
//            return MARVEL_FRAME_ENABLE_SIZE;
//        else
//            return ENABLE_SIZE;
//    }

	static public void setIdentifyPhotoEnableResolution(SnapsTemplate template, SnapsLayoutControl control) throws Exception {
		int wdith = 0;
		int height = 0;
		if (Integer.parseInt(control.imgData.F_IMG_WIDTH) >= Integer.parseInt(control.imgData.F_IMG_HEIGHT)) {
			wdith = Integer.parseInt(control.imgData.F_IMG_WIDTH);
			height = Integer.parseInt(control.imgData.F_IMG_HEIGHT);
		} else {
			wdith = Integer.parseInt(control.imgData.F_IMG_HEIGHT);
			height = Integer.parseInt(control.imgData.F_IMG_WIDTH);
		}

		Rect resolutionRect = ResolutionUtil.getEnableResolution(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_PIXEL_WIDTH, control);
		if (resolutionRect == null) {
			return;
		}

		int resolutionWidth = resolutionRect.right;
		int resolutionHeight = resolutionRect.bottom;
		control.isNoPrintImage = wdith < resolutionWidth || height < resolutionHeight;
	}

	private static float getEnableSize() {
		if (Const_PRODUCT.isMarvelFrame() || Const_PRODUCT.isWoodFrame() || Const_PRODUCT.isInteiorFrame() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
			return ResolutionConstants.PICTURE_PRINT_SIZE;
		} else if (Config.isIdentifyPhotoPrint()) {
			return ResolutionConstants.PICTURE_PRINT_SIZE_3X5;
		} else if (isFabricProduct()) {
			return ResolutionConstants.PRINT_FABRIC;
		} else {
			return ResolutionConstants.PRINT_SIZE;
		}
	}

	private static boolean isFabricProduct() {
		if (Const_PRODUCT.isMagicalReflectiveSloganProduct()) return true;
		if (Const_PRODUCT.isReflectiveSloganProduct()) return true;
		if (Const_PRODUCT.isHolographySloganProduct()) return true;
		if (Const_PRODUCT.isFabricPosterProduct()) return true;

		return false;
	}
}
