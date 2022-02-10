package com.snaps.mobile.activity.common.products.base;

import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.log.Dlog;

public class Fryingpan {

    private static final String TAG = Fryingpan.class.getSimpleName();
    private float widthRatio;
    private float heightRatio;
    private float productWidth;
    private float productHeight;

    public void turnOnFire(float productWidth, float productHeight, float templateWidth, float templateHeight) {
        if (productWidth > 0f && productHeight > 0f && templateWidth > 0f && templateHeight > 0f) {
            widthRatio = Math.round(productWidth / templateWidth * 1000f) / 1000.0f; // 소수점 4번째 자리에서 반올림 -> 소수점 3자리 까지만 사용
            heightRatio = Math.round(productHeight / templateHeight * 1000f) / 1000.0f; // 소수점 4번째 자리에서 반올림 -> 소수점 3자리 까지만 사용

        } else {
            widthRatio = 1.0f;
            heightRatio = 1.0f;
        }

        this.productWidth = productWidth;
        this.productHeight = productHeight;

        Dlog.d("widthRatio : " + widthRatio);
        Dlog.d("heightRatio : " + heightRatio);
    }

    /**
     * 기존에 씬의 크기와 배경의 크기는 동일했으나 변경됨
     * <p>
     * 배경등록은 기본 템플릿의 사이즈로 등록
     * <p>
     * 기본 템플릿의 배경을 변경되는 기종의 씬의 크기에 맞춰 변경
     * <p>
     * Paper full 의 형태로 리사이즈 & 리포지션
     *
     * @param control
     */
    public void flip(SnapsBgControl control) {
        validateFryingpanState();

        control.width = String.valueOf(productWidth);
        control.height = String.valueOf(productHeight);
    }

    /**
     * 기본적으로 paper full 의 형태를 유지하므로 사용한 이미지가 있다면 이미지 정보를 리셋을 한다
     * <p>
     * 가로, 세로 비율에 맞춰 리사이즈
     * <p>
     * x, y 좌표 비율에 맞춰 리포지션
     *
     * @param control
     */
    public void flip(SnapsLayoutControl control) {

        validateFryingpanState();

        Dlog.d("SnapsLayoutControl Before : x : " + control.getIntX() + ", y : " + control.getIntY() + ", width : " + control.getIntWidth() + ", height : " + control.getIntHeight());

        int x = Math.round(control.getIntX() * widthRatio);
        int y = Math.round(control.getIntY() * heightRatio);
        int width = Math.round(control.getIntWidth() * widthRatio);
        int height = Math.round(control.getIntHeight() * heightRatio);

        control.x = String.valueOf(x);
        control.y = String.valueOf(y);
        control.width = String.valueOf(width);
        control.height = String.valueOf(height);

        Dlog.d("SnapsLayoutControl Ater : x : " + control.getIntX() + ", y : " + control.getIntY() + ", width : " + control.getIntWidth() + ", height : " + control.getIntHeight());

    }

    /**
     * 클립아트에 사용한 가로, 세로 비율을 그대로 사용
     * <p>
     * 상품의 짧은 쪽을 기준으로 한 비율을 나머지 쪽에 적용
     *
     * @param control
     */
    public void flip(SnapsClipartControl control) {

        validateFryingpanState();

        Dlog.d("SnapsClipartControl Before : x : " + control.getIntX() + ", y : " + control.getIntY() + ", width : " + control.getIntWidth() + ", height : " + control.getIntHeight());

        int x = Math.round(control.getIntX() * widthRatio);
        int y = Math.round(control.getIntY() * heightRatio);
        int width = Math.round(control.getIntWidth() * widthRatio);
        int height = Math.round(control.getIntHeight() * widthRatio);

        control.x = String.valueOf(x);
        control.y = String.valueOf(y);
        control.width = String.valueOf(width);
        control.height = String.valueOf(height);

        Dlog.d("SnapsClipartControl Ater : x : " + control.getIntX() + ", y : " + control.getIntY() + ", width : " + control.getIntWidth() + ", height : " + control.getIntHeight());
    }

    /**
     * 텍스트에 사용한 가로, 세로 비율을 그대로 사용
     * <p>
     * 상품의 짧은 쪽을 기준으로 한 비율을 나머지 쪽에 적용
     * <p>
     * 텍스트 사이즈는 정수로 처리함.
     *
     * @param control
     */
    public void flip(SnapsTextControl control) {

        validateFryingpanState();

        Dlog.d("SnapsTextControl Before : x : " + control.getIntX() + ", y : " + control.getIntY() + ", width : " + control.getIntWidth() + ", height : " + control.getIntHeight());

        int x = Math.round(control.getIntX() * widthRatio);
        int y = Math.round(control.getIntY() * heightRatio);
        int width = Math.round(control.getIntWidth() * widthRatio);
        int height = Math.round(control.getIntHeight() * widthRatio);

        int originFontSize = Integer.parseInt(control.format.fontSize);
        int fontSize = (int) (originFontSize * widthRatio);

        control.x = String.valueOf(x);
        control.y = String.valueOf(y);
        control.width = String.valueOf(width);
        control.height = String.valueOf(height);
        control.format.fontSize = String.valueOf(fontSize);

        Dlog.d("SnapsTextControl Ater : x : " + control.getIntX() + ", y : " + control.getIntY() + ", width : " + control.getIntWidth() + ", height : " + control.getIntHeight());

    }

    private void validateFryingpanState() {
        if (widthRatio == 0f || heightRatio == 0f) {
            throw new IllegalArgumentException("Before flip, must turn on fire ! (call turnOnFire())");
        }
    }

    /**
     * For Test
     */
    public float getWidthRatio() {
        return widthRatio;
    }

    public float getHeightRatio() {
        return heightRatio;
    }
}
