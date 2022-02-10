package com.snaps.mobile.component.image_edit_componet;

import android.graphics.Matrix;

/**
 * Created by ysjeong on 2017. 6. 8..
 */

public class SnapsImageCropMatrixInfo {
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Matrix tempMatrix = new Matrix();
	private Matrix scaledMatrix = new Matrix();
	private Matrix originMatrix = new Matrix(); //최초 초기값
	private Matrix touchDownMatrix = new Matrix(); //화면을 클릭하는 순간의 값
	private Matrix lastAllowMatrix = new Matrix(); //화면을 클릭하는 순간의 값

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public Matrix getSavedMatrix() {
		return savedMatrix;
	}

	public void setSavedMatrix(Matrix savedMatrix) {
		this.savedMatrix = savedMatrix;
	}

	public Matrix getTempMatrix() {
		return tempMatrix;
	}

	public void setTempMatrix(Matrix tempMatrix) {
		this.tempMatrix = tempMatrix;
	}

	public Matrix getScaledMatrix() {
		return scaledMatrix;
	}

	public void setScaledMatrix(Matrix scaledMatrix) {
		this.scaledMatrix = scaledMatrix;
	}

	public Matrix getOriginMatrix() {
		return originMatrix;
	}

	public void setOriginMatrix(Matrix originMatrix) {
		this.originMatrix = originMatrix;
	}

	public Matrix getTouchDownMatrix() {
		return touchDownMatrix;
	}

	public void setTouchDownMatrix(Matrix touchDownMatrix) {
		this.touchDownMatrix = touchDownMatrix;
	}

	public Matrix getLastAllowMatrix() {
		return lastAllowMatrix;
	}

	public void setLastAllowMatrix(Matrix lastAllowMatrix) {
		this.lastAllowMatrix = lastAllowMatrix;
	}
}
