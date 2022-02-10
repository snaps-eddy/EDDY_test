package com.snaps.mobile.presentation.editor.imageEdit

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF

class MatrixCalculator {
    fun createRectToRectMatrix(srcWidth: Float, srcHeight: Float, dstWidth: Float, dstHeight: Float) : Matrix {
        val srcRect = RectF(0f, 0f, srcWidth, srcHeight)
        val dstRect = RectF(0f, 0f, dstWidth, dstHeight)
        return Matrix().apply {
            setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER)
        }
    }

    fun getBitmapRect(matrix: Matrix, orgBitmapWidth: Float, orgBitmapHeight: Float): RectF {
        val matrixValues = FloatArray(9)
        matrix.getValues(matrixValues)
        val offsetX = matrixValues[Matrix.MTRANS_X]
        val offsetY = matrixValues[Matrix.MTRANS_Y]
        val width = orgBitmapWidth * matrixValues[Matrix.MSCALE_X]
        val height = orgBitmapHeight * matrixValues[Matrix.MSCALE_Y]
        return RectF(offsetX, offsetY, offsetX + width, offsetY + height)
    }

    fun getBitmapCenterPoint(matrix: Matrix, orgBitmapWidth: Float, orgBitmapHeight: Float): PointF {
        val rect = getBitmapRect(matrix, orgBitmapWidth, orgBitmapHeight)
        return PointF(rect.left + rect.width() / 2, rect.top + rect.height() / 2)
    }

    fun getScale(matrix: Matrix): Float {
        val matrixValues = FloatArray(9)
        matrix.getValues(matrixValues)
        return matrixValues[Matrix.MSCALE_X]
    }

    fun getBitmapOffset(matrix: Matrix) : PointF {
        val matrixValues = FloatArray(9)
        matrix.getValues(matrixValues)
        val offsetX = matrixValues[Matrix.MTRANS_X]
        val offsetY = matrixValues[Matrix.MTRANS_Y]
        return PointF(offsetX, offsetY)
    }
}