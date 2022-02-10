package com.snaps.common.utils.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsSceneMaskControl;
import com.snaps.common.structure.page.SnapsPage;

/**
 * @Marko 하트케이스, 범퍼케이스, 추후 다른 제품에 비슷한 기능이 필요할거 같아 따로 빼둠.
 */
public class SkinComposer {
    /**
     * 단말기 이미지, 단말기 케이스 이미지, 씬 마스크 이미지, 그리고 씬 마스크 좌표를 합성(???)해서 이미지(명칭이 애매...)를 만든다.
     *
     * @param bitmapPhone 단말기 이미지
     * @param bitmapCase  단말기 케이스 이미지
     * @param bitmapScene 씬 마스크
     * @return 결과 이미지
     */
    //TODO::메소드 이름이 이상하다.. 누가 수정해줘요~
    public Bitmap composeFinalMask(Bitmap bitmapPhone, Bitmap bitmapCase, SnapsSceneMaskControl sceneMaskControl, Bitmap bitmapScene, int scaleFactor) {

        SnapsTemplate snapsTemplate = SnapsTemplateManager.getInstance().getSnapsTemplate();
        SnapsPage snapsPage = snapsTemplate.getPages().get(0);
        int scaledWidth = snapsPage.getWidth() * scaleFactor;
        int scaledHeight = snapsPage.getHeight() * scaleFactor;

        int sceneMaskOriginCoordinatesX = (bitmapPhone.getWidth() - scaledWidth) / 2;
        int sceneMaskOriginCoordinatesY = (bitmapPhone.getHeight() - scaledHeight) / 2;
        int sceneMaskControlX = sceneMaskOriginCoordinatesX + (sceneMaskControl.getIntX() * 2);
        int sceneMaskControlY = sceneMaskOriginCoordinatesY + (sceneMaskControl.getIntY() * 2);

        int w = bitmapPhone.getWidth();
        int h = bitmapPhone.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Bitmap bitmapFinalMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasFinalMask = new Canvas(bitmapFinalMask);

        canvasFinalMask.drawBitmap(bitmapPhone, 0, 0, paint);
        canvasFinalMask.drawBitmap(bitmapCase, 0, 0, paint);

        Bitmap bitmapSceneMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasSceneMask = new Canvas(bitmapSceneMask);

        //이 메소드에서 아래가 핵심!
        ColorMatrix colorMatrixAlpha = new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 255, 0
        });
//
//        공부
//        https://jamssoft.tistory.com/159
//        https://developer.android.com/reference/android/graphics/ColorMatrix
//        만약 RGB값은 유지하고 알파 128기준으로 투명/불투명으로 설정하고 싶으면 아래와 같이 하면된다. (128이하는 불투명이다.)
//        ColorMatrix colorMatrixAlpha = new ColorMatrix(new float[]{
//                1, 0, 0, 0, 0,
//                0, 1, 0, 0, 0,
//                0, 0, 1, 0, 0,
//                0, 0, 0, 0, 255, -128 * 255
//        });
//
//        예를 들어보면
//        알파 0 : 0 x 255 + (-128 * 255) = -32640 --> 0
//        알파 127 : 127 x 255 + (-128 * 255) = 32385 + -32640 = -255 (0미만은 0) --> 0
//        알파 128 : 128 x 255 + (-128 * 255) = 32640 + -32640 = 0
//        알파 129 : 129 x 255 + (-128 * 255) = 32895 + -32640 = 255
//        알파 255 : 255 x 255 + (-128 * 255) = 65025 + -32640 = 32385 (255초과는 255) -> 255


        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrixAlpha));

        canvasSceneMask.drawBitmap(bitmapScene, sceneMaskControlX, sceneMaskControlY, paint);
        paint.setColorFilter(null);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvasFinalMask.drawBitmap(bitmapSceneMask, 0, 0, paint);
        paint.setXfermode(null);

        Bitmap bitmapCamera = createCameraBitmap(bitmapPhone, bitmapCase);
        canvasFinalMask.drawBitmap(bitmapCamera, 0, 0, paint);

        return bitmapFinalMask;
    }

    /**
     * 단말기 이미지, 단말기 케이스 이미지를 이용해서 카메라 영역 이미지를 뽑아낸다. (결과 이미지의 크기는 단말기 이미지2배 뻥튀기하고 가운데 위치하게 수, 단말기 케이스 이미지와 동일)
     *
     * @param bitmapPhone
     * @param bitmapCase
     * @return bitmap
     */
    private Bitmap createCameraBitmap(Bitmap bitmapPhone, Bitmap bitmapCase) {
        int w = bitmapPhone.getWidth();
        int h = bitmapPhone.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Bitmap bitmapCameraMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasCameraMask = new Canvas(bitmapCameraMask);

        //이 메소드에서 아래가 핵심!
        ColorMatrix colorMatrixAlpha = new ColorMatrix(new float[]{
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 255, 0
        });

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrixAlpha));
        canvasCameraMask.drawBitmap(bitmapCase, 0, 0, paint);
        paint.setColorFilter(null);

        Bitmap bitmapCamera = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasCamera = new Canvas(bitmapCamera);

        canvasCamera.drawBitmap(bitmapPhone, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvasCamera.drawBitmap(bitmapCameraMask, 0, 0, paint);

        return bitmapCamera;
    }

    public Bitmap composeBackground(Bitmap bitmapPhone, Bitmap bitmapCase) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int w = bitmapPhone.getWidth();
        int h = bitmapPhone.getHeight();

        Bitmap bitmapPhoneBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasPhoneBackground = new Canvas(bitmapPhoneBackground);

        canvasPhoneBackground.drawBitmap(bitmapPhone, 0, 0, paint);
        canvasPhoneBackground.drawBitmap(bitmapCase, 0, 0, paint);

        return bitmapPhoneBackground;
    }

}
