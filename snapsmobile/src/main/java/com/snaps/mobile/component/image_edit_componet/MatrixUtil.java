package com.snaps.mobile.component.image_edit_componet;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;

/**
 * Created by ysjeong on 2017. 6. 7..
 */

public class MatrixUtil {

    public static Matrix getMatrixAppliedEditInfo(Bitmap resource, AdjustableCropInfo cInfo, ImageView view, int controlWidth, int controlHeight) throws Exception {
        //이동 계산
        AdjustableCropInfo.CropImageRect clipRect = cInfo.getClipRect();
        AdjustableCropInfo.CropImageRect imgRect = cInfo.getImgRect();

        float clipWidthRatio = 1.f;
        float clipHeightRatio = 1.f;

        if (clipRect != null) {
            clipWidthRatio = clipRect.width / controlWidth;
            clipHeightRatio = clipRect.height / controlHeight;
        }

        Matrix imageViewMatrix = MatrixUtil.getDefaultCenterMatrix(view, controlWidth, controlHeight, resource);

        //핀치 줌
        if (imgRect != null) {
            float scaleX = imgRect.scaleX;
            float scaleY = imgRect.scaleY;

            imageViewMatrix.postScale(scaleX, scaleY, controlWidth / 2, controlHeight / 2);

            //회전 적용
            imageViewMatrix.postRotate(imgRect.angle, controlWidth / 2, controlHeight / 2);

            //이동 적용
            float moveX = imgRect.movedX / clipWidthRatio;
            float moveY = imgRect.movedY / clipHeightRatio;

            imageViewMatrix.postTranslate(moveX, moveY);
        }

        return imageViewMatrix;
    }

    public static Matrix getDefaultCenterMatrix(ImageView imageView, int imageViewMeasuredWidth, int imageViewMeasuredHeight, Bitmap resourceBitmap) throws Exception {
        Matrix imageViewMatrix = imageView.getImageMatrix();
        if (resourceBitmap == null) return imageViewMatrix;

        float drawableWidth = resourceBitmap.getWidth();
        float drawableHeight = resourceBitmap.getHeight();

        float scale;

        if (imageViewMeasuredWidth <= 0 || imageViewMeasuredHeight <= 0 || drawableWidth <= 0 || drawableHeight <= 0) return imageViewMatrix;

        float clipRectRatio = imageViewMeasuredWidth / (float)imageViewMeasuredHeight;
        float imageRatio = drawableWidth / drawableHeight;

        if (imageRatio > clipRectRatio) {
            scale = imageViewMeasuredHeight / drawableHeight;
        } else {
            scale = imageViewMeasuredWidth / drawableWidth;
        }

        drawableHeight *= scale;
        drawableWidth *= scale;

        imageViewMatrix.setScale(scale, scale);

        //center 맞추기
        float offsetX = (drawableWidth - imageViewMeasuredWidth) / 2;
        float offsetY = (drawableHeight - imageViewMeasuredHeight) / 2;
        imageViewMatrix.postTranslate(-offsetX, -offsetY);

        return imageViewMatrix;
    }

    public static float getXValueFromMatrix(Matrix matrix) {

        float[] values = new float[9];
        matrix.getValues(values);
        return values[2];
    }

    public static float getYValueFromMatrix(Matrix matrix) {

        float[] values = new float[9];
        matrix.getValues(values);
        return values[5];
    }

    public static float getWidthFromMatrix(Matrix matrix, @NonNull ImageView imageView) {
        float[] values = new float[9];
        matrix.getValues(values);

        Drawable d = imageView.getDrawable();
        int imageWidth = d.getIntrinsicWidth();
        return imageWidth * values[0];
    }

    public static float getHeightFromMatrix(Matrix matrix,  @NonNull ImageView imageView) {
        float[] values = new float[9];
        matrix.getValues(values);

        Drawable d = imageView.getDrawable();
        int imageHeight = d.getIntrinsicHeight();
        return imageHeight * values[4];
    }

    public static RectF getMatrixRect(@NonNull Matrix matrix, @NonNull ImageView imageView) {
        float l = MatrixUtil.getXValueFromMatrix(matrix);
        float t = MatrixUtil.getYValueFromMatrix(matrix);

        float r = l + MatrixUtil.getWidthFromMatrix(matrix, imageView);
        float b = t + MatrixUtil.getHeightFromMatrix(matrix, imageView);

        return new RectF(l, t, r, b);
    }

    public static float getAngle(@NonNull Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return Math.round(Math.atan2(values[Matrix.MSKEW_X],
                values[Matrix.MSCALE_X]) * (180 / Math.PI));
    }

    public static float getMeasuredMatrixValue(int type, @NonNull Matrix matrix, @NonNull Matrix orgMatrix) {
        float[] values = new float[9];
        matrix.getValues(values);

        float[] initValues = new float[9];
        orgMatrix.getValues(initValues);

        float value = values[type] - initValues[type];

        return type == 0 || type == 4 ? value + 1 : value;
    }
}
