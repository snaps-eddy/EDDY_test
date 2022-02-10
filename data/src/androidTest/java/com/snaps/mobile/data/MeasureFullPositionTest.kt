package com.snaps.mobile.data

import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import com.snaps.mobile.domain.asset.AnalysisInfo
import com.snaps.mobile.domain.asset.AssetImageType
import com.snaps.mobile.domain.project.ImageThumbnail
import com.snaps.mobile.domain.save.SceneObject
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test

class MeasureFullPositionTest {

    @Test
    fun hello_world() {
//        <object type="image" bgColor="16777215" angle="0" angleClip="0" uploadType="file" file="2021101208131616531.jpg" imgYear="2021" imgSeq="0141318936"
//        x="-129.0" y="-1.0" width="774" height="787" clipX="0.0" clipY="-1.0" clipWidth="515" clipHeight="787"
//        mask="" alpha="255" borderOption="inner" bordersinglecolortype="" />

        val clipX = 0f
        val clipY = -1.0f
        val clipWidth = 515f
        val clipHeight = 787f

        val originWidth = 973f
        val originHeight = 1080f

        val orientation = 1 // 1 -> 0, 6 -> 90

        val sceneObject = SceneObject.Image().apply {
            drawIndex = "33"
            x = clipX
            y = clipY
            width = clipWidth
            height = clipHeight
        }

        /**
         * 오리엔테이션 먹인 Width, Height 로 계산할 것.
         */
        val thumbnailImage = ImageThumbnail(
            imgSeq = "",
            year = "",
            localId = "",
            type = AssetImageType.Device,
            thumbnailUri = "",
            originWidth = originWidth,
            originHeight = originHeight,
            thumbnailRemotePath = "",
            analysisInfo = AnalysisInfo(),
            orientation = orientation,
            date = "",
        )

        sceneObject.insertImage(thumbnailImage)

        sceneObject.innerImage?.let {
            if (it.angle == 90 || it.angle == 270) {
                val originRect = RectF(it.x, it.y, it.x + it.height, it.y + it.width)
                val measuredPosition = RectF(originRect).also { oo ->
                    Matrix().apply {
                        setRotate(-it.angle.toFloat(), oo.centerX(), oo.centerY())
                        mapRect(oo)
                    }
                }
                Log.d("SNAPS#", "X : ${measuredPosition.left + clipX}")
                Log.d("SNAPS#", "Y : ${measuredPosition.top + clipY}")
                Log.d("SNAPS#", "WIDTH : ${measuredPosition.width()}")
                Log.d("SNAPS#", "HEIGHT : ${measuredPosition.height()}")
                it.angle shouldBe 90

            } else {
                Log.d("SNAPS#", "X : ${it.x + clipX}")
                Log.d("SNAPS#", "Y : ${it.y + clipY}")
                Log.d("SNAPS#", "WIDTH : ${it.width}")
                Log.d("SNAPS#", "HEIGHT : ${it.height}")
                it.angle shouldNotBe 90
            }
        }
    }

}